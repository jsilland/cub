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

import com.google.common.collect.Maps;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.message.Message;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class MacStringsTest extends TestCase {

  public void testReadFrom() throws Exception {
    URL stringsUrl = Resources.getResource(MacStringsTest.class, "Localizable.strings");
    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(stringsUrl);
    ReadableMessageCatalog catalog = new MacStrings().readFrom(inputSupplier.getInput());
    Map<String, Message> messages = Maps.newHashMap();
    for (Message message : catalog.readMessages()) {
      messages.put(message.getId(), message);
    }
    Assert.assertEquals(24, messages.size());
  }

  public void testWriteTo() throws Exception {

  }
}
