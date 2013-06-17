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

import com.google.i18n.pseudolocalization.methods.AccenterTest;
import com.google.i18n.pseudolocalization.methods.BracketAdderTest;
import com.google.i18n.pseudolocalization.methods.ExpanderTest;
import com.google.i18n.pseudolocalization.methods.FakeBidiTest;
import com.google.i18n.pseudolocalization.methods.HtmlPreserverTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Run all pseudolocalizer tests.
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(AccenterTest.class);
    suite.addTestSuite(BracketAdderTest.class);
    suite.addTestSuite(ExpanderTest.class);
    suite.addTestSuite(FakeBidiTest.class);
    suite.addTestSuite(HtmlPreserverTest.class);
    suite.addTestSuite(PipelineTest.class);
    return suite;
  }
}
