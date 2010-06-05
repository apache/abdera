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

import java.io.Serializable;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.XmlUtil.XMLVersion;

/**
 * <p>
 * The top level artifact of the Feed Object Model. The Parser component processes data from an InputStream and returns
 * a Document instance. The type of Document returned depends on the XML format being parsed. The Feed Object Model
 * supports four basic types of documents: FeedDocument, EntryDocument, ServiceDocument (Atom Publishing Protocol
 * Introspection Documents) and XmlDocument (any arbitrary XML).
 * </p>
 */
public interface Document<T extends Element> extends Base, Serializable {

    /**
     * Returns the root element of the document (equivalent to DOM's getDocumentElement)
     * 
     * @return The root element of the document
     */
    T getRoot();

    /**
     * Sets the root element of the document
     * 
     * @param root Set the root element of the document
     */
    Document<T> setRoot(T root);

    /**
     * Returns the Base URI of the document. All relative URI's contained in the document will be resolved according to
     * this base.
     * 
     * @return The Base IRI
     */
    IRI getBaseUri();

    /**
     * Sets the Base URI of the document. All relative URI's contained in the document will be resolved according to
     * this base.
     * 
     * @param base The Base URI
     * @throws IRISyntaxException if the IRI is malformed
     */
    Document<T> setBaseUri(String base);

    /**
     * Returns the content type of this document
     * 
     * @return The content type of this document
     */
    MimeType getContentType();

    /**
     * Sets the content type for this document
     * 
     * @param contentType The content type of document
     * @throws MimeTypeParseException if the content type is malformed
     */
    Document<T> setContentType(String contentType);

    /**
     * Returns the last modified date for this document
     * 
     * @return The last-modified date
     */
    Date getLastModified();

    /**
     * Sets the last modified date for this document
     * 
     * @param lastModified the last-modified date
     */
    Document<T> setLastModified(Date lastModified);

    /**
     * Gets the charset used for this document
     * 
     * @return The character encoding used for this document
     */
    String getCharset();

    /**
     * Sets the charset used for this document
     * 
     * @param charset The character encoding to use
     */
    Document<T> setCharset(String charset);

    /**
     * Add a processing instruction to the document
     * 
     * @param target The processing instruction target
     * @param value The processing instruction value
     */
    Document<T> addProcessingInstruction(String target, String value);

    /**
     * Get the values for the given processing instruction
     */
    String[] getProcessingInstruction(String target);

    /**
     * Add a xml-stylesheet processing instruction to the document
     * 
     * @param href The href of the stylesheet
     * @param media The media target for this stylesheet or null if none
     */
    Document<T> addStylesheet(String href, String media);

    /**
     * Return the entity tag for this document
     */
    EntityTag getEntityTag();

    /**
     * Set the entity tag for this document
     */
    Document<T> setEntityTag(EntityTag tag);

    /**
     * Set the entity tag for this document
     */
    Document<T> setEntityTag(String tag);

    /**
     * Get the language
     */
    String getLanguage();

    /**
     * Returns the value of the xml:lang attribute as a Lang object
     */
    Lang getLanguageTag();

    /**
     * set the base language
     */
    Document<T> setLanguage(String lang);

    /**
     * Get the slug for this document
     */
    String getSlug();

    /**
     * Set the slug for this document
     */
    Document<T> setSlug(String slug);

    /**
     * Return true if insignificant whitespace must be preserved
     */
    boolean getMustPreserveWhitespace();

    /**
     * Set to true to preserve insignificant whitespace
     */
    Document<T> setMustPreserveWhitespace(boolean preserve);

    /**
     * Get the XMLVersion used by this document
     */
    XMLVersion getXmlVersion();
}
