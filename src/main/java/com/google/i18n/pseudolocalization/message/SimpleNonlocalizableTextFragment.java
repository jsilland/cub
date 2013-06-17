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
 * A {@link NonlocalizableTextFragment} that simply stores the nonlocalizable
 * text.
 */
public class SimpleNonlocalizableTextFragment implements NonlocalizableTextFragment {

  private final String text;

  public SimpleNonlocalizableTextFragment(String text) {
    this.text = text;
  }

  public void accept(VisitorContext ctx, MessageFragmentVisitor mfv) {
    mfv.visitNonlocalizableTextFragment(ctx, this);
  }

  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "NonLoc: " + text;
  }
}
