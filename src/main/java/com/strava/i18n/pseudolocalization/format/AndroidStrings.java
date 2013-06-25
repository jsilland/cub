/**
 * Copyright 2013 Strava Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.strava.i18n.pseudolocalization.format;

import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * A message catalog capable of reading a writing Android strings.xml files.
 *
 * @author julien@strava.com (Julien Silland)
 */
public class AndroidStrings implements MessageCatalog {

  @Override
  public ReadableMessageCatalog readFrom(InputStream istr) throws IOException {
    DocumentBuilder documentBuilder;
    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IOException(e);
    }

    Document document;
    try {
      document = documentBuilder.parse(istr);
    } catch (SAXException e) {
      throw new IOException(e);
    }

    document.getDocumentElement().normalize();
    Element resources = document.getDocumentElement();

    final ImmutableList.Builder<Message> messages = ImmutableList.builder();

    NodeList strings = resources.getElementsByTagName("string");
    for (int i = 0; i < strings.getLength(); i++) {
      Element string = (Element) strings.item(i);
      String stringName = string.getAttribute("name");
      String messageValue = string.getChildNodes().getLength() == 0 ?
          "" : string.getFirstChild().getNodeValue();
      Message message = new FormattedMessage(
          AndroidMessageKey.forSimpleMessage(stringName).toString(), messageValue);
      messages.add(message);
    }

    NodeList arrays = resources.getElementsByTagName("string-array");
    for (int i = 0; i < arrays.getLength(); i++) {
      Element array = (Element) arrays.item(i);
      String arrayName = array.getAttribute("name");
      NodeList items = array.getElementsByTagName("item");
      for (int j = 0; j < items.getLength(); j++) {
        Element item = (Element) items.item(j);
        if ("item".equals(item.getTagName())) {
          AndroidMessageKey key = AndroidMessageKey.forArrayPosition(arrayName, j);
          Message message = new FormattedMessage(key.toString(),
              item.getFirstChild().getNodeValue());
          messages.add(message);
        }
      }
    }

    NodeList plurals = resources.getElementsByTagName("plurals");
    for (int i = 0; i < plurals.getLength(); i++) {
      Element plural = (Element) plurals.item(i);
      String pluralName = plural.getAttribute("name");
      NodeList forms = plural.getElementsByTagName("item");
      for (int j = 0; j < forms.getLength(); j++) {
        Element form = (Element) forms.item(j);
        if ("item".equals(form.getTagName())) {
          String quantity = form.getAttribute("quantity");
          AndroidMessageKey key = AndroidMessageKey.forPlural(pluralName,
              AndroidMessageKey.PluralForm.of(quantity));
          Message message = new FormattedMessage(key.toString(),
              form.getFirstChild().getNodeValue());
          messages.add(message);
        }
      }
    }

    return new ReadableMessageCatalog() {
      @Override
      public void close() throws IOException {
        // nop
      }

      @Override
      public Iterable<Message> readMessages() throws IOException {
        return messages.build();
      }
    };
  }

  @Override
  public WritableMessageCatalog writeTo(final OutputStream ostr) throws IOException {
    return new WritableMessageCatalog() {

      private final Map<String, Message> strings = Maps.newHashMap();
      private final Multimap<String, IndexedMessage> arrays = TreeMultimap.create();
      private final Table<String, AndroidMessageKey.PluralForm, Message> plurals =
          HashBasedTable.create();

      @Override
      public void close() throws IOException {
        DocumentBuilder documentBuilder;
        try {
          documentBuilder = DocumentBuilderFactory.newInstance()
              .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
          throw new IOException(e);
        }

        Document document = documentBuilder.newDocument();
        Element resources = document.createElement("resources");
        document.appendChild(resources);

        for (Map.Entry<String, Message> string : strings.entrySet()) {
          Element stringNode = document.createElement("string");

          Attr nameNode = document.createAttribute("name");
          nameNode.setValue(string.getKey());
          stringNode.setAttributeNode(nameNode);
          ToStringVisitor visitor = new ToStringVisitor();
          string.getValue().accept(visitor);
          Text text = document.createTextNode(visitor.fragmentVisitor.getStringResult());
          stringNode.appendChild(text);

          resources.appendChild(stringNode);
        }

        for (String name : arrays.keySet()) {
          Element arrayNode = document.createElement("string-array");
          Attr nameNode = document.createAttribute("name");
          nameNode.setValue(name);
          arrayNode.setAttributeNode(nameNode);

          for (IndexedMessage message : arrays.get(name)) {
            Element item = document.createElement("item");
            ToStringVisitor visitor = new ToStringVisitor();
            message.getMessage().accept(visitor);
            Text text = document.createTextNode(visitor.fragmentVisitor.getStringResult());
            item.appendChild(text);
            arrayNode.appendChild(item);
          }

          resources.appendChild(arrayNode);
        }

        for (String name : plurals.rowKeySet()) {
          Element pluralsNode = document.createElement("plurals");
          Attr nameNode = document.createAttribute("name");
          nameNode.setValue(name);
          pluralsNode.setAttributeNode(nameNode);

          for (Map.Entry<AndroidMessageKey.PluralForm, Message> form : plurals.row(name).entrySet()) {
            Element item = document.createElement("item");
            Attr quantity = document.createAttribute("quantity");
            quantity.setValue(form.getKey().getValue());
            item.setAttributeNode(quantity);

            ToStringVisitor visitor = new ToStringVisitor();
            form.getValue().accept(visitor);
            Text text = document.createTextNode(visitor.fragmentVisitor.getStringResult());
            item.appendChild(text);

            pluralsNode.appendChild(item);
          }

          resources.appendChild(pluralsNode);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
          transformer = transformerFactory.newTransformer();
          transformer.setOutputProperty(OutputKeys.INDENT, "yes");
          transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
          throw new IOException(e);
        }

        try {
          transformer.transform(new DOMSource(document), new StreamResult(ostr));
        } catch (TransformerException e) {
          throw new IOException(e);
        }
      }

      @Override
      public void writeMessage(Message msg) throws IOException {
        if (!msg.getId().startsWith("/android/")) {
          return;
        }

        AndroidMessageKey key = AndroidMessageKey.parse(msg.getId());
        switch (key.getType()) {

          case STRING:
            strings.put(key.getKey(), msg);
            break;
          case ARRAY:
            arrays.put(key.getKey(), new IndexedMessage(key.getIndex(), msg));
            break;
          case PLURAL:
            plurals.put(key.getKey(), key.getPluralForm(), msg);
            break;
        }
      }
    };
  }

  private static class IndexedMessage implements Comparable<IndexedMessage> {

    private final int index;
    private final Message message;

    public IndexedMessage(int index, Message message) {
      this.index = index;
      this.message = message;
    }

    public Message getMessage() {
      return message;
    }

    @Override
    public int compareTo(IndexedMessage other) {
      return index - other.index;
    }
  }

}
