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

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;

/**
 * <p>
 * Represents an Atom Link element.
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *  The "atom:link" element defines a reference from an entry or feed to
 *  a Web resource.  This specification assigns no meaning to the content
 *  (if any) of this element.
 * 
 *  atomLink =
 *     element atom:link {
 *        atomCommonAttributes,
 *        attribute href { atomUri },
 *        attribute rel { atomNCName | atomUri }?,
 *        attribute type { atomMediaType }?,
 *        attribute hreflang { atomLanguageTag }?,
 *        attribute title { text }?,
 *        attribute length { text }?,
 *        undefinedContent
 *     }
 * </pre>
 */
public interface Link extends ExtensibleElement {

    public static final String REL_ALTERNATE = "alternate";
    public static final String REL_CURRENT = "current";
    public static final String REL_ENCLOSURE = "enclosure";
    public static final String REL_FIRST = "first";
    public static final String REL_LAST = "last";
    public static final String REL_NEXT = "next";
    public static final String REL_PAYMENT = "payment";
    public static final String REL_PREVIOUS = "previous";
    public static final String REL_RELATED = "related";
    public static final String REL_SELF = "self";
    public static final String REL_VIA = "via";
    public static final String REL_REPLIES = "replies";
    public static final String REL_LICENSE = "license";
    public static final String REL_EDIT = "edit";
    public static final String REL_EDIT_MEDIA = "edit-media";
    public static final String REL_SERVICE = "service";

    public static final String IANA_BASE = "http://www.iana.org/assignments/relation/";

    public static final String REL_ALTERNATE_IANA = IANA_BASE + REL_ALTERNATE;
    public static final String REL_CURRENT_IANA = IANA_BASE + REL_CURRENT;
    public static final String REL_ENCLOSURE_IANA = IANA_BASE + REL_ENCLOSURE;
    public static final String REL_FIRST_IANA = IANA_BASE + REL_FIRST;
    public static final String REL_LAST_IANA = IANA_BASE + REL_LAST;
    public static final String REL_NEXT_IANA = IANA_BASE + REL_NEXT;
    public static final String REL_PAYMENT_IANA = IANA_BASE + REL_PAYMENT;
    public static final String REL_PREVIOUS_IANA = IANA_BASE + REL_PREVIOUS;
    public static final String REL_RELATED_IANA = IANA_BASE + REL_RELATED;
    public static final String REL_SELF_IANA = IANA_BASE + REL_SELF;
    public static final String REL_VIA_IANA = IANA_BASE + REL_VIA;
    public static final String REL_REPLIES_IANA = IANA_BASE + REL_REPLIES;
    public static final String REL_LICENSE_IANA = IANA_BASE + REL_LICENSE;
    public static final String REL_EDIT_IANA = IANA_BASE + REL_EDIT;
    public static final String REL_EDIT_MEDIA_IANA = IANA_BASE + REL_EDIT_MEDIA;
    public static final String REL_SERVICE_IANA = IANA_BASE + REL_SERVICE;

    /**
     * RFC4287: The "href" attribute contains the link's IRI. atom:link elements MUST have an href attribute, whose
     * value MUST be a IRI reference [RFC3987].
     * 
     * @return The href IRI value
     * @throws IRISyntaxException if the href is malformed
     */
    IRI getHref();

    /**
     * Returns the value of the link's href attribute resolved against the in-scope Base IRI
     * 
     * @return The href IRI value
     * @throws IRISyntaxException if the href is malformed
     */
    IRI getResolvedHref();

    /**
     * RFC4287: The "href" attribute contains the link's IRI. atom:link elements MUST have an href attribute, whose
     * value MUST be a IRI reference [RFC3987].
     * 
     * @param href The href IRI
     * @throws IRISyntaxException if the href is malformed
     */
    Link setHref(String href);

    /**
     * <p>
     * RFC4287: atom:link elements MAY have a "rel" attribute that indicates the link relation type. If the "rel"
     * attribute is not present, the link element MUST be interpreted as if the link relation type is "alternate"... The
     * value of "rel" MUST be a string that is non-empty and matches either the "isegment-nz-nc" or the "IRI" production
     * in [RFC3987]. Note that use of a relative reference other than a simple name is not allowed. If a name is given,
     * implementations MUST consider the link relation type equivalent to the same name registered within the IANA
     * Registry of Link Relations (Section 7), and thus to the IRI that would be obtained by appending the value of the
     * rel attribute to the string "http://www.iana.org/assignments/relation/". The value of "rel" describes the meaning
     * of the link, but does not impose any behavioral requirements on Atom Processors.
     * </p>
     * 
     * @return The rel attribute value
     */
    String getRel();

    /**
     * <p>
     * RFC4287: atom:link elements MAY have a "rel" attribute that indicates the link relation type. If the "rel"
     * attribute is not present, the link element MUST be interpreted as if the link relation type is "alternate"... The
     * value of "rel" MUST be a string that is non-empty and matches either the "isegment-nz-nc" or the "IRI" production
     * in [RFC3987]. Note that use of a relative reference other than a simple name is not allowed. If a name is given,
     * implementations MUST consider the link relation type equivalent to the same name registered within the IANA
     * Registry of Link Relations (Section 7), and thus to the IRI that would be obtained by appending the value of the
     * rel attribute to the string "http://www.iana.org/assignments/relation/". The value of "rel" describes the meaning
     * of the link, but does not impose any behavioral requirements on Atom Processors.
     * </p>
     * 
     * @param rel The rel attribute value
     */
    Link setRel(String rel);

    /**
     * RFC4287: On the link element, the "type" attribute's value is an advisory media type: it is a hint about the type
     * of the representation that is expected to be returned when the value of the href attribute is dereferenced. Note
     * that the type attribute does not override the actual media type returned with the representation. Link elements
     * MAY have a type attribute, whose value MUST conform to the syntax of a MIME media type [MIMEREG].
     * 
     * @return The value of the type attribute
     * @throws MimeTypeParseException if the type is malformed
     */
    MimeType getMimeType();

    /**
     * RFC4287: On the link element, the "type" attribute's value is an advisory media type: it is a hint about the type
     * of the representation that is expected to be returned when the value of the href attribute is dereferenced. Note
     * that the type attribute does not override the actual media type returned with the representation. Link elements
     * MAY have a type attribute, whose value MUST conform to the syntax of a MIME media type [MIMEREG].
     * 
     * @param type The link type
     * @throws MimeTypeParseException if the type is malformed
     */
    Link setMimeType(String type);

    /**
     * RFC4287: The "hreflang" attribute's content describes the language of the resource pointed to by the href
     * attribute. When used together with the rel="alternate", it implies a translated version of the entry. Link
     * elements MAY have an hreflang attribute, whose value MUST be a language tag [RFC3066].
     * 
     * @return The hreflang value
     */
    String getHrefLang();

    /**
     * RFC4287: The "hreflang" attribute's content describes the language of the resource pointed to by the href
     * attribute. When used together with the rel="alternate", it implies a translated version of the entry. Link
     * elements MAY have an hreflang attribute, whose value MUST be a language tag [RFC3066].
     * 
     * @param lang The hreflang value
     */
    Link setHrefLang(String lang);

    /**
     * RFC4287: The "title" attribute conveys human-readable information about the link. The content of the "title"
     * attribute is Language-Sensitive. Entities such as "&amp;amp;" and "&amp;lt;" represent their corresponding
     * characters ("&amp;" and "&lt;", respectively), not markup. Link elements MAY have a title attribute.
     * 
     * @return The title attribute
     */
    String getTitle();

    /**
     * RFC4287: The "title" attribute conveys human-readable information about the link. The content of the "title"
     * attribute is Language-Sensitive. Entities such as "&amp;amp;" and "&amp;lt;" represent their corresponding
     * characters ("&amp;" and "&lt;", respectively), not markup. Link elements MAY have a title attribute.
     * 
     * @param title The title attribute
     */
    Link setTitle(String title);

    /**
     * RFC4287: The "length" attribute indicates an advisory length of the linked content in octets; it is a hint about
     * the content length of the representation returned when the URI in the href attribute is mapped to a IRI and
     * dereferenced. Note that the length attribute does not override the actual content length of the representation as
     * reported by the underlying protocol. Link elements MAY have a length attribute.
     * 
     * @return The length attribute value
     */
    long getLength();

    /**
     * RFC4287: The "length" attribute indicates an advisory length of the linked content in octets; it is a hint about
     * the content length of the representation returned when the IRI in the href attribute is mapped to a URI and
     * dereferenced. Note that the length attribute does not override the actual content length of the representation as
     * reported by the underlying protocol. Link elements MAY have a length attribute.
     * 
     * @param length The length attribute value
     */
    Link setLength(long length);

}
