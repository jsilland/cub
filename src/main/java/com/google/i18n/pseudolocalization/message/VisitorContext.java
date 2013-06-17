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
package com.google.i18n.pseudolocalization.message;

import java.util.List;

/**
 * Context around a {@link MessageFragment} that allows inserting, removing,
 * and replacing nodes.
 * <p>
 * All changes are made on a copy of a message, so mutating the message while
 * visiting it does not alter the visitation order.
 */
public interface VisitorContext {

  /**
   * Create a new text fragment.
   * 
   * @param text
   * @return a {@link TextFragment} instance containing {@code text}
   */
  TextFragment createTextFragment(String text);

  /**
   * Create a new non-localizable text fragment.
   * 
   * @param text
   * @return a {@link NonlocalizableTextFragment} instance containing
   *     {@code text}
   */
  NonlocalizableTextFragment createNonlocalizableTextFragment(String text);

  /**
   * Insert a new {@link MessageFragment} after another fragment, or at the end
   * of the message if none is supplied.
   *
   * @param reference reference fragment to insert after, or null to indicate
   *     at the beginning of the message
   * @param newFrag
   */
  void insertAfter(MessageFragment reference, MessageFragment newFrag);

  /**
   * Insert a new {@link MessageFragment} before another fragment, or at the
   * beginning of the message if none is supplied.
   *
   * @param reference reference fragment to insert before, or null to indicate
   *     at the beginning of the message
   * @param newFrag
   */
  void insertBefore(MessageFragment reference, MessageFragment newFrag);

  /**
   * Replace a fragment with zero or more {@link MessageFragment}s.
   * 
   * @param fragment
   * @param replacements
   */
  void replaceFragment(MessageFragment fragment, List<MessageFragment> replacements);

  /**
   * Replace a fragment with zero or more {@link MessageFragment}s.
   * 
   * @param fragment
   * @param replacements
   */
  void replaceFragment(MessageFragment fragment, MessageFragment... replacements);
}
