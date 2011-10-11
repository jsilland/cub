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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface of a message catalog which is both readable and writable.
 * 
 * NOTE: THIS API IS EXPERIMENTAL AND SUBJECT TO CHANGE.
 */
public interface MessageCatalog {

  /**
   * Read a message catalog from an input stream.
   * 
   * @param istr
   * @return a {@link ReadableMessageCatalog} instance
   * @throws IOException
   */
  ReadableMessageCatalog readFrom(InputStream istr) throws IOException;

  /**
   * Write a message catalog to an output stream.
   * 
   * @param ostr
   * @return a {@link WritableMessageCatalog} instance
   * @throws IOException
   */
  WritableMessageCatalog writeTo(OutputStream ostr) throws IOException;
}
