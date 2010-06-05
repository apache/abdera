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

import java.io.InputStream;

import javax.activation.DataHandler;

/**
 * A text value. Returned by the Abdera XPath implementation when querying for text nodes (e.g.
 * xpath.selectNodes("//text()"); ...). There should be very little reason why an application would use this. It is
 * provided to keep applications from having to deal directly with the underlying parser impl
 */
public interface TextValue {

    /**
     * A DataHandler for Base64 encoded binary data
     */
    DataHandler getDataHandler();

    /**
     * An InputStream used to read the text content
     */
    InputStream getInputStream();

    /**
     * Return the text value
     */
    String getText();

    /**
     * The parent element
     */
    <T extends Base> T getParentElement();

    /**
     * Delete this node
     */
    void discard();

}
