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
 * A visitor for {@link VariantForm} instances.
 */
public interface VariantFormVisitor {

  /**
   * Called after all fragments under this {@link VariantForm} have been
   * visited.
   * 
   * @param ctx
   * @param form
   */
  void endVariantForm(VisitorContext ctx, VariantForm form);

  /**
   * Visit a {@link VariantForm}.
   * 
   * @param ctx
   * @param form
   * @return a {@link MessageFragmentVisitor} to use for visiting fragments
   *     inside this form, or null if no visit is needed.
   */
  MessageFragmentVisitor visitVariantForm(VisitorContext ctx, VariantForm form);
}
