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
import com.google.i18n.pseudolocalization.PseudolocalizationMethod;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.PseudolocalizationTestCase;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.util.Arrays;

/**
 * Test for {@link HtmlPreserver}.
 */
public class HtmlPreserverTest extends PseudolocalizationTestCase {

  private static class Mangler extends DefaultVisitor implements PseudolocalizationMethod {

    @Override
    public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
      ctx.insertBefore(fragment, new SimpleNonlocalizableTextFragment("[loc:"));
      ctx.insertAfter(fragment, new SimpleNonlocalizableTextFragment("]"));
    }
  }

  private final PseudolocalizationPipeline pipeline = new PseudolocalizationPipeline(
      Arrays.<PseudolocalizationMethod>asList(new HtmlPreserver(), new Mangler())) { };

  public void testAttributes() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello <a href=\"http://google.com\">there</a>!");
    assertEquals("[loc:Hello ]<a href=\"http://google.com\">[loc:there]</a>[loc:!]",
        msg);
  }

  public void testEntities() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello &#64; there &#x2D; &amp;");
    assertEquals("[loc:Hello ]&#64;[loc: there ]&#x2D;[loc: ]&amp;", msg);
  }

  public void testMissingEndTag() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello <p>there!");
    assertEquals("[loc:Hello ]<p>[loc:there!]</p>", msg);

    msg = runPipeline(pipeline, "Hello<br>there!");
    assertEquals("[loc:Hello]<br>[loc:there!]", msg);
  }

  public void testNested() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "a<b><i>b<div class=\"class\">c</div></i>d</b>e");
    assertEquals("[loc:a]<b><i>[loc:b]<div class=\"class\">[loc:c]</div></i>[loc:d]</b>[loc:e]",
        msg);
  }

  public void testNotHtml() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "x < y & a > b");
    assertEquals("[loc:x < y & a > b]", msg);
  }

  public void testSelfClosedTag() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello<br/>there");
    assertEquals("[loc:Hello]<br/>[loc:there]", msg);
  }

  public void testSurroundingTag() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello <b>there</b>!");
    assertEquals("[loc:Hello ]<b>[loc:there]</b>[loc:!]", msg);
  }

  public void testText() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello there");
    assertEquals("[loc:Hello there]", msg);
  }

  public void testUnknownTag() throws PseudolocalizationException {
    String msg = runPipeline(pipeline, "Hello <bork>there</bork>");
    assertEquals("[loc:Hello ]<bork>[loc:there]</bork>", msg);
  }
}
