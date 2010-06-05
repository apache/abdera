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
package org.apache.abdera.i18n.templates;

import java.io.Serializable;

/**
 * Used to resolve values for template variables
 */
public interface Context extends Cloneable, Serializable, Iterable<String> {

    /**
     * Resolve a value for the specified variable. The method can return either a String, an Array or a Collection.
     */
    <T> T resolve(String var);

    /**
     * True if IRI expansion is enabled
     */
    boolean isIri();

    /**
     * True if IRI expansion is to be enabled
     */
    void setIri(boolean isiri);

    /**
     * Clear this context
     */
    void clear();

}
