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
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.Message;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class YamlStringsTest extends TestCase {

  public void testReadFrom() throws Exception {
    URL stringsUrl = Resources.getResource(YamlStringsTest.class, "en-US.yml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    new YamlStrings().readFrom(inputSupplier.getInput());
  }

  public void testWriteTo() throws Exception {
    URL stringsUrl = Resources.getResource(YamlStringsTest.class, "en-US.yml");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    WritableMessageCatalog catalog = new YamlStrings().writeTo(output);

    for (Message message : new YamlStrings().readFrom(inputSupplier.getInput()).readMessages()) {
      catalog.writeMessage(message);
    }

    catalog.close();
  }
}
