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

import java.io.Closeable;
import java.io.IOException;

/**
 * A message catalog that allows reading structured messages.
 */
public interface ReadableMessageCatalog extends Closeable {

  /**
   * Must be called when this catalog is no longer needed.
   */
  void close() throws IOException;

  /**
   * Load messages from the catalog.  This may involve reading the entire
   * contents of the catalog immediately, or it may read them as requested
   * from the iterator.
   * 
   * @return a collection of {@link Message}s
   * @throws IOException
   */
  Iterable<Message> readMessages() throws IOException;
}
