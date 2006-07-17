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

import javax.activation.MimeType;
import javax.xml.namespace.QName;

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
 * The Factory interface is the primary means by which Feed Object Model
 * instances are built.  Factories are specific to parser implementations.
 * Users will generally not have to know anything about the Factory 
 * implementation, which will be automatically selected based on the
 * Abdera configuration options.
 * 
 * <p>
 *   <code>Factory factory = Factory.INSTANCE;</code>
 * </p>
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
  
  Content newContent();
  
  Content newContent(Content.Type type);
  
  Content newContent(Content.Type type, Element parent);
  
  Content newContent(MimeType mediaType);
  
  Content newContent(MimeType mediaType, Element parent);
  
  DateTime newPublished();
  
  DateTime newPublished(Element parent);
  
  DateTime newUpdated();
  
  DateTime newUpdated(Element parent);
  
  DateTime newDateTime(QName qname, Element parent);
  
  Generator newDefaultGenerator();
  
  Generator newDefaultGenerator(Element parent);
  
  Generator newGenerator();
  
  Generator newGenerator(Element parent);
  
  IRI newID();
  
  IRI newID(Element parent);
  
  IRI newIcon();
  
  IRI newIcon(Element parent);

  IRI newLogo();
  
  IRI newLogo(Element parent);
  
  IRI newUri();
  
  IRI newUri(Element parent);
  
  IRI newIRIElement(QName qname, Element parent);
  
  Link newLink();
  
  Link newLink(Element parent);
  
  Person newAuthor();
  
  Person newAuthor(Element parent);
  
  Person newContributor();

  Person newContributor(Element parent);

  Person newPerson(QName qname, Element parent);
  
  Source newSource();
  
  Source newSource(Element parent);
  
  Text newText(QName qname, Text.Type type);
  
  Text newText(QName qname, Text.Type type, Element parent);
  
  Text newTitle();
  
  Text newTitle(Element parent);
  
  Text newTitle(Text.Type type);
  
  Text newTitle(Text.Type type, Element parent);
  
  Text newSubtitle();

  Text newSubtitle(Element parent);
  
  Text newSubtitle(Text.Type type);
  
  Text newSubtitle(Text.Type type, Element parent);
  
  Text newSummary();
  
  Text newSummary(Element parent);
  
  Text newSummary(Text.Type type);
  
  Text newSummary(Text.Type type, Element parent);
  
  Text newRights();
  
  Text newRights(Element parent);
  
  Text newRights(Text.Type type);
  
  Text newRights(Text.Type type, Element parent);
  
  Element newName();
  
  Element newName(Element parent);
  
  Element newEmail();
  
  Element newEmail(Element parent);
  
  Element newElement(QName qname);
  
  Element newElement(QName qname, Base parent);
  
  Element newExtensionElement(QName qname);
  
  Element newExtensionElement(QName qname, Base parent);
  
  Control newControl();
  
  Control newControl(Element parent);
  
  Div newDiv();
  
  Div newDiv(Base parent);

  /**
   * Registers an extension implementation class for this Factory instance only
   */
  <T extends Base>void registerExtension(QName qname, Class impl);
  
  /**
   * Registers an extension factory for this Factory instance only
   */
  void registerExtension(ExtensionFactory extensionFactory);
    
}
