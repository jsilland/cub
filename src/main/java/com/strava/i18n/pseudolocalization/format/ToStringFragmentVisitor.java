/**
 * Copyright 2013 Strava Inc.
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

package com.strava.i18n.pseudolocalization.format;

import com.google.i18n.pseudolocalization.message.*;

public class ToStringFragmentVisitor implements MessageFragmentVisitor {

  private final StringBuilder builder = new StringBuilder();

  @Override
  public void visitNonlocalizableTextFragment(VisitorContext ctx,
      NonlocalizableTextFragment fragment) {
    builder.append(fragment.getText());
  }

  @Override
  public void visitPlaceholder(VisitorContext ctx, Placeholder placeholder) {
    builder.append(placeholder.getTextRepresentation());
  }

  @Override
  public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
    builder.append(fragment.getText());
  }

  @Override
  public VariantFragmentVisitor visitVariantFragment(VisitorContext ctx, VariantFragment fragment) {
    return null;
  }

  public String getStringResult() {
    return builder.toString();
  }
}
