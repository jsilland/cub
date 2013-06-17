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
package com.google.i18n.pseudolocalization.message;

import com.google.i18n.pseudolocalization.message.impl.AbstractMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple implementation of {@link Message} which maintains a list of message
 * fragments and provides a context that allows mutating the fragments.  It
 * is useful for simple messages that don't support variant forms, or as a
 * base for implementing more complex message structures.
 */
public class SimpleMessage extends AbstractMessage {

  private final Context context = new Context();

  private List<MessageFragment> fragments;

  /**
   * A {@link VisitorContext} which can manipulate the fragments in this
   * message.
   */
  protected class Context implements VisitorContext {

    public NonlocalizableTextFragment createNonlocalizableTextFragment(String text) {
      return new SimpleNonlocalizableTextFragment(text);
    }

    public TextFragment createTextFragment(String text) {
      return new SimpleTextFragment(text);
    }

    public void insertAfter(MessageFragment reference, MessageFragment newFrag) {
      int index = fragments.size();
      if (reference != null) {
        index = fragments.indexOf(reference);
        if (index < 0) {
          throw new RuntimeException("missing reference fragment");
        }
        index++;
      }
      fragments.add(index, newFrag);
    }

    public void insertBefore(MessageFragment reference, MessageFragment newFrag) {
      int index = 0;
      if (reference != null) {
        index = fragments.indexOf(reference);
        if (index < 0) {
          throw new RuntimeException("missing reference fragment");
        }
      }
      fragments.add(index, newFrag);
    }

    public void replaceFragment(MessageFragment fragment, List<MessageFragment> replacements) {
      int index = fragments.indexOf(fragment);
      if (index < 0) {
        throw new RuntimeException("missing reference fragment");
      }
      fragments.remove(index);
      for (MessageFragment replacement : replacements) {
        fragments.add(index++, replacement);
      }
    }

    public void replaceFragment(MessageFragment fragment, MessageFragment... replacements) {
      replaceFragment(fragment, Arrays.<MessageFragment>asList(replacements));
    }
  }

  public SimpleMessage(String text) {
    fragments = new ArrayList<MessageFragment>();
    fragments.add(new SimpleTextFragment(text));
  }

  /**
   * Construct a message from a list of fragments (which may only be
   * {@link TextFragment}, {@link NonlocalizableTextFragment}, and
   * {@link Placeholder} instances).
   * 
   * @param fragments
   */
  protected SimpleMessage(List<MessageFragment> fragments) {
    this.fragments = new ArrayList<MessageFragment>();
    // TODO: assert subtypes?
    this.fragments.addAll(fragments);
  }

  @Override
  protected Iterable<MessageFragment> getFragments() {
    return fragments;
  }

  @Override
  protected VisitorContext getVisitorContext() {
    return context;
  }

  /**
   * Collect the textual representations of all fragments.
   *
   * @return textual representation of the message
   */
  public String getText() {
    final StringBuilder buf = new StringBuilder();
    accept(new DefaultVisitor() {
      @Override
      public void visitNonlocalizableTextFragment(VisitorContext ctx,
          NonlocalizableTextFragment fragment) {
        buf.append(fragment.getText());
      }

      @Override
      public void visitPlaceholder(VisitorContext ctx, Placeholder placeholder) {
        buf.append(placeholder.getTextRepresentation());
      }

      @Override
      public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
        buf.append(fragment.getText());
      }
    });
    return buf.toString();
  }
}
