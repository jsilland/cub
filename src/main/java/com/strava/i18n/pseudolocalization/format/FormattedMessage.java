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

import com.google.common.base.Objects;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;
import com.google.i18n.pseudolocalization.message.impl.AbstractPlaceholder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A message supporting {@link java.util.Formatter}-like placeholders.
 *
 * @author Julien Silland (julien@strava.com)
 */
public class FormattedMessage extends SimpleMessage {

  private static final class FormattedPlaceholder extends AbstractPlaceholder {

    private final String text;

    public FormattedPlaceholder(String text) {
      this.text = text;
    }

    @Override
    public String getTextRepresentation() {
      return text;
    }

    @Override
    public String toString() {
      return getTextRepresentation();
    }
  }

  private static final Pattern FORMAT_PATTERN = Pattern.compile(
      "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

  private static List<MessageFragment> parseMessage(String text) {
    List<MessageFragment> list = new ArrayList<MessageFragment>();
    Matcher m = FORMAT_PATTERN.matcher(text);
    int start = 0;
    while (m.find()) {
      String plainText = text.substring(start, m.start());
      start = m.end();
      if (plainText.length() > 0) {
        list.add(new SimpleTextFragment(plainText));
      }
      list.add(new FormattedPlaceholder(m.group()));
    }
    String plainText = text.substring(start);
    if (plainText.length() > 0) {
      list.add(new SimpleTextFragment(plainText));
    }
    return list;
  }

  private final String key;

  /**
   * Constructs an instance of this class from a plain string
   *
   * @param key the key of the message
   * @param text the content of the message
   */
  public FormattedMessage(String key, String text) {
    super(parseMessage(text));
    this.key = key;
  }

  @Override
  public String getId() {
    return key;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("Id", getId())
        .add("Fragments", getFragments())
        .toString();
  }


}
