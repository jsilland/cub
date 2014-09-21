/**
 * Copyright 2013 Strava Inc.
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

package com.strava.i18n.pseudolocalization.format;

import com.google.common.base.Objects;
import com.google.i18n.pseudolocalization.PseudolocalizationException;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;
import com.google.i18n.pseudolocalization.message.impl.AbstractPlaceholder;
import org.htmlparser.Attribute;
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
 * Represents a message in a YAML file.
 *
 * @author Julien Silland (julien@strava.com)
 */
public class YamlMessage extends SimpleMessage {

  private static class YamlPlaceholder extends AbstractPlaceholder {

    private final String text;

    public YamlPlaceholder(String text) {
      this.text = text;
    }

    @Override
    public String getTextRepresentation() {
      return text;
    }

    @Override
    public String toString() {
      return getTextRepresentation();
    }
  }

  /**
   * Pattern matching a YAML formatter
   */
  private static final Pattern FORMAT_PATTERN = Pattern.compile("%\\{[a-zA-Z_]+\\}");

  private static List<MessageFragment> parseMessage(String text)
      throws PseudolocalizationException {
    Parser parser = Parser.createParser(text, "UTF-8");
    final List<MessageFragment> list = new ArrayList<MessageFragment>();
    try {
      parser.visitAllNodesWith(new NodeVisitor() {
        @Override
        public void visitTag(Tag tag) {
          StringBuffer tagString = new StringBuffer();
          tagString.append('<');
          for (Attribute attribute : (List<Attribute>) tag.getAttributesEx()) {
            attribute.toString(tagString);
          }
          tagString.append('>');

          list.add(new SimpleNonlocalizableTextFragment(tagString.toString()));
        }

        @Override
        public void visitEndTag(Tag tag) {
          list.add(new SimpleNonlocalizableTextFragment(tag.toHtml(true)));
        }

        @Override
        public void visitStringNode(Text node) {
          String text = node.getText();
          Matcher m = FORMAT_PATTERN.matcher(text);
          int start = 0;
          while (m.find()) {
            String plainText = text.substring(start, m.start());
            start = m.end();
            if (plainText.length() > 0) {
              list.add(new SimpleTextFragment(plainText));
            }
            list.add(new YamlPlaceholder(m.group()));
          }
          String plainText = text.substring(start);
          if (plainText.length() > 0) {
            list.add(new SimpleTextFragment(plainText));
          }
        }
      });
    } catch (ParserException pe) {
      throw new PseudolocalizationException(pe);
    }

    return list;
  }

  private final String key;

  /**
   * Exhaustive contructor.
   *
   * @param key the message's identifier.
   * @param text the message's text.
   */
  public YamlMessage(String key, String text) throws PseudolocalizationException {
    super(parseMessage(text));
    this.key = key;
  }

  @Override
  public String getId() {
    return key;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("Id", getId())
        .add("Fragments", getFragments())
        .toString();
  }
}
