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
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.Arrays;

/**
 * Fake a bidirectional locale by wrapping words composed of characters
 * having a strong Left-To-Right (LTR) directionality (ie. [^\W0-9_]+)
 * with RLO and PDF Unicode characters.
 * <br/>
 * RLO : Right-to-Left Override
 * PDF : Pop Directional Formatting (kind of end marker)
 *
 * @see <a href="http://www.w3.org/International/questions/qa-bidi-controls">W3C bidi info</a> 
 * @see <a href="http://www.fileformat.info/info/unicode/char/202e/index.htm">RLO character</a>
 * @see <a href="http://www.fileformat.info/info/unicode/char/202c/index.htm">PDF character</a>
 */
public class FakeBidi extends DefaultVisitor implements PseudolocalizationMethod {

  /**
   * Register this method.
   */
  public static void register() {
    PseudolocalizationPipeline.registerMethodClass("fakebidi", FakeBidi.class);
  }

  /** Right-to-left override character. */
  private static final String RLO = "\u202e";

  /** Pop direction formatting character. */
  private static final String PDF = "\u202c";

  @Override
  public final void visitTextFragment(VisitorContext ctx, TextFragment textFragment) {
    String text = textFragment.getText();
    StringBuilder output = new StringBuilder();
    boolean wrapping = false;
    for (int index = 0; index < text.length(); ) {
      int codePoint = text.codePointAt(index);
      index += Character.charCount(codePoint);
      byte directionality = Character.getDirectionality(codePoint);
      boolean needsWrap = (directionality == Character.DIRECTIONALITY_LEFT_TO_RIGHT);
      if (needsWrap != wrapping) {
        wrapping = needsWrap;
        output.append(wrapping ? RLO : PDF);
      }
      output.appendCodePoint(codePoint);
    }
    if (wrapping) {
      output.append(PDF);
    }
    ctx.replaceFragment(textFragment, Arrays.<MessageFragment>asList(ctx.createTextFragment(
        output.toString())));
  }
}
