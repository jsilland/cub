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
import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.NonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.Map;

/**
 * This pseudo-localization method expands a string by adding words at
 * the end of the string. Words are added in the order given in the
 * string array until the string has the expected size.  If the
 * string has fewer words than a threshold (configurable as an argument and
 * defaulting to {@link #DEFAULT_NUM_WORDS_THRESHOLD}, it will be expanded
 * by about 50% of its initial size.  If it is above the threshold, its size
 * will be doubled (expanded by about 100%).
*/
public class Expander extends DefaultVisitor implements PseudolocalizationMethod {
  // TODO(jat): implement a stack of counts, so you can count only the longest
  // form of a variant message rather than all forms.

  public static void register() {
    PseudolocalizationPipeline.registerMethodClass("expand", Expander.class);
  }

  private static final int DEFAULT_NUM_WORDS_THRESHOLD = 3;

  private static final String[] NUMBERS = {
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
    "seventeen", "eighteen",  "nineteen", "twenty", "twentyone", "twentytwo",
    "twentythree", "twentyfour", "twentyfive", "twentysix", "twentyseven",
    "twentyeight", "twentynine", "thirty", "thirtyone", "thirtytwo",
    "thirtythree", "thirtyfour", "thirtyfive", "thirtysix", "thirtyseven",
    "thirtyeight", "thirtynine", "forty"
  };

  private final int threshold;

  private int charCount = 0;
  private int wordCount = 0;

  public Expander() {
    this(null);
  }

  /**
   * Create an {@link Expander} with a non-default threshold.
   * 
   * @param options a string containing an integral value for the expansion
   *     threshold in the number of words, or null for the default
   */
  public Expander(Map<String, String> options) {
    int thresholdArg = DEFAULT_NUM_WORDS_THRESHOLD;
    if (options != null) {
      String thresholdEntry = options.get("threshold");
      if (thresholdEntry != null) {
        thresholdArg = Integer.parseInt(thresholdEntry);
      }
    }
    threshold = thresholdArg;
  }

  @Override
  public void endMessage(VisitorContext ctx, Message message) {
    int expansion = charCount;
    if (wordCount <= threshold) {
      // for short strings, expand by 50% but at least 1 character
      expansion = (expansion + 1) / 2;
    }
    StringBuilder expansionText = new StringBuilder();
    int wordIndex = 0;
    while (expansion > 0) {
      String word = NUMBERS[wordIndex++ % NUMBERS.length];
      expansionText.append(' ').append(word);
      expansion -= word.length() + 1;
    }
    NonlocalizableTextFragment suffix = ctx.createNonlocalizableTextFragment(
        expansionText.toString());
    ctx.insertAfter(null, suffix);
  }

  @Override
  public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
    charCount = 0;
    wordCount = 0;
    return this;
  }

  @Override
  public void visitTextFragment(VisitorContext context, TextFragment fragment) {
    String text = fragment.getText();
    charCount += text.codePointCount(0, text.length());
    if (wordCount <= threshold) {
      wordCount += text.split(" ").length;
    }
  }
}
