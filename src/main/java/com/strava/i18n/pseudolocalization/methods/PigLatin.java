/**
 * Copyright 2014 Strava Inc.
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

package com.strava.i18n.pseudolocalization.methods;

import com.google.common.base.CharMatcher;
import com.google.i18n.pseudolocalization.PseudolocalizationMethod;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.message.*;
import com.ibm.icu.text.BreakIterator;

public class PigLatin extends DefaultVisitor implements PseudolocalizationMethod {

  private static final CharMatcher NON_LETTERS = CharMatcher.JAVA_LETTER.negate();

  public static void register() {
    PseudolocalizationPipeline.registerMethodClass("piglatin", PigLatin.class);
  }

  @Override
  public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
    return this;
  }

  @Override
  public void visitTextFragment(VisitorContext context, TextFragment fragment) {
    String text = fragment.getText();

    BreakIterator breakIterator = BreakIterator.getWordInstance();
    breakIterator.setText(text);
    StringBuilder stringBuilder = new StringBuilder();

    int start = breakIterator.first();
    for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
      String word = text.substring(start, end);
      stringBuilder.append(pigLatinify(word));
    }

    context.replaceFragment(fragment, new SimpleTextFragment(stringBuilder.toString()));
  }

  public static final String pigLatinify(String word) {
    if (NON_LETTERS.matchesAllOf(word)) {
      return word;
    }
    int split = firstVowel(word);
    return word.substring(split) + word.substring(0, split) + "ay";
  }

  private static int firstVowel(String word) {
    word = word.toLowerCase();
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i)=='a' || word.charAt(i)=='e' ||
          word.charAt(i)=='i' || word.charAt(i)=='o' ||
          word.charAt(i)=='u') {
        return i;
      }
    }
    return 0;
  }
}
