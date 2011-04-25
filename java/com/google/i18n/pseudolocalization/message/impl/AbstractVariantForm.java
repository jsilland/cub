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

import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.VariantForm;
import com.google.i18n.pseudolocalization.message.VariantFormVisitor;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A base for {@link VariantForm} implementations that implements the basic
 * visitor API.
 */
public abstract class AbstractVariantForm implements VariantForm {

  public void accept(VisitorContext ctx, VariantFormVisitor vfv) {
    MessageFragmentVisitor mfv = vfv.visitVariantForm(ctx, this);
    if (mfv != null) {
      List<MessageFragment> copy = new ArrayList<MessageFragment>();
      for (MessageFragment fragment : getFragments()) {
        copy.add(fragment);
      }
      for (MessageFragment fragment : copy) {
        fragment.accept(ctx, mfv);
      }
    }
    vfv.endVariantForm(ctx, this);
  }

  /**
   * Get the list of message fragments for this variant form.
   * 
   * @return message fragments
   */
  protected abstract Iterable<MessageFragment> getFragments();
}
