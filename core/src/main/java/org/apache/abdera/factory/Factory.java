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
import org.apache.abdera.model.Categories;
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


/**
 * The Factory interface is the primary means by which Feed Object Model
 * instances are built.  Factories are specific to parser implementations.
 * Users will generally not have to know anything about the Factory 
 * implementation, which will be automatically selected based on the
 * Abdera configuration options.
 */
public interface Factory {

  /**
   * Create a new Parser instance.
   */  
  Parser newParser();

  /**
   * Create a new Document instance with a root Element of type T.
   */  
  <T extends Element>Document<T> newDocument();

  /**
   * Create a new Service element.
   */
  Service newService();

  /**
   * Create a new Service element as a child of the given Base.
   */
  Service newService(Base parent);

  /**
   * Create a new Workspace element.
   */
  Workspace newWorkspace();

  /**
   * Create a new Workspace element as a child of the given Element.
   */
  Workspace newWorkspace(Element parent);

  /**
   * Create a new Collection element.
   */
  Collection newCollection();

  /**
   * Create a new Collection element as a child of the given Element.
   */
  Collection newCollection(Element parent);

  /**
   * Create a new Feed element.
   */
  Feed newFeed();

  /**
   * Create a new Feed element as a child of the given Base.
   */
  Feed newFeed(Base parent);

  /**
   * Create a new Entry element.
   */
  Entry newEntry();

  /**
   * Create a new Entry element as a child of the given Base.
   */
  Entry newEntry(Base parent);

  /**
   * Create a new Category element.
   */
  Category newCategory();

  /**
   * Create a new Category element as a child of the given Element.
   */
  Category newCategory(Element parent);

  /**
   * Create a new Content element.
   */
  Content newContent();

  /**
   * Create a new Content element of the given Content.Type.
   */
  Content newContent(Content.Type type);

  /**
   * Create a new Content element of the given Content.Type as a child of the given Element.
   */
  Content newContent(Content.Type type, Element parent);

  /**
   * Create a new Content element of the given MediaType.
   */
  Content newContent(MimeType mediaType);

  /**
   * Create a new Content element of the given MediaType as a child of the given Element.
   */  
  Content newContent(MimeType mediaType, Element parent);

  /**
   * Create a new published element.
   */
  DateTime newPublished();

  /**
   * Create a new published element as a child of the given Element.
   */
  DateTime newPublished(Element parent);

  /**
   * Create a new updated element.
   */
  DateTime newUpdated();

  /**
   * create a new updated element as a child of the given Element.
   */
  DateTime newUpdated(Element parent);

  /**
   * Create a new app:modified element
   */
  DateTime newModified();
  
  /**
   * create a new app:modified element as a child of the given Element
   */
  DateTime newModified(Element parent);
  
  /**
   * Create a new DateTime element with the given QName as a child of the given Element.
   */
  DateTime newDateTime(QName qname, Element parent);

  /**
   * Create a new Generator with Abdera's default name and version.
   */
  Generator newDefaultGenerator();

  /**
   * Create a new Generator with Abdera's default name and version as a child of the given Element.
   */
  Generator newDefaultGenerator(Element parent);

  /**
   * Create a new Generator element.
   */
  Generator newGenerator();

  /**
   * Create a new Generator element as a child of the given Element.
   */
  Generator newGenerator(Element parent);

  /**
   * Create a new id element.
   */
  IRI newID();

  /**
   * Create a new id element as a child of the given Element.
   */
  IRI newID(Element parent);

  /**
   * Create a new icon element.
   */
  IRI newIcon();

  /**
   * Create a new icon element as a child of the given Element.
   */
  IRI newIcon(Element parent);

  /**
   * Create a new logo element.
   */
  IRI newLogo();

  /**
   * Create a new logo element as a child of the given Element.
   */
  IRI newLogo(Element parent);

  /**
   * Create a new uri element.
   */
  IRI newUri();

  /**
   * Create a new uri element as a child of the given Element.
   */
  IRI newUri(Element parent);

  /**
   * Create a new IRI element with the given QName as a child of the given Element.
   */
  IRI newIRIElement(QName qname, Element parent);

  /**
   * Create a new Link element.
   */
  Link newLink();

  /**
   * Create a new Link element as a child of the given Element.
   */
  Link newLink(Element parent);

  /**
   * Create a new author element.
   */
  Person newAuthor();

  /**
   * Create a new author element as a child of the given Element.
   */
  Person newAuthor(Element parent);

  /**
   * Create a new contributor element.
   */
  Person newContributor();

  /**
   * Create a new contributor element as a child of the given Element.
   */
  Person newContributor(Element parent);

  /**
   * Create a new Person element with the given QName as a child of the given Element.
   */
  Person newPerson(QName qname, Element parent);

  /**
   * Create a new Source element.
   */
  Source newSource();

  /**
   * Create a new Source element as a child of the given Element.
   */
  Source newSource(Element parent);

  /**
   * Create a new Text element with the given QName and Text.Type.
   */
  Text newText(QName qname, Text.Type type);

  /**
   * Create a new Text element with the given QName and Text.Type as a child of the given Element.
   */
  Text newText(QName qname, Text.Type type, Element parent);

  /**
   * Create a new title element.
   */
  Text newTitle();

  /**
   * Create a new title element as a child of the given Element.
   */
  Text newTitle(Element parent);

  /**
   * Create a new title element with the given Text.Type.
   */
  Text newTitle(Text.Type type);

  /**
   * Create a new title element with the given Text.Type as a child of the given Element.
   */
  Text newTitle(Text.Type type, Element parent);

  /**
   * Create a new subtitle element.
   */
  Text newSubtitle();

  /**
   * Create a new subtitle element as a child of the given Element.
   */
  Text newSubtitle(Element parent);

  /**
   * Create a new subtitle element with the given Text.Type.
   */
  Text newSubtitle(Text.Type type);

  /**
   * Create a new subtitle element with the given Text.Type as a child of the given Element.
   */
  Text newSubtitle(Text.Type type, Element parent);

  /**
   * Create a new summary element.
   */
  Text newSummary();

  /**
   * Create a new summary element as a child of the given Element.
   */
  Text newSummary(Element parent);

  /**
   * Create a new summary element with the given Text.Type.
   */
  Text newSummary(Text.Type type);

  /**
   * Create a new summary element with the given Text.Type as a child of the given Element.
   */
  Text newSummary(Text.Type type, Element parent);

  /**
   * Create a new rights element.
   */
  Text newRights();

  /**
   * Create a new rights element as a child of the given Element.
   */
  Text newRights(Element parent);

  /**
   * Create a new rights element with the given Text.Type.
   */
  Text newRights(Text.Type type);

  /**
   * Create a new rights element with the given Text.Type as a child of the given Element.
   */
  Text newRights(Text.Type type, Element parent);

  /**
   * Create a new name element.
   */
  Element newName();

  /**
   * Create a new name element as a child of the given Element.
   */
  Element newName(Element parent);

  /**
   * Create a new email element.
   */
  Element newEmail();

  /**
   * Create a new email element as a child of the given Element.
   */
  Element newEmail(Element parent);

  /**
   * Create a new Element with the given QName.
   */
  Element newElement(QName qname);

  /**
   * Create a new Element with the given QName as a child of the given Base.
   */
  Element newElement(QName qname, Base parent);

  /**
   * Create a new extension element with the given QName.
   */
  Element newExtensionElement(QName qname);

  /**
   * Create a new extension element with the given QName as a child of the given Base.
   */
  Element newExtensionElement(QName qname, Base parent);

  /**
   * Create a new Control element.
   */
  Control newControl();

  /**
   * Create a new Control element as a child of the given Element.
   */
  Control newControl(Element parent);
  
  /**
   * Create a new Div element.
   */
  Div newDiv();

  /**
   * Create a new Div element as a child of the given Base.
   */
  Div newDiv(Base parent);

  /**
   * Registers an extension implementation class for this Factory instance only
   */
  <T extends Base>void registerExtension(QName qname, Class impl);
  
  /**
   * Registers an extension factory for this Factory instance only
   */
  void registerExtension(ExtensionFactory extensionFactory);
    
  /**
   * Create a new Categories element 
   */
  Categories newCategories();
  
  /**
   * Create a new Categories element as a child of the given Element
   */
  Categories newCategories(Base parent);
  
  /**
   * Generate a new random UUID URI 
   */
  String newUuidUri();
}
