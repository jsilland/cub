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

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.Message;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class AndroidStringsTest extends TestCase {

  public void testReadFromStrings() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "strings.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    ReadableMessageCatalog catalog = new AndroidStrings().readFrom(inputSupplier.getInput());
    List<Message> messages = Lists.newArrayList(catalog.readMessages());
    Assert.assertEquals(1, messages.size());
  }

  public void testReadFromArrays() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "arrays.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    ReadableMessageCatalog catalog = new AndroidStrings().readFrom(inputSupplier.getInput());
    List<Message> messages = Lists.newArrayList(catalog.readMessages());
    Assert.assertEquals(4, messages.size());
  }

  public void testReadFromPlurals() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "plurals.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    ReadableMessageCatalog catalog = new AndroidStrings().readFrom(inputSupplier.getInput());
    List<Message> messages = Lists.newArrayList(catalog.readMessages());
    Assert.assertEquals(3, messages.size());
  }

  public void testWriteToStrings() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "strings.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    MessageCatalog messageCatalog = new AndroidStrings();
    ReadableMessageCatalog readableCatalog = messageCatalog.readFrom(inputSupplier.getInput());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    WritableMessageCatalog writableCatalog = messageCatalog.writeTo(outputStream);

    for (Message message : readableCatalog.readMessages()) {
      writableCatalog.writeMessage(message);
    }

    writableCatalog.close();

    String content = new String(outputStream.toByteArray(), Charsets.UTF_8);
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
    + "<resources>\n"
    + "  <string name=\"hello\">Hello!</string>\n"
    + "</resources>\n";
    Assert.assertEquals(expected, content);
  }

  public void testWriteToArrays() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "arrays.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    MessageCatalog messageCatalog = new AndroidStrings();
    ReadableMessageCatalog readableCatalog = messageCatalog.readFrom(inputSupplier.getInput());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    WritableMessageCatalog writableCatalog = messageCatalog.writeTo(outputStream);

    for (Message message : readableCatalog.readMessages()) {
      writableCatalog.writeMessage(message);
    }

    writableCatalog.close();

    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
        + "<resources>\n"
        + "  <string-array name=\"suits_array\">\n"
        + "    <item>Diamonds</item>\n"
        + "    <item>Hearts</item>\n"
        + "    <item>Clubs</item>\n"
        + "    <item>Spades</item>\n"
        + "  </string-array>\n"
        + "</resources>\n";

    String actual = new String(outputStream.toByteArray(), Charsets.UTF_8);

    Assert.assertEquals(expected, actual);

  }

  public void testWriteToPlurals() throws Exception {
    URL stringsUrl = Resources.getResource(AndroidStringsTest.class, "plurals.xml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    MessageCatalog messageCatalog = new AndroidStrings();
    ReadableMessageCatalog readableCatalog = messageCatalog.readFrom(inputSupplier.getInput());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    WritableMessageCatalog writableCatalog = messageCatalog.writeTo(outputStream);

    for (Message message : readableCatalog.readMessages()) {
      writableCatalog.writeMessage(message);
    }

    writableCatalog.close();

    String actual = new String(outputStream.toByteArray(), Charsets.UTF_8);
    Set<String> expectedEntries = Sets.newHashSet(
        "    <item quantity=\"other\">kilometers</item>",
        "    <item quantity=\"zero\">kilometers</item>",
        "    <item quantity=\"one\">kilometer</item>"
    );

    for (String line : actual.split("\n")) {
      expectedEntries.remove(line);
    }

    Assert.assertEquals(0, expectedEntries.size());
  }
}
