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
 * Represents an Atom Text Contruct.
 * </p>
 * <p>
 * Atom allows three kinds of Text constructs:
 * </p>
 * <ul>
 * <li>Text, consisting of content that is to be interpreted as plain text with no markup. For instance,
 * <code>&lt;title type="text">&amp;lt;title&amp;gt;&lt;/title></code> is interpreted as literal characer "&lt;"
 * followed by the word "content", followed by the literal character "&gt;".</li>
 * <li>HTML, consisting of content that is to be interpreted as escaped HTML markup. For instance,
 * <code>&lt;title type="html">&amp;lt;b&amp;gt;title&amp;lt;/b&amp;gt;&lt;/title></code> is interpreted as the word
 * "content" surrounded by the HTML <code>&lt;b&gt;</code> and <code>&lt;/b&gt;</code> tags.</li>
 * <li>XHTML, consisting of well-formed XHTML content wrapped in an XHTML div element. For instance,
 * <code>&lt;title type="xhtml">&lt;div xmlns="http://www.w3.org/1999/xhtml">&lt;b>Title&lt;/b>&lt;/div>&lt;/title></code>
 * .</li>
 * </ul>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *  A Text construct contains human-readable text, usually in small
 *  quantities.  The content of Text constructs is Language-Sensitive.
 * 
 *  atomPlainTextConstruct =
 *     atomCommonAttributes,
 *     attribute type { "text" | "html" }?,
 *     text
 * 
 *  atomXHTMLTextConstruct =
 *     atomCommonAttributes,
 *     attribute type { "xhtml" },
 *     xhtmlDiv
 * 
 *  atomTextConstruct = atomPlainTextConstruct | atomXHTMLTextConstruct
 * </pre>
 */
public interface Text extends Element {

    /**
     * Text Constructs can be either Text, HTML or XHTML
     */
    public static enum Type {
        /** Plain Text **/
        TEXT,
        /** Escaped HTML **/
        HTML,
        /** Welformed XHTML **/
        XHTML;

        /**
         * Return the text type
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
                else
                    type = null;
            }
            return type;
        }

    };

    /**
     * Return the Text.Type
     * 
     * @return The Text.Type
     */
    Type getTextType();

    /**
     * Set the Text.Type
     * 
     * @param type The Text.Type
     */
    Text setTextType(Type type);

    /**
     * Return the text value element
     * 
     * @return A xhtml:div
     */
    Div getValueElement();

    /**
     * Set the text value element
     * 
     * @param value The xhtml:div
     */
    Text setValueElement(Div value);

    /**
     * Return the text value
     * 
     * @return The text value
     */
    String getValue();

    /**
     * Set the text value
     * 
     * @param value The text value
     */
    Text setValue(String value);

    /**
     * Return the wrapped value
     * 
     * @return The text value wrapped in a xhtml:div
     */
    String getWrappedValue();

    /**
     * Set the wrapped value
     * 
     * @param wrappedValue The text value wrapped in a xhtml:div
     */
    Text setWrappedValue(String wrappedValue);

}
