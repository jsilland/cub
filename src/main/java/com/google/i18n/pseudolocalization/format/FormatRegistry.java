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


import java.util.HashMap;
import java.util.Map;

/**
 * Registry of known file formats.
 */
public class FormatRegistry {

  private static Map<String, Class<? extends MessageCatalog>> registry;
  private static final Object registryLock = new Object[0];

  static {
    synchronized(registryLock) {
      registry = new HashMap<String, Class<? extends MessageCatalog>>();
      registry.put("properties", JavaProperties.class);
    }
  }

  /**
   * Get a {@link MessageCatalog} for a given file extension. If nothing
   * matches, a default is returned that treats the entire file contents as a
   * single message.
   * 
   * @param extension
   * @return a {@link MessageCatalog} instance to use to read/write a file with
   *     the specified extension
   * @throws RuntimeException if the chosen {@link MessageCatalog} could not be
   *     instantiated
   */
  public static MessageCatalog getMessageCatalog(String extension) {
    Class<? extends MessageCatalog> clazz;
    synchronized (registryLock) {
      clazz = registry.get(extension);
    }
    if (clazz == null) {
      // if an unknown format, simply treat the entire file as one message
      clazz = MessagePerFile.class;
    }
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Register a new {@link MessageCatalog} class with one or more extensions.
   * 
   * @param clazz
   * @param extensions one or more file extensions to register
   */
  public static void register(Class<? extends MessageCatalog> clazz,
      String... extensions) {
    synchronized(registryLock) {
      for (String extension : extensions) {
        registry.put(extension, clazz);
      }
    }
  }

  private FormatRegistry() {
  }
}
