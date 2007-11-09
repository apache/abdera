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
package org.apache.abdera.writer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.NamedItem;

/**
 * The StreamWriter interface provides a for producing Atom documents 
 * based on a streaming api.  This approach provides a lightweight
 * alternative to building up an object model.
 * 
 * The StreamWriter is NOT synchronized and is NOT threadsafe
 */
public interface StreamWriter 
  extends NamedItem {

  StreamWriter flush();
  
  /**
   * True if the StreamWriter should autoclose the buffer after calling endDocument
   */
  StreamWriter setAutoclose(boolean auto);
  
  /**
   * True if the StreamWriter should autoflush the buffer
   */
  StreamWriter setAutoflush(boolean auto);
  
  /**
   * Set the target java.io.Writer
   */
  StreamWriter setWriter(java.io.Writer writer);
  
  /**
   * Set the target java.io.OutputStream
   */
  StreamWriter setOutputStream(java.io.OutputStream out);
  
  /**
   * Set the target java.io.OutputStream
   */
  StreamWriter setOutputStream(java.io.OutputStream out, String charset);
  
  /**
   * Set the target WritableByteChannel
   */
  StreamWriter setChannel(java.nio.channels.WritableByteChannel channel);

  /**
   * Set the target WritableByteChannel
   */
  StreamWriter setChannel(java.nio.channels.WritableByteChannel channel, String charset);
  
  /**
   * Start the document
   * @param xmlversion The XML version
   * @param charset the Character Encoding
   */
  StreamWriter startDocument(String xmlversion, String charset);

  /**
   * Start the document
   * @param xmlversion The XML version
   */
  StreamWriter startDocument(String xmlversion);
  
  /**
   * Start the document
   */
  StreamWriter startDocument();
 
  /**
   * End the document
   */
  StreamWriter endDocument();
  
  /**
   * Start an atom:feed element
   */
  StreamWriter startFeed();

  /**
   * End the atom:feed element
   */
  StreamWriter endFeed();
  
  /**
   * Start an atom:entry element
   */
  StreamWriter startEntry();
  
  /**
   * End the atom:entry element
   */
  StreamWriter endEntry();
  
  /**
   * Write an atom:id element
   * @param iri The value
   */
  StreamWriter writeId(String iri);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   */
  StreamWriter writeIcon(String iri);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   */
  StreamWriter writeLogo(String iri);
  
  /**
   * Write an IRI element
   * @param iri The value
   */
  StreamWriter writeIRIElement(QName qname, String iri);

  /**
   * Write an atom:id element
   * @param iri The value
   */
  StreamWriter writeId(IRI iri);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   */
  StreamWriter writeIcon(IRI iri);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   */
  StreamWriter writeLogo(IRI iri);
  
  /**
   * Write an IRI element
   * @param iri The value
   */
  StreamWriter writeIRIElement(QName qname, IRI iri);
  
  /**
   * Write an atom:id element with a new IRI value
   * @param iri The value
   */
  StreamWriter writeId();

  /**
   * Write an atom:updated element
   * @param date The date value
   */
  StreamWriter writeUpdated(Date date);
  
  /**
   * Write an atom:published element
   * @param date The date value
   */
  StreamWriter writePublished(Date date);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   */
  StreamWriter writeEdited(Date date);
  
  /**
   * Write a Date element
   * @param qname The element qname
   * @param date The date value
   */
  StreamWriter writeDate(QName qname, Date date);

  /**
   * Write an atom:updated element
   * @param date The date value
   */
  StreamWriter writeUpdated(String date);
  
  /**
   * Write an atom:published element
   * @param date The date value
   */
  StreamWriter writePublished(String date);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   */
  StreamWriter writeEdited(String date);
  
  /**
   * Write a Date element
   * @param qname The element qname
   * @param date The date value
   */
  StreamWriter writeDate(QName qname, String date);
  
  /**
   * End the person element
   */
  StreamWriter endPerson();
  
  
  /**
   * End the atom:link
   */
  StreamWriter endLink();

  /**
   * Write an atom:link element
   * @param iri The href value
   */
  StreamWriter writeLink(String iri);

  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   */
  StreamWriter writeLink(String iri, String rel);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   */
  StreamWriter writeLink(String iri, String rel, String type);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   */
  StreamWriter writeLink(String iri, String rel, String type, String title, String hreflang, int length);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   */
  StreamWriter startLink(String iri);

  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   */
  StreamWriter startLink(String iri, String rel);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   */
  StreamWriter startLink(String iri, String rel, String type);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   */
  StreamWriter startLink(String iri, String rel, String type, String title, String hreflang, int length);
  
  /**
   * End the atom:category
   */
  StreamWriter endCategory();

  /**
   * Write an atom:category element
   * @param term The term value
   */
  StreamWriter writeCategory(String term);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   */
  StreamWriter writeCategory(String term, String scheme);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   */
  StreamWriter writeCategory(String term, String scheme, String label);
  
  /**
   * Start an atom:category element
   * @param term The term value
   */
  StreamWriter startCategory(String term);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   */
  StreamWriter startCategory(String term, String scheme);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   */
  StreamWriter startCategory(String term, String scheme, String label);
  
  
  /**
   * Start an atom:source element
   */
  StreamWriter startSource();
  
  /**
   * End the atom:source
   */
  StreamWriter endSource();
  
  /**
   * Write a Text element
   * @param qname The element qname
   * @param type The text type
   * @param value The text value
   */
  StreamWriter writeText(QName qname, Text.Type type, String value);
  
  /**
   * Start a Text element
   * @param qname The element qname
   * @param type The text type
   */
  StreamWriter startText(QName qname, Text.Type type);
  
  /**
   * End the atom:content element
   */
  StreamWriter endContent();
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  StreamWriter writeContent(Content.Type type, String value);
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  StreamWriter writeContent(Content.Type type, InputStream value) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  StreamWriter writeContent(Content.Type type, DataHandler value) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  StreamWriter writeContent(String type, String value);
  
  /**
   * Start an atom:content element
   * @param type The text type
   */  
  StreamWriter startContent(Content.Type type);
  
  /**
   * Start an atom:content element
   * @param type The text type
   */  
  StreamWriter startContent(String type);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The src value
   */  
  StreamWriter startContent(Content.Type type, String src);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The src value
   */  
  StreamWriter startContent(String type, String src);
  
  /**
   * Start an element
   * @param qname Element qname
   */  
  StreamWriter startElement(QName qname);
  
  /**
   * Start an element
   */
  StreamWriter startElement(String name);
  
  /**
   * Start an element
   */
  StreamWriter startElement(String name, String namespace);
  
  /**
   * Start an element
   */
  StreamWriter startElement(String name, String namespace, String prefix);
  
  /**
   * Write element text
   * @param value The text value
   */  
  StreamWriter writeElementText(String value);
  
  /**
   * Write element text
   * @param datahandler The text value
   */  
  StreamWriter writeElementText(DataHandler datahandler) throws IOException;
  
  /**
   * Write element text
   * @param in The text value
   */  
  StreamWriter writeElementText(InputStream in) throws IOException;
  
  /**
   * End the element
   */
  StreamWriter endElement();
  
  /**
   * Write an atom:title element
   * @param value The text value
   */
  StreamWriter writeTitle(String value);
  
  /**
   * Write an atom:title element
   * @param type The text type
   * @param value The text value
   */
  StreamWriter writeTitle(Text.Type type, String value);

  /**
   * Write an atom:subtitle element
   * @param value The text value
   */
  StreamWriter writeSubtitle(String value);
  
  /**
   * Write an atom:subtitle element
   * @param type The text type
   * @param value The text value
   */
  StreamWriter writeSubtitle(Text.Type type, String value);
  
  /**
   * Write an atom:summary element
   * @param value The text value
   */
  StreamWriter writeSummary(String value);
  
  /**
   * Write an atom:summary element
   * @param type The text type
   * @param value The text value
   */
  StreamWriter writeSummary(Text.Type type, String value);
  
  /**
   * Write an atom:rights element
   * @param value The text value
   */
  StreamWriter writeRights(String value);
  
  /**
   * Write an atom:rights element
   * @param type The text type
   * @param value The text value
   */
  StreamWriter writeRights(Text.Type type, String value);
  
  /**
   * Write a person element
   * @param qname the element qname
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  StreamWriter writePerson(QName qname, String name, String email, String uri);

  /**
   * Start a person element
   * @param qname The element qname
   */  
  StreamWriter startPerson(QName qname);
  
  /**
   * Write a person name
   * @param name The person name
   */
  StreamWriter writePersonName(String name);

  /**
   * Write a person email
   * @param email The person email
   */
  StreamWriter writePersonEmail(String email);

  /**
   * Write a person uri
   * @param uri The person uri
   */
  StreamWriter writePersonUri(String uri);
  
  /**
   * Write an atom:author element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  StreamWriter writeAuthor(String name, String email, String uri);
  
  /**
   * Start an atom:author element
   */
  StreamWriter startAuthor();
  
  /**
   * End the atom:author element
   */
  StreamWriter endAuthor();
  
  /**
   * Write an atom:contributor element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  StreamWriter writeContributor(String name, String email, String uri);
  
  /**
   * Start an atom:contributor element
   */
  StreamWriter startContributor();
  
  /**
   * End an atom:contributor element
   */
  StreamWriter endContributor();
  
  /**
   * Write an atom:generator element
   * @param version The version value
   * @param uri The uri value
   * @param value The text value
   */
  StreamWriter writeGenerator(String version, String uri, String value);

  /**
   * Start an atom:generator element
   * @param version The version value
   * @param uri The uri value
   */
  StreamWriter startGenerator(String version, String uri);
  
  /**
   * End the atom:generator element
   */
  StreamWriter endGenerator();
  
  /**
   * Write an XML comment
   */
  StreamWriter writeComment(String value);
  
  /**
   * Write an XML Processing Instruction
   */
  StreamWriter writePI(String value);

  /**
   * Write an XML Processing Instruction
   */
  StreamWriter writePI(String value, String target);
  
  
  /**
   * Start an app:service element
   */
  StreamWriter startService();
  
  /**
   * End an app:service element
   */
  StreamWriter endService();
  
  /**
   * Start an app:workspace element
   */
  StreamWriter startWorkspace();
  
  /**
   * End an app:workspace element
   */
  StreamWriter endWorkspace();
  
  /**
   * Start an app:collection element
   * @param href The href value
   */
  StreamWriter startCollection(String href);
  
  /**
   * End an app:collection element
   */
  StreamWriter endCollection();
  
  /**
   * Writes app:accept elements
   * @param accepts accept element values
   */
  StreamWriter writeAccepts(String... accepts);
  
  /**
   * Start an app:categories element
   */
  StreamWriter startCategories();
  
  /**
   * Start an app:categories element
   * @param fixed True if the app:categories element is fixed
   */
  StreamWriter startCategories(boolean fixed);
  
  /**
   * Start an app:categories element
   * @param fixed True if the app:categories element is fixed
   * @param scheme The scheme value
   */
  StreamWriter startCategories(boolean fixed, String scheme);
  
  /**
   * End the app:categories element
   */
  StreamWriter endCategories();

  /**
   * Start the app:control element
   */
  StreamWriter startControl();
  
  /**
   * End the app:control element
   */
  StreamWriter endControl();
  
  /**
   * Write an app:draft element
   * @param draft true if app:draft=yes
   */
  StreamWriter writeDraft(boolean draft);
  
  /**
   * Write an attribute
   */
  StreamWriter writeAttribute(String name, String value);
  
  /**
   * Write an attribute
   */
  StreamWriter writeAttribute(String name, String namespace, String value);
  
  /**
   * Write an attribute
   */
  StreamWriter writeAttribute(String name, String namespace, String prefix, String value);
  
  /**
   * Write an attribute
   */
  StreamWriter writeAttribute(QName qname, String value);
  
}

