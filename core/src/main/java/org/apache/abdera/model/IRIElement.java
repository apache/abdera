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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;

/**
 * <p>
 * The IRI interface provides a common base for a set of feed and entry elements whose content value must be a valid
 * IRI/URI reference. These include the elements atom:icon, atom:logo, and atom:id.
 * </p>
 */
public interface IRIElement extends Element {

    /**
     * Returns the value of the element as a java.net.URI
     * 
     * @return The IRI value of this element
     */
    IRI getValue();

    /**
     * Sets the value of the element
     * 
     * @param iri The iri value
     * @throws IRISyntaxException if the value is malformed
     */
    IRIElement setValue(String iri);

    /**
     * Set the value of this element using the normalization as specified in RFC4287
     * 
     * @param iri A non-normalized IRI
     * @throws IRISyntaxException if the iri is malformed
     */
    IRIElement setNormalizedValue(String iri);

    /**
     * Returns the value of the element resolved against the current in-scope Base URI
     * 
     * @return The resolved IRI value
     */
    IRI getResolvedValue();

}
