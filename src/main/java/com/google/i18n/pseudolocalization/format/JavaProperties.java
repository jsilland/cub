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
import com.google.i18n.pseudolocalization.message.impl.IterableTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

public class JavaProperties implements MessageCatalog {

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
          return new MessageFormatMessage(val, properties.getProperty(val));
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
      properties.put(msg.getId(), ((MessageFormatMessage) msg).getText());
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
