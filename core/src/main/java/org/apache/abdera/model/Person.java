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
 * An Atom Person Construct
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *   A Person construct is an element that describes a person,
 *   corporation, or similar entity (hereafter, 'person').
 * 
 *   atomPersonConstruct =
 *     atomCommonAttributes,
 *     (element atom:name { text }
 *      &amp; element atom:uri { atomUri }?
 *      &amp; element atom:email { atomEmailAddress }?
 *      &amp; extensionElement*)
 * 
 * </pre>
 */
public interface Person extends ExtensibleElement, Element {

    /**
     * The "atom:name" element's content conveys a human-readable name for the person. The content of atom:name is
     * Language-Sensitive. Person constructs MUST contain exactly one "atom:name" element.
     * 
     * @return The atom:name element
     */
    Element getNameElement();

    /**
     * The "atom:name" element's content conveys a human-readable name for the person. The content of atom:name is
     * Language-Sensitive. Person constructs MUST contain exactly one "atom:name" element.
     * 
     * @param element The atom:name element
     */
    Person setNameElement(Element element);

    /**
     * The "atom:name" element's content conveys a human-readable name for the person. The content of atom:name is
     * Language-Sensitive. Person constructs MUST contain exactly one "atom:name" element.
     * 
     * @param name The person name
     * @return The newly created atom:name element
     */
    Element setName(String name);

    /**
     * The "atom:name" element's content conveys a human-readable name for the person. The content of atom:name is
     * Language-Sensitive. Person constructs MUST contain exactly one "atom:name" element.
     * 
     * @return The name value
     */
    String getName();

    /**
     * The "atom:email" element's content conveys an e-mail address associated with the person. Person constructs MAY
     * contain an atom:email element, but MUST NOT contain more than one. Its content MUST conform to the "addr-spec"
     * production in [RFC2822].
     * 
     * @return the atom:email element
     */
    Element getEmailElement();

    /**
     * The "atom:email" element's content conveys an e-mail address associated with the person. Person constructs MAY
     * contain an atom:email element, but MUST NOT contain more than one. Its content MUST conform to the "addr-spec"
     * production in [RFC2822].
     * 
     * @param element The atom:email element
     */
    Person setEmailElement(Element element);

    /**
     * The "atom:email" element's content conveys an e-mail address associated with the person. Person constructs MAY
     * contain an atom:email element, but MUST NOT contain more than one. Its content MUST conform to the "addr-spec"
     * production in [RFC2822].
     * 
     * @param email The person email
     * @return the newly created atom:email element
     */
    Element setEmail(String email);

    /**
     * The "atom:email" element's content conveys an e-mail address associated with the person. Person constructs MAY
     * contain an atom:email element, but MUST NOT contain more than one. Its content MUST conform to the "addr-spec"
     * production in [RFC2822].
     * 
     * @return the person's emali
     */
    String getEmail();

    /**
     * The "atom:uri" element's content conveys an IRI associated with the person. Person constructs MAY contain an
     * atom:uri element, but MUST NOT contain more than one. The content of atom:uri in a Person construct MUST be an
     * IRI reference [RFC3987].
     * 
     * @return the atom:uri element
     */
    IRIElement getUriElement();

    /**
     * The "atom:uri" element's content conveys an IRI associated with the person. Person constructs MAY contain an
     * atom:uri element, but MUST NOT contain more than one. The content of atom:uri in a Person construct MUST be an
     * IRI reference [RFC3987].
     * 
     * @param uri The atom:uri element
     */
    Person setUriElement(IRIElement uri);

    /**
     * The "atom:uri" element's content conveys an IRI associated with the person. Person constructs MAY contain an
     * atom:uri element, but MUST NOT contain more than one. The content of atom:uri in a Person construct MUST be an
     * IRI reference [RFC3987].
     * 
     * @param uri The atom:uri value
     * @throws IRISyntaxException if the uri is malformed
     */
    IRIElement setUri(String uri);

    /**
     * The "atom:uri" element's content conveys an IRI associated with the person. Person constructs MAY contain an
     * atom:uri element, but MUST NOT contain more than one. The content of atom:uri in a Person construct MUST be an
     * IRI reference [RFC3987].
     * 
     * @return The atom:uri value
     * @throws IRISyntaxException if the uri is invalid
     */
    IRI getUri();
}
