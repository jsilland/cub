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

/**
 * A {@link MessageFragment} which reprsents a placeholder, a value to be filled
 * in at runtime.
 */
public interface Placeholder extends MessageFragment {

  /**
   * Get the textual representation of this placeholder as it would appear
   * in the reassembled message.  For example, MessageFormat-style placeholders
   * might return {0}.
   *
   * @return textual representation
   */
  String getTextRepresentation();
}
