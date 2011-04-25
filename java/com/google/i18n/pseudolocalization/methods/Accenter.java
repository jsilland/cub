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

import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link com.google.i18n.pseudolocalization.PseudolocalizationMethod
 * PseudolocalizationMethod} which replaces ASCII characters with accented
 * versions or similar characters. This allows detection of unlocalized strings
 * in the application, while still keeping the result readable.
 */
public class Accenter extends CharacterSubstituter {

  private static final String METHOD_NAME = "accents";

  private static final Map<Integer, String> LATIN_REPLACEMENTS;
  private static final Map<Integer, String> EXTENDED_REPLACEMENTS;

  static {
    LATIN_REPLACEMENTS = new HashMap<Integer, String>();
    LATIN_REPLACEMENTS.put((int) ' ', "\u2003");
    LATIN_REPLACEMENTS.put((int) '!', "\u00a1");
    LATIN_REPLACEMENTS.put((int) '"', "\u2033");
    LATIN_REPLACEMENTS.put((int) '#', "\u266f");
    LATIN_REPLACEMENTS.put((int) '$', "\u20ac");
    LATIN_REPLACEMENTS.put((int) '%', "\u2030");
    LATIN_REPLACEMENTS.put((int) '&', "\u214b"); // TODO(jat): better substitution
    LATIN_REPLACEMENTS.put((int) '\'', "\u00b4");
    LATIN_REPLACEMENTS.put((int) '(', "{");
    LATIN_REPLACEMENTS.put((int) ')', "}");
    LATIN_REPLACEMENTS.put((int) '*', "\u204e"); // TODO(jat): better substitution
    LATIN_REPLACEMENTS.put((int) '+', "\u207a");
    LATIN_REPLACEMENTS.put((int) ',', "\u060c");
    LATIN_REPLACEMENTS.put((int) '-', "\u2010");
    LATIN_REPLACEMENTS.put((int) '.', "\u00b7");
    LATIN_REPLACEMENTS.put((int) '/', "\u2044");
    LATIN_REPLACEMENTS.put((int) '0', "\u24ea");
    LATIN_REPLACEMENTS.put((int) '1', "\u2460");
    LATIN_REPLACEMENTS.put((int) '2', "\u2461");
    LATIN_REPLACEMENTS.put((int) '3', "\u2462");
    LATIN_REPLACEMENTS.put((int) '4', "\u2463");
    LATIN_REPLACEMENTS.put((int) '5', "\u2464");
    LATIN_REPLACEMENTS.put((int) '6', "\u2465");
    LATIN_REPLACEMENTS.put((int) '7', "\u2466");
    LATIN_REPLACEMENTS.put((int) '8', "\u2467");
    LATIN_REPLACEMENTS.put((int) '9', "\u2468");
    LATIN_REPLACEMENTS.put((int) ':', "\u2236");
    LATIN_REPLACEMENTS.put((int) ';', "\u204f"); // TODO(jat): better substitution
    LATIN_REPLACEMENTS.put((int) '<', "\u2264");
    LATIN_REPLACEMENTS.put((int) '=', "\u2242");
    LATIN_REPLACEMENTS.put((int) '>', "\u2265");
    LATIN_REPLACEMENTS.put((int) '?', "\u00bf");
    LATIN_REPLACEMENTS.put((int) '@', "\u055e"); // TODO(jat): better substitution
    LATIN_REPLACEMENTS.put((int) 'A', "\u00c5");
    LATIN_REPLACEMENTS.put((int) 'B', "\u0181");
    LATIN_REPLACEMENTS.put((int) 'C', "\u00c7");
    LATIN_REPLACEMENTS.put((int) 'D', "\u00d0");
    LATIN_REPLACEMENTS.put((int) 'E', "\u00c9");
    LATIN_REPLACEMENTS.put((int) 'F', "\u0191");
    LATIN_REPLACEMENTS.put((int) 'G', "\u011c");
    LATIN_REPLACEMENTS.put((int) 'H', "\u0124");
    LATIN_REPLACEMENTS.put((int) 'I', "\u00ce");
    LATIN_REPLACEMENTS.put((int) 'J', "\u0134");
    LATIN_REPLACEMENTS.put((int) 'K', "\u0136");
    LATIN_REPLACEMENTS.put((int) 'L', "\u013b");
    LATIN_REPLACEMENTS.put((int) 'M', "\u1e40");
    LATIN_REPLACEMENTS.put((int) 'N', "\u00d1");
    LATIN_REPLACEMENTS.put((int) 'O', "\u00d6");
    LATIN_REPLACEMENTS.put((int) 'P', "\u00de");
    LATIN_REPLACEMENTS.put((int) 'Q', "\u01ea");
    LATIN_REPLACEMENTS.put((int) 'R', "\u0154");
    LATIN_REPLACEMENTS.put((int) 'S', "\u0160");
    LATIN_REPLACEMENTS.put((int) 'T', "\u0162");
    LATIN_REPLACEMENTS.put((int) 'U', "\u00db");
    LATIN_REPLACEMENTS.put((int) 'V', "\u1e7c");
    LATIN_REPLACEMENTS.put((int) 'W', "\u0174");
    LATIN_REPLACEMENTS.put((int) 'X', "\u1e8a");
    LATIN_REPLACEMENTS.put((int) 'Y', "\u00dd");
    LATIN_REPLACEMENTS.put((int) 'Z', "\u017d");
    LATIN_REPLACEMENTS.put((int) '[', "\u2045");
    LATIN_REPLACEMENTS.put((int) '\\', "\u2216");
    LATIN_REPLACEMENTS.put((int) ']', "\u2046");
    LATIN_REPLACEMENTS.put((int) '^', "\u02c4");
    LATIN_REPLACEMENTS.put((int) '_', "\u203f");
    LATIN_REPLACEMENTS.put((int) '`', "\u2035");
    LATIN_REPLACEMENTS.put((int) 'a', "\u00e5");
    LATIN_REPLACEMENTS.put((int) 'b', "\u0180");
    LATIN_REPLACEMENTS.put((int) 'c', "\u00e7");
    LATIN_REPLACEMENTS.put((int) 'd', "\u00f0");
    LATIN_REPLACEMENTS.put((int) 'e', "\u00e9");
    LATIN_REPLACEMENTS.put((int) 'f', "\u0192");
    LATIN_REPLACEMENTS.put((int) 'g', "\u011d");
    LATIN_REPLACEMENTS.put((int) 'h', "\u0125");
    LATIN_REPLACEMENTS.put((int) 'i', "\u00ee");
    LATIN_REPLACEMENTS.put((int) 'j', "\u0135");
    LATIN_REPLACEMENTS.put((int) 'k', "\u0137");
    LATIN_REPLACEMENTS.put((int) 'l', "\u013c");
    LATIN_REPLACEMENTS.put((int) 'm', "\u0271");
    LATIN_REPLACEMENTS.put((int) 'n', "\u00f1");
    LATIN_REPLACEMENTS.put((int) 'o', "\u00f6");
    LATIN_REPLACEMENTS.put((int) 'p', "\u00fe");
    LATIN_REPLACEMENTS.put((int) 'q', "\u01eb");
    LATIN_REPLACEMENTS.put((int) 'r', "\u0155");
    LATIN_REPLACEMENTS.put((int) 's', "\u0161");
    LATIN_REPLACEMENTS.put((int) 't', "\u0163");
    LATIN_REPLACEMENTS.put((int) 'u', "\u00fb");
    LATIN_REPLACEMENTS.put((int) 'v', "\u1e7d");
    LATIN_REPLACEMENTS.put((int) 'w', "\u0175");
    LATIN_REPLACEMENTS.put((int) 'x', "\u1e8b");
    LATIN_REPLACEMENTS.put((int) 'y', "\u00fd");
    LATIN_REPLACEMENTS.put((int) 'z', "\u017e");
    LATIN_REPLACEMENTS.put((int) '{', "(");
    LATIN_REPLACEMENTS.put((int) '|', "\u00a6");
    LATIN_REPLACEMENTS.put((int) '}', ")");
    LATIN_REPLACEMENTS.put((int) '~', "\u02de");

    EXTENDED_REPLACEMENTS = new HashMap<Integer, String>();
    EXTENDED_REPLACEMENTS.putAll(LATIN_REPLACEMENTS);
    // TODO(jat): choose replacements from a wider range.
    EXTENDED_REPLACEMENTS.put((int) '%', "\u0609");
  }

  public static void register() {
    PseudolocalizationPipeline.registerMethodClass(METHOD_NAME, Accenter.class);
  }

  public Accenter() {
    this(null);
  }

  public Accenter(Map<String, String> options) {
    super(chooseReplacements(options));
  }

  private static Map<Integer, String> chooseReplacements(Map<String, String> options) {
    if (options != null) {
      if (options.containsKey(METHOD_NAME + ":extended")) {
        return EXTENDED_REPLACEMENTS;
      }
    }
    return LATIN_REPLACEMENTS;
  }
}
