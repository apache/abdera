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

import org.apache.abdera.Abdera;
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
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;

/**
 * The Factory interface is the primary means by which Feed Object Model instances are built. Factories are specific to
 * parser implementations. Users will generally not have to know anything about the Factory implementation, which will
 * be automatically selected based on the Abdera configuration options.
 */
public interface Factory {

    /**
     * Create a new Parser instance.
     * 
     * @return A new instance of the Parser associated with this Factory
     */
    Parser newParser();

    /**
     * Create a new Document instance with a root Element of type T.
     * 
     * @return A new instance of a Document
     */
    <T extends Element> Document<T> newDocument();

    /**
     * Create a new Service element.
     * 
     * @return A newly created Service element
     */
    Service newService();

    /**
     * Create a new Service element as a child of the given Base.
     * 
     * @param parent The element or document to which the new Service should be added as a child
     * @return A newly created Service element
     */
    Service newService(Base parent);

    /**
     * Create a new Workspace element.
     * 
     * @return A newly created Workspace element
     */
    Workspace newWorkspace();

    /**
     * Create a new Workspace element as a child of the given Element.
     * 
     * @param parent The element to which the new Workspace should be added as a child
     * @return A newly created Workspace element
     */
    Workspace newWorkspace(Element parent);

    /**
     * Create a new Collection element.
     * 
     * @return A newly created Collection element
     */
    Collection newCollection();

    /**
     * Create a new Collection element as a child of the given Element.
     * 
     * @param parent The element to which the new Collection should be added as a child
     * @return A newly created Collection element
     */
    Collection newCollection(Element parent);

    /**
     * Create a new Feed element. A new Document containing the Feed will be created automatically
     * 
     * @return A newly created Feed element.
     */
    Feed newFeed();

    /**
     * Create a new Feed element as a child of the given Base.
     * 
     * @param parent The element or document to which the new Feed should be added as a child
     * @return A newly created Feed element
     */
    Feed newFeed(Base parent);

    /**
     * Create a new Entry element. A new Document containing the Entry will be created automatically
     * 
     * @return A newly created Entry element
     */
    Entry newEntry();

    /**
     * Create a new Entry element as a child of the given Base.
     * 
     * @param parent The element or document to which the new Entry should be added as a child
     * @return A newly created Entry element
     */
    Entry newEntry(Base parent);

    /**
     * Create a new Category element.
     * 
     * @return A newly created Category element
     */
    Category newCategory();

    /**
     * Create a new Category element as a child of the given Element.
     * 
     * @param parent The element to which the new Category should be added as a child
     * @return A newly created Category element
     */
    Category newCategory(Element parent);

    /**
     * Create a new Content element.
     * 
     * @return A newly created Content element with type="text"
     */
    Content newContent();

    /**
     * Create a new Content element of the given Content.Type.
     * 
     * @param type The Content.Type for the newly created Content element.
     * @return A newly created Content element using the specified type
     */
    Content newContent(Content.Type type);

    /**
     * Create a new Content element of the given Content.Type as a child of the given Element.
     * 
     * @param type The Content.Type for the newly created Content element.
     * @param parent The element to which the new Content should be added as a child
     * @return A newly created Content element using the specified type
     */
    Content newContent(Content.Type type, Element parent);

    /**
     * Create a new Content element of the given MediaType.
     * 
     * @param mediaType The MIME media type to be specified by the type attribute
     * @return A newly created Content element using the specified MIME type
     */
    Content newContent(MimeType mediaType);

    /**
     * Create a new Content element of the given MediaType as a child of the given Element.
     * 
     * @param mediaType The MIME media type to be specified by the type attribute
     * @param parent The element to which the new Content should be added as a child
     * @return A newly created Content element using the specified mediatype.
     */
    Content newContent(MimeType mediaType, Element parent);

    /**
     * Create a new published element.
     * 
     * @return A newly created atom:published element
     */
    DateTime newPublished();

    /**
     * Create a new published element as a child of the given Element.
     * 
     * @param parent The element to which the new Published element should be added as a child
     * @return A newly created atom:published element
     */
    DateTime newPublished(Element parent);

    /**
     * Create a new updated element.
     * 
     * @return A newly created atom:updated element
     */
    DateTime newUpdated();

    /**
     * create a new updated element as a child of the given Element.
     * 
     * @param parent The element to which the new Updated element should be added as a child
     * @return A newly created atom:updated element
     */
    DateTime newUpdated(Element parent);

    /**
     * Create a new app:edited element. The app:edited element is defined by the Atom Publishing Protocol specification
     * for use in atom:entry elements created and edited using that protocol. The element should only ever appear as a
     * child of atom:entry.
     * 
     * @return A newly created app:edited element
     */
    DateTime newEdited();

    /**
     * Create a new app:edited element. The app:edited element is defined by the Atom Publishing Protocol specification
     * for use in atom:entry elements created and edited using that protocol. The element should only ever appear as a
     * child of atom:entry.
     * 
     * @param parent The element to which the new Edited element should be added as a child
     * @return A newly created app:edited element
     */
    DateTime newEdited(Element parent);

    /**
     * Create a new DateTime element with the given QName as a child of the given Element. RFC4287 provides the abstract
     * Atom Date Construct as a reusable component. Any extension element whose value is a Date/Time SHOULD reuse this
     * construct to maintain consistency with the base specification.
     * 
     * @param qname The XML QName of the Atom Date element to create
     * @param parent The element to which the new Atom Date element should be added as a child
     * @return The newly created Atom Date Construct element
     */
    DateTime newDateTime(QName qname, Element parent);

    /**
     * Create a new Generator with Abdera's default name and version.
     * 
     * @return A newly created and pre-populated atom:generator element
     */
    Generator newDefaultGenerator();

    /**
     * Create a new Generator using Abdera's default name and version as a child of the given Element.
     * 
     * @param parent The element to which the new Generator element should be added as a child
     * @return A newly created and pre-populated atom:generator element
     */
    Generator newDefaultGenerator(Element parent);

    /**
     * Create a new Generator element.
     * 
     * @return A newly created atom:generator element
     */
    Generator newGenerator();

    /**
     * Create a new Generator element as a child of the given Element.
     * 
     * @param parent The element to which the new Generator element should be added as a child
     * @return A newly creatd atom:generator element
     */
    Generator newGenerator(Element parent);

    /**
     * Create a new id element.
     * 
     * @return A newly created atom:id element
     */
    IRIElement newID();

    /**
     * Create a new id element as a child of the given Element.
     * 
     * @param parent The element to which the new ID element should be added as a child
     * @return A newly created atom:id element
     */
    IRIElement newID(Element parent);

    /**
     * Create a new icon element.
     * 
     * @return A newly created atom:icon element
     */
    IRIElement newIcon();

    /**
     * Create a new icon element as a child of the given Element.
     * 
     * @param parent The element to which the new Icon element should be added as a child
     * @return A newly created atom:icon element
     */
    IRIElement newIcon(Element parent);

    /**
     * Create a new logo element.
     * 
     * @return A newly created atom:logo element
     */
    IRIElement newLogo();

    /**
     * Create a new logo element as a child of the given Element.
     * 
     * @param parent The element to which the new Logo element should be added as a child
     * @return A newly created atom:logo element
     */
    IRIElement newLogo(Element parent);

    /**
     * Create a new uri element.
     * 
     * @return A newly created atom:uri element
     */
    IRIElement newUri();

    /**
     * Create a new uri element as a child of the given Element.
     * 
     * @param parent The element to which the new URI element should be added as a child
     * @return A newly created atom:uri element
     */
    IRIElement newUri(Element parent);

    /**
     * Create a new IRI element with the given QName as a child of the given Element.
     * 
     * @param qname The XML QName of the new IRI element
     * @param parent The element to which the new generic IRI element should be added as a child
     * @return A newly created element whose text value can be an IRI
     */
    IRIElement newIRIElement(QName qname, Element parent);

    /**
     * Create a new Link element.
     * 
     * @return A newly created atom:link element
     */
    Link newLink();

    /**
     * Create a new Link element as a child of the given Element.
     * 
     * @param parent The element to which the new Link element should be added as a child
     * @return A newly created atom:uri element
     */
    Link newLink(Element parent);

    /**
     * Create a new author element.
     * 
     * @return A newly created atom:author element
     */
    Person newAuthor();

    /**
     * Create a new author element as a child of the given Element.
     * 
     * @param parent The element to which the new Author element should be added as a child
     * @return A newly created atom:author element
     */
    Person newAuthor(Element parent);

    /**
     * Create a new contributor element.
     * 
     * @return A newly created atom:contributor element
     */
    Person newContributor();

    /**
     * Create a new contributor element as a child of the given Element.
     * 
     * @param parent The element to which the new Contributor element should be added as a child
     * @return A newly created atom:contributor element
     */
    Person newContributor(Element parent);

    /**
     * Create a new Person element with the given QName as a child of the given Element. RFC4287 provides the abstract
     * Atom Person Construct to represent people and other entities within an Atom Document. Extensions that wish to
     * represent people SHOULD reuse this construct.
     * 
     * @param qname The XML QName of the newly created Person element
     * @param parent The element to which the new Person element should be added as a child
     * @return A newly created Atom Person Construct element
     */
    Person newPerson(QName qname, Element parent);

    /**
     * Create a new Source element.
     * 
     * @return A newly created atom:source element
     */
    Source newSource();

    /**
     * Create a new Source element as a child of the given Element.
     * 
     * @param parent The element to which the new Source element should be added as a child
     * @return A newly created atom:source element
     */
    Source newSource(Element parent);

    /**
     * Create a new Text element with the given QName and Text.Type. RFC4287 provides the abstract Text Construct to
     * represent simple Text, HTML or XHTML within a document. This construct is used by Atom core elements like
     * atom:title, atom:summary, atom:rights, atom:subtitle, etc and SHOULD be reused by extensions that need a way of
     * embedding text in a document.
     * 
     * @param qname The XML QName of the Text element to create
     * @param type The type of text (plain text, HTML or XHTML)
     * @return A newly created Atom Text Construct element
     */
    Text newText(QName qname, Text.Type type);

    /**
     * Create a new Text element with the given QName and Text.Type as a child of the given Element.
     * 
     * @param qname The XML QName of the Text element to create
     * @param type The type of text (plain text, HTML or XHTML)
     * @param parent The element to which the new Updated element should be added as a child
     * @return A newly created Atom Text Construct element
     */
    Text newText(QName qname, Text.Type type, Element parent);

    /**
     * Create a new title element.
     * 
     * @return A newly created atom:title element
     */
    Text newTitle();

    /**
     * Create a new title element as a child of the given Element.
     * 
     * @param parent The element to which the new Title element should be added as a child
     * @return A newly created atom:title element
     */
    Text newTitle(Element parent);

    /**
     * Create a new title element with the given Text.Type.
     * 
     * @param type The type of text used in the title (plain text, HTML, XHTML)
     * @return A newly created atom:title element
     */
    Text newTitle(Text.Type type);

    /**
     * Create a new title element with the given Text.Type as a child of the given Element.
     * 
     * @param type The type of text used in the title (plain text, HTML, XHTML)
     * @param parent The element to which the new Updated element should be added as a child
     * @return A newly created atom:title element
     */
    Text newTitle(Text.Type type, Element parent);

    /**
     * Create a new subtitle element.
     * 
     * @return A newly created atom:subtitle element
     */
    Text newSubtitle();

    /**
     * Create a new subtitle element as a child of the given Element.
     * 
     * @param parent The element to which the new Subtitle element should be added as a child
     * @return A newly created atom:subtitle element
     */
    Text newSubtitle(Element parent);

    /**
     * Create a new subtitle element with the given Text.Type.
     * 
     * @param type The type of text used in the subtitle (plain text, HTML, XHTML)
     * @return A newly created atom:subtitle element
     */
    Text newSubtitle(Text.Type type);

    /**
     * Create a new subtitle element with the given Text.Type as a child of the given Element.
     * 
     * @param type The type of text used i the subtitle (plain text, HTML, XHTML)
     * @param parent The element to which the new Subtitle element should be added as a child
     * @return A newly created atom:subtitle element
     */
    Text newSubtitle(Text.Type type, Element parent);

    /**
     * Create a new summary element.
     * 
     * @return A newly created atom:summary element
     */
    Text newSummary();

    /**
     * Create a new summary element as a child of the given Element.
     * 
     * @param parent The element to which the new Summary element should be added as a child
     * @return A newly created atom:summary element
     */
    Text newSummary(Element parent);

    /**
     * Create a new summary element with the given Text.Type.
     * 
     * @param type The type of text used in the summary (plain text, HTML, XHTML)
     * @return A newly created atom:summary element
     */
    Text newSummary(Text.Type type);

    /**
     * Create a new summary element with the given Text.Type as a child of the given Element.
     * 
     * @param type The type of text used in the summary (plain text, HTML, XHTML)
     * @param parent The element to which the new Summary element should be added as a child
     * @return A newly created atom:summary element
     */
    Text newSummary(Text.Type type, Element parent);

    /**
     * Create a new rights element.
     * 
     * @return A newly created atom:rights element
     */
    Text newRights();

    /**
     * Create a new rights element as a child of the given Element.
     * 
     * @param parent The element to which the new Rights element should be added as a child
     * @return A newly created atom:rights element
     */
    Text newRights(Element parent);

    /**
     * Create a new rights element with the given Text.Type.
     * 
     * @param type The type of text used in the Rights (plain text, HTML, XHTML)
     * @return A newly created atom:rights element
     */
    Text newRights(Text.Type type);

    /**
     * Create a new rights element with the given Text.Type as a child of the given Element.
     * 
     * @param type The type of text used in the Rights (plain text, HTML, XHTML)
     * @param parent The element to which the new Rights element should be added as a child
     * @return A newly created atom:rights element
     */
    Text newRights(Text.Type type, Element parent);

    /**
     * Create a new name element.
     * 
     * @return A newly created atom:name element
     */
    Element newName();

    /**
     * Create a new name element as a child of the given Element.
     * 
     * @param parent The element to which the new Name element should be added as a child
     * @return A newly created atom:summary element
     */
    Element newName(Element parent);

    /**
     * Create a new email element.
     * 
     * @return A newly created atom:email element
     */
    Element newEmail();

    /**
     * Create a new email element as a child of the given Element.
     * 
     * @param parent The element to which the new Email element should be added as a child
     * @return A newly created atom:email element
     */
    Element newEmail(Element parent);

    /**
     * Create a new Element with the given QName.
     * 
     * @return A newly created element
     */
    <T extends Element> T newElement(QName qname);

    /**
     * Create a new Element with the given QName as a child of the given Base.
     * 
     * @param qname The XML QName of the element to create
     * @param parent The element or document to which the new element should be added as a child
     * @return A newly created element
     */
    <T extends Element> T newElement(QName qname, Base parent);

    /**
     * Create a new extension element with the given QName.
     * 
     * @param qname The XML QName of the element to create
     * @return A newly created element
     */
    <T extends Element> T newExtensionElement(QName qname);

    /**
     * Create a new extension element with the given QName as a child of the given Base.
     * 
     * @param qname The XML QName of the element to create
     * @param parent The element or document to which the new element should be added as a child
     * @return A newly created element
     */
    <T extends Element> T newExtensionElement(QName qname, Base parent);

    /**
     * Create a new Control element. The app:control element is introduced by the Atom Publishing Protocol as a means of
     * allowing publishing clients to provide metadata to a server affecting the way an entry is published. The control
     * element SHOULD only ever appear as a child of the atom:entry and MUST only ever appear once.
     * 
     * @return A newly app:control element
     */
    Control newControl();

    /**
     * Create a new Control element as a child of the given Element.
     * 
     * @param parent The element to which the new Control element should be added as a child
     * @return A newly app:control element
     */
    Control newControl(Element parent);

    /**
     * Create a new Div element.
     * 
     * @return A newly xhtml:div element
     */
    Div newDiv();

    /**
     * Create a new Div element as a child of the given Base.
     * 
     * @param parent The element or document to which the new XHTML div element should be added as a child
     * @return A newly xhtml:div element
     */
    Div newDiv(Base parent);

    /**
     * Registers an extension factory for this Factory instance only
     * 
     * @param extensionFactory An ExtensionFactory instance
     */
    Factory registerExtension(ExtensionFactory extensionFactory);

    /**
     * Create a new Categories element. The app:categories element is introduced by the Atom Publishing Protocol as a
     * means of providing a listing of atom:category's that can be used by entries in a collection.
     * 
     * @return A newly app:categories element
     */
    Categories newCategories();

    /**
     * Create a new Categories element. The app:categories element is introduced by the Atom Publishing Protocol as a
     * means of providing a listing of atom:category's that can be used by entries in a collection.
     * 
     * @param parent The element or document to which the new Categories element should be added as a child
     * @return A newly app:categories element
     */
    Categories newCategories(Base parent);

    /**
     * Generate a new random UUID URI
     */
    String newUuidUri();

    /**
     * Get the Abdera instance for this factory
     */
    Abdera getAbdera();

    /**
     * Get the mime type for the specified extension element / document
     */
    <T extends Base> String getMimeType(T base);

    /**
     * Returns a listing of extension factories registered
     */
    String[] listExtensionFactories();
}
