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

import com.google.i18n.pseudolocalization.message.VariantForm;
import com.google.i18n.pseudolocalization.message.VariantFormVisitor;
import com.google.i18n.pseudolocalization.message.VariantFragmentVisitor;
import com.google.i18n.pseudolocalization.message.VariantSelector;
import com.google.i18n.pseudolocalization.message.VisitorContext;

/**
 * A base for {@link VariantSelector} implementations that implements the basic
 * visitor API.
 */
public abstract class AbstractVariantSelector implements VariantSelector {

  public abstract String getEndRepresentation();

  public abstract String getStartRepresentation();

  public void accept(VisitorContext ctx, VariantFragmentVisitor vfv) {
    VariantFormVisitor formVisitor = vfv.visitSelector(ctx, this);
    if (formVisitor != null) {
      for (VariantForm form : getVariantForms()) {
        form.accept(ctx, formVisitor);
      }
    }
    vfv.endSelector(ctx, this);
  }

  /**
   * Get the list of variant forms for this variant selector.
   * 
   * @return variant forms
   */
  protected abstract Iterable<VariantForm> getVariantForms();
}
