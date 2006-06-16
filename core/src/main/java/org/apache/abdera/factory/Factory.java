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
package org.apache.abdera.factory;

import javax.activation.MimeTypeParseException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.InReplyTo;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.StringElement;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Total;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.ServiceUtil;


/**
 * @author James M Snell (jasnell@us.ibm.com)
 * 
 * The Factory interface is the primary means by which Feed Object Model
 * instances are built.  Factories are specific to parser implementations.
 * Users will generally not have to know anything about the Factory 
 * implementation, which will be automatically selected based on the
 * Abdera configuration options.
 * 
 * <code>Factory factory = Factory.INSTANCE;</code>
 */
public interface Factory {

  /**
   * The default Factory instance
   */
  public static final Factory INSTANCE = ServiceUtil.newFactoryInstance();
  
  Parser newParser();
  
  <T extends Element>Document<T> newDocument();
  
  Service newService();
  
  Service newService(Base parent);
  
  Workspace newWorkspace(Element parent);
  
  Collection newCollection(Element parent);
  
  Feed newFeed();
  
  Feed newFeed(Base parent);
  
  Entry newEntry();
  
  Entry newEntry(Base parent);
  
  Category newCategory(Element parent);
  
  Category newCategory(URI scheme, String term, String label, Element parent);
  
  Content newContent(Content.Type type, Element parent);
  
  Content newContent(Content.Type type, MimeType mediaType, Element parent);
  
  Content newTextContent(String value, Element parent);
  
  Content newHtmlContent(String value, Element parent);
  
  Content newXhtmlContent(Div value, Element parent);
  
  Content newXmlContent(MimeType mediaType, URI src, ExtensionElement element, Element parent);
  
  Content newMediaContent(MimeType mediaType, URI src, DataHandler dataHandler, Element parent);

  DateTime newPublished(Element parent);
  
  DateTime newPublished(AtomDate dateTime, Element parent);
  
  DateTime newPublished(Date date, Element parent);
  
  DateTime newPublished(String date, Element parent);
  
  DateTime newPublished(Calendar date, Element parent);
  
  DateTime newPublished(long date, Element parent);  
  
  DateTime newUpdated(Element parent);
  
  DateTime newUpdated(AtomDate dateTime, Element parent);
  
  DateTime newUpdated(Date date, Element parent);
  
  DateTime newUpdated(String date, Element parent);
  
  DateTime newUpdated(Calendar date, Element parent);
  
  DateTime newUpdated(long date, Element parent);
  
  DateTime newDateTime(QName qname, Element parent);
  
  DateTime newDateTime(QName qname, AtomDate dateTime, Element parent);
  
  DateTime newDateTime(QName qname, Date date, Element parent);
  
  DateTime newDateTime(QName qname, String date, Element parent);
  
  DateTime newDateTime(QName qname, Calendar date, Element parent);
  
  DateTime newDateTime(QName qname, long date, Element parent);
  
  Generator newDefaultGenerator(Element parent);
  
  Generator newGenerator(Element parent);
  
  Generator newGenerator(URI uri, String version, String value, Element parent);
  
  IRI newID(Element parent);
  
  IRI newID(String id, Element parent) throws URISyntaxException;
  
  IRI newID(URI id, Element parent);
  
  IRI newIcon(Element parent);

  IRI newIcon(URI uri, Element parent);
  
  IRI newIcon(String URI, Element parent) throws URISyntaxException;
  
  IRI newLogo(Element parent);
  
  IRI newLogo(URI uri, Element parent);
  
  IRI newLogo(String URI, Element parent) throws URISyntaxException;
  
  IRI newUri(Element parent);
  
  IRI newUri(URI uri, Element parent);
  
  IRI newUri(String URI, Element parent) throws URISyntaxException;
  
  IRI newIRIElement(QName qname, Element parent);
  
  IRI newIRIElement(QName qname, URI uri, Element parent);
  
  IRI newIRIElement(QName qname, String URI, Element parent) throws URISyntaxException;
  
  Link newLink(Element parent);
  
  Link newLink(String href, String rel, MimeType type, String title, String hreflang, long length, Element parent) throws URISyntaxException;
  
  Link newLink(URI href, String rel, MimeType type, String title, String hreflang, long length, Element parent);
  
  Person newAuthor(Element parent);
  
  Person newAuthor(String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newAuthor(String name, String email, URI uri, Element parent);  
  
  Person newContributor(Element parent);

  Person newContributor(String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newContributor(String name, String email, URI uri, Element parent);  

  Person newPerson(QName qname, Element parent);
  
  Person newPerson(QName qname, String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newPerson(QName qname, String name, String email, URI uri, Element parent);
  
  Source newSource(Element parent);
  
  Text newText(QName qname, Text.Type type, Element parent);
  
  Text newTextTitle(String value, Element parent);
  
  Text newHtmlTitle(String value, Element parent);
  
  Text newXhtmlTitle(Div value, Element parent);

  Text newTextSubtitle(String value, Element parent);
  
  Text newHtmlSubtitle(String value, Element parent);
  
  Text newXhtmlSubtitle(Div value, Element parent);
  
  Text newTextSummary(String value, Element parent);
  
  Text newHtmlSummary(String value, Element parent);
  
  Text newXhtmlSummary(Div value, Element parent);
  
  Text newTextRights(String value, Element parent);
  
  Text newHtmlRights(String value, Element parent);
  
  Text newXhtmlRights(Div value, Element parent);
  
  Text newTextText(QName qname, String value, Element parent);
  
  Text newHtmlText(QName qname, String value, Element parent);
  
  Text newXhtmlText(QName qname, Div value, Element parent);
  
  StringElement newName(Element parent);
  
  StringElement newName(String value, Element parent);
  
  StringElement newEmail(Element parent);
  
  StringElement newEmail(String value, Element parent);
  
  StringElement newStringElement(QName qname, Element parent);
  
  StringElement newStringElement(QName qname, String value, Element parent);

  ExtensionElement newExtensionElement(QName qname, Base parent);
  
  Control newControl(Element parent);
  
  Control newControl(boolean draft, Element parent);
  
  Div newDiv(Base parent);
  
  InReplyTo newInReplyTo(Element parent);
  
  InReplyTo newInReplyTo(URI ref, Element parent);
  
  InReplyTo newInReplyTo(String ref, Element parent) throws URISyntaxException;
  
  InReplyTo newInReplyTo(URI ref, URI source, URI href, MimeType type, Element parent);
  
  InReplyTo newInReplyTo(String ref, String source, String href, String type, Element parent) throws URISyntaxException, MimeTypeParseException;
  
  Total newTotal(Element parent);
  
  Total newTotal(int totalResponse, Element parent);
  
  void registerAsSimpleExtension(QName qname);
  
}
