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

import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

/**
 * A base for {@link TextFragment} implementations that implements the basic
 * visitor API.
 */
public abstract class AbstractTextFragment implements TextFragment {

  public void accept(VisitorContext ctx, MessageFragmentVisitor mfv) {
    mfv.visitTextFragment(ctx, this);
  }

  public abstract String getText();
}
