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
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.ExtensionFactoryMap;
import org.apache.abdera.factory.Factory;
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
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.Version;
import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterData;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMFactory extends OMLinkedListImplFactory implements Factory, Constants, ExtensionFactory {

    private final ExtensionFactoryMap factoriesMap;
    private final Abdera abdera;

    public static void registerAsDefault() {
        System.setProperty(OMAbstractFactory.META_FACTORY_NAME_PROPERTY, FOMFactory.class.getName());
    }

    public FOMFactory() {
        this(new Abdera());
    }

    public FOMFactory(Abdera abdera) {
        List<ExtensionFactory> f = abdera.getConfiguration().getExtensionFactories();
        factoriesMap =
            new ExtensionFactoryMap((f != null) ? new ArrayList<ExtensionFactory>(f)
                : new ArrayList<ExtensionFactory>());
        this.abdera = abdera;
    }

    public Parser newParser() {
        return new FOMParser(abdera);
    }

    public <T extends Element> Document<T> newDocument() {
        return new FOMDocument(this);
    }

    @Override
    public OMDocument createOMDocument(OMXMLParserWrapper parserWrapper) {
        return new FOMDocument(parserWrapper, this);
    }

    public Service newService(Base parent) {
        return new FOMService((OMContainer)parent, this);
    }

    public Workspace newWorkspace() {
        return newWorkspace(null);
    }

    public Workspace newWorkspace(Element parent) {
        return new FOMWorkspace((OMContainer)parent, this);
    }

    public Collection newCollection() {
        return newCollection(null);
    }

    public Collection newCollection(Element parent) {
        return new FOMCollection((OMContainer)parent, this);
    }

    public Collection newMultipartCollection(Element parent) {
        return new FOMMultipartCollection((OMContainer)parent, this);
    }

    public Feed newFeed() {
        Document<Feed> doc = newDocument();
        return newFeed(doc);
    }

    public Entry newEntry() {
        Document<Entry> doc = newDocument();
        return newEntry(doc);
    }

    public Service newService() {
        Document<Service> doc = newDocument();
        return newService(doc);
    }

    public Feed newFeed(Base parent) {
        return new FOMFeed((OMContainer)parent, this);
    }

    public Entry newEntry(Base parent) {
        return new FOMEntry((OMContainer)parent, this);
    }

    public Category newCategory() {
        return newCategory(null);
    }

    public Category newCategory(Element parent) {
        return new FOMCategory((OMContainer)parent, this);
    }

    public Content newContent() {
        return newContent(Content.Type.TEXT);
    }

    public Content newContent(Type type) {
        if (type == null)
            type = Content.Type.TEXT;
        return newContent(type, null);
    }

    public Content newContent(Type type, Element parent) {
        if (type == null)
            type = Content.Type.TEXT;
        Content content = new FOMContent(type, (OMContainer)parent, this);
        if (type.equals(Content.Type.XML))
            content.setMimeType(XML_MEDIA_TYPE);
        return content;
    }

    public Content newContent(MimeType mediaType) {
        return newContent(mediaType, null);
    }

    public Content newContent(MimeType mediaType, Element parent) {
        Content.Type type = (MimeTypeHelper.isXml(mediaType.toString())) ? Content.Type.XML : Content.Type.MEDIA;
        Content content = newContent(type, parent);
        content.setMimeType(mediaType.toString());
        return content;
    }

    public DateTime newDateTime(QName qname, Element parent) {
        return new FOMDateTime(qname, (OMContainer)parent, this);
    }

    public Generator newDefaultGenerator() {
        return newDefaultGenerator(null);
    }

    public Generator newDefaultGenerator(Element parent) {
        Generator generator = newGenerator(parent);
        generator.setVersion(Version.VERSION);
        generator.setText(Version.APP_NAME);
        generator.setUri(Version.URI);
        return generator;
    }

    public Generator newGenerator() {
        return newGenerator(null);
    }

    public Generator newGenerator(Element parent) {
        return new FOMGenerator((OMContainer)parent, this);
    }

    public IRIElement newID() {
        return newID(null);
    }

    public IRIElement newID(Element parent) {
        return new FOMIRI(Constants.ID, (OMContainer)parent, this);
    }

    public IRIElement newIRIElement(QName qname, Element parent) {
        return new FOMIRI(qname, (OMContainer)parent, this);
    }

    public Link newLink() {
        return newLink(null);
    }

    public Link newLink(Element parent) {
        return new FOMLink((OMContainer)parent, this);
    }

    public Person newPerson(QName qname, Element parent) {
        return new FOMPerson(qname, (OMContainer)parent, this);
    }

    public Source newSource() {
        return newSource(null);
    }

    public Source newSource(Element parent) {
        return new FOMSource((OMContainer)parent, this);
    }

    public Text newText(QName qname, Text.Type type) {
        return newText(qname, type, null);
    }

    public Text newText(QName qname, Text.Type type, Element parent) {
        if (type == null)
            type = Text.Type.TEXT;
        return new FOMText(type, qname, (OMContainer)parent, this);
    }

    public <T extends Element> T newElement(QName qname) {
        return (T)newElement(qname, null);
    }

    public <T extends Element> T newElement(QName qname, Base parent) {
        return (T)newExtensionElement(qname, parent);
    }

    public <T extends Element> T newExtensionElement(QName qname) {
        return (T)newExtensionElement(qname, null);
    }

    public <T extends Element> T newExtensionElement(QName qname, Base parent) {
        String ns = qname.getNamespaceURI();
        Element el = (Element)createElement(qname, (OMContainer)parent, this, null);
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns)) ? el : factoriesMap.getElementWrapper(el));
    }

    public Control newControl() {
        return newControl(null);
    }

    public Control newControl(Element parent) {
        return new FOMControl((OMContainer)parent, this);
    }

    public DateTime newPublished() {
        return newPublished(null);
    }

    public DateTime newPublished(Element parent) {
        return newDateTime(Constants.PUBLISHED, parent);
    }

    public DateTime newUpdated() {
        return newUpdated(null);
    }

    public DateTime newUpdated(Element parent) {
        return newDateTime(Constants.UPDATED, parent);
    }

    public DateTime newEdited() {
        return newEdited(null);
    }

    public DateTime newEdited(Element parent) {
        return newDateTime(Constants.EDITED, parent);
    }

    public IRIElement newIcon() {
        return newIcon(null);
    }

    public IRIElement newIcon(Element parent) {
        return newIRIElement(Constants.ICON, parent);
    }

    public IRIElement newLogo() {
        return newLogo(null);
    }

    public IRIElement newLogo(Element parent) {
        return newIRIElement(Constants.LOGO, parent);
    }

    public IRIElement newUri() {
        return newUri(null);
    }

    public IRIElement newUri(Element parent) {
        return newIRIElement(Constants.URI, parent);
    }

    public Person newAuthor() {
        return newAuthor(null);
    }

    public Person newAuthor(Element parent) {
        return newPerson(Constants.AUTHOR, parent);
    }

    public Person newContributor() {
        return newContributor(null);
    }

    public Person newContributor(Element parent) {
        return newPerson(Constants.CONTRIBUTOR, parent);
    }

    public Text newTitle() {
        return newTitle(Text.Type.TEXT);
    }

    public Text newTitle(Element parent) {
        return newTitle(Text.Type.TEXT, parent);
    }

    public Text newTitle(Text.Type type) {
        return newTitle(type, null);
    }

    public Text newTitle(Text.Type type, Element parent) {
        return newText(Constants.TITLE, type, parent);
    }

    public Text newSubtitle() {
        return newSubtitle(Text.Type.TEXT);
    }

    public Text newSubtitle(Element parent) {
        return newSubtitle(Text.Type.TEXT, parent);
    }

    public Text newSubtitle(Text.Type type) {
        return newSubtitle(type, null);
    }

    public Text newSubtitle(Text.Type type, Element parent) {
        return newText(Constants.SUBTITLE, type, parent);
    }

    public Text newSummary() {
        return newSummary(Text.Type.TEXT);
    }

    public Text newSummary(Element parent) {
        return newSummary(Text.Type.TEXT, parent);
    }

    public Text newSummary(Text.Type type) {
        return newSummary(type, null);
    }

    public Text newSummary(Text.Type type, Element parent) {
        return newText(Constants.SUMMARY, type, parent);
    }

    public Text newRights() {
        return newRights(Text.Type.TEXT);
    }

    public Text newRights(Element parent) {
        return newRights(Text.Type.TEXT, parent);
    }

    public Text newRights(Text.Type type) {
        return newRights(type, null);
    }

    public Text newRights(Text.Type type, Element parent) {
        return newText(Constants.RIGHTS, type, parent);
    }

    public Element newName() {
        return newName(null);
    }

    public Element newName(Element parent) {
        return newElement(Constants.NAME, parent);
    }

    public Element newEmail() {
        return newEmail(null);
    }

    public Element newEmail(Element parent) {
        return newElement(Constants.EMAIL, parent);
    }

    public Div newDiv() {
        return newDiv(null);
    }

    public Div newDiv(Base parent) {
        return new FOMDiv(DIV, (OMContainer)parent, this);
    }

    protected OMElement createElement(QName qname, OMContainer parent, OMFactory factory, Object objecttype) {
        OMElement element = null;
        OMNamespace namespace = this.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
        if (FEED.equals(qname)) {
            element = new FOMFeed(qname.getLocalPart(), namespace, parent, factory);
        } else if (SERVICE.equals(qname) || PRE_RFC_SERVICE.equals(qname)) {
            element = new FOMService(qname.getLocalPart(), namespace, parent, factory);
        } else if (ENTRY.equals(qname)) {
            element = new FOMEntry(qname.getLocalPart(), namespace, parent, factory);
        } else if (AUTHOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
        } else if (CATEGORY.equals(qname)) {
            element = new FOMCategory(qname.getLocalPart(), namespace, parent, factory);
        } else if (CONTENT.equals(qname)) {
            Content.Type type = (Content.Type)objecttype;
            element = new FOMContent(qname.getLocalPart(), namespace, type, parent, factory);
        } else if (CONTRIBUTOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
        } else if (GENERATOR.equals(qname)) {
            element = new FOMGenerator(qname.getLocalPart(), namespace, parent, factory);
        } else if (ICON.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (ID.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (LOGO.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (LINK.equals(qname)) {
            element = new FOMLink(qname.getLocalPart(), namespace, parent, factory);
        } else if (PUBLISHED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);
        } else if (RIGHTS.equals(qname)) {
            Text.Type type = (Text.Type)objecttype;
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (SOURCE.equals(qname)) {
            element = new FOMSource(qname.getLocalPart(), namespace, parent, factory);
        } else if (SUBTITLE.equals(qname)) {
            Text.Type type = (Text.Type)objecttype;
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (SUMMARY.equals(qname)) {
            Text.Type type = (Text.Type)objecttype;
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (TITLE.equals(qname)) {
            Text.Type type = (Text.Type)objecttype;
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (UPDATED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);
        } else if (WORKSPACE.equals(qname) || PRE_RFC_WORKSPACE.equals(qname)) {
            element = new FOMWorkspace(qname.getLocalPart(), namespace, parent, factory);
        } else if (COLLECTION.equals(qname) || PRE_RFC_COLLECTION.equals(qname)) {
            element = new FOMCollection(qname.getLocalPart(), namespace, parent, factory);
        } else if (NAME.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), namespace, parent, factory);
        } else if (EMAIL.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), namespace, parent, factory);
        } else if (URI.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (CONTROL.equals(qname) || PRE_RFC_CONTROL.equals(qname)) {
            element = new FOMControl(qname.getLocalPart(), namespace, parent, factory);
        } else if (DIV.equals(qname)) {
            element = new FOMDiv(qname.getLocalPart(), namespace, parent, factory);
        } else if (CATEGORIES.equals(qname) || PRE_RFC_CATEGORIES.equals(qname)) {
            element = new FOMCategories(qname.getLocalPart(), namespace, parent, factory);
        } else if (EDITED.equals(qname) || PRE_RFC_EDITED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);
        } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
            element = (OMElement)new FOMExtensibleElement(qname, parent, this);
        } else {
            element = (OMElement)new FOMExtensibleElement(qname, null, this);
        }
        return element;
    }

    protected OMElement createElement(QName qname, OMContainer parent, FOMBuilder builder) {
        OMElement element = null;
        if (FEED.equals(qname)) {
            element = new FOMFeed(qname.getLocalPart(), parent, this, builder);
        } else if (SERVICE.equals(qname) || PRE_RFC_SERVICE.equals(qname)) {
            element = new FOMService(qname.getLocalPart(), parent, this, builder);
        } else if (ENTRY.equals(qname)) {
            element = new FOMEntry(qname.getLocalPart(), parent, this, builder);
        } else if (AUTHOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), parent, this, builder);
        } else if (CATEGORY.equals(qname)) {
            element = new FOMCategory(qname.getLocalPart(), parent, this, builder);
        } else if (CONTENT.equals(qname)) {
            Content.Type type = builder.getContentType();
            if (type == null)
                type = Content.Type.TEXT;
            element = new FOMContent(qname.getLocalPart(), type, parent, this, builder);
        } else if (CONTRIBUTOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), parent, this, builder);
        } else if (GENERATOR.equals(qname)) {
            element = new FOMGenerator(qname.getLocalPart(), parent, this, builder);
        } else if (ICON.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (ID.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (LOGO.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (LINK.equals(qname)) {
            element = new FOMLink(qname.getLocalPart(), parent, this, builder);
        } else if (PUBLISHED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (RIGHTS.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null)
                type = Text.Type.TEXT;
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (SOURCE.equals(qname)) {
            element = new FOMSource(qname.getLocalPart(), parent, this, builder);
        } else if (SUBTITLE.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null)
                type = Text.Type.TEXT;
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (SUMMARY.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null)
                type = Text.Type.TEXT;
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (TITLE.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null)
                type = Text.Type.TEXT;
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (UPDATED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (WORKSPACE.equals(qname) || PRE_RFC_WORKSPACE.equals(qname)) {
            element = new FOMWorkspace(qname.getLocalPart(), parent, this, builder);
        } else if (COLLECTION.equals(qname) || PRE_RFC_COLLECTION.equals(qname)) {
            element = new FOMCollection(qname.getLocalPart(), parent, this, builder);
        } else if (NAME.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), parent, this, builder);
        } else if (EMAIL.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), parent, this, builder);
        } else if (URI.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (CONTROL.equals(qname) || PRE_RFC_CONTROL.equals(qname)) {
            element = new FOMControl(qname.getLocalPart(), parent, this, builder);
        } else if (DIV.equals(qname)) {
            element = new FOMDiv(qname.getLocalPart(), parent, this, builder);
        } else if (CATEGORIES.equals(qname) || PRE_RFC_CATEGORIES.equals(qname)) {
            element = new FOMCategories(qname.getLocalPart(), parent, this, builder);
        } else if (EDITED.equals(qname) || PRE_RFC_EDITED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
            element = new FOMExtensibleElement(qname.getLocalPart(), parent, this, builder);
        }
        return element;
    }

    public Factory registerExtension(ExtensionFactory factory) {
        factoriesMap.addFactory(factory);
        return this;
    }

    public Categories newCategories() {
        Document<Categories> doc = newDocument();
        return newCategories(doc);
    }

    public Categories newCategories(Base parent) {
        return new FOMCategories((OMContainer)parent, this);
    }

    public String newUuidUri() {
        return FOMHelper.generateUuid();
    }

    // public void setElementWrapper(Element internal, Element wrapper) {
    // factoriesMap.setElementWrapper(internal, wrapper);
    // }
    //  

    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null)
            return null;
        String ns = internal.getQName().getNamespaceURI();
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns) || internal.getQName().equals(DIV)) ? internal
            : factoriesMap.getElementWrapper(internal));
    }

    public String[] getNamespaces() {
        return factoriesMap.getNamespaces();
    }

    public boolean handlesNamespace(String namespace) {
        return factoriesMap.handlesNamespace(namespace);
    }

    public Abdera getAbdera() {
        return abdera;
    }

    public <T extends Base> String getMimeType(T base) {
        String type = factoriesMap.getMimeType(base);
        return type;
    }

    public String[] listExtensionFactories() {
        return factoriesMap.listExtensionFactories();
    }

    @Override
    public OMComment createOMComment(OMContainer arg0, String arg1) {
        return new FOMComment(arg0, arg1, this, false);
    }

    @Override
    public OMProcessingInstruction createOMProcessingInstruction(OMContainer arg0, String arg1, String arg2) {
        return new FOMProcessingInstruction(arg0, arg1, arg2, this, false);
    }

    @Override
    public CoreCharacterData createCharacterData() {
        return new FOMCharacterData(this);
    }

    @Override
    public CoreCDATASection createCDATASection() {
        return new FOMCDATASection(this);
    }
}
