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
 * Default visitor which includes no-ops for all visitor methods.  All methods  
 * which return sub-visitors return {@code this}.
 * <p>
 * Most visitors should extend this class to avoid future compatibility breaks
 * where possible.
 */
public class DefaultVisitor implements MessageVisitor, MessageFragmentVisitor, VariantFormVisitor,
    VariantFragmentVisitor {

  public void endMessage(VisitorContext ctx, Message message) {
  }

  public void endSelector(VisitorContext ctx, VariantSelector selector) {
  }

  public void endVariantForm(VisitorContext ctx, VariantForm form) {
  }

  public void endVariantFragment(VisitorContext ctx, VariantFragment fragment) {
  }

  public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
    return this;
  }

  public void visitNonlocalizableTextFragment(VisitorContext ctx,
      NonlocalizableTextFragment fragment) {
  }

  public void visitPlaceholder(VisitorContext ctx, Placeholder placeholder) {
  }

  public VariantFormVisitor visitSelector(VisitorContext ctx, VariantSelector selector) {
    return this;
  }

  public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
  }

  public MessageFragmentVisitor visitVariantForm(VisitorContext ctx, VariantForm form) {
    return this;
  }

  public VariantFragmentVisitor visitVariantFragment(VisitorContext ctx,
      VariantFragment fragment) {
    return this;
  }
}
