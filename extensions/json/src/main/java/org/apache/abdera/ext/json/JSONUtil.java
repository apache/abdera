package org.apache.abdera.ext.json;
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
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.bidi.BidiHelper;
import org.apache.abdera.ext.history.FeedPagingHelper;
import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadHelper;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.TextValue;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.util.Constants;
import org.apache.abdera.xpath.XPath;

@SuppressWarnings("unchecked")
public class JSONUtil {

  public static void toJson(Base base, Writer writer) throws IOException {
    JSONStream jstream = new JSONStream(writer);
    if (base instanceof Document)
      toJson((Document)base,jstream);
    else if (base instanceof Element)
      toJson((Element)base,jstream);
    writer.flush();
  }
  
  private static boolean isSameAsParentBase(Element element) {
    IRI parentbase = null;
    if (element.getParentElement() != null) {
      parentbase = element instanceof Document ?
        ((Document)element).getBaseUri() :
        ((Element)element).getResolvedBaseUri();
    }
    if (parentbase == null && element.getResolvedBaseUri() != null) return false;
    return parentbase.equals(element.getResolvedBaseUri());
  }
  
  private static void toJson(Element element, JSONStream jstream) throws IOException {
    jstream.startObject();

    writeLanguageFields(element, jstream);
    if (!isSameAsParentBase(element))
      jstream.writeField("xml:base", element.getResolvedBaseUri());
    
    if (element instanceof Categories) {
      Categories categories = (Categories) element;
      jstream.writeField("fixed", categories.isFixed()?"true":"false");
      jstream.writeField("scheme", categories.getScheme());
      writeList("categories",categories.getCategories(),jstream);
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Category) {
      Category category = (Category) element;      
      jstream.writeField("term", category.getTerm());
      jstream.writeField("scheme", category.getScheme());
      jstream.writeField("label", category.getLabel());
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Collection) {
      Collection collection = (Collection)element;
      jstream.writeField("href", collection.getResolvedHref());
      writeElement("title",collection.getTitleElement(),jstream);
      String[] accepts = collection.getAccept();
      if (accepts != null || accepts.length > 0) {
        jstream.writeField("accept");
        jstream.startArray();
        for (int n = 0; n < accepts.length; n++) {
          jstream.writeQuoted(accepts[n]);
          if (n < accepts.length - 1) jstream.writeSeparator();
        }
        jstream.endArray();
      }
      List<Categories> cats = collection.getCategories();
      if (cats.size() > 0)
        writeList("categories",collection.getCategories(),jstream);
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Content) {
      Content content = (Content)element;      
      jstream.writeField("src", content.getResolvedSrc());
      switch(content.getContentType()) {
        case TEXT:
          jstream.writeField("type","text");
          jstream.writeField("value",content.getValue());
          break;
        case HTML:
          jstream.writeField("type","html");
          jstream.writeField("value",content.getValue());
          break;
        case XHTML:
          jstream.writeField("type","xhtml");
          jstream.writeField("value",content.getValue());
          writeElementValue((Div)content.getValueElement(), jstream);
          break;
        case MEDIA:
        case XML:
          jstream.writeField("type",content.getMimeType());
          if (content.getSrc() == null)
            jstream.writeField("value",content.getValue());
          break;
      }
    } else if (element instanceof Control) {
      Control control = (Control)element;
      jstream.writeField("draft", control.isDraft()?"true":"false");
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Entry) {
      Entry entry = (Entry)element;
      jstream.writeField("id", entry.getId());
      writeElement("title", entry.getTitleElement(),jstream);
      writeElement("summary", entry.getSummaryElement(),jstream);
      writeElement("rights", entry.getRightsElement(),jstream);     
      writeElement("content", entry.getContentElement(),jstream);
      jstream.writeField("updated", entry.getUpdated());
      jstream.writeField("published", entry.getPublished());
      jstream.writeField("edited", entry.getEdited());
      writeElement("source", entry.getSource(),jstream);
      writeList("authors",entry.getAuthors(),jstream);
      writeList("contributors",entry.getContributors(),jstream);
      writeList("links",entry.getLinks(),jstream);
      writeList("categories",entry.getCategories(),jstream);
      writeList("inreplyto",ThreadHelper.getInReplyTos(entry),jstream);            
      writeExtensions((ExtensibleElement)element,jstream);      
    } else if (element instanceof Generator) {
      Generator generator = (Generator)element;
      jstream.writeField("version", generator.getVersion());
      jstream.writeField("uri", generator.getResolvedUri());
      jstream.writeField("value", generator.getText());
    } else if (element instanceof Link) {
      Link link = (Link)element;      
      jstream.writeField("href", link.getResolvedHref());
      jstream.writeField("rel", link.getRel());
      jstream.writeField("title", link.getTitle());
      jstream.writeField("type", link.getMimeType());
      jstream.writeField("hreflang", link.getHrefLang());
      if (link.getLength() > -1)
        jstream.writeField("length", link.getLength());
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Person) {
      Person person = (Person)element;      
      jstream.writeField("name",person.getName());
      jstream.writeField("email",person.getEmail());
      jstream.writeField("uri",person.getUriElement().getResolvedValue());
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Service) {
      Service service = (Service)element;
      writeList("workspaces",service.getWorkspaces(),jstream);
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof Source) {
      Source source = (Source)element;
      jstream.writeField("id", source.getId());
      writeElement("title", source.getTitleElement(),jstream);
      writeElement("subtitle", source.getSubtitleElement(),jstream);
      writeElement("rights", source.getRightsElement(),jstream);
      jstream.writeField("updated", source.getUpdated());
      writeElement("generator", source.getGenerator(),jstream);
      jstream.writeField("icon", source.getIconElement().getResolvedValue());
      jstream.writeField("logo", source.getLogoElement().getResolvedValue());
      writeList("authors",source.getAuthors(),jstream);
      writeList("contributors",source.getContributors(),jstream);
      writeList("links",source.getLinks(),jstream);
      writeList("categories",source.getCategories(),jstream);
      if (FeedPagingHelper.isComplete(source))
        jstream.writeField("complete",true);      
      if (FeedPagingHelper.isArchive(source))
        jstream.writeField("archive",true);
      if (source instanceof Feed) {
        writeList("entries",((Feed)source).getEntries(),jstream);
      }
      writeExtensions((ExtensibleElement)element,jstream);      
    } else if (element instanceof Text) {
      Text text = (Text)element;      
      switch(text.getTextType()) {
        case TEXT:
          jstream.writeField("type","text");
          jstream.writeField("value",text.getValue());
          break;
        case HTML:
          jstream.writeField("type","html");
          jstream.writeField("value",text.getValue());
          break;
        case XHTML:
          jstream.writeField("type","xhtml");
          jstream.writeField("value",text.getValue());
          writeElementValue(text.getValueElement(), jstream);
          break;
      }
    } else if (element instanceof Workspace) {
      Workspace workspace = (Workspace)element;
      writeElement("title",workspace.getTitleElement(),jstream);
      writeList("collections",workspace.getCollections(),jstream);
      writeExtensions((ExtensibleElement)element,jstream);
    } else if (element instanceof InReplyTo) {
      InReplyTo irt = (InReplyTo)element;      
      jstream.writeField("ref",irt.getRef());
      jstream.writeField("href",irt.getResolvedHref());
      jstream.writeField("type",irt.getMimeType());
      jstream.writeField("source",irt.getResolvedSource());
    } else {
      writeElement(element,null,jstream);
    }
    jstream.endObject();
  }
    
  private static void writeElementValue(Element element, JSONStream jstream) throws IOException {
    jstream.writeField("valuehash");
    writeElementChildren(element, jstream);
  }

  private static void writeElement(Element child, QName parentqname, JSONStream jstream) throws IOException {
    QName childqname = child.getQName();
    String prefix = childqname.getPrefix();
    String uri = childqname.getNamespaceURI();
    jstream.startArray();
    
    if (prefix != null && !"".equals(prefix)) {
      jstream.writeQuoted(childqname.getPrefix() + ":" + childqname.getLocalPart());
    } else {
      jstream.writeQuoted(childqname.getLocalPart());
    } 
    jstream.writeSeparator();
    List<QName> attributes = child.getAttributes();
    jstream.startObject();
    if (!Constants.XHTML_NS.equals(uri) && !isSameNamespace(childqname, parentqname)) {
      if (prefix != null && !"".equals(prefix))
        jstream.writeField("xmlns:" + prefix);
      else 
        jstream.writeField("xmlns");
      jstream.writeQuoted(childqname.getNamespaceURI());
    }
    if (!isSameAsParentBase(child))
      jstream.writeField("xml:base",child.getResolvedBaseUri());
    writeLanguageFields(child, jstream);
    for (QName attr : attributes) {
      jstream.writeField(attr.getLocalPart(),child.getAttributeValue(attr));
    }
    jstream.endObject();
    jstream.writeSeparator();
    writeElementChildren((Element)child,jstream);
    jstream.endArray();
  }
  
  private static void writeElementChildren(Element element, JSONStream jstream) throws IOException {
    jstream.startArray();
    Object[] children = getChildren(element);
    QName parentqname = element.getQName();
    for (int n = 0; n < children.length; n++) {
      Object child = children[n];
      if (child instanceof Element) {
        writeElement((Element)child, parentqname, jstream);
        if (n < children.length-1) jstream.writeSeparator();
      } else if (child instanceof TextValue) {
        TextValue textvalue = (TextValue) child;
        String value = textvalue.getText();
        if (!element.getMustPreserveWhitespace()) {
          if (!value.matches("\\s*")) {
            jstream.writeQuoted(value.trim());
            if (n < children.length-1) jstream.writeSeparator();
          }
        } else { 
          jstream.writeQuoted(value);
          if (n < children.length-1) jstream.writeSeparator();
        }
      }
    }
    jstream.endArray();
  }
  
  private static void writeExtensions(ExtensibleElement element, JSONStream jstream) throws IOException {
    writeExtensions(element,jstream,true);
  }
  
  private static void writeExtensions(ExtensibleElement element, JSONStream jstream, boolean startsep) throws IOException {
    List<QName> attributes = element.getExtensionAttributes();
    writeList("extensions",element.getExtensions(),jstream);
    if (attributes.size() > 0) {
      jstream.writeField("attributes");
      jstream.startArray();
      for (int n = 0; n < attributes.size(); n++) {
        QName qname = attributes.get(n);
        writeAttribute(qname, element.getAttributeValue(qname), jstream);
        if (n < attributes.size()-1) jstream.writeSeparator();
      }
      jstream.endArray();
    }
  }
  
  private static void writeLanguageFields(Element element, JSONStream jstream) throws IOException {
    String parentlang = null;
    BidiHelper.Direction parentdir = BidiHelper.Direction.UNSPECIFIED;
    if (element.getParentElement() != null) {
      Base parent = element.getParentElement();
      parentlang = parent instanceof Document ?
        ((Document)parent).getLanguage() :
        ((Element)parent).getLanguage();
      if (parent instanceof Element) {
        parentdir = BidiHelper.getDirection((Element)parent);
      }
    }
    String lang = element.getLanguage();
    BidiHelper.Direction dir = BidiHelper.getDirection(element);
    if (parentlang == null || (parentlang != null && !parentlang.equalsIgnoreCase(lang)))
      
      jstream.writeField("xml:lang",lang);
    if (dir != null && dir != BidiHelper.Direction.UNSPECIFIED && !dir.equals(parentdir)) {
      jstream.writeField("dir", dir.name().toLowerCase());
    }
  }
  
  private static void writeElement(String name, Element element, JSONStream jstream) throws IOException {
    if (element != null) {
      jstream.writeField(name);
      toJson(element,jstream);
    }
  }
  
  private static boolean writeList(String name, List list, JSONStream jstream) throws IOException {
    if (list == null || list.size() == 0) return false;
    jstream.writeField(name);
    jstream.startArray();
    for (int n = 0; n < list.size(); n++) {
      toJson((Element)list.get(n),jstream);
      if (n < list.size()-1) jstream.writeSeparator();
    }
    jstream.endArray();
    return true;
  }
  
  private static void toJson(Document document, JSONStream jstream) throws IOException {
    jstream.startObject();    
    jstream.writeField("base", document.getBaseUri());
    jstream.writeField("charset", document.getCharset());
    jstream.writeField("contenttype", document.getContentType());
    jstream.writeField("etag", document.getEntityTag());
    jstream.writeField("language", document.getLanguage());
    jstream.writeField("slug", document.getSlug());
    jstream.writeField("lastmodified", document.getLastModified());
    jstream.writeField("whitespace", "false");
    writeElement("root",document.getRoot(),jstream);
    jstream.endObject();
  }
  
  private static void writeAttribute(QName qname, String value, JSONStream jstream) throws IOException {
    jstream.startArray();
    String prefix = qname.getPrefix();
    String localpart = qname.getLocalPart();
    String uri = qname.getNamespaceURI();
    if (prefix != null && !"".equals(prefix)) {
      jstream.writeQuoted(prefix + ":" + localpart);
      jstream.writeSeparator();
      jstream.startObject();
      jstream.writeField("xmlns:" + prefix, uri);
      jstream.endObject();
    } else {
      jstream.writeQuoted(localpart);
    }
    jstream.writeSeparator();
    jstream.writeQuoted(value);
    jstream.endArray();
  }
  
  private static Object[] getChildren(Element element) {
    Abdera abdera = element.getFactory().getAbdera();
    XPath xpath = abdera.getXPath();
    List<Object> nodes = xpath.selectNodes("node()", element);
    return nodes.toArray(new Object[nodes.size()]);
  }
  
  private static boolean isSameNamespace(QName q1, QName q2) {
    if (q1 == null && q2 != null) return false;
    if (q1 != null && q2 == null) return false;
    String p1 = q1 == null ? "" : q1.getPrefix() != null ? q1.getPrefix() : "";
    String p2 = q2 == null ? "" : q2.getPrefix() != null ? q2.getPrefix() : "";
    String n1 = q1 == null ? "" : q1.getNamespaceURI() != null ? q1.getNamespaceURI() : "";
    String n2 = q2 == null ? "" : q2.getNamespaceURI() != null ? q2.getNamespaceURI() : "";
    return n1.equals(n2) && p1.equals(p2);
  }
    
}
