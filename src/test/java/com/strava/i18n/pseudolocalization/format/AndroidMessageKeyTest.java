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

import junit.framework.Assert;
import junit.framework.TestCase;

public class AndroidMessageKeyTest extends TestCase {

  public void testParseWithoutPrefix() {
    try {
      AndroidMessageKey.parse("foo");
      Assert.fail();
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  public void testCreateAndParseSimpleMessage() {
    AndroidMessageKey key = AndroidMessageKey.forSimpleMessage("key");
    try {
      key.getIndex();
      Assert.fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    try {
      key.getPluralForm();
      Assert.fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.STRING, key.getType());
    Assert.assertEquals("key", key.getKey());

    AndroidMessageKey copy = AndroidMessageKey.parse(key.toString());
    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.STRING, copy.getType());
    Assert.assertEquals("key", copy.getKey());
  }

  public void testCreateAndParseArrayMessage() {
    AndroidMessageKey key = AndroidMessageKey.forArrayPosition("key", 4);

    try {
      key.getPluralForm();
      Assert.fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.ARRAY, key.getType());
    Assert.assertEquals("key", key.getKey());
    Assert.assertEquals(new Integer(4), key.getIndex());

    AndroidMessageKey copy = AndroidMessageKey.parse(key.toString());
    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.ARRAY, copy.getType());
    Assert.assertEquals("key", copy.getKey());
    Assert.assertEquals(new Integer(4), copy.getIndex());
  }

  public void testCreateAndParsePluralMessage() {
    AndroidMessageKey key = AndroidMessageKey.forPlural("key", AndroidMessageKey.PluralForm.FEW);

    try {
      key.getIndex();
      Assert.fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.PLURAL, key.getType());
    Assert.assertEquals("key", key.getKey());
    Assert.assertEquals(AndroidMessageKey.PluralForm.FEW, key.getPluralForm());

    AndroidMessageKey copy = AndroidMessageKey.parse(key.toString());
    Assert.assertEquals(AndroidMessageKey.AndroidMessageType.PLURAL, copy.getType());
    Assert.assertEquals("key", copy.getKey());
    Assert.assertEquals(AndroidMessageKey.PluralForm.FEW, copy.getPluralForm());
  }
}
