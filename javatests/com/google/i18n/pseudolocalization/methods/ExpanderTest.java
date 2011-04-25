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
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;

/**
 * Test for {@link com.google.i18n.pseudolocalization.methods.Expander}.
 */
public class ExpanderTest extends PseudolocalizationTestCase {

  public void testArg() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(
        "expand:threshold=1");
    String result = runPipeline(pipeline, new SimpleTextFragment("a "),
        new SimpleNonlocalizableTextFragment("<br>"),
        new SimpleTextFragment(" b"));
    assertEquals("a <br> b one", result);
  }

  public void testLong() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("expand");
    String msg = runPipeline(pipeline, "a message longer than the threshold");
    assertEquals("a message longer than the threshold one two three four five "
        + "six seven eight", msg);
  }

  public void testOnce() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("expand");
    assertEquals("Hello <br> there one two", runPreparsedHtml(pipeline));
  }

  public void testSingleChar() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("expand");
    String msg = runPipeline(pipeline, "a");
    assertEquals("a one", msg);
  }

  public void testTwice() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline("expand",
        "expand");
    assertEquals("Hello <br> there one two one two", runPreparsedHtml(pipeline));
  }
}
