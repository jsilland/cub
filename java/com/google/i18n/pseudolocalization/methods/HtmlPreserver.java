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

import org.htmlparser.Attribute;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
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

  private static final Set<String> LOCALIZABLE_ATTRIBUTES;

  static {
    LOCALIZABLE_ATTRIBUTES = new HashSet<String>();
    // TODO: some automated way of generating this list
    LOCALIZABLE_ATTRIBUTES.add("alt");
    LOCALIZABLE_ATTRIBUTES.add("title");
    LOCALIZABLE_ATTRIBUTES.add("button/value");
    LOCALIZABLE_ATTRIBUTES.add("input/value");
    LOCALIZABLE_ATTRIBUTES.add("option/label");
    LOCALIZABLE_ATTRIBUTES.add("optgroup/label");
  }

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
        public void visitEndTag(Tag tag) {
          result.add(ctx.createNonlocalizableTextFragment(tag.toHtml(true)));
        }

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
          // Convert a tag into a sequence of fragments, which always start and
          // end with a non-localizable text fragment but may have localizable
          // text fragments in the middle.  For example,
          //    <input value="Submit">
          // becomes:
          //   [ NLTF('<input value="'), LTF('Submit'), NLTF('">') ]
          
          // Warning: this is derived from tag.toTagHtml, and may need to be
          // modified if HtmlParser is updated.
          @SuppressWarnings("unchecked")
          Vector<Attribute> attributes = tag.getAttributesEx();
          int n = attributes.size();
          StringBuffer buf = new StringBuffer();
          buf.append("<");
          String tagName = null;
          for (int i = 0; i < n; ++i){
              Attribute attribute = attributes.elementAt(i);
              if (i == 0) {
                tagName = attribute.getName();
              } else if (isLocalizableAttribute(tagName, attribute)) {
                attribute.getName(buf);
                attribute.getAssignment(buf);
                char quote = attribute.getQuote();
                if (quote != 0) {
                  buf.append(quote);
                }
                if (buf.length() > 0) {
                  result.add(ctx.createNonlocalizableTextFragment(buf.toString()));
                }
                result.add(ctx.createTextFragment(attribute.getValue()));
                buf = new StringBuffer();
                if (quote != 0) {
                  buf.append(quote);
                }
                continue;
              }
              attribute.toString(buf);
          }
          buf.append(">");
          result.add(ctx.createNonlocalizableTextFragment(buf.toString()));
        }

        /**
         * Check if a particular attribute contains localizable content.
         * 
         * @param tagName
         * @param attribute
         * @return true if the attribute's value is localizable, false otherwise
         */
        private boolean isLocalizableAttribute(String tagName, Attribute attribute) {
          if (!attribute.isValued()) {
            return false;
          }
          String attrName = attribute.getName();
          return LOCALIZABLE_ATTRIBUTES.contains(attrName)
              || LOCALIZABLE_ATTRIBUTES.contains(tagName + "/" + attrName);
        }
      });
      ctx.replaceFragment(text, result);
    } catch (ParserException e) {
      // ignore parse failures
    }
    // if we can't parse it, leave it unchanged
  }
}
