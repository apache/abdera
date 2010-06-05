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
 * A processing instruction. Returned by the Abdera XPath implementation when querying for PI nodes (e.g.
 * xpath.selectNodes("//processing-instruction()"); ...). There should be very little reason for applications to use
 * this. It is provided to keep applications from having to deal with the underlying parser implementation
 */
public interface ProcessingInstruction {

    /**
     * Delete this PI
     */
    void discard();

    /**
     * The Abdera Factory
     */
    Factory getFactory();

    /**
     * The parent node
     */
    <T extends Base> T getParentElement();

    /**
     * The PI target
     */
    String getTarget();

    /**
     * The PI target
     */
    void setTarget(String target);

    /**
     * The PI text
     */
    String getText();

    /**
     * The PI text
     */
    <T extends ProcessingInstruction> T setText(String text);

}
