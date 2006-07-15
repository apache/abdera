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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URISyntaxException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
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
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.Version;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

public class FOMFactory 
  extends OMLinkedListImplFactory 
  implements Factory, Constants {

  private final static Map<QName,Class> qclasses = new HashMap<QName,Class>();
  static {
    qclasses.put(FEED, FOMFeed.class);
    qclasses.put(SERVICE, FOMService.class);
    qclasses.put(ENTRY, FOMEntry.class);
    qclasses.put(AUTHOR, FOMPerson.class);
    qclasses.put(CATEGORY, FOMCategory.class);
    qclasses.put(CONTENT, FOMContent.class);
    qclasses.put(CONTRIBUTOR, FOMPerson.class);
    qclasses.put(GENERATOR, FOMGenerator.class);
    qclasses.put(ICON, FOMIRI.class);
    qclasses.put(ID, FOMIRI.class);
    qclasses.put(LOGO, FOMIRI.class);
    qclasses.put(LINK, FOMLink.class);
    qclasses.put(PUBLISHED, FOMDateTime.class);
    qclasses.put(RIGHTS, FOMText.class);
    qclasses.put(SOURCE, FOMSource.class);
    qclasses.put(SUBTITLE, FOMText.class);
    qclasses.put(SUMMARY, FOMText.class);
    qclasses.put(TITLE, FOMText.class);
    qclasses.put(UPDATED, FOMDateTime.class);
    qclasses.put(WORKSPACE, FOMWorkspace.class);
    qclasses.put(COLLECTION, FOMCollection.class);
    qclasses.put(NAME, FOMElement.class);
    qclasses.put(EMAIL, FOMElement.class);
    qclasses.put(URI, FOMIRI.class);
    qclasses.put(CONTROL, FOMControl.class);
    qclasses.put(DIV, FOMDiv.class);
  }
  
  private Map<Class,Class> alternatives = null;
  private Map<QName,Class> extensions = null;
  private List<ExtensionFactory> factories = null;
  
  public Parser newParser() {
    return new FOMParser();
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> newDocument() {
    return _newInstance(FOMDocument.class, CONSTRUCTORS[8], this);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> newDocument(
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDocument.class, CONSTRUCTORS[9], this, parserWrapper);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> newDocument(
    T root, 
    OMXMLParserWrapper parserWrapper) {
      FOMDocument<T> doc = (FOMDocument<T>) newDocument(parserWrapper);
      doc.setRoot(root);
      return doc;
  }
  
  public Service newService(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMService.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }

  public Service newService(
    Base parent) {
      return _newInstance(FOMService.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Workspace newWorkspace(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMWorkspace.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Workspace newWorkspace() {
    return newWorkspace(null);
  }
  
  public Workspace newWorkspace(
    Element parent) {
      return _newInstance(FOMWorkspace.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Collection newCollection(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMCollection.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Collection newCollection() {
    return newCollection(null);
  }
  
  public Collection newCollection(
    Element parent) {
      return _newInstance(FOMCollection.class, CONSTRUCTORS[1], (OMContainer)parent, this);
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
  
  public Feed newFeed(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMFeed.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Feed newFeed(
    Base parent) {
      return _newInstance(FOMFeed.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Entry newEntry(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMEntry.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Entry newEntry(
    Base parent) {
      return _newInstance(FOMEntry.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Category newCategory(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMCategory.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Category newCategory() {
    return newCategory(null);
  }
  
  public Category newCategory(
    Element parent) {
      return _newInstance(FOMCategory.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Content newContent(
    QName qname,
    Type type, 
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      if (type == null) type = Content.Type.TEXT;
      return _newInstance(FOMContent.class, CONSTRUCTORS[2], 
          qname, type, parent, this, parserWrapper);
  }
  
  public Content newContent() {
    return newContent(Content.Type.TEXT);
  }
  
  public Content newContent(Type type) {
    if (type == null) type = Content.Type.TEXT;
    return newContent(type, null);
  }
  
  public Content newContent(
    Type type, 
    Element parent) {
      if (type == null) type = Content.Type.TEXT;
      Content content = 
        _newInstance(
          FOMContent.class, CONSTRUCTORS[3], type, (OMContainer)parent, this);
      try {
        if (type.equals(Content.Type.XML))
          content.setMimeType("application/xml");
      } catch (MimeTypeParseException e) { /* Can't happen */ }
      return content;
  }
  
  public Content newContent(MimeType mediaType) {
    return newContent(mediaType, null);
  }
  
  public Content newContent(
    MimeType mediaType, 
    Element parent) {
    Content.Type type = 
      (MimeTypeHelper.isXml(mediaType.toString())) ? 
         Content.Type.XML : Content.Type.MEDIA;
    Content content = newContent(type, parent);
    try {
      content.setMimeType(mediaType.toString());
    } catch (MimeTypeParseException e) { /* Can't happen */ }
    return content;
  }

  public DateTime newDateTimeElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDateTime.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public DateTime newDateTime(
    QName qname, 
    Element parent) {
      return _newInstance(FOMDateTime.class, CONSTRUCTORS[4], qname, (OMContainer)parent, this);
  }

  public Generator newGenerator(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMGenerator.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Generator newDefaultGenerator() {
    return newDefaultGenerator(null);
  }
  
  public Generator newDefaultGenerator(
    Element parent) {
      Generator generator = newGenerator(parent);
      generator.setVersion(Version.VERSION);
      generator.setText(Version.APP_NAME);
      try {
        generator.setUri(Version.URI);
      } catch (URISyntaxException e) { /* Can't happen */ }
      return generator;
  }
  
  public Generator newGenerator() {
    return newGenerator(null);
  }
  
  public Generator newGenerator(
      Element parent) {
    return _newInstance(FOMGenerator.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public IRI newID(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMIRI.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public IRI newID() {
    return newID(null);
  }
  
  public IRI newID(
    Element parent) {
      return _newInstance(
          FOMIRI.class, CONSTRUCTORS[4], 
          Constants.ID, (OMContainer)parent, this);
  }

  public IRI newURIElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMIRI.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public IRI newIRIElement(
    QName qname, 
    Element parent) {
      return _newInstance(
        FOMIRI.class, CONSTRUCTORS[4], 
        qname, (OMContainer)parent, this);
  }

  public Link newLink(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMLink.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Link newLink() {
    return newLink(null);
  }
  
  public Link newLink(
    Element parent) {
      return _newInstance(FOMLink.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Person newPerson(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMPerson.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Person newPerson(
    QName qname, 
    Element parent) {
      return _newInstance(
        FOMPerson.class, CONSTRUCTORS[4], 
        qname, (OMContainer)parent, this);
  }

  public Source newSource(
    QName qname, 
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMSource.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Source newSource() {
    return newSource(null);
  }
  
  public Source newSource(
    Element parent) {
      return _newInstance(FOMSource.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }

  public Text newText(
    QName qname,
    Text.Type type,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
    if (type == null) type = Text.Type.TEXT;
    return _newInstance(FOMText.class, CONSTRUCTORS[5], 
        type, qname, parent, this, parserWrapper);
  }
  
  public Text newText(
    QName qname,
    Text.Type type) {
    return newText(qname, type, null);
  }
  
  public Text newText(
    QName qname, 
    Text.Type type, 
    Element parent) {
    if (type == null) type = Text.Type.TEXT;
    return _newInstance(FOMText.class, CONSTRUCTORS[6], 
        type, qname,(OMContainer)parent, this);
  }
  
  public Element newElement(QName qname) {
    return newElement(qname, null);
  }
  
  public Element newElement(
    QName qname, 
    Base parent) {
      return _newInstance(
        FOMElement.class, CONSTRUCTORS[4], 
        qname, (OMContainer)parent, this);
  }
  
  public Element newExtensionElement(QName qname) {
    return newExtensionElement(qname, (Base)null);
  }
  
  public Element newExtensionElement(
    QName qname, 
    Base parent) {
    return newExtensionElement(qname, (OMContainer)parent);
  }
  
  private Element newExtensionElement(
    QName qname, 
    OMContainer parent) {
      return newExtensionElement(qname, parent, null);
  }

  private List<ExtensionFactory> getExtensionFactories() {
    if (factories == null) {
      factories = new ArrayList<ExtensionFactory>(
        org.apache.abdera.util.ServiceUtil.loadExtensionFactories());
    }
    return factories;
  }
  
  @SuppressWarnings("unchecked")
  public Element newExtensionElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
    Element element = null;
    List<ExtensionFactory> factories = getExtensionFactories();
    Class _class = getExtensionClass(qname);
    if (_class == null && factories != null) {
      for (ExtensionFactory factory : factories) {
        if (factory instanceof FOMExtensionFactory &&
            factory.handlesNamespace(qname.getNamespaceURI())) {
            if (parserWrapper != null) {
              element = ((FOMExtensionFactory)factory).newExtensionElement(
                qname, (Base)parent, this, parserWrapper);
            } else {
              element = factory.newExtensionElement(qname, (Base)parent, this); 
            }
        }
      }
    }
    if (_class == null) _class = FOMElement.class;
    if (element == null) {
      if (parserWrapper != null) {
        element = (Element) _newInstance(_class, CONSTRUCTORS[0], qname, (OMContainer)parent, this, parserWrapper);
      } else {
        element = (Element) _newInstance(
          _class, CONSTRUCTORS[4], 
          qname, (OMContainer)parent, this);
      }
    }
    return element;
  }
  
  @SuppressWarnings("unchecked")
  private Class<Base> getExtensionClass(QName qname) {
    return (extensions != null && extensions.containsKey(qname)) ? 
      extensions.get(qname) : null;
  }
  
  public Control newControl() {
    return newControl(null);
  }
   
  public Control newControl(Element parent) {
    return _newInstance(FOMControl.class, CONSTRUCTORS[1], (OMContainer)parent, this);
  }
  
  public Control newControl(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMControl.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
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

  public IRI newIcon() {
    return newIcon(null);
  }
  
  public IRI newIcon(Element parent) {
    return newIRIElement(Constants.ICON, parent);
  }

  public IRI newLogo() {
    return newLogo(null);
  }
  
  public IRI newLogo(Element parent) {
    return newIRIElement(Constants.LOGO, parent);
  }

  public IRI newUri() {
    return newUri(null);
  }
  
  public IRI newUri(Element parent) {
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

  public Person newContributor(
    Element parent) {
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
    return _newInstance(
      FOMDiv.class, CONSTRUCTORS[4], 
      Constants.DIV, (OMContainer)parent, this);
  }
  
  public Div newDiv(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDiv.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }
  
  public Element newElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMElement.class, CONSTRUCTORS[0], qname, parent, this, parserWrapper);
  }

  
  
  private boolean isContent(QName qname) {
    return CONTENT.equals(qname);
  }
  
  private boolean isText(QName qname) {
    return TITLE.equals(qname) ||
           SUMMARY.equals(qname) ||
           SUBTITLE.equals(qname) ||
           RIGHTS.equals(qname);
  }
  
  @SuppressWarnings("unchecked")
  protected OMElement createElement(
      QName qname, 
      OMContainer parent, 
      OMFactory factory,
      Object objecttype) {
      OMElement element = null;
      OMNamespace namespace = 
        this.createOMNamespace(
          qname.getNamespaceURI(), 
          qname.getPrefix());
      if (qclasses.containsKey(qname)) {
        if (isContent(qname)) {
          Content.Type type = (Content.Type)objecttype;
          element = (OMElement) _newInstance(
            qclasses.get(qname), CONSTRUCTORS[10], 
            qname.getLocalPart(), namespace, type, parent, this);
        } else if (isText(qname)) {
          Text.Type type = (Text.Type)objecttype;
          element = (OMElement) _newInstance(
            qclasses.get(qname), 
            CONSTRUCTORS[11], 
            type, qname.getLocalPart(), 
            namespace, parent, this);
        } else {
          element = (OMElement) _newInstance(
            qclasses.get(qname), CONSTRUCTORS[7], 
            qname.getLocalPart(), namespace, parent, this);
        }
      } else if (parent instanceof ExtensibleElement || 
                 parent instanceof Document) {
        element = (OMElement) newExtensionElement(qname, parent);
      }
      return element;
    }
  
  @SuppressWarnings("unchecked")
  protected OMElement createElement(
    QName qname, 
    OMContainer parent, 
    FOMBuilder builder) {
    OMElement element = null;    
    if (qclasses.containsKey(qname)) {
      if (isContent(qname)) {
        Content.Type type = builder.getContentType();
        element = (OMElement) _newInstance(
          qclasses.get(qname), CONSTRUCTORS[2], 
          qname, type, parent, this, builder);
      } else if (isText(qname)) {
        Text.Type type = builder.getTextType();
        element = (OMElement) _newInstance(
            qclasses.get(qname), CONSTRUCTORS[5], 
            type, qname, parent, this, builder);
      } else {
        element = (OMElement) _newInstance(
          qclasses.get(qname), 
          CONSTRUCTORS[0], 
          qname, 
          parent, 
          this, 
          builder);
      }
    } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
      element = (OMElement) newExtensionElement(qname, parent, builder);
    }
    return element;
  }

  public void registerExtension(QName qname, Class impl) {
    if (extensions == null) extensions = new HashMap<QName,Class>();
    extensions.put(qname, impl);
  }
  
  public void registerExtension(ExtensionFactory factory) {
    getExtensionFactories().add(factory);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Base>void registerAlternative(Class<T> base, Class<? extends T> extension) {
    if (!base.isAssignableFrom(extension))
      throw new IllegalArgumentException("The extension must extend the base");
    if (alternatives == null) alternatives = new HashMap<Class,Class>();
    if (extension != null)
      alternatives.put(base, extension);
    else if (alternatives.containsKey(base))
      alternatives.remove(base);
  }
  
  // Internals for Alternative creation
  
  private Class _getAlternative(Class _class) {
    if (alternatives == null) alternatives = new HashMap<Class,Class>();
    return (alternatives.containsKey(_class)) ? alternatives.get(_class) : _class;
  }
  
  private static final Class[][] CONSTRUCTORS = {
    new Class[] {
      QName.class, 
      OMContainer.class,
      OMFactory.class,
      OMXMLParserWrapper.class},
    new Class[] {
      OMContainer.class,
      OMFactory.class},
    new Class[] {
      QName.class,
      Content.Type.class,
      OMContainer.class,
      OMFactory.class,
      OMXMLParserWrapper.class},
    new Class[] {
      Content.Type.class,
      OMContainer.class,
      OMFactory.class}, 
    new Class[] {
      QName.class,
      OMContainer.class,
      OMFactory.class}, 
    new Class[] {
      Text.Type.class,
      QName.class,
      OMContainer.class,
      OMFactory.class,
      OMXMLParserWrapper.class},
    new Class[] {
      Text.Type.class,
      QName.class,
      OMContainer.class,
      OMFactory.class},
    new Class[] {
      String.class, 
      OMNamespace.class, 
      OMContainer.class, 
      OMFactory.class},
    new Class[] {
      OMFactory.class},
    new Class[] {
      OMFactory.class,
      OMXMLParserWrapper.class},
    new Class[] {
      String.class, 
      OMNamespace.class,
      Content.Type.class,
      OMContainer.class, 
      OMFactory.class},
    new Class[] {
      Text.Type.class,
      String.class, 
      OMNamespace.class, 
      OMContainer.class, 
      OMFactory.class}
  };
    
  /**
   * This likely has some class loader issues we need to be concerned about
   */
  @SuppressWarnings("unchecked")
  private <T extends Base>T _newInstance(
    Class<T> _class, 
    Class[] argtypes, 
    Object... args) {
    Class _altclass = _getAlternative(_class);
    try {
      return (T) _altclass.getConstructor(argtypes).newInstance(args);
    } catch (Exception e) {
      try {
        return _class.getConstructor(argtypes).newInstance(args);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }
}
