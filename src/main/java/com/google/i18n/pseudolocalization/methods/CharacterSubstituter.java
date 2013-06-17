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
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.Map;

/**
 * A base for pseudolocalization methods which replace particular codepoints
 * with substitutions.
 */
public abstract class CharacterSubstituter extends DefaultVisitor
    implements PseudolocalizationMethod {

  private final Map<Integer, String> replacements;

  /**
   * Subclasses supply a replacement table to use.
   * 
   * @param replacements map of code points to replacement strings
   */
  protected CharacterSubstituter(Map<Integer, String> replacements) {
    this.replacements = replacements;
  }

  /**
   * Replace characters in the supplied {@link TextFragment} according to
   * the replacement table supplied by subclasses.
   * 
   * @param ctx
   * @param textFragment
   */
  @Override
  public void visitTextFragment(final VisitorContext ctx, TextFragment textFragment) {
    String text = textFragment.getText();
    StringBuilder buf = new StringBuilder();
    int index = 0;
    while (index < text.length()) {
      int codePoint = text.codePointAt(index);
      index += Character.charCount(codePoint);
      String replacement = replacements.get(codePoint);
      if (replacement != null) {
        buf.append(replacement);
      } else {
        buf.appendCodePoint(codePoint);
      }
    }
    ctx.replaceFragment(textFragment, ctx.createTextFragment(buf.toString()));
  }
}
