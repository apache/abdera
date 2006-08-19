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

package org.apache.abdera.ext.json;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.writer.NamedWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONWriter implements NamedWriter {

  public static final String NAME = "json";
  
  public String getName() {
    return NAME;
  }
  
  public Object write(Base base) throws IOException {
    try {
      return toJSON(base).toString();
    } catch(Exception e) {
      throw new IOException(e.getMessage());
    }   
  }

  public void writeTo(Base base, OutputStream out) throws IOException {
    try {
      Object result = toJSON(base);
      out.write(result.toString().getBytes());
    } catch(Exception e) {
      throw new IOException(e.getMessage());
    }        
  }

  public void writeTo(Base base, java.io.Writer out) throws IOException {
    try {
      Object result = toJSON(base);
      out.write(result.toString());
    } catch(Exception e) {
      throw new IOException(e.getMessage());
    }    
  }
  
  public static Object toJSON(Object object) throws Exception {
    if(object instanceof Feed) {
      return toJSON((Feed) object);
    } else if(object instanceof Entry) {
      return toJSON((Entry) object);
    } else if(object instanceof Service) {
      return toJSON((Service) object);
    } else if(object instanceof Document) {
      return toJSON(((Document)object).getRoot());
    }
    return new IllegalArgumentException("Element is not supported by JSONWriter.");
  }
  
  public static JSONObject toJSON(Entry entry) throws Exception {
    JSONObject jsentry = new JSONObject();
    if(entry.getTitle() != null)
      jsentry.put("title", entry.getTitle());
    
    if(entry.getSummary() != null)
        jsentry.put("summary", entry.getSummary());
                
    if(entry.getId() != null)
        jsentry.put("id", entry.getId().toString());
       
    if(entry.getUpdated() != null)
        jsentry.put("updated", entry.getUpdated().toString());
    
    if(entry.getPublished() != null)
        jsentry.put("published", entry.getPublished().toString());
    
    jsentry.put("authors", personsToJSON(entry.getAuthors()));    
    
    jsentry.put("contributors", personsToJSON(entry.getContributors()));
    
    jsentry.put("categories", categoriesToJSON(entry.getCategories()));    
    
    jsentry.put("links", linksToJSON(entry.getLinks()));
    
    JSONObject jscontent = new JSONObject();
    if(entry.getContentElement() != null) {
        
        Content content = entry.getContentElement();
        Type type = entry.getContentType();
        if (type.equals(Content.Type.HTML) || 
            type.equals(Content.Type.XHTML) || 
            type.equals(Content.Type.TEXT)) {
          jscontent.put("type", type.toString().toLowerCase());
        } else {
          jscontent.put("type", content.getMimeType().toString());
        }
        jscontent.put("value", JSONObject.quote(content.getValue()));
        jsentry.put("content", jscontent);
    }    
    
    return jsentry;
  }

  public static JSONObject toJSON(Feed feed) throws Exception {
    JSONObject jsfeed = new JSONObject();
    
    if (feed.getGenerator() != null) {
      Generator gen = feed.getGenerator();
      JSONObject jsgen = new JSONObject();
      jsgen.put("uri",gen.getUri().toString());
      jsgen.put("value",gen.getText());
    }
    
    if (feed.getTitle() != null) {
      jsfeed.put("title",feed.getTitle());
    }
    
    if (feed.getSubtitle() != null) {
      jsfeed.put("subtitle",feed.getSubtitle());
    }
    
    if (feed.getId() != null) {
      jsfeed.put("id",feed.getId().toString());
    }    
    
    if (feed.getRights() != null) {
      jsfeed.put("rights",feed.getRights());
    }
    
    if (feed.getLogo() != null) {
      jsfeed.put("logo",feed.getLogo().toString());
    }
    
    if (feed.getIcon() != null) {
      jsfeed.put("icon",feed.getIcon().toString());
    }    
    
    if (feed.getUpdatedString() != null) {
      jsfeed.put("updated",feed.getUpdatedString());
    }        
    
    jsfeed.put("authors", personsToJSON(feed.getAuthors()));
    
    jsfeed.put("contributors", personsToJSON(feed.getContributors()));
   
    jsfeed.put("categories", categoriesToJSON(feed.getCategories()));
    
    jsfeed.put("links", linksToJSON(feed.getLinks()));    
    
    JSONArray jsentries = new JSONArray();
    List<Entry> entries = feed.getEntries();
    for(Entry entry : entries) {
       jsentries.put(toJSON(entry));
    }
    
    jsfeed.put("entries", jsentries);
    
    return jsfeed;
  }

  
  public static JSONObject toJSON(Service service) throws Exception {
    JSONObject jssvc = new JSONObject();
    JSONArray jsworkspaces = new JSONArray();
    List<Workspace> workspaces = service.getWorkspaces();
    for(Workspace workspace : workspaces) {
      JSONObject jsworkspace = new JSONObject();
      JSONArray jscollections = new JSONArray();
      jsworkspace.put("title", workspace.getTitle());
      List<Collection> collections = workspace.getCollections();
      for(Collection collection : collections) {
        JSONObject jscollection = new JSONObject();
        JSONArray jsaccepts = new JSONArray();
        String[] accepts = collection.getAccept();
        for(String accept : accepts) {
          jsaccepts.put(accept);
        }
        jscollection.put("href", collection.getHref().toString());
        jscollection.put("accept", jsaccepts);
        jscollections.put(jscollection);
      }
      jsworkspace.put("collections", jscollections);
      jsworkspaces.put(jsworkspace);
    }
    jssvc.put("workspaces", jsworkspaces);
    
    return jssvc;
  }  
  
  private static JSONArray categoriesToJSON(List<Category> categories) throws URISyntaxException, JSONException {
    JSONArray jscategories = new JSONArray();
    for (Category category : categories) {
      if (category.getScheme() != null || category.getLabel() != null || category.getTerm() != null) {
        JSONObject jscategory = new JSONObject();
        if (category.getScheme() != null)
          jscategory.put("scheme", category.getScheme().toString());

        if (category.getTerm() != null)
          jscategory.put("term", category.getTerm());

        if (category.getLabel() != null)
          jscategory.put("label", category.getLabel());
        jscategories.put(jscategory);
      }
    }
    return jscategories;
  }
  
  private static JSONArray personsToJSON(List<Person> persons) throws URISyntaxException, JSONException {
    JSONArray jspersons = new JSONArray();
    for (Person p : persons) {
      if(p.getName() != null || p.getUri() != null || p.getEmail() != null) {
      JSONObject jsperson = new JSONObject();
      if(p.getName() != null)
        jsperson.put("name", p.getName());
      if(p.getUri() != null)
        jsperson.put("uri", p.getUri().toString());
      if(p.getEmail() != null)
        jsperson.put("email", p.getEmail());
      jspersons.put(jsperson);
      }
    }    
    return jspersons;
  }
  
  private static JSONArray linksToJSON(List<Link> links) throws URISyntaxException, MimeTypeParseException, JSONException {
    JSONArray jslinks = new JSONArray();
    for(Link link : links) {
      JSONObject jslink = new JSONObject();
      if(link.getHref() != null) {
        jslink.put("href", link.getHref().toString());
        
        if(link.getRel() != null)
          jslink.put("rel", link.getRel());
        
        if(link.getMimeType() != null)
          jslink.put("type", link.getMimeType().getBaseType());
        
        if(link.getHrefLang() != null)
          jslink.put("hreflang", link.getHrefLang());
      }
    }
    return jslinks;
  }
  
}
