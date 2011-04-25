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
 * A visitor for visiting a {@link VariantFragment}.
 */
public interface VariantFragmentVisitor {

  /**
   * Called after all forms in a {@link VariantSelector} have been visited.
   * 
   * @param selector
   */
  void endSelector(VisitorContext ctx, VariantSelector selector);

  /**
   * Called after all selectors in a {@link VariantFragment} have been visited.
   * 
   * @param fragment
   */
  void endVariantFragment(VisitorContext ctx, VariantFragment fragment);

  /**
   * Visit one selector.
   * 
   * @param selector
   * @return a {@link VariantFormVisitor} instance to use for visiting forms
   *     in this selector, or null if no visit is necessary
   */
  VariantFormVisitor visitSelector(VisitorContext ctx, VariantSelector selector);
}
