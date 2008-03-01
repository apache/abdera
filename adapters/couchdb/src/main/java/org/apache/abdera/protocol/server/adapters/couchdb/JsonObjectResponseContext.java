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
package org.apache.abdera.protocol.server.adapters.couchdb;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.xml.namespace.QName;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

import com.fourspaces.couchdb.Document;

/**
 * ResponseContext implementation that serializes a JSON Document from a 
 * CouchDB database as an Atom document using the Abdera StreamWriter.
 */
public class JsonObjectResponseContext 
  extends StreamWriterResponseContext {
  
  private final FeedConfiguration config;
  private final Document doc;
  private final Document[] entries;

  protected JsonObjectResponseContext(
    Abdera abdera,
    FeedConfiguration config,
    Document doc) {
      this(abdera,config,doc,(Document[])null);
  }
  
  protected JsonObjectResponseContext(
    Abdera abdera,
    FeedConfiguration config,
    Document doc,
    Document... entries) {
      super(abdera);
      this.doc = doc;
      this.entries = entries;
      this.config = config;
      setContentType(
        entries != null ? 
          "application/atom+xml" :
          "application/atom+xml;type=entry");
  }

  protected JsonObjectResponseContext(
    Abdera abdera, 
    FeedConfiguration config,
    String encoding,
    Document doc,
    Document... entries) {
      super(abdera, encoding);
      this.doc = doc;
      this.entries = entries;
      this.config = config;
  }
  
  protected void writeTo(
    StreamWriter sw) 
      throws IOException {
    sw.startDocument();
    if (entries != null) 
      writeJsonFeed(config,doc, entries, sw);
    else  writeJsonEntry(doc, sw);
    sw.endDocument();
  }
  
  public boolean hasEntity() {
    return true;
  }

  private static void writeJsonFeed(
    FeedConfiguration config,
    Document doc, 
    Document[] entries,
    StreamWriter writer) {
      writer.startFeed();
      writer.writeId(config.getServerConfiguration().getServerUri() + "/" + config.getFeedId())
            .writeTitle(config.getFeedTitle())
            .writeAuthor(config.getFeedAuthor())
            .writeLink(config.getFeedUri())
            .writeLink(config.getFeedUri(), "self")
            .writeUpdated(new Date());
      for (Document entry : entries)
        writeJsonEntry(entry, writer);
      writer.endFeed();
  }
  
  private static void writeJsonEntry(
    Document doc, 
    StreamWriter writer) {
      writer.startEntry();
      writeJsonAttributes(doc.getJSONObject("attributes"), writer);
      for (Object key : doc.keySet()) {
        String name = (String) key;
        if (!name.startsWith("_")) {
          if (name.equals("id"))
            writer.writeId(doc.getString(name));
          else if (name.equals("title")) {
            writeJsonTextElement(
              Constants.TITLE, 
              doc.get(name), 
              writer);
          } else if (name.equals("summary")) {
            writeJsonTextElement(
                Constants.SUMMARY, 
                doc.get(name), 
                writer);
          } else if (name.equals("rights")) {
            writeJsonTextElement(
                Constants.RIGHTS, 
                doc.get(name), 
                writer);
          } else if (name.equals("links")) {
            JSONArray array = doc.getJSONArray(name);
            for (int n = 0; n < array.size(); n++) {
              JSONObject obj = array.getJSONObject(n);
              writer.startElement(Constants.LINK);
              writeJsonAttributes(obj, writer);
              if (obj.containsKey("extensions"))
                writeJsonExtensions(obj.getJSONArray("extensions"), writer);
              writer.endElement();
            }
          } else if (name.equals("categories")) {
            JSONArray array = doc.getJSONArray(name);
            for (int n = 0; n < array.size(); n++) {
              JSONObject obj = array.getJSONObject(n);
              writer.startElement(Constants.CATEGORY);
              writeJsonAttributes(obj, writer);
              if (obj.containsKey("extensions"))
                writeJsonExtensions(obj.getJSONArray("extensions"), writer);
              writer.endElement();
            }            
          } else if (name.equals("authors")) {
            JSONArray array = doc.getJSONArray(name);
            for (int n = 0; n < array.size(); n++) {
              JSONObject obj = array.getJSONObject(n);
              writeJsonPerson(Constants.AUTHOR, obj, writer);
            }
          } else if (name.equals("contributors")) {
            JSONArray array = doc.getJSONArray(name);
            for (int n = 0; n < array.size(); n++) {
              JSONObject obj = array.getJSONObject(n);
              writeJsonPerson(Constants.CONTRIBUTOR, obj, writer);
            }            
          } else if (name.equals("updated")) {
            writer.writeUpdated(doc.getString(name));
          } else if (name.equals("published")) {
            writer.writePublished(doc.getString(name));
          } else if (name.equals("edited")) {
            writer.writeEdited(doc.getString(name));
          } else if (name.equals("content")) {
            writeJsonContentElement( 
              doc.get(name), 
              writer);
          } else if (name.equals("control")) {
            JSONObject control = doc.getJSONObject("control");
            writer.startControl();
            writeJsonAttributes(control.getJSONObject("attributes"), writer);
            writer.writeDraft(control.getBoolean("draft"));
            if (control.containsKey("extensions"))
              writeJsonExtensions(control.getJSONArray("extensions"), writer);
            writer.endControl();
          } else if (name.equals("source")) {
            
          }
        }
      }
      writer.endEntry();
  }
  
  private static void writeJsonPerson(
    QName qname, 
    JSONObject object, 
    StreamWriter writer) {
      writer.startPerson(qname);
      writer.writePersonName(object.getString("name"));
      if (object.containsKey("email"))
        writer.writePersonEmail(object.getString("email"));
      if (object.containsKey("uri"))
        writer.writePersonUri(object.getString("uri"));
      if (object.containsKey("extensions"))
        writeJsonExtensions(object.getJSONArray("extensions"), writer);
      writer.endPerson();
  }

  private static void writeJsonContentElement(
    Object value, 
    StreamWriter writer) {
      if (value instanceof String) {
        writer.writeContent(Content.Type.TEXT, (String) value);
      } else if (value instanceof JSONObject) {
        writeJsonContent( 
          (JSONObject)value, 
          writer);
      }
  }

  private static void writeJsonContent(
    JSONObject object, 
    StreamWriter writer) {
      String type = object.getJSONObject("attributes").getString("type");
      Content.Type ctype = Content.Type.valueOf(type.toUpperCase());
      writer.startContent(ctype);
      if (ctype == Content.Type.XHTML) 
        writer.startElement(Constants.DIV);
      JSONArray array = object.getJSONArray("children");
      for (int n = 0; n < array.size(); n++) {
        Object child = array.get(n);
        if (child instanceof String) {
          writer.writeElementText((String)child);
        } else if (child instanceof JSONObject) {
          writeJsonTextMarkup(
            (JSONObject) child, 
            writer, 
            ctype == Content.Type.XHTML);
        }
      }
      if (ctype == Content.Type.XHTML) 
        writer.endElement();
      writer.endElement();
  }
  
  private static void writeJsonTextElement(
    QName qname, 
    Object value, 
    StreamWriter writer) {
      if (value instanceof String) {
        writer.writeText(qname, Text.Type.TEXT, (String) value);
      } else if (value instanceof JSONObject) {
        writeJsonText(
          qname, 
          (JSONObject)value, 
          writer);
      }
  }
  
  private static void writeJsonText(
    QName qname,
    JSONObject object, 
    StreamWriter writer) {
      String type = object.getJSONObject("attributes").getString("type");
      Text.Type texttype = Text.Type.valueOf(type.toUpperCase());
      writer.startText(qname,texttype);
      if (texttype == Text.Type.XHTML) 
        writer.startElement(Constants.DIV);
      JSONArray array = object.getJSONArray("children");
      for (int n = 0; n < array.size(); n++) {
        Object child = array.get(n);
        if (child instanceof String) {
          writer.writeElementText((String)child);
        } else if (child instanceof JSONObject) {
          writeJsonTextMarkup(
            (JSONObject) child, 
            writer, 
            texttype == Text.Type.XHTML);
        }
      }
      if (texttype == Text.Type.XHTML) 
        writer.endElement();
      writer.endElement();
  }
  
  private static void writeJsonTextMarkup(
    JSONObject object, 
    StreamWriter writer, 
    boolean xhtml) {
      String name = object.getString("name");
      if (xhtml) {
        writer.startElement(name, Constants.XHTML_NS);
        writeJsonAttributes(object.getJSONObject("attributes"), writer);
        JSONArray array = object.getJSONArray("children");
        for (int n = 0; n < array.size(); n++) {
          Object obj = array.get(n);
          if (obj instanceof String) {
            writer.writeElementText((String) obj);
          } else if (obj instanceof JSONObject) {
            writeJsonTextMarkup((JSONObject) obj, writer, xhtml);
          }
        }
        writer.endElement();
      } else {
        writer.writeElementText("<" + name);
      
        JSONObject attrs = object.getJSONObject("attributes");
        for (Object attr : attrs.entrySet()) {
          Map.Entry<?,?> entry = (Map.Entry<?,?>)attr;
          String attrname = (String) entry.getKey();
          String value = (String) entry.getValue();
          writer.writeElementText(" " + attrname + "=\"" + value + "\"");
        }
        
        writer.writeElementText(">");
        JSONArray array = object.getJSONArray("children");
        for (int n = 0; n < array.size(); n++) {
          Object obj = array.get(n);
          if (obj instanceof String) {
            writer.writeElementText((String) obj);
          } else if (obj instanceof JSONObject) {
            writeJsonTextMarkup((JSONObject) obj, writer, xhtml);
          }
        }        
        writer.writeElementText("</" + name  + ">");
      }
  }
  
  private static void writeJsonAttributes(
    JSONObject object, 
    StreamWriter writer) {
      for (Object key : object.keySet()) {
        String name = (String) key;
        if (!name.startsWith("xmlns:")) {
          Object value = object.get(name);
          if (value instanceof String) {
            writer.writeAttribute(name, (String)value);
          } else if (value instanceof JSONObject){
            JSONObject valueObj = (JSONObject) value;
            String namespace = null;
            JSONObject valueAttrs = valueObj.getJSONObject("attributes");
            for (Object vkey : valueAttrs.keySet()) {
              namespace = valueAttrs.getString((String) vkey);
              break;
            }
            String[] namesplit = name.split(":",2);
            writer.writeAttribute(
              namesplit[1], 
              namespace, 
              namesplit[0], 
              valueObj.getString("value"));
          }
        }
      }
  }
  
  private static void writeJsonExtensions(
    JSONArray array, 
    StreamWriter writer) {
      for (int n = 0; n < array.size(); n++) {
        JSONObject object = array.getJSONObject(n);
        writeJsonObject(object,writer);
      }
  }
  
  private static void writeJsonObject(
    JSONObject object, 
    StreamWriter writer) {
      String namespace = null;
      JSONObject valueAttrs = object.getJSONObject("attributes");
      for (Object vkey : valueAttrs.keySet()) {
        String svkey = (String) vkey;
        if (svkey.startsWith("xmlns:"))
          namespace = valueAttrs.getString(svkey);
        break;
      }
      String name = object.getString("name");
      String[] namesplit = name.split(":",2);
      if (namesplit.length > 1) 
        writer.startElement(namesplit[1], namespace, namesplit[0]);
      else 
        writer.startElement(name, namespace);
      writeJsonAttributes(valueAttrs, writer);
      JSONArray children = object.getJSONArray("children");
      for (int i = 0; i < children.size(); i++) {
        Object child = children.get(i);
        if (child instanceof String) {
          writer.writeElementText((String)child);
        } else if (child instanceof JSONObject) {
          writeJsonObject((JSONObject) child,writer);
        }
      }
      writer.endElement();
  }
}
