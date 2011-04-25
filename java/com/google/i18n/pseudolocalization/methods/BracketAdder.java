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
package com.google.i18n.pseudolocalization.methods;

import com.google.i18n.pseudolocalization.PseudolocalizationMethod;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.NonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

/**
 * A {@link PseudolocalizationMethod} that adds brackets around the entire
 * message, to help identify where the application is concatenating separate
 * messages (which is bad because some locales might need to change the two
 * messages when concatenated, such as rearranging the order).  Generally, this
 * should be the last method applied.
 */
public class BracketAdder extends DefaultVisitor implements PseudolocalizationMethod {

  public static void register() {
    PseudolocalizationPipeline.registerMethodClass("brackets", BracketAdder.class);
  }

  @Override
  public void endMessage(VisitorContext ctx, Message message) {
    NonlocalizableTextFragment suffix = ctx.createNonlocalizableTextFragment("]");
    ctx.insertAfter(null, suffix);
  }

  @Override
  public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
    NonlocalizableTextFragment prefix = ctx.createNonlocalizableTextFragment("[");
    ctx.insertBefore(null, prefix);
    // no need to visit all of the message
    return null;
  }
}
