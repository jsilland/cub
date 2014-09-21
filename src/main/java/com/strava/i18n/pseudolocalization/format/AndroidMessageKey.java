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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the key of an Android message, in which various bits of data are encoded (name,
 * plural form, array index, etc...).
 *
 * @author Julien Silland (julien@strava.com)
 */
public class AndroidMessageKey {

  private static final String PREFIX = "/android";
  private static final Joiner SLASH_JOINER = Joiner.on('/').skipNulls();
  private static final Splitter SLASH_SPLITTER = Splitter.on('/').omitEmptyStrings();

  /**
   * Enumeration of the type of messages found in an Android XML file.
   */
  public static enum AndroidMessageType {
    STRING("string"), ARRAY("array"), PLURAL("plural");

    private static final Map<String, AndroidMessageType> VALUES = new HashMap<String,
        AndroidMessageType>() {{
      for (AndroidMessageType type : AndroidMessageType.values()) {
        put(type.getValue(), type);
      }
    }};

    private final String value;

    private AndroidMessageType(String value) {
      this.value = value;
    }

    /**
     * Returns the value associated with this instance.
     */
    private String getValue() {
      return value;
    }

    /**
     * Looks up an instance of this enumeration by value.
     *
     * @param value the value to look up
     */
    private static AndroidMessageType of(String value) {
       if (VALUES.containsKey(value)) {
         return VALUES.get(value);
       }
      throw new IllegalArgumentException();
    }
  }

  /**
   * Enumeration of the plural forms supported by Android.
   */
  public static enum PluralForm {
    ZERO("zero"), ONE("one"), TWO("two"), FEW("few"), MANY("many"), OTHER("other");

    private static final Map<String, PluralForm> VALUES = new HashMap<String, PluralForm>() {{
      for (PluralForm form : PluralForm.values()) {
        put(form.getValue(), form);
      }
    }};

    private final String value;

    private PluralForm(String value) {
      this.value = value;
    }

    /**
     * Returns the value associated with this instance.
     */
    public String getValue() {
      return value;
    }

    /**
     * Looks up an instance of this enumeration by value.
     *
     * @param value the value to look up
     */
    public static PluralForm of(String value) {
      if (VALUES.containsKey(value)) {
        return VALUES.get(value);
      }
      throw new IllegalArgumentException();
    }
  }

  public static AndroidMessageKey parse(String key) {
    Preconditions.checkArgument(key.startsWith(PREFIX));
    List<String> parts = ImmutableList.copyOf(SLASH_SPLITTER.split(key));
    AndroidMessageType type = AndroidMessageType.of(parts.get(1));
    switch (type) {

      case STRING:
        return new AndroidMessageKey(type, parts.get(2), 0, null);
      case ARRAY:
        Integer index = Integer.parseInt(parts.get(3));
        return new AndroidMessageKey(type, parts.get(2), index, null);
      case PLURAL:
        PluralForm form = PluralForm.of(parts.get(3));
        return new AndroidMessageKey(type, parts.get(2), 0, form);
    }
    return null;
  }

  /**
   * Returns a message key representing a simple message
   *
   * @param key the key of the message in the XML file
   */
  public static AndroidMessageKey forSimpleMessage(String key) {
    return new AndroidMessageKey(AndroidMessageType.STRING, key, null, null);
  }

  /**
   * Returns a message key representing an item in an array
   *
   * @param key the key of the array in the XML file
   * @param index the index of the item within the array
   */
  public static AndroidMessageKey forArrayPosition(String key, int index) {
    return new AndroidMessageKey(AndroidMessageType.ARRAY, key, index, null);
  }

  /**
   * Returns a message key representing a plural form
   *
   * @param key the key of the message in the XML file
   * @param form the plural form represented of the message
   */
  public static AndroidMessageKey forPlural(String key, PluralForm form) {
    return new AndroidMessageKey(AndroidMessageType.PLURAL, key, null, form);
  }

  private final AndroidMessageType type;
  private final String key;
  private final Integer index;
  private final PluralForm pluralForm;

  private AndroidMessageKey(AndroidMessageType type, String key, Integer index,
                                  PluralForm pluralForm) {
    this.type = type;
    this.key = key;
    this.index = index;
    this.pluralForm = pluralForm;
  }

  /**
   * Returns the type of this key
   */
  public AndroidMessageType getType() {
    return type;
  }

  /**
   * Returns the key of this message in the XML file
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the index of this message withing its array
   *
   * @throws java.lang.IllegalStateException if this message key is not that of an array item
   */
  public Integer getIndex() {
    if (type != AndroidMessageType.ARRAY) {
      throw new IllegalStateException();
    }
    return index;
  }

  /**
   * Returns the plural form of this message
   *
   * @throws java.lang.IllegalStateException if this message key is not that of a plural message
   */
  public PluralForm getPluralForm() {
    if (type != AndroidMessageType.PLURAL) {
      throw new IllegalStateException();
    }
    return pluralForm;
  }

  @Override
  public String toString() {
    return SLASH_JOINER.join(PREFIX, type.getValue(), key, index,
        pluralForm != null ? pluralForm.getValue() : null);
  }
}
