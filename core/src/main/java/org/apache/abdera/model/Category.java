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

/**
 * <p>
 * Provides categorization informaton for a feed or entry
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *  The "atom:category" element conveys information about a category
 *  associated with an entry or feed.  This specification assigns no
 *  meaning to the content (if any) of this element.
 * 
 *  atomCategory =
 *     element atom:category {
 *        atomCommonAttributes,
 *        attribute term { text },
 *        attribute scheme { atomUri }?,
 *        attribute label { text }?,
 *        undefinedContent
 *     }
 * </pre>
 */
public interface Category extends ExtensibleElement {

    /**
     * RFC4287: The "term" attribute is a string that identifies the category to which the entry or feed belongs.
     * Category elements MUST have a "term" attribute.
     * 
     * @return The string value of the term attribute
     */
    String getTerm();

    /**
     * RFC4287: The "term" attribute is a string that identifies the category to which the entry or feed belongs.
     * Category elements MUST have a "term" attribute.
     * 
     * @param term The string value of the term attribute
     */
    Category setTerm(String term);

    /**
     * RFC4287: The "scheme" attribute is an IRI that identifies a categorization scheme. Category elements MAY have a
     * "scheme" attribute.
     * 
     * @return The IRI value of the scheme attribute
     */
    IRI getScheme();

    /**
     * RFC4287: The "scheme" attribute is an IRI that identifies a categorization scheme. Category elements MAY have a
     * "scheme" attribute.
     * 
     * @param scheme The IRI of the scheme
     */
    Category setScheme(String scheme);

    /**
     * RFC4287: The "label" attribute provides a human-readable label for display in end-user applications. The content
     * of the "label" attribute is Language-Sensitive. Entities such as "&amp;amp;" and "&amp;lt;" represent their
     * corresponding characters ("&amp;" and "&lt;", respectively), not markup. Category elements MAY have a "label"
     * attribute.
     * 
     * @return The value of the human-readable label
     */
    String getLabel();

    /**
     * RFC4287: The "label" attribute provides a human-readable label for display in end-user applications. The content
     * of the "label" attribute is Language-Sensitive. Entities such as "&amp;amp;" and "&amp;lt;" represent their
     * corresponding characters ("&amp;" and "&lt;", respectively), not markup. Category elements MAY have a "label"
     * attribute.
     * 
     * @param label The value of the human-readable label
     */
    Category setLabel(String label);

}
