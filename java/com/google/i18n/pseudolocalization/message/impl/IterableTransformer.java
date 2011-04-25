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
package com.google.i18n.pseudolocalization.message.impl;

import java.util.Iterator;

/**
 * A utility class which allows creating an {@link Iterable} from another
 * {@link Iterable}, by transforming the values as they are retrieved.
 *
 * @param <S> source {@link Iterable} type
 * @param <T> destination {@link Iterable} type
 */
public abstract class IterableTransformer<S, T> implements Iterable<T> {

  private final Iterable<S> src;

  public IterableTransformer(Iterable<S> src) {
    this.src = src;
  }

  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private Iterator<S> it = src.iterator();

      public boolean hasNext() {
        return it.hasNext();
      }

      public T next() {
        S val = it.next();
        return transform(val);
      }

      public void remove() {
        it.remove();
      }
    };
  }

  /**
   * Produce a {@code <T>} value given an {@code <S>} value.
   * 
   * @param val source value
   * @return transformed value
   */
  protected abstract T transform(S val);
}
