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

import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Superclass for pseudolocalization tests.
 */
public abstract class PseudolocalizationTestCase extends TestCase {

  public PseudolocalizationTestCase() {
    super();
  }

  /**
   * @param name
   */
  public PseudolocalizationTestCase(String name) {
    super(name);
  }

  /**
   * @param pipeline
   * @param fragments
   * @return result
   * @throws PseudolocalizationException
   */
  protected String runPipeline(PseudolocalizationPipeline pipeline, MessageFragment... fragments)
      throws PseudolocalizationException {
    return runPipeline(pipeline, Arrays.<MessageFragment>asList(fragments));
  }

  /**
   * @param pipeline
   * @param fragments
   * @return result
   * @throws PseudolocalizationException
   */
  protected String runPipeline(PseudolocalizationPipeline pipeline,
      List<MessageFragment> fragments) throws PseudolocalizationException {
    SimpleMessage msg = new SimpleMessage(fragments) { };
    pipeline.localize(msg);
    return msg.getText();
  }

  /**
   * @param pipeline
   * @param text
   * @return result
   * @throws PseudolocalizationException
   */
  protected String runPipeline(PseudolocalizationPipeline pipeline, String text)
      throws PseudolocalizationException {
    return pipeline.localize(text);
  }

  /**
   * @param pipeline
   * @return result
   * @throws PseudolocalizationException
   */
  protected String runPreparsedHtml(PseudolocalizationPipeline pipeline)
      throws PseudolocalizationException {
    SimpleMessage msg = new SimpleMessage(Arrays.<MessageFragment>asList(
        new SimpleTextFragment("Hello "),
        new SimpleNonlocalizableTextFragment("<br>"),
        new SimpleTextFragment(" there"))) { };
    pipeline.localize(msg);
    return msg.getText();
  }

  /**
   * @param pipeline
   * @return result
   * @throws PseudolocalizationException
   */
  protected String runUnparsedHtml(PseudolocalizationPipeline pipeline)
      throws PseudolocalizationException {
    return runPipeline(pipeline, "Hello <br> there");
  }
}
