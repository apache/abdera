/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.model;

import org.apache.abdera.factory.Factory;

/**
 * A comment. Returned by the Abdera XPath implementation when querying for comment nodes (e.g.
 * xpath.selectNodes("//comment()"); ...). Most applications should never have much of a reason to use this interface.
 * It is provided primarily to avoid application from having to deal directly with the underlying parser implementation
 */
public interface Comment {

    /**
     * Delete the comment node
     */
    void discard();

    /**
     * The text of this comment node
     */
    String getText();

    /**
     * The text of this comment node
     */
    Comment setText(String text);

    /**
     * The Abdera Factory
     */
    Factory getFactory();

    /**
     * The parent node
     */
    <T extends Base> T getParentElement();
}
