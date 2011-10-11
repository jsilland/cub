/*
 * Copyright 2011 Google Inc.
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
package com.google.i18n.pseudolocalization.format;

import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;
import com.google.i18n.pseudolocalization.message.impl.AbstractPlaceholder;
import com.google.i18n.pseudolocalization.message.impl.IterableTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaProperties implements MessageCatalog {

  static class PropertiesMessage extends SimpleMessage {

    private static List<MessageFragment> parseMessage(String text) {
      List<MessageFragment> list = new ArrayList<MessageFragment>();
      // TODO: handle quoting
      Matcher m = MESSAGE_FORMAT_ARG.matcher(text);
      int start = 0;
      while (m.find()) {
        String plainText = text.substring(start, m.start());
        start = m.end();
        if (plainText.length() > 0) {
          list.add(new SimpleTextFragment(plainText));
        }
        list.add(new MessageFormatPlaceholder(m.group()));
      }
      String plainText = text.substring(start);
      if (plainText.length() > 0) {
        list.add(new SimpleTextFragment(plainText));
      }
      return list;
    }

    private final String key;

    public PropertiesMessage(String key, String text) {
      super(parseMessage(text));
      this.key = key;
    }

    @Override
    public String getId() {
      return key;
    }
  }

  private static class JavaPropertiesReader implements ReadableMessageCatalog {

    private final InputStream stream;
    private Properties properties;

    public JavaPropertiesReader(InputStream stream) {
      this.stream = stream;
    }
    
    private void ensureProperties() throws IOException {
      if (properties != null) {
        return;
      }
      properties = new Properties();
      try {
        properties.load(stream);
      } finally {
        stream.close();
      }
    }

    public void close() {
      // do nothing
    }

    public Iterable<Message> readMessages() throws IOException {
      ensureProperties();
      return new IterableTransformer<String, Message>(Collections.unmodifiableSet(
          properties.stringPropertyNames())) {
        @Override
        protected Message transform(String val) {
          return new PropertiesMessage(val, properties.getProperty(val));
        }
      };
    }    
  }

  private static class JavaPropertiesWriter implements WritableMessageCatalog {

    private final Properties properties = new Properties();
    private final OutputStream stream;

    JavaPropertiesWriter(OutputStream stream) {
      this.stream = stream;
    }

    public void close() throws IOException {
      properties.store(stream, "");
    }

    public void writeMessage(Message msg) {
      properties.put(msg.getId(), ((PropertiesMessage) msg).getText());
    }
  }

  private static class MessageFormatPlaceholder extends AbstractPlaceholder {

    private final String text;

    public MessageFormatPlaceholder(String text) {
      this.text = text;
    }

    @Override
    public String getTextRepresentation() {
      return text;
    }
  }

  /**
   * Matches {number}, {number,word}, {number,word,extra} as a quick hack for
   * MessageFormat-style placeholders.  Note that this is an incomplete solution
   * since it doesn't handle quoting.
   */
  static final Pattern MESSAGE_FORMAT_ARG = Pattern.compile(
      "\\{((\\d+)|#|(\\w+))(,\\w+(,[^\\}]+)?)?\\}");

  public ReadableMessageCatalog readFrom(InputStream istr) {
    return new JavaPropertiesReader(istr);
  }

  public WritableMessageCatalog writeTo(OutputStream ostr) {
    return new JavaPropertiesWriter(ostr);
  }
}
