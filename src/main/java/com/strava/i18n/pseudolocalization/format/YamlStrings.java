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
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.i18n.pseudolocalization.PseudolocalizationException;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.Message;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * Represents a set of messages in a YAML file.
 *
 * @author Julien Silland (julien@strava.com)
 */
public class YamlStrings implements MessageCatalog {

  Joiner DOT = Joiner.on('.');

  @Override
  public ReadableMessageCatalog readFrom(InputStream istr) throws IOException {
    InputStreamReader reader = new InputStreamReader(istr, Charsets.UTF_8);
    final List<Message> messages = Lists.newArrayList();

    try {
      marshall(new Yaml().load(reader), new Stack<String>(), messages);
    } catch (PseudolocalizationException pe) {
      throw new IOException(pe);
    }

    return new ReadableMessageCatalog() {
      @Override
      public void close() throws IOException {
        // noop
      }

      @Override
      public Iterable<Message> readMessages() throws IOException {
        return messages;
      }
    };
  }

  public void marshall(Object yaml, Stack<String> keyStack, List<Message> messages)
      throws PseudolocalizationException {
    if (!(yaml instanceof Map)) {
      return;
    }
    Map<String, Object> entries = (Map<String, Object>) yaml;
    for (Map.Entry<String, Object> entry : entries.entrySet()) {
      keyStack.push(entry.getKey());
      if (entry.getValue() instanceof String) {
        messages.add(new YamlMessage(DOT.join(keyStack), (String) entry.getValue()));
      } else {
        marshall(entry.getValue(), keyStack, messages);
      }
      keyStack.pop();
    }
  }

  @Override
  public WritableMessageCatalog writeTo(final OutputStream ostr) throws IOException {
    return new WritableMessageCatalog() {

      Map<String, Object> root = Maps.newHashMap();

      @Override
      public void close() throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        options.setWidth(10000);
        new Yaml(options).dump(root, new OutputStreamWriter(ostr, Charsets.UTF_8));
      }

      @Override
      public void writeMessage(Message message) throws IOException {
        LinkedList<String> tokens = Lists.newLinkedList();
        for (String token : Splitter.on('.').split(message.getId())) {
          tokens.offer(token);
        }
        marshall(root, tokens, message);
      }

      public void marshall(Map<String, Object> into, LinkedList<String> tokens, Message message) {
        String token = tokens.poll();

        if (tokens.isEmpty()) {
          ToStringVisitor visitor = new ToStringVisitor();
          message.accept(visitor);
          String value = visitor.fragmentVisitor.getStringResult();
          into.put(token, value);
          return;
        }

        Map<String, Object> sub = into.containsKey(token) ? (Map<String,
            Object>) into.get(token) : Maps.<String, Object>newHashMap();
        into.put(token, sub);

        marshall(sub, tokens, message);
      }
    };
  }
}
