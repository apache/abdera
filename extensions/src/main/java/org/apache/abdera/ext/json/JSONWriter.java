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
import java.util.List;

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
    
    JSONObject jscontent = new JSONObject();
    if(entry.getContentElement() != null) {
        
        Content content = entry.getContentElement();
        Type type = entry.getContentType();
        if (type.equals(Content.Type.HTML) || 
            type.equals(Content.Type.XHTML) || 
            type.equals(Content.Type.TEXT)) {
          jscontent.put("type", type.toString());
        } else {
          jscontent.put("type", content.getMimeType().toString());
        }
        jscontent.put("value", content.getValue());
        jsentry.put("content", jscontent);
    }
    
    JSONArray jscategories = new JSONArray();
    List<Category> categories = entry.getCategories();
    for(Category category : categories) {      
      if(category.getScheme() != null || 
          category.getLabel() != null ||
          category.getTerm() != null) {
        JSONObject jscategory = new JSONObject();
        if(category.getScheme() != null)
          jscategory.put("scheme", category.getScheme().toString());
        
        if(category.getTerm() != null)
          jscategory.put("term", category.getTerm());
        
        if(category.getLabel() != null)
          jscategory.put("label", category.getLabel());
        jscategories.put(jscategory);
      }
    }
    jsentry.put("categories", jscategories);
    
    if(entry.getId() != null)
        jsentry.put("id", entry.getId().toString());
    
    JSONArray jslinks = new JSONArray();
    List<Link> links = entry.getLinks();
    for(Link link : links) {
      JSONObject jslink = new JSONObject();
      jslink.put("href", link.getHref().toString());
      jslink.put("rel", link.getRel());
      jslinks.put(jslink);
    }
    jsentry.put("links", jslinks);
    
    if(entry.getUpdated() != null)
        jsentry.put("updated", entry.getUpdated().toString());
    
    if(entry.getPublished() != null)
        jsentry.put("published", entry.getPublished().toString());
    
    // authors
    List<Person> authors = entry.getAuthors();
    JSONArray jsauthors = new JSONArray();
    for (Person p : authors) {
      JSONObject jsauthor = new JSONObject();
      if(p.getName() != null)
        jsauthor.put("name", p.getName());
      if(p.getUri() != null)
        jsauthor.put("uri", p.getUri().toString());
      if(p.getEmail() != null)
        jsauthor.put("email", p.getEmail());
      jsauthors.put(jsauthor);
    }    
    if(jsauthors.length() > 0)
      jsentry.put("authors", jsauthors);
    
    // contributors
    List<Person> contributors = entry.getContributors();
    JSONArray jscontributors = new JSONArray();
    for (Person p : contributors) {
      JSONObject jscontributor = new JSONObject();
      if(p.getName() != null)
        jscontributor.put("name", p.getName());
      if(p.getUri() != null)
        jscontributor.put("uri", p.getUri().toString());
      if(p.getEmail() != null)
        jscontributor.put("email", p.getEmail());
      jscontributors.put(jscontributor);
    }    
    if(jsauthors.length() > 0)
      jsentry.put("contributors", jscontributors);
    
    return jsentry;
  }

  public static JSONObject toJSON(Feed feed) throws Exception {
    JSONObject jsfeed = new JSONObject();
    
    if (feed.getId() != null) {
      jsfeed.put("id",feed.getId().toString());
    }
    
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
    
    if (feed.getRights() != null) {
      jsfeed.put("rights",feed.getRights());
    }
    
    if (feed.getLogo() != null) {
      jsfeed.put("logo",feed.getLogo().toString());
    }
    
    if (feed.getUpdatedString() != null) {
      jsfeed.put("updated",feed.getUpdatedString());
    }
    
    if (feed.getIcon() != null) {
      jsfeed.put("icon",feed.getIcon().toString());
    }
    
    //  authors
    List<Person> authors = feed.getAuthors();
    JSONArray jsauthors = new JSONArray();
    for (Person p : authors) {
      JSONObject jsauthor = new JSONObject();
      if(p.getName() != null)
        jsauthor.put("name", p.getName());
      if(p.getUri() != null)
        jsauthor.put("uri", p.getUri().toString());
      if(p.getEmail() != null)
        jsauthor.put("email", p.getEmail());
      jsauthors.put(jsauthor);
    }    
    if(jsauthors.length() > 0)
      jsfeed.put("authors", jsauthors);
    
    // contributors
    List<Person> contributors = feed.getContributors();
    JSONArray jscontributors = new JSONArray();
    for (Person p : contributors) {
      JSONObject jscontributor = new JSONObject();
      if(p.getName() != null)
        jscontributor.put("name", p.getName());
      if(p.getUri() != null)
        jscontributor.put("uri", p.getUri().toString());
      if(p.getEmail() != null)
        jscontributor.put("email", p.getEmail());
      jscontributors.put(jscontributor);
    }    
    if(jsauthors.length() > 0)
      jsfeed.put("contributors", jscontributors);
    
    JSONArray jslinks = new JSONArray();
    List<Link> links = feed.getLinks();
    for(Link link : links) {
      JSONObject jslink = new JSONObject();
      jslink.put("href", link.getHref().toString());
      jslink.put("rel", link.getRel());
      jslinks.put(jslink);
    }        
    jsfeed.put("links", jslinks);
    
    JSONArray jscategories = new JSONArray();
    List<Category> categories = feed.getCategories();
    for(Category category : categories) {
      JSONObject jscategory = new JSONObject();
      jscategory.put("scheme", category.getScheme().toString());
      jscategory.put("term", category.getTerm());
      jscategory.put("label", category.getLabel());
      jscategories.put(jscategory);
    }
    jsfeed.put("categories", jscategories);
    
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
  
}
