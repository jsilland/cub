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

import com.google.i18n.pseudolocalization.PseudolocalizationException;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.PseudolocalizationTestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * Test for {@link Accenter}.
 */
public class AccenterTest extends PseudolocalizationTestCase {
  // TODO(jat): add tests for extended accents when implemented

  private static Set<Character> skipCheck;
  
  static {
    skipCheck = new HashSet<Character>();
    // TODO(jat): perhaps use U+1D7CE - U+1D7D7 if those can generally be displayed
    // These don't have the right numeric value.
    skipCheck.add('0');
    skipCheck.add('1');
    skipCheck.add('2');
    skipCheck.add('3');
    skipCheck.add('4');
    skipCheck.add('5');
    skipCheck.add('6');
    skipCheck.add('7');
    skipCheck.add('8');
    skipCheck.add('9');
  }

  public void testAllAccented() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("accents");
    for (char ch = 0x20; ch < 0x7F; ++ch) {
      String result = runPipeline(pipeline, String.valueOf(ch));
      assertGoodSubstitution(ch, result);
    }
  }

  public void testAllExtended() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(
        "accents:extended");
    for (char ch = 0x20; ch < 0x7F; ++ch) {
      String result = runPipeline(pipeline, String.valueOf(ch));
      assertGoodSubstitution(ch, result);
    }
  }

  public void testOnce() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("accents");
    assertEquals("Ĥéļļö\u2003<br>\u2003ţĥéŕé", runPreparsedHtml(pipeline));
  }

  public void testTwice() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("accents",
        "accents");
    assertEquals("Ĥéļļö\u2003<br>\u2003ţĥéŕé", runPreparsedHtml(pipeline));
  }

  /**
   * Assert that the supplied substitution is a good one.  Checks:
   * <ul>
   * <li>The character is changed
   * <li>The basic character properties remain the same (letter, uppercase,
   *     lowercase, digit, space characte)
   * <li>If a digit, the numeric value remains the same
   * </ul>  
   * 
   * @param ch
   * @param substitution
   */
  private void assertGoodSubstitution(char ch, String substitution) {
    String charName = "0x" + Integer.toHexString(ch) + " (" + ch + ')';
    switch (substitution.length()) {
      case 0:
        fail("empty substitution for " + charName);
        break;
      case 1:
        char actualChar = substitution.charAt(0);
        assertTrue(charName + " didn't change", ch != actualChar);
        if (!skipCheck.contains(ch)) {
          assertEquals("isLetter(" + charName + ")", Character.isLetter(ch),
              Character.isLetter(actualChar));
          assertEquals("isUpperCase(" + charName + ")", Character.isUpperCase(ch),
              Character.isUpperCase(actualChar));
          assertEquals("isLowerCase(" + charName + ")", Character.isLowerCase(ch),
              Character.isLowerCase(actualChar));
          assertEquals("isDigit(" + charName + ")", Character.isDigit(ch),
              Character.isDigit(actualChar));
          assertEquals("isSpaceChar(" + charName + ")", Character.isSpaceChar(ch),
              Character.isSpaceChar(actualChar));
          if (Character.isDigit(ch)) {
            assertEquals("getNumericValue(" + charName + ")", Character.getNumericValue(ch),
                Character.getNumericValue(actualChar));
          }
        }
        break;
      default:
        // if it is a multicharacter substitution, don't try and compare info
        break;
    }
  }
}
