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

/**
 * Test for {@link BracketAdder}.
 */
public class BracketAdderTest extends PseudolocalizationTestCase {

  public void testOnce() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("brackets");
    assertEquals("[Hello <br> there]", runPreparsedHtml(pipeline));
  }

  public void testTwice() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("brackets",
        "brackets");
    assertEquals("[[Hello <br> there]]", runPreparsedHtml(pipeline));
  }
}
