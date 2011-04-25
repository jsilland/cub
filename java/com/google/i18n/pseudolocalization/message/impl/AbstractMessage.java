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
package com.google.i18n.pseudolocalization.message.impl;

import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.MessageVisitor;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A base for {@link Message} implementations that implements the basic visitor
 * API.
 */
public abstract class AbstractMessage implements Message {

  public final void accept(MessageVisitor visitor) {
    VisitorContext ctx = getVisitorContext();
    MessageFragmentVisitor mfv = visitor.visitMessage(ctx, this);
    if (mfv != null) {
      List<MessageFragment> copy = new ArrayList<MessageFragment>();
      for (MessageFragment fragment : getFragments()) {
        copy.add(fragment);
      }
      for (MessageFragment fragment : copy) {
        fragment.accept(ctx, mfv);
      }
    }
    visitor.endMessage(ctx, this);
  }

  public String getId() {
    return null;
  }

  /**
   * Get a {@link VisitorContext} that can be used by visitors to mutate this
   * message.
   * 
   * @return a {@link VisitorContext} instance
   */
  protected abstract VisitorContext getVisitorContext();

  /**
   * Get the list of message fragments for this message.
   * 
   * @return message fragments
   */
  protected abstract Iterable<MessageFragment> getFragments();
}
