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
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A pseudolocalization method that attempts to parse HTML and leave the tags
 * and their attributes alone, while passing other content through the rest of
 * the pseudolocalization chain. This should generally be the first method on
 * the chain.
 */
public class HtmlPreserver extends DefaultVisitor implements PseudolocalizationMethod {

  /**
   * Regex to match HTML entities - see the
   * <a href="http://www.w3.org/TR/html4/charset.html#entities">HTML4 spec</a>.
   */
  private static final Pattern ENTITY_PATTERN = Pattern.compile(
      "&((#\\d+)|(#[xX][a-fA-F0-9]+)|(\\w+));");

  public static void register() {
    PseudolocalizationPipeline.registerMethodClass("html", HtmlPreserver.class);
  }

  @Override
  public void visitTextFragment(final VisitorContext ctx, TextFragment text) {
    final List<MessageFragment> result = new ArrayList<MessageFragment>();
    Parser parser = Parser.createParser(text.getText(), "UTF-8");
    try {
      parser.visitAllNodesWith(new NodeVisitor() {
        @Override
        public void visitStringNode(Text string) {
          String val = string.getText();
          Matcher m = ENTITY_PATTERN.matcher(val);
          int start = 0;
          while (m.find()) {
            String plainText = val.substring(start, m.start());
            start = m.end();
            String entity = m.group();
            if (plainText.length() > 0) {
              result.add(ctx.createTextFragment(plainText));
            }
            result.add(ctx.createNonlocalizableTextFragment(entity));
          }
          String plainText = val.substring(start);
          if (plainText.length() > 0) {
            result.add(ctx.createTextFragment(plainText));
          }
        }

        @Override
        public void visitTag(Tag tag) {
          result.add(ctx.createNonlocalizableTextFragment(tag.toTagHtml()));
        }

        @Override
        public void visitEndTag(Tag tag) {
          result.add(ctx.createNonlocalizableTextFragment(tag.toHtml(true)));
        }
      });
      ctx.replaceFragment(text, result);
    } catch (ParserException e) {
      // ignore parse failures
    }
    // if we can't parse it, leave it unchanged
  }
}
