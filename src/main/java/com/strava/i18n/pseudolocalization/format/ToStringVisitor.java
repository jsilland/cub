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

import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragmentVisitor;
import com.google.i18n.pseudolocalization.message.MessageVisitor;
import com.google.i18n.pseudolocalization.message.VisitorContext;

public class ToStringVisitor implements MessageVisitor {

  public final ToStringFragmentVisitor fragmentVisitor = new ToStringFragmentVisitor();

  @Override
  public void endMessage(VisitorContext ctx, Message message) {

  }

  @Override
  public MessageFragmentVisitor visitMessage(VisitorContext ctx, Message message) {
    return fragmentVisitor;
  }
}
