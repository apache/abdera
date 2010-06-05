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

import javax.activation.DataHandler;
import javax.activation.MimeType;

import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.i18n.iri.IRI;

/**
 * <p>
 * Represents an atom:content element.
 * </p>
 * <p>
 * Atom has a very clearly defined and extremely flexible content model. The model allows for five basic types of
 * content:
 * </p>
 * <ul>
 * <li>Text, consisting of content that is to be interpreted as plain text with no markup. For instance,
 * <code>&lt;content type="text">&amp;lt;content&amp;gt;&lt;/content></code> is interpreted as literal characer "&lt;"
 * followed by the word "content", followed by the literal character "&gt;".</li>
 * <li>HTML, consisting of content that is to be interpreted as escaped HTML markup. For instance,
 * <code>&lt;content type="html">&amp;lt;b&amp;gt;content&amp;lt;/b&amp;gt;&lt;/content></code> is interpreted as the
 * word "content" surrounded by the HTML <code>&lt;b&gt;</code> and <code>&lt;/b&gt;</code> tags.</li>
 * <li>XHTML, consisting of well-formed XHTML content wrapped in an XHTML div element. For instance,
 * <code>&lt;content type="xhtml">&lt;div xmlns="http://www.w3.org/1999/xhtml">&lt;b>Content&lt;/b>&lt;/div>&lt;/content></code>
 * .</li>
 * <li>XML, consisting of well-formed XML content. For instance,
 * <code>&lt;content type="application/xml">&lt;a xmlns="...">&lt;b>&lt;c/>&lt;/b>&lt;/a>&lt;/content></code>. The
 * content could, alternatively, be linked to via the src attribute,
 * <code>&lt;content type="application/xml" src="http://example.org/foo.xml"/></code>.</li>
 * <li>Media, consisting of content conforming to any MIME media type.
 * <ul>
 * <li>Text media types are encoded literally, e.g.
 * <code>&lt;content type="text/calendar">BEGIN:VCALENDAR...&lt;/content></code>.</li>
 * <li>Other media types are encoded as Base64 strings, e.g.
 * <code>&lt;content type="image/jpeg">{Base64}&lt;/content></code>.</li>
 * <li>Alternatively, media content may use the src attribute,
 * <code>&lt;content type="text/calendar" src="http://example.org/foo.cal"/></code>,
 * <code>&lt;content type="image/jpeg" src="http://example.org/foo.jpg" /></code></li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *  The "atom:content" element either contains or links to the content of
 *  the entry.  The content of atom:content is Language-Sensitive.
 * 
 *  atomInlineTextContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { "text" | "html" }?,
 *        (text)*
 *     }
 * 
 *  atomInlineXHTMLContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { "xhtml" },
 *        xhtmlDiv
 *     }
 *  atomInlineOtherContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { atomMediaType }?,
 *        (text|anyElement)*
 *     }
 * 
 *  atomOutOfLineContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { atomMediaType }?,
 *        attribute src { atomUri },
 *        empty
 *     }
 * 
 *  atomContent = atomInlineTextContent
 *   | atomInlineXHTMLContent
 *   | atomInlineOtherContent
 *   | atomOutOfLineContent
 * 
 * </pre>
 */
public interface Content extends Element {

    /**
     * Used to identify the type of content
     */
    public enum Type {
        /** Plain text **/
        TEXT,
        /** Escaped HTML **/
        HTML,
        /** Welformed XHTML **/
        XHTML,
        /** Welformed XML **/
        XML,
        /** Base64-encoded Binary **/
        MEDIA;

        /**
         * Return an appropriate Type given the specified @type attribute value
         */
        public static Type typeFromString(String val) {
            Type type = TEXT;
            if (val != null) {
                if (val.equalsIgnoreCase("text"))
                    type = TEXT;
                else if (val.equalsIgnoreCase("html"))
                    type = HTML;
                else if (val.equalsIgnoreCase("xhtml"))
                    type = XHTML;
                else if (MimeTypeHelper.isXml(val))
                    type = XML;
                else {
                    type = MimeTypeHelper.isMimeType(val) ? MEDIA : null;
                }
            }
            return type;
        }
    }

    /**
     * Returns the Content Type
     * 
     * @return The Content Type
     */
    Type getContentType();

    /**
     * Set the Content Type
     * 
     * @param type The Content Type
     */
    Content setContentType(Type type);

    /**
     * Return the value element or null if type="text", type="html" or type is some non-XML media type
     * 
     * @return The first child element of the atom:content element or null
     */
    <T extends Element> T getValueElement();

    /**
     * Set the value element of the content. If the value is a Div, the type attribute will be set to type="xhtml",
     * otherwise, the attribute will be set to type="application/xml"
     * 
     * @param value The element to set
     */
    <T extends Element> Content setValueElement(T value);

    /**
     * RFC4287: On the atom:content element, the value of the "type" attribute MAY be one of "text", "html", or "xhtml".
     * Failing that, it MUST conform to the syntax of a MIME media type, but MUST NOT be a composite type. If neither
     * the type attribute nor the src attribute is provided, Atom Processors MUST behave as though the type attribute
     * were present with a value of "text".
     * 
     * @return null if type = text, html or xhtml, otherwise a media type
     */
    MimeType getMimeType();

    /**
     * RFC4287: On the atom:content element, the value of the "type" attribute MAY be one of "text", "html", or "xhtml".
     * Failing that, it MUST conform to the syntax of a MIME media type, but MUST NOT be a composite type. If neither
     * the type attribute nor the src attribute is provided, Atom Processors MUST behave as though the type attribute
     * were present with a value of "text".
     * 
     * @param type The media type
     * @throws MimeTypeParseException if the media type is malformed
     */
    Content setMimeType(String type);

    /**
     * <p>
     * RFC4287: atom:content MAY have a "src" attribute, whose value MUST be an IRI reference. If the "src" attribute is
     * present, atom:content MUST be empty. Atom Processors MAY use the IRI to retrieve the content and MAY choose to
     * ignore remote content or to present it in a different manner than local content.
     * </p>
     * <p>
     * If the "src" attribute is present, the "type" attribute SHOULD be provided and MUST be a MIME media type, rather
     * than "text", "html", or "xhtml".
     * </p>
     * 
     * @return The IRI value of the src attribute or null if none
     * @throws IRISyntaxException if the src attribute value is malformed
     */
    IRI getSrc();

    /**
     * Returns the fully qualified URI form of the content src attribute.
     * 
     * @return The IRI value of the src attribute resolved against the in-scope Base URI
     * @throws IRISyntaxException if the src attribute value is malformed
     */
    IRI getResolvedSrc();

    /**
     * <p>
     * RFC4287: atom:content MAY have a "src" attribute, whose value MUST be an IRI reference. If the "src" attribute is
     * present, atom:content MUST be empty. Atom Processors MAY use the IRI to retrieve the content and MAY choose to
     * ignore remote content or to present it in a different manner than local content.
     * </p>
     * <p>
     * If the "src" attribute is present, the "type" attribute SHOULD be provided and MUST be a MIME media type, rather
     * than "text", "html", or "xhtml".
     * </p>
     * 
     * @param src The IRI to use as the src attribute value for the content
     * @throws IRISyntaxException if the src value is malformed
     */
    Content setSrc(String src);

    /**
     * Attempts to Base64 decode the string value of the content element.
     * 
     * @return A DataHandler or null
     * @throws UnsupportedOperationException if type = text, html, xhtml, or any application/*+xml, or text/* type
     */
    DataHandler getDataHandler();

    /**
     * Sets the string value of the content element by Base64 encoding the specifed byte array.
     * 
     * @param dataHandler The DataHandler for the binary content requiring Base64 encoding
     * @throws UnsupportedOperationException if type = text, html, xhtml, or any application/*+xml, or text/* type
     */
    Content setDataHandler(DataHandler dataHandler);

    /**
     * Returns the string value of this atom:content element
     * 
     * @return The string value
     */
    String getValue();

    /**
     * Set the string value of the atom:content element
     * 
     * @param value The string value
     */
    Content setValue(String value);

    /**
     * Return the string value of the atom:content element with the enclosing div tag if type="xhtml"
     * 
     * @return The div wrapped value
     */
    String getWrappedValue();

    /**
     * Set the string value of the atom:content with the enclosing div tag
     * 
     * @param wrappedValue The string value with the wrapping div tag
     */
    Content setWrappedValue(String wrappedValue);
}
