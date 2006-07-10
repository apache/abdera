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

import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
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
  
  Workspace newWorkspace();
  
  Workspace newWorkspace(Element parent);
  
  Collection newCollection();
  
  Collection newCollection(Element parent);
  
  Feed newFeed();
  
  Feed newFeed(Base parent);
  
  Entry newEntry();
  
  Entry newEntry(Base parent);

  Category newCategory();
  
  Category newCategory(Element parent);
  
  Category newCategory(String term);
  
  Category newCategory(String term, Element parent);
  
  Category newCategory(String term, URI scheme, String label);
  
  Category newCategory(String term, URI scheme, String label, Element parent);

  Content newContent();
  
  Content newContent(String value);
  
  Content newContent(Content.Type type);
  
  Content newContent(Content.Type type, Element parent);
  
  Content newContent(String value, Content.Type type);
  
  Content newContent(Element value, Content.Type type);

  Content newContent(String value, Content.Type type, Element parent);
  
  Content newContent(Element value, Content.Type type, Element parent);  
  
  Content newContent(MimeType mediaType);
  
  Content newContent(MimeType mediaType, Element parent);
  
  Content newContent(URI src, MimeType mediaType);
  
  Content newContent(URI src, MimeType mediaType, Element parent);
  
  Content newContent(Element element, MimeType mediaType);
  
  Content newContent(Element element, MimeType mediaType, Element parent);
  
  Content newContent(DataHandler dataHandler, MimeType mediatype);
  
  Content newContent(DataHandler dataHandler, MimeType mediaType, Element parent);

  Content newContent(String value, MimeType mediatype);
  
  Content newContent(String value, MimeType mediaType, Element parent);
  
  DateTime newPublished();
  
  DateTime newPublished(AtomDate dateTime);
  
  DateTime newPublished(Date date);
  
  DateTime newPublished(String date);
  
  DateTime newPublished(Calendar date);
  
  DateTime newPublished(long date);  
  
  DateTime newPublished(Element parent);
  
  DateTime newPublished(AtomDate dateTime, Element parent);
  
  DateTime newPublished(Date date, Element parent);
  
  DateTime newPublished(String date, Element parent);
  
  DateTime newPublished(Calendar date, Element parent);
  
  DateTime newPublished(long date, Element parent);  
  
  DateTime newUpdated();
  
  DateTime newUpdated(AtomDate dateTime);
  
  DateTime newUpdated(Date date);
  
  DateTime newUpdated(String date);
  
  DateTime newUpdated(Calendar date);
  
  DateTime newUpdated(long date);
  
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
  
  Generator newDefaultGenerator();
  
  Generator newDefaultGenerator(Element parent);
  
  Generator newGenerator();
  
  Generator newGenerator(Element parent);
  
  Generator newGenerator(URI uri, String version, String value);
  
  Generator newGenerator(URI uri, String version, String value, Element parent);
  
  IRI newID();
  
  IRI newID(String id) throws URISyntaxException;
  
  IRI newID(URI id);
  
  IRI newID(Element parent);
  
  IRI newID(String id, Element parent) throws URISyntaxException;
  
  IRI newID(URI id, Element parent);

  IRI newIcon();
  
  IRI newIcon(String uri) throws URISyntaxException;
  
  IRI newIcon(URI uri);
  
  IRI newIcon(Element parent);

  IRI newIcon(URI uri, Element parent);
  
  IRI newIcon(String URI, Element parent) throws URISyntaxException;
  
  IRI newLogo();
  
  IRI newLogo(String uri) throws URISyntaxException;
  
  IRI newLogo(URI uri);
  
  IRI newLogo(Element parent);
  
  IRI newLogo(URI uri, Element parent);
  
  IRI newLogo(String URI, Element parent) throws URISyntaxException;

  IRI newUri();
  
  IRI newUri(URI uri);
  
  IRI newUri(String uri) throws URISyntaxException;
  
  IRI newUri(Element parent);
  
  IRI newUri(URI uri, Element parent);
  
  IRI newUri(String uri, Element parent) throws URISyntaxException;
  
  IRI newIRIElement(QName qname, Element parent);
  
  IRI newIRIElement(QName qname, URI uri, Element parent);
  
  IRI newIRIElement(QName qname, String URI, Element parent) throws URISyntaxException;

  Link newLink();
  
  Link newLink(String href, String rel, MimeType type, String title, String hreflang, long length) throws URISyntaxException;
  
  Link newLink(URI href, String rel, MimeType type, String title, String hreflang, long length);
  
  Link newLink(Element parent);
  
  Link newLink(String href, String rel, MimeType type, String title, String hreflang, long length, Element parent) throws URISyntaxException;
  
  Link newLink(URI href, String rel, MimeType type, String title, String hreflang, long length, Element parent);

  Person newAuthor();
  
  Person newAuthor(String name, String email, String uri) throws URISyntaxException;
  
  Person newAuthor(String name, String email, URI uri);
  
  Person newAuthor(Element parent);
  
  Person newAuthor(String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newAuthor(String name, String email, URI uri, Element parent);  

  Person newContributor();

  Person newContributor(String name, String email, String uri) throws URISyntaxException;
  
  Person newContributor(String name, String email, URI uri);  
  
  Person newContributor(Element parent);

  Person newContributor(String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newContributor(String name, String email, URI uri, Element parent);  

  Person newPerson(QName qname, Element parent);
  
  Person newPerson(QName qname, String name, String email, String uri, Element parent) throws URISyntaxException;
  
  Person newPerson(QName qname, String name, String email, URI uri, Element parent);
  
  Source newSource();
  
  Source newSource(Element parent);
  
  Text newText(QName qname, Text.Type type);
  
  Text newText(QName qname, Text.Type type, Element parent);
  
  Text newTitle();
  
  Text newTitle(String value);
  
  Text newTitle(Element parent);
  
  Text newTitle(Text.Type type);
  
  Text newTitle(Text.Type type, Element parent);
  
  Text newTitle(String value, Text.Type type);
  
  Text newTitle(String value, Text.Type type, Element parent);
  
  Text newTitle(Div value);
  
  Text newTitle(Div value, Element parent);

  Text newSubtitle();

  Text newSubtitle(String value);
  
  Text newSubtitle(Element parent);
  
  Text newSubtitle(Text.Type type);
  
  Text newSubtitle(Text.Type type, Element parent);
  
  Text newSubtitle(String value, Text.Type type);
  
  Text newSubtitle(String value, Text.Type type, Element parent);
  
  Text newSubtitle(Div value);
  
  Text newSubtitle(Div value, Element parent);
  
  Text newSummary();
  
  Text newSummary(String value);
  
  Text newSummary(Element parent);
  
  Text newSummary(Text.Type type);
  
  Text newSummary(Text.Type type, Element parent);
  
  Text newSummary(String value, Text.Type type);
  
  Text newSummary(String value, Text.Type type, Element parent);
  
  Text newSummary(Div value);
  
  Text newSummary(Div value, Element parent);

  Text newRights();
  
  Text newRights(String value);
  
  Text newRights(Element parent);
  
  Text newRights(Text.Type type);
  
  Text newRights(Text.Type type, Element parent);
  
  Text newRights(String value, Text.Type type);
  
  Text newRights(String value, Text.Type type, Element parent);
  
  Text newRights(Div value);
  
  Text newRights(Div value, Element parent);

  Text newText(QName qname, String value, Text.Type type);
  
  Text newText(QName qname, String value, Text.Type type, Element parent);
  
  Text newText(QName qname, Div value);
  
  Text newText(QName qname, Div value, Element parent);

  Element newName();
  
  Element newName(String value);
  
  Element newName(Element parent);
  
  Element newName(String value, Element parent);
  
  Element newEmail();
  
  Element newEmail(String value);
  
  Element newEmail(Element parent);
  
  Element newEmail(String value, Element parent);
  
  Element newElement(QName qname);
  
  Element newElement(QName qname, String value);
  
  Element newElement(QName qname, Base parent);
  
  Element newElement(QName qname, String value, Base parent);

  Element newExtensionElement(QName qname);
  
  Element newExtensionElement(QName qname, Base parent);
  
  Control newControl();
  
  Control newControl(boolean draft);
  
  Control newControl(Element parent);
  
  Control newControl(boolean draft, Element parent);
  
  Div newDiv();
  
  Div newDiv(Base parent);

  <T extends Base>void registerExtension(QName qname, Class impl);
  
  <T extends Base>void registerAlternative(Class<T> base, Class<? extends T> alternative);
  
}
