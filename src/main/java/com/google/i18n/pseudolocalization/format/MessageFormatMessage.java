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

package com.google.i18n.pseudolocalization.format;

import com.google.common.base.Objects;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * A source message whose format is based upon {@link MessageFormat}.
 */
public class MessageFormatMessage extends SimpleMessage {

  private static List<MessageFragment> parseMessage(String text) {
    List<MessageFragment> list = new ArrayList<MessageFragment>();
    // TODO: handle quoting
    Matcher m = JavaProperties.MESSAGE_FORMAT_ARG.matcher(text);
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

  public MessageFormatMessage(String key, String text) {
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
