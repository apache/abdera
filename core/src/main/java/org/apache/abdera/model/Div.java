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

/**
 * <p>
 * Represents an XHTML div tag.
 * </p>
 */
public interface Div extends ExtensibleElement {

    /**
     * Returns the array of class attribute values on the div
     * 
     * @return A listing of class attribute values
     */
    String[] getXhtmlClass();

    /**
     * Returns the value of the div element's id attribute
     * 
     * @return The value of the id attribute
     */
    String getId();

    /**
     * Returns the value of the div element's title attribute
     * 
     * @return The value of the title attribute
     */
    String getTitle();

    /**
     * Sets the value of the div element's id attribute
     * 
     * @param id The value of the id attribute
     */
    Div setId(String id);

    /**
     * Set the value of the div element's title attribute
     * 
     * @param title The value of the title attribute
     */
    Div setTitle(String title);

    /**
     * Sets the array of class attribute values on the div
     * 
     * @param classes A listing of class attribute values
     */
    Div setXhtmlClass(String[] classes);

    /**
     * Returns the value of the div element
     * 
     * @return The value of the div element
     */
    String getValue();

    /**
     * Set the value of the div element
     * 
     * @param value The text value
     */
    void setValue(String value);
}
