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
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link FakeBidi}.
 */
public class FakeBidiTest extends PseudolocalizationTestCase {

  private static final String HTML_START = "<a href=\"http://google.com/\">";
  private static final String HTML_END = "</a>";

  private PseudolocalizationPipeline pipeline;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    pipeline = PseudolocalizationPipeline.buildPipeline("fakebidi");
  }

  private void runTest(String input, String expected) throws PseudolocalizationException {
    String result = runPipeline(pipeline, input);
    assertEquals(expected, result);
  }

  private void runHtmlTest(String input, String expected) throws PseudolocalizationException {
    String result = runPipeline(pipeline, new SimpleNonlocalizableTextFragment(HTML_START),
        new SimpleTextFragment(input),
        new SimpleNonlocalizableTextFragment(HTML_END)); 
    assertEquals(HTML_START + expected + HTML_END, result);
  }

  public void testText() throws Exception {
    runTest("", "");
    runTest("a", "\u202ea\u202c");
    runTest("Chuck Norris peut diviser par zéro.",
         "\u202eChuck\u202c \u202eNorris\u202c \u202epeut\u202c "
         + "\u202ediviser\u202c \u202epar\u202c \u202ezéro\u202c.");
    runTest("Hello 123 Goodbye!", "\u202eHello\u202c 123 \u202eGoodbye\u202c!");
  }

  public void testHtml() throws Exception {
    runHtmlTest("", "");
    runHtmlTest("Google", "\u202eGoogle\u202c");

    List<MessageFragment> fragments = new ArrayList<MessageFragment>();
    fragments.add(new SimpleNonlocalizableTextFragment("<a href=\"http://chucknorrisfacts.fr/\">"
        + "<strong>"));
    fragments.add(new SimpleTextFragment("Chuck Norris"));
    fragments.add(new SimpleNonlocalizableTextFragment("</strong>"));
    fragments.add(new SimpleTextFragment(" peut diviser par zéro."));
    fragments.add(new SimpleNonlocalizableTextFragment("</a>"));
    String result = runPipeline(pipeline, fragments);
    assertEquals("<a href=\"http://chucknorrisfacts.fr/\">"
         + "<strong>\u202eChuck\u202c \u202eNorris\u202c</strong> "
         + "\u202epeut\u202c \u202ediviser\u202c \u202epar\u202c "
         + "\u202ezéro\u202c.</a>", result);
  }
}
