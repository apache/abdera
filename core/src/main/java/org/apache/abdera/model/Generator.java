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
 * Identifies the software implementation that produced the Atom feed.
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 * The "atom:generator" element's content identifies the agent used to
 *  generate a feed, for debugging and other purposes.
 *  The content of this element, when present, MUST be a string that is a
 *  human-readable name for the generating agent.  Entities such as
 *  "&amp;amp;" and "&amp;lt;" represent their corresponding characters 
 *  ("&amp;" and "&lt;" respectively), not markup.
 * 
 *  The atom:generator element MAY have a "uri" attribute whose value
 *  MUST be an IRI reference [RFC3987].  When dereferenced, the resulting
 *  URI (mapped from an IRI, if necessary) SHOULD produce a
 *  representation that is relevant to that agent.
 * 
 *  The atom:generator element MAY have a "version" attribute that
 *  indicates the version of the generating agent.
 * </pre>
 */
public interface Generator extends Element {

    /**
     * The atom:generator element MAY have a "uri" attribute whose value MUST be an IRI reference [RFC3987]. When
     * dereferenced, the resulting URI (mapped from an IRI, if necessary) SHOULD produce a representation that is
     * relevant to that agent.
     * 
     * @throws IRISyntaxException if the uri is malformed
     */
    IRI getUri();

    /**
     * Returns the fully qualified form of the generator element's uri attribute (resolved against the in-scope Base
     * URI)
     * 
     * @return the resolved uri value
     * @throws IRISyntaxException if the uri is malformed
     */
    IRI getResolvedUri();

    /**
     * The atom:generator element MAY have a "uri" attribute whose value MUST be an IRI reference [RFC3987]. When
     * dereferenced, the resulting URI (mapped from an IRI, if necessary) SHOULD produce a representation that is
     * relevant to that agent.
     * 
     * @param uri The URI attribute value
     * @throws IRISyntaxException if the uri is malformed
     */
    Generator setUri(String uri);

    /**
     * The atom:generator element MAY have a "version" attribute that indicates the version of the generating agent.
     * 
     * @return The version attribute value
     */
    String getVersion();

    /**
     * The atom:generator element MAY have a "version" attribute that indicates the version of the generating agent.
     * 
     * @param version The version attribute
     */
    Generator setVersion(String version);

}
