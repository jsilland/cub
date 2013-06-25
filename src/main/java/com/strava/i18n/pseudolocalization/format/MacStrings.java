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
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.Message;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacStrings implements MessageCatalog {

  @Override
  public ReadableMessageCatalog readFrom(InputStream istr) throws IOException {
    InputStreamReader reader = new InputStreamReader(istr, Charsets.UTF_8);
    final List<Message> messages = Lists.newArrayList();

    return CharStreams.readLines(reader, new LineProcessor<ReadableMessageCatalog>() {

      private final Pattern MAC_STRING = Pattern.compile(
          "^(\\s)?\"([A-Z_]+)\"(\\s)?=(\\s)?\"(.+)\";(\\s)?$");

      @Override
      public boolean processLine(String line) throws IOException {
        Matcher matcher = MAC_STRING.matcher(line);
        if (matcher.matches()) {
          String key = matcher.group(2);
          String value = matcher.group(5);
          messages.add(new MacMessage(key, value));
        }
        return true;
      }

      @Override
      public ReadableMessageCatalog getResult() {
        return new ReadableMessageCatalog() {
          @Override
          public void close() throws IOException {
            // nop
          }

          @Override
          public Iterable<Message> readMessages() throws IOException {
            return messages;
          }
        };
      }
    });
  }

  @Override
  public WritableMessageCatalog writeTo(final OutputStream ostr) throws IOException {
    return new WritableMessageCatalog() {
      @Override
      public void close() throws IOException {
        // nop
      }

      @Override
      public void writeMessage(Message msg) throws IOException {
        ToStringVisitor visitor = new ToStringVisitor();
        msg.accept(visitor);
        String value = visitor.fragmentVisitor.getStringResult();
        String line = String.format("\"%s\" = \"%s\";\n", msg.getId(), value);
        ostr.write(line.getBytes(Charsets.UTF_8));
      }
    };
  }
}
