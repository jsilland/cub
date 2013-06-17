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
package com.google.i18n.pseudolocalization;

import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.VisitorContext;

/**
 * Test for pseudolocalization pipeline infrastructure.
 */
public class PipelineTest extends PseudolocalizationTestCase {

  /**
   * A {@link PseudolocalizationMethod} which has no default constructor, used
   * to test error handling.
   */
  public static class TestNoDefaultCtorMethod implements PseudolocalizationMethod {

    public TestNoDefaultCtorMethod(int unused) {
    }

    public void endMessage(VisitorContext ctx, Message message) {
      fail("shouldn't get called");
    }

    public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
      fail("shouldn't get called");
      return null;
    }
  }

  public void testBadMethodDefaultCtor() {
    try {
      PseudolocalizationPipeline.registerMethodClass("nodefctor", TestNoDefaultCtorMethod.class);
      fail("expected RuntimeException for an unknown method");
    } catch (RuntimeException expected) {
    }
  }

  public void testBadMethodName() {
    try {
      PseudolocalizationPipeline.buildPipeline("bogus");
      fail("expected RuntimeException for an unknown method");
    } catch (RuntimeException expected) {
    }
  }

  public void testComposite() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(false,
        "accents", "expand", "brackets");
    String msg = runPreparsedHtml(pipeline);
    assertEquals("[Ĥéļļö\u2003<br>\u2003ţĥéŕé one two]", msg);
  }

  public void testCompositeHtml() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(true, "accents",
        "expand", "brackets");
    String msg = runUnparsedHtml(pipeline);
    assertEquals("[Ĥéļļö\u2003<br>\u2003ţĥéŕé one two]", msg);
  }

  public void testEmpty() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(false);
    String msg = runPreparsedHtml(pipeline);
    assertEquals("Hello <br> there", msg);
  }

  public void testReuse() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.buildPipeline(false);
    String msg = runPreparsedHtml(pipeline);
    assertEquals("Hello <br> there", msg);

    msg = runPreparsedHtml(pipeline);
    assertEquals("Hello <br> there", msg);
  }

  public void testMissingVariant() {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.getVariantPipeline("bogus");
    assertNull(pipeline);
  }

  public void testVariantAccent() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.getVariantPipeline(false,
      "PsACcent");
    String msg = runPreparsedHtml(pipeline);
    assertEquals("[Ĥéļļö\u2003<br>\u2003ţĥéŕé one two]", msg);
  }

  public void testVariantBidi() throws PseudolocalizationException {
    PseudolocalizationPipeline pipeline = PseudolocalizationPipeline.getVariantPipeline(true,
        "psbidi");
    String msg = runUnparsedHtml(pipeline);
    assertEquals("\u202eHello\u202c <br> \u202ethere\u202c", msg);
  }
}
