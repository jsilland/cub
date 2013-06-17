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

/**
 * Visitor for a single {@link Message}.
 */
public interface MessageVisitor {

  /**
   * Called after finishing visiting a message.
   * 
   * @param message
   */
  void endMessage(VisitorContext ctx, Message message);

  /**
   * Called at the beginning of visiting a message.
   * 
   * @param ctx
   * @param message
   * @return visitor to use for visiting fragments of this message, or null if
   *     the fragments need not be visited
   */
  MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message);
}
