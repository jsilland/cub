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
 * Visitor interface for visiting individual message fragments.
 */
public interface MessageFragmentVisitor {

  /**
   * Visit a message fragment that contains non-localizable text.  This may be
   * HTML tags, format strings, etc.
   *
   * @param ctx
   * @param fragment
   */
  void visitNonlocalizableTextFragment(VisitorContext ctx,
      NonlocalizableTextFragment fragment);

  /**
   * Visit a message fragment that is a placeholder for some content to be
   * substituted at runtime.
   *
   * @param ctx
   * @param placeholder
   */
  void visitPlaceholder(VisitorContext ctx, Placeholder placeholder);

  /**
   * Visit a message fragment that contains localizable text.
   *
   * @param ctx
   * @param fragment
   */
  void visitTextFragment(VisitorContext ctx, TextFragment fragment);

  /**
   * Visit a message fragment that represents one or more fragments, one of
   * which will be chosen at runtime.  Examples include plural or gender
   * selection of the text to use.
   *
   * @param ctx
   * @param fragment
   * @return visitor to use for visiting parts of the variant fragment, or
   *     null if no visit is required
   */
  VariantFragmentVisitor visitVariantFragment(VisitorContext ctx,
      VariantFragment fragment);
}
