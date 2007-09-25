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
import org.apache.abdera.ext.thread.Total;
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
    if (base instanceof Document)
      toJson((Document)base,writer);
    else if (base instanceof Element)
      toJson((Element)base,writer);
    writer.flush();
  }
  
  private static Object[] getChildren(Element element) {
    Abdera abdera = element.getFactory().getAbdera();
    XPath xpath = abdera.getXPath();
    List<Object> nodes = xpath.selectNodes("node()", element);
    return nodes.toArray(new Object[nodes.size()]);
  }
  
  private static void toJson(Element element, Writer writer) throws IOException {
    writer.write('{');
        
    if (element instanceof Categories) {
      Categories categories = (Categories) element;
      writeField("fixed", categories.isFixed()?"true":"false", writer);
      if (categories.getScheme() != null) {
        writer.write(',');
        writeField("scheme", categories.getScheme().toASCIIString(), writer);
      } 
      writer.write(',');
      writeList("categories",categories.getCategories(),writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Category) {
      Category category = (Category) element;

      writeLanguageFields(element, writer);
      
      writeField("term", category.getTerm(), writer);
      if (category.getScheme() != null) {
        writer.write(',');
        writeField("scheme", category.getScheme().toASCIIString(), writer);
      }
      if (category.getLabel() != null) {
        writer.write(',');
        writeField("label", category.getLabel(), writer);
      }
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Collection) {
      Collection collection = (Collection)element;
      if (collection.getResolvedHref() != null) {
        writeField("href", collection.getResolvedHref().toASCIIString(), writer);
        writer.write(',');
      }
      if (collection.getTitleElement() != null) {
        writeElement("title",collection.getTitleElement(),writer);
        writer.write(',');
      }
      writer.write("\"accept\":[");
      String[] accepts = collection.getAccept();
      for (int n = 0; n < accepts.length; n++) {
        writer.write("\"" + escape(accepts[n]) + "\"");
        if (n < accepts.length - 1) writer.write(',');
      }
      writer.write("]");
      writer.write(',');
      writeList("categories",collection.getCategories(),writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Content) {
      Content content = (Content)element;      
      writeLanguageFields(element, writer);

      if (element.getResolvedBaseUri() != null) {
        writeField("base", element.getResolvedBaseUri().toASCIIString(), writer);
        writer.write(',');
      }      
      if (content.getResolvedSrc() != null) {
        writeField("src", content.getResolvedSrc().toASCIIString(), writer);
        writer.write(',');
      }
     
      switch(content.getContentType()) {
        case TEXT:
          writeField("type","text",writer);
          writer.write(',');
          writeField("value",content.getValue(),writer);
          break;
        case HTML:
          writeField("type","html",writer);
          writer.write(',');
          writeField("value",content.getValue(),writer);
          break;
        case XHTML:
          writeField("type","xhtml",writer);
          writer.write(',');
          writeXHTMLValue((Div)content.getValueElement(), writer);
          writer.write(',');
          writeField("display",content.getValue(), writer);
          break;
        case MEDIA:
          writeField("type",content.getMimeType().toString(),writer);
          if (content.getSrc() == null) {
            writer.write(',');
            writeField("value",content.getValue(),writer);
          }
          break;
        case XML:
          writeField("type",content.getMimeType().toString(),writer);
          if (content.getSrc() == null) {
            writer.write(',');
            writeField("value",content.getValue(),writer);
          }
          break;
      }
    } else if (element instanceof Control) {
      Control control = (Control)element;
      writeField("draft", control.isDraft()?"true":"false",writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Entry) {
      Entry entry = (Entry)element;

      writeField("id", entry.getId().toASCIIString(),writer);
      writer.write(',');
      
      writeElement("title", entry.getTitleElement(),writer);
      writer.write(',');
      
      if (entry.getSummary() != null) {
        writeElement("summary", entry.getSummaryElement(),writer);
        writer.write(',');
      }
      
      if (entry.getRights() != null) {
        writeElement("rights", entry.getRightsElement(),writer);
        writer.write(',');
      }
      
      if (entry.getContentElement() != null) {
        writeElement("content", entry.getContentElement(),writer);
        writer.write(',');
      }
      
      writeField("updated", entry.getUpdated().getTime(),writer);
      writer.write(',');
      
      if (entry.getPublished() != null) {
        writeField("published", entry.getPublished().getTime(),writer);
        writer.write(',');
      }
      
      if (entry.getEdited() != null) {
        writeField("edited", entry.getEdited().getTime(),writer);
        writer.write(',');
      }
      
      if (entry.getSource() != null) {
        writeElement("source", entry.getSource(),writer);
        writer.write(',');
      }
      
      writeList("authors",entry.getAuthors(),writer);
      writer.write(',');

      writeList("contributors",entry.getContributors(),writer);
      writer.write(',');
      
      writeList("links",entry.getLinks(),writer);
      writer.write(',');
      
      writeList("categories",entry.getCategories(),writer);
      writer.write(',');
      
      writeList("inreplyto",ThreadHelper.getInReplyTos(entry),writer);
      writer.write(',');
      
      List<Link> links = entry.getLinks("replies");
      writer.write("\"replies\":[");
      Total total = ThreadHelper.getTotal(entry);
      if (total != null) {
        writer.write("\n{");
        writeField("count",total.getValue(),writer);
        writer.write('}');
        if (links.size() > 0) writer.write(',');
      }
      for (int n = 0; n < links.size(); n++) {
        Link link = links.get(n);
        writer.write("\n{");
        writeField("href",link.getResolvedHref().toASCIIString(),writer);
        writer.write(',');
        if (link.getMimeType() != null) {
          writeField("type",link.getMimeType().toString(),writer);
          writer.write(',');
        }
        writeField("count",ThreadHelper.getCount(link),writer);
        writer.write('}');
        if (n < links.size()-1) writer.write(',');
      }
      writer.write("]");
      writer.write(',');
      
      links = entry.getLinks("license");
      writer.write("\"licenses\":[");
      for (int n = 0; n < links.size(); n++) {
        Link link = links.get(n);
        writer.write("\n{");
        writeField("href",link.getResolvedHref().toASCIIString(),writer);
        if (link.getMimeType() != null) {
          writer.write(',');
          writeField("type",link.getMimeType().toString(),writer);
        }
        writer.write('}');
        if (n < links.size()-1) writer.write(',');
      }
      writer.write("]");
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
      
    } else if (element instanceof Generator) {
      Generator generator = (Generator)element;
      
      writeLanguageFields(element, writer);
      
      if (generator.getVersion() != null) {
        writeField("version", generator.getVersion(), writer);
      }    
      if (generator.getResolvedUri() != null) {
        writer.write(',');  
        writeField("uri", generator.getResolvedUri().toASCIIString(), writer);
      }
      if (generator.getText() != null) {
        writer.write(',');
        writeField("value", generator.getText(), writer);
      }      
    } else if (element instanceof Link) {
      Link link = (Link)element;
      
      writeLanguageFields(element, writer);

      writeField("href", link.getResolvedHref().toASCIIString(), writer);
      
      if (link.getRel() != null) {
        writer.write(',');
        writeField("rel", link.getRel(), writer);
      }
      if (link.getTitle() != null) {
        writer.write(',');
        writeField("title", link.getTitle(), writer);
      }
      if (link.getMimeType() != null) {
        writer.write(',');
        writeField("type", link.getMimeType().toString(), writer);
      }
      if (link.getHrefLang() != null) {
        writer.write(',');
        writeField("hreflang", link.getHrefLang(), writer);
      }
      if (link.getLength() > -1) {
        writer.write(',');
        writeField("length", link.getLength(), writer);
      }

      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Person) {
      Person person = (Person)element;
      
      writeLanguageFields(element, writer);
      
      writeField("name",person.getName(),writer);
      writer.write(',');
      writeField("email",person.getEmail(),writer);
      writer.write(',');
      if (person.getUriElement() != null)
        writeField("uri",person.getUriElement().getResolvedValue().toASCIIString(),writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Service) {
      Service service = (Service)element;
      writeList("workspaces",service.getWorkspaces(),writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof Source) {
      Source source = (Source)element;

      writeField("id", source.getId().toASCIIString(),writer);
      writer.write(',');
      
      writeElement("title", source.getTitleElement(),writer);
      writer.write(',');
      
      if (source.getSubtitle() != null) {
        writeElement("subtitle", source.getSubtitleElement(),writer);
        writer.write(',');
      }
      
      if (source.getRights() != null) {
        writeElement("rights", source.getRightsElement(),writer);
        writer.write(',');
      }
      
      writeField("updated", source.getUpdated().getTime(),writer);
      writer.write(',');

      if (source.getGenerator() != null) {
        writeElement("generator", source.getGenerator(),writer);
        writer.write(',');
      }
      
      if (source.getIconElement() != null) {
        writeField("icon", source.getIconElement().getResolvedValue().toASCIIString(),writer);
        writer.write(',');
      }
      
      if (source.getLogoElement() != null) {
        writeField("logo", source.getLogoElement().getResolvedValue().toASCIIString(),writer);
        writer.write(',');
      }
      
      writeList("authors",source.getAuthors(),writer);
      writer.write(',');

      writeList("contributors",source.getContributors(),writer);
      writer.write(',');
      
      writeList("links",source.getLinks(),writer);
      writer.write(',');
      
      writeList("categories",source.getCategories(),writer);
      writer.write(',');
      
      writeField("complete",FeedPagingHelper.isComplete(source), writer);
      writer.write(',');
      
      writeField("archive",FeedPagingHelper.isArchive(source), writer);
      
      if (source instanceof Feed) {
        writer.write(',');
        writeList("entries",((Feed)source).getEntries(),writer);
      }
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
      
    } else if (element instanceof Text) {
      Text text = (Text)element;
      
      writeLanguageFields(element, writer);
      
      if (element.getResolvedBaseUri() != null) {
        writeField("base", element.getResolvedBaseUri().toASCIIString(), writer);
        writer.write(',');
      }
      
      switch(text.getTextType()) {
        case TEXT:
          writeField("type","text",writer);
          writer.write(',');
          writeField("value",text.getValue(),writer);
          break;
        case HTML:
          writeField("type","html",writer);
          writer.write(',');
          writeField("value",text.getValue(),writer);
          break;
        case XHTML:
          writeField("type","xhtml",writer);
          writer.write(',');
          writeXHTMLValue(text.getValueElement(), writer);
          writer.write(',');
          writeField("display",text.getValue(), writer);
          break;
      }
    } else if (element instanceof Workspace) {
      Workspace workspace = (Workspace)element;

      writeElement("title",workspace.getTitleElement(),writer);
      writer.write(',');
      writeList("collections",workspace.getCollections(),writer);
      
      writer.write(',');
      writeExtensions((ExtensibleElement)element,writer);
    } else if (element instanceof InReplyTo) {
      InReplyTo irt = (InReplyTo)element;
      
      writeField("ref",irt.getRef().toASCIIString(),writer);
      
      if (irt.getHref() != null) {
        writer.write(',');
        writeField("href",irt.getResolvedHref().toASCIIString(),writer);
      }
      
      if (irt.getMimeType() != null) {
        writer.write(',');
        writeField("type",irt.getMimeType().toString(),writer);
      }
      
      if (irt.getSource() != null) {
        writer.write(',');
        writeField("source",irt.getResolvedSource().toASCIIString(),writer);
      }
      
    } else {
      writeQName(element.getQName(),writer);
      writer.write(',');
      writeLanguageFields(element, writer);

      if (element.getResolvedBaseUri() != null) {
        writeField("base", element.getResolvedBaseUri().toASCIIString(), writer);
        writer.write(',');
      }
      writer.write("\"attributes\":");
      writer.write('[');
      List<QName> attributes = element.getAttributes();
      for (int n = 0; n < attributes.size(); n++) {
        QName qname = attributes.get(n);
        writeAttribute(qname, element.getAttributeValue(qname), writer);
        if (n < attributes.size()-1) writer.write(',');
      }
      writer.write(']');
      writer.write(',');    
      writer.write("\"children\":");
      writeChildren(element, writer);
    }
    writer.write('}');
  }
    
  private static void writeXHTMLValue(Div div, Writer writer) throws IOException {
    writer.write("\n\"value\":");
    writeXHTMLChildren(div, writer);
  }

  private static void writeXHTMLChildren(Element element, Writer writer) throws IOException {
    writer.write('[');
    Object[] children = getChildren(element);
    for (int n = 0; n < children.length; n++) {
      Object child = children[n];
      if (child instanceof Element) {
        Element childel = (Element) child;
        QName childqname = childel.getQName();
        writer.write("[\n");
        if (childqname.getNamespaceURI() != null && childqname.getNamespaceURI().equals(Constants.XHTML_NS)) {
          writer.write('"');
          writer.write(escape(childqname.getLocalPart()));
          writer.write('"');
        } else {
          writeQName(childqname,writer,false);
        }
        writer.write(',');
        List<QName> attributes = childel.getAttributes();
        writer.write('{');
        for (int i = 0; i < attributes.size(); i++) {
          QName attr = attributes.get(i);
          writer.write('"');
          writer.write(escape(attr.getLocalPart()));
          writer.write('"');
          writer.write(':');
          writer.write('"');
          writer.write(escape(childel.getAttributeValue(attr)));
          writer.write('"');
          if (i < attributes.size()-1) writer.write(',');
        }
        writer.write('}');
        writer.write(',');
        writeXHTMLChildren((Element)child,writer);
        writer.write("]\n");      
      } else if (child instanceof TextValue) {
        TextValue textvalue = (TextValue) child;
        writer.write("\"" + escape(textvalue.getText()) + "\"");
      }
      if (n < children.length-1) writer.write(',');
    }
    writer.write(']');
  }
  
  private static void writeChildren(Element element, Writer writer) throws IOException {
    writer.write('[');
    Object[] children = getChildren(element);
    for (int n = 0; n < children.length; n++) {
      Object child = children[n];
      if (child instanceof Element) {
        toJson((Element)child, writer);
      } else if (child instanceof TextValue) {
        TextValue textvalue = (TextValue) child;
        writer.write("\"" + escape(textvalue.getText()) + "\"");
      }
      if (n < children.length-1) writer.write(',');
    }
    writer.write(']');
  }
  
  private static void writeExtensions(ExtensibleElement element, Writer writer) throws IOException {
    writeList("extensions",element.getExtensions(),writer);
    writer.write(',');
    writer.write("\"attributes\":");
    writer.write('[');
    List<QName> attributes = element.getExtensionAttributes();
    for (int n = 0; n < attributes.size(); n++) {
      QName qname = attributes.get(n);
      writeAttribute(qname, element.getAttributeValue(qname), writer);
      if (n < attributes.size()-1) writer.write(',');
    }
    writer.write(']');
  }
  
  private static void writeLanguageFields(Element element, Writer writer) throws IOException {
    String lang = element.getLanguage();
    boolean whitespace = element.getMustPreserveWhitespace();
    BidiHelper.Direction dir = BidiHelper.getDirection(element);
    if (lang != null) {
      writeField("language",lang,writer);
    }
    
    if (!whitespace) {
      if (lang != null) writer.write(',');
      writeField("whitespace", "false", writer);
    }
    
    if (dir != null && dir != BidiHelper.Direction.UNSPECIFIED) {
      if (lang != null || !whitespace) writer.write(',');
      writeField("dir", dir.name().toLowerCase(), writer);
    }
    
    if (lang != null || 
        !whitespace || 
        (dir != null && dir != BidiHelper.Direction.UNSPECIFIED)) 
      writer.write(',');
  }
  
  private static void writeElement(String name, Element element, Writer writer) throws IOException {
    if (element != null) {
      writer.write("\n\"" + escape(name) + "\":");
      toJson(element,writer);
    }
  }
  
  private static void writeList(String name, List list, Writer writer) throws IOException {
    writer.write("\n\"" + escape(name) + "\":[");
    for (int n = 0; n < list.size(); n++) {
      toJson((Element)list.get(n),writer);
      if (n < list.size()-1) writer.write(",\n");
    }
    writer.write("]");
  }
  
  private static void toJson(Document document, Writer writer) throws IOException {
    writer.write('{');
    
    if (document.getBaseUri() != null) {
      writeField("base", document.getBaseUri().toASCIIString(), writer);
      writer.write(',');
    }
    
    if (document.getCharset() != null) {
      writeField("charset", document.getCharset(), writer);
      writer.write(',');
    }
    
    if (document.getContentType() != null) {
      writeField("contenttype", document.getContentType().toString(), writer);
      writer.write(',');
    }    
    
    if (document.getEntityTag() != null) {
      writeField("etag", document.getEntityTag().toString(), writer);
      writer.write(',');
    }
    
    if (document.getLanguage() != null) {
      writeField("language", document.getLanguage().toString(), writer);
      writer.write(',');
    }
    
    if (document.getSlug() != null) {
      writeField("slug", document.getSlug(), writer);
      writer.write(',');
    }
    
    if (document.getLastModified() != null) {
      writeField("lastmodified", document.getLastModified().getTime(), writer);
      writer.write(',');
    }
    
    if (!document.getMustPreserveWhitespace()) {
      writeField("whitespace", "false", writer);
      writer.write(',');
    }
    
    Element root = document.getRoot();
    if (root != null) {
      writer.write("\n\"root\":");
      toJson(root,writer);
    } else {
      writer.write("\"root\":null");
    }
    
    writer.write('}');
  }

  private static void writeField(String name, boolean value, Writer writer) throws IOException {
    writer.write("\n\"");
    writer.write(escape(name));
    writer.write('"');
    writer.write(':');
    writer.write(value?"true":"false");
  }
  
  private static void writeField(String name, String value, Writer writer) throws IOException {
    if (value == null) return;
    writer.write("\n\"");
    writer.write(escape(name));
    writer.write('"');
    writer.write(':');
    writer.write('"');
    writer.write(escape(value));
    writer.write('"');
  }
  
  private static void writeField(String name, Number value, Writer writer) throws IOException {
    if (value == null) return;
    writer.write("\n\"");
    writer.write(escape(name));
    writer.write('"');
    writer.write(':');
    writer.write(value.toString());
  }
  
  private static void writeQName(QName qname, Writer writer) throws IOException {
    writeQName(qname,writer,true);
  }
  
  private static void writeQName(QName qname, Writer writer, boolean includefieldname) throws IOException {
    if (includefieldname) writer.write("\n\"qname\":");
    writer.write('{');
    writeField("name",escape(qname.getLocalPart()),writer);
    if (qname.getNamespaceURI() != null) {
      writer.write(',');
      writeField("ns",escape(qname.getNamespaceURI()),writer);
    }
    if (qname.getPrefix() != null) {
      writer.write(',');
      writeField("prefix",escape(qname.getPrefix()),writer);
    }
    writer.write('}');
  }
  
  private static void writeAttribute(QName qname, String value, Writer writer) throws IOException {
    writer.write("\n{");
    writeQName(qname,writer);
    writer.write(',');
    writeField("value",escape(value),writer);
    writer.write('}');
  }
  
  private static String escape(String value) {
    StringBuffer buf = new StringBuffer();
    char[] chars = value.toCharArray();
    char b = 0;
    String t = null;
    for (char c : chars) {
      switch(c) {
        case '\\':
        case '"':
          buf.append('\\');
          buf.append(c);
          break;
        case '/':
          if (b == '<') buf.append('\\');
          buf.append(c);
          break;
        case '\b':
          buf.append("\\b");
          break;
        case '\t':
          buf.append("\\t");
          break;
        case '\n':
          buf.append("\\n");
          break;
        case '\f':
          buf.append("\\f");
          break;
        case '\r':
          buf.append("\\r");
          break;
        default:
          if (c < ' ' || c > 127) {
            t = "000" + Integer.toHexString(c);
            buf.append("\\u" + t.substring(t.length() - 4));
          } else {
            buf.append(c);
          }
        }
        b = c;
      }
    return buf.toString();
    }
    
}
