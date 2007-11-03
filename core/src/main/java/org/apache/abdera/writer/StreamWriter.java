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
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.NamedItem;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;

/**
 * The StreamWriter interface provides a for producing Atom documents 
 * based on a streaming api.  This approach provides a lightweight
 * alternative to building up an object model 
 */
public interface StreamWriter 
  extends NamedItem {

  /**
   * Set the target java.io.Writer
   */
  void setWriter(java.io.Writer writer);
  
  /**
   * Set the target java.io.OutputStream
   */
  void setOutputStream(java.io.OutputStream out);
  
  /**
   * Set the target java.io.OutputStream
   */
  void setOutputStream(java.io.OutputStream out, String charset);
  
  /**
   * Start the document
   * @param xmlversion The XML version
   * @param charset the Character Encoding
   */
  void startDocument(String xmlversion, String charset);

  /**
   * Start the document
   * @param xmlversion The XML version
   */
  void startDocument(String xmlversion);
  
  /**
   * Start the document
   */
  void startDocument();
 
  /**
   * End the document
   */
  void endDocument();
  
  /**
   * Start an atom:feed element
   */
  void startFeed();

  /**
   * Start an atom:feed element
   * @param attribute Extension attributes
   */
  void startFeed(Map<QName,String> attributes);
  
  /**
   * End the atom:feed element
   */
  void endFeed();
  
  /**
   * Start an atom:entry element
   */
  void startEntry();
  
  /**
   * Start an atom:entry element
   * @param attribute Extension attributes
   */
  void startEntry(Map<QName,String> attributes);
  
  /**
   * End the atom:entry element
   */
  void endEntry();
  
  /**
   * Write an atom:id element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeId(String iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeIcon(String iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeLogo(String iri, Map<QName,String> attributes);
  
  /**
   * Write an IRI element element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeIRIElement(QName qname, String iri, Map<QName,String> attributes);

  /**
   * Write an atom:id element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeId(IRI iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeIcon(IRI iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeLogo(IRI iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:iri element
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeIRIElement(QName qname, IRI iri, Map<QName,String> attributes);
  
  /**
   * Write an atom:id element
   * @param iri The value
   */
  void writeId(String iri);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   */
  void writeIcon(String iri);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   */
  void writeLogo(String iri);
  
  /**
   * Write an IRI element
   * @param iri The value
   */
  void writeIRIElement(QName qname, String iri);

  /**
   * Write an atom:id element
   * @param iri The value
   */
  void writeId(IRI iri);
  
  /**
   * Write an atom:icon element
   * @param iri The value
   */
  void writeIcon(IRI iri);
  
  /**
   * Write an atom:logo element
   * @param iri The value
   */
  void writeLogo(IRI iri);
  
  /**
   * Write an IRI element
   * @param iri The value
   */
  void writeIRIElement(QName qname, IRI iri);
  
  /**
   * Write an atom:id element with a new IRI value
   * @param iri The value
   */
  void writeId();

  /**
   * Write an atom:id element with a new IRI value
   * @param iri The value
   * @param attribute Extension attributes
   */
  void writeId(Map<QName,String> attributes);
  
  /**
   * Write an atom:updated element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeUpdated(Date date, Map<QName,String> attributes);
  
  /**
   * Write an atom:published element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writePublished(Date date, Map<QName,String> attributes);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeEdited(Date date, Map<QName,String> attributes);
  
  /**
   * Write a Date element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeDate(QName qname, Date date, Map<QName,String> attributes);

  /**
   * Write an atom:updated element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeUpdated(String date, Map<QName,String> attributes);
  
  /**
   * Write an atom:published element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writePublished(String date, Map<QName,String> attributes);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeEdited(String date, Map<QName,String> attributes);
  
  /**
   * Write a Date element
   * @param qname The element qname
   * @param date The date value
   * @param attributes Extension attributes
   */
  void writeDate(QName qname, String date, Map<QName,String> attributes);
  
  /**
   * Write an atom:updated element
   * @param date The date value
   */
  void writeUpdated(Date date);
  
  /**
   * Write an atom:published element
   * @param date The date value
   */
  void writePublished(Date date);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   */
  void writeEdited(Date date);
  
  /**
   * Write a Date element
   * @param qname The element qname
   * @param date The date value
   */
  void writeDate(QName qname, Date date);

  /**
   * Write an atom:updated element
   * @param date The date value
   */
  void writeUpdated(String date);
  
  /**
   * Write an atom:published element
   * @param date The date value
   */
  void writePublished(String date);
  
  /**
   * Write an atom:edited element
   * @param date The date value
   */
  void writeEdited(String date);
  
  /**
   * Write a Date element
   * @param qname The element qname
   * @param date The date value
   */
  void writeDate(QName qname, String date);
  
  /**
   * Write a person element
   * @param qname The element qname
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   * @param attributes Extension attributes
   */  
  void writePerson(QName qname, String name, String email, String uri, Map<QName,String> attributes);
  
  /**
   * Start a person element
   * @param qname The element qname
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   * @param attributes Extension attributes
   */  
  void startPerson(QName qname, Map<QName,String> attributes);
  
  /**
   * Write a person name element
   * @param name The person name
   * @param attributes Extension attributes
   */
  void writePersonName(String name,Map<QName,String> attributes);
  
  /**
   * Write a person email element
   * @param email The person email
   * @param attributes Extension attributes
   */
  void writePersonEmail(String email,Map<QName,String> attributes);
  
  /**
   * Write a person uri element
   * @param uri The person uri
   * @param attributes Extension attributes
   */
  void writePersonUri(String uri,Map<QName,String> attributes);
  
  /**
   * End the person element
   */
  void endPerson();
  
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param attributes Extension attributes
   */
  void writeLink(String iri,Map<QName,String> attributes);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param attributes Extension attributes
   */
  void writeLink(String iri, String rel,Map<QName,String> attributes);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param attributes Extension attributes
   */
  void writeLink(String iri, String rel, String type,Map<QName,String> attributes);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   * @param attributes Extension attributes
   */
  void writeLink(String iri, String rel, String type, String title, String hreflang, int length,Map<QName,String> attributes);

  /**
   * Start a atom:link element
   * @param iri The href value
   * @param attributes Extension attributes
   */
  void startLink(String iri,Map<QName,String> attributes);

  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param attributes Extension attributes
   */
  void startLink(String iri, String rel,Map<QName,String> attributes);

  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param attributes Extension attributes
   */
  void startLink(String iri, String rel, String type,Map<QName,String> attributes);

  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   * @param attributes Extension attributes
   */
  void startLink(String iri, String rel, String type, String title, String hreflang, int length,Map<QName,String> attributes);
  
  /**
   * End the atom:link
   */
  void endLink();

  /**
   * Write an atom:link element
   * @param iri The href value
   */
  void writeLink(String iri);

  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   */
  void writeLink(String iri, String rel);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   */
  void writeLink(String iri, String rel, String type);
  
  /**
   * Write an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   */
  void writeLink(String iri, String rel, String type, String title, String hreflang, int length);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   */
  void startLink(String iri);

  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   */
  void startLink(String iri, String rel);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   */
  void startLink(String iri, String rel, String type);
  
  /**
   * Start an atom:link element
   * @param iri The href value
   * @param rel The rel value
   * @param type The type value
   * @param title The title value
   * @param hreflang The hreflang value
   * @param length The link length
   */
  void startLink(String iri, String rel, String type, String title, String hreflang, int length);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param attributes Extension attributes
   */
  void writeCategory(String term,Map<QName,String> attributes);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param attributes Extension attributes
   */
  void writeCategory(String term, String scheme,Map<QName,String> attributes);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   * @param attributes Extension attributes
   */
  void writeCategory(String term, String scheme, String label,Map<QName,String> attributes);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param attributes Extension attributes
   */
  void startCategory(String term,Map<QName,String> attributes);

  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param attributes Extension attributes
   */
  void startCategory(String term, String scheme,Map<QName,String> attributes);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   * @param attributes Extension attributes
   */
  void startCategory(String term, String scheme, String label,Map<QName,String> attributes);
  
  /**
   * End the atom:category
   */
  void endCategory();

  /**
   * Write an atom:category element
   * @param term The term value
   */
  void writeCategory(String term);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   */
  void writeCategory(String term, String scheme);
  
  /**
   * Write an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   */
  void writeCategory(String term, String scheme, String label);
  
  /**
   * Start an atom:category element
   * @param term The term value
   */
  void startCategory(String term);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   */
  void startCategory(String term, String scheme);
  
  /**
   * Start an atom:category element
   * @param term The term value
   * @param scheme The term value
   * @param label The term value
   */
  void startCategory(String term, String scheme, String label);
  
  
  /**
   * Start an atom:source element
   * @param attributes Extension attributes
   */
  void startSource(Map<QName,String> attributes);
  
  /**
   * Start an atom:source element
   */
  void startSource();
  
  /**
   * End the atom:source
   */
  void endSource();
  
  /**
   * Write a Text element
   * @param qname The element qname
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void writeText(QName qname, Text.Type type, String value,Map<QName,String> attributes);
  
  /**
   * Start a Text element
   * @param qname The element qname
   * @param type The text type
   * @param attribute Extension attributes
   */
  void startText(QName qname, Text.Type type,Map<QName,String> attributes);

  /**
   * Write a Text element
   * @param qname The element qname
   * @param type The text type
   * @param value The text value
   */
  void writeText(QName qname, Text.Type type, String value);
  
  /**
   * Start a Text element
   * @param qname The element qname
   * @param type The text type
   */
  void startText(QName qname, Text.Type type);
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void writeContent(Content.Type type, String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void writeContent(Content.Type type, InputStream value,Map<QName,String> attributes) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void writeContent(Content.Type type, DataHandler value,Map<QName,String> attributes) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void writeContent(String type, String value,Map<QName,String> attributes);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param value The text value
   * @param attribute Extension attributes
   */
  void startContent(Content.Type type,Map<QName,String> attributes);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param attribute Extension attributes
   */
  void startContent(String type,Map<QName,String> attributes);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The src value
   * @param attribute Extension attributes
   */
  void startContent(Content.Type type, String src,Map<QName,String> attributes);

  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The text value
   * @param attribute Extension attributes
   */
  void startContent(String type, String src,Map<QName,String> attributes);
    
  /**
   * End the atom:content element
   */
  void endContent();
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  void writeContent(Content.Type type, String value);
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  void writeContent(Content.Type type, InputStream value) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  void writeContent(Content.Type type, DataHandler value) throws IOException;
  
  /**
   * Write an atom:content element
   * @param type The text type
   * @param value The text value
   */  
  void writeContent(String type, String value);
  
  /**
   * Start an atom:content element
   * @param type The text type
   */  
  void startContent(Content.Type type);
  
  /**
   * Start an atom:content element
   * @param type The text type
   */  
  void startContent(String type);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The src value
   */  
  void startContent(Content.Type type, String src);
  
  /**
   * Start an atom:content element
   * @param type The text type
   * @param src The src value
   */  
  void startContent(String type, String src);
  
  /**
   * Start an element
   * @param qname Element qname
   * @param attributes Extension attributes
   */  
  void startElement(QName qname,Map<QName,String> attributes);
  
  /**
   * Start an element
   * @param qname Element qname
   */  
  void startElement(QName qname);
  
  /**
   * Write element text
   * @param value The text value
   */  
  void writeElementText(String value);
  
  /**
   * Write element text
   * @param datahandler The text value
   */  
  void writeElementText(DataHandler datahandler) throws IOException;
  
  /**
   * Write element text
   * @param in The text value
   */  
  void writeElementText(InputStream in) throws IOException;
  
  /**
   * End the element
   */
  void endElement();
  
  /**
   * Write an atom:title element
   * @param value The text value
   */
  void writeTitle(String value);
  
  /**
   * Write an atom:title element
   * @param type The text type
   * @param value The text value
   */
  void writeTitle(Text.Type type, String value);

  /**
   * Write an atom:subtitle element
   * @param value The text value
   */
  void writeSubtitle(String value);
  
  /**
   * Write an atom:subtitle element
   * @param type The text type
   * @param value The text value
   */
  void writeSubtitle(Text.Type type, String value);
  
  /**
   * Write an atom:summary element
   * @param value The text value
   */
  void writeSummary(String value);
  
  /**
   * Write an atom:summary element
   * @param type The text type
   * @param value The text value
   */
  void writeSummary(Text.Type type, String value);
  
  /**
   * Write an atom:rights element
   * @param value The text value
   */
  void writeRights(String value);
  
  /**
   * Write an atom:rights element
   * @param type The text type
   * @param value The text value
   */
  void writeRights(Text.Type type, String value);
  
  /**
   * Write an atom:title element
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeTitle(String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:title element
   * @param type The text type
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeTitle(Text.Type type, String value,Map<QName,String> attributes);

  /**
   * Write an atom:subtitle element
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeSubtitle(String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:subtitle element
   * @param type The text type
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeSubtitle(Text.Type type, String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:summary element
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeSummary(String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:summary element
   * @param type The text type
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeSummary(Text.Type type, String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:rights element
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeRights(String value,Map<QName,String> attributes);
  
  /**
   * Write an atom:rights element
   * @param type The text type
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeRights(Text.Type type, String value,Map<QName,String> attributes);
  
  
  /**
   * Write a person element
   * @param qname the element qname
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  void writePerson(QName qname, String name, String email, String uri);

  /**
   * Start a person element
   * @param qname The element qname
   */  
  void startPerson(QName qname);
  
  /**
   * Write a person name
   * @param name The person name
   */
  void writePersonName(String name);

  /**
   * Write a person email
   * @param email The person email
   */
  void writePersonEmail(String email);

  /**
   * Write a person uri
   * @param uri The person uri
   */
  void writePersonUri(String uri);
  
  /**
   * Write an atom:author element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  void writeAuthor(String name, String email, String uri);
  
  /**
   * Write an atom:author element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   * @param attribute ExtensionAttributes
   */
  void writeAuthor(String name, String email, String uri, Map<QName,String> attributes);
  
  /**
   * Start an atom:author element
   */
  void startAuthor();
  
  /**
   * Start an atom:author element
   * @params attributes ExtensionAttributes
   */
  void startAuthor(Map<QName,String> attributes);
  
  /**
   * End the atom:author element
   */
  void endAuthor();
  
  /**
   * Write an atom:contributor element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   */
  void writeContributor(String name, String email, String uri);
  
  /**
   * Write an atom:contributor element
   * @param name The person name
   * @param email The person email
   * @param uri The person uri
   * @param attributes Extension attributes
   */
  void writeContributor(String name, String email, String uri, Map<QName,String> attributes);
  
  /**
   * Start an atom:contributor element
   */
  void startContributor();
  
  /**
   * Start an atom:contributor element
   * @param attributes Extension attributes
   */
  void startContributor(Map<QName,String> attributes);
  
  /**
   * End an atom:contributor element
   */
  void endContributor();
  
  /**
   * Write an atom:generator element
   * @param version The version value
   * @param uri The uri value
   * @param value The text value
   */
  void writeGenerator(String version, String uri, String value);

  /**
   * Write an atom:generator element
   * @param version The version value
   * @param uri The uri value
   * @param value The text value
   * @param attributes Extension attributes
   */
  void writeGenerator(String version, String uri, String value, Map<QName,String> attributes);

  /**
   * Start an atom:generator element
   * @param version The version value
   * @param uri The uri value
   */
  void startGenerator(String version, String uri);
  
  /**
   * Start an atom:generator element
   * @param version The version value
   * @param uri The uri value
   * @param attributes Extension attributes
   */
  void startGenerator(String version, String uri, Map<QName,String> attributes);
  
  /**
   * End the atom:generator element
   */
  void endGenerator();
  
  /**
   * Write an XML comment
   */
  void writeComment(String value);
  
  /**
   * Write an XML Processing Instruction
   */
  void writePI(String value);

  /**
   * Write an XML Processing Instruction
   */
  void writePI(String value, String target);
  
  
  /**
   * Start an app:service element
   */
  void startService();
  
  /**
   * Start an app:service element
   * @param attributes Extension attributes
   */
  void startService(Map<QName,String> attributes);
  
  /**
   * End an app:service element
   */
  void endService();
  
  /**
   * Start an app:workspace element
   */
  void startWorkspace();
  
  /**
   * Start an app:workspace element
   * @param attributes Extension attributes
   */
  void startWorkspace(Map<QName,String> attributes);
  
  /**
   * End an app:workspace element
   */
  void endWorkspace();
  
  /**
   * Start an app:collection element
   * @param href The href value
   */
  void startCollection(String href);
  
  /**
   * Start an app:collection element
   * @param href The href value
   * @param attributes Extension attributes
   */
  void startCollection(String href, Map<QName,String> attributes);
  
  /**
   * End an app:collection element
   */
  void endCollection();
  
  /**
   * Writes app:accept elements
   * @param accepts accept element values
   */
  void writeAccepts(String... accepts);
  
  /**
   * Start an app:categories element
   */
  void startCategories();
  
  /**
   * Start an app:categories element
   * @param fixed True if the app:categories element is fixed
   */
  void startCategories(boolean fixed);
  
  /**
   * Start an app:categories element
   * @param fixed True if the app:categories element is fixed
   * @param scheme The scheme value
   */
  void startCategories(boolean fixed, String scheme);
  
  /**
  * Start an app:categories element
  * @param attributes Extension attributes
  */
  void startCategories(Map<QName,String> attributes);
  
  /**
  * Start an app:categories element
  * @param fixed True if the app:categories element is fixed
  * @param attributes Extension attributes
  */
  void startCategories(boolean fixed, Map<QName,String> attributes);
  
  /**
   * Start an app:categories element
   * @param fixed True if the app:categories element is fixed
   * @param scheme The scheme value
   * @param attributes Extension attributes
   */
  void startCategories(boolean fixed, String scheme, Map<QName,String> attributes);
  
  /**
   * End the app:categories element
   */
  void endCategories();

  /**
   * Start the app:control element
   */
  void startControl();
  
  /**
   * Start the app:control element
   * @param attributes Extension attributes
   */
  void startControl(Map<QName,String> attributes);
  
  /**
   * End the app:control element
   */
  void endControl();
  
  /**
   * Write an app:draft element
   * @param draft true if app:draft=yes
   */
  void writeDraft(boolean draft);
  
}

