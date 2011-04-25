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

import java.util.Map;

/**
 * A factory for creating {@link PseudolocalizationMethod} instances.
 */
public interface PseudolocalizationMethodFactory {

  /**
   * Create a new instance of a {@link PseudolocalizationMethod}.
   * 
   * @param options creation options or null if none; each key is prefixed with
   *     the method name for those specified with the method name - ie,
   *     "method:arg1=14:arg2" will appear in the map as "method:arg1"=>"14" and
   *     "method:arg2" => "" (as well as any global options)
   * @return {@link PseudolocalizationMethod} instance
   * @throws RuntimeException if the method cannot be created
   */
  PseudolocalizationMethod create(Map<String, String> options);
}
