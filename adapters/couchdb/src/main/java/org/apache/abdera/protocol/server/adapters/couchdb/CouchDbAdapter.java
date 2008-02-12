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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ManagedCollectionAdapter;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.fourspaces.couchdb.ViewResults;

public class CouchDbAdapter 
  extends ManagedCollectionAdapter {

  private final DocumentComparator sorter = new DocumentComparator();
  private final String host;
  private final int port;
  
  public CouchDbAdapter(
    Abdera abdera, 
    FeedConfiguration config) {
      this(
        abdera,
        config,
        null,
        -1);
  }
  
  public CouchDbAdapter(
    Abdera abdera, 
    FeedConfiguration config,
    String host, 
    int port) {
      super(abdera, config);
      if (host == null) { 
        host = (String)config.getProperty("couchdb.host");
        if (host == null) host = "localhost";
      }
      if (port < 0) {
        try {
          port = Integer.parseInt((String)config.getProperty("couchdb.port"));
        } catch (Exception e) {}
        if (port < 0) port = 5984;
      }
      this.host = host;
      this.port = port;
  }
  
  private synchronized Database getDatabase(
    RequestContext request) {
      String name = request.getTarget().getParameter("feed");
      Session session = new Session(host,port);
      Database db = null;
      try {
        db = session.getDatabase(name);
      } catch (Exception e) {}
      if (db == null)
        db = session.createDatabase(name);
      return db;
  }
  
  public ResponseContext getFeed(
    RequestContext request) {
      Database db = getDatabase(request);
      ViewResults res = db.getAllDocuments();
      List<Document> entries = new ArrayList<Document>();
      List<Document> docs = res.getResults();
      for (Document doc : docs) {
        entries.add(
          db.getDocument(doc.getString("id")));
      }
      Collections.sort(entries, sorter);
      return new JsonObjectResponseContext(
        request.getAbdera(),
        config,
        res,
        entries.toArray(new Document[entries.size()]))
          .setStatus(200)
          .setEntityTag(res.getRev());
  }
      
  public ResponseContext deleteEntry(
    RequestContext request) {
      Target target = request.getTarget();
      String feed = target.getParameter("feed");
      String entry = target.getParameter("entry");
      Session session = new Session(host,port);
      Database db = session.getDatabase(feed);
      Document doc = db.getDocument(entry);
      if (doc != null) {
        db.deleteDocument(doc);
        return ProviderHelper.nocontent();
      } else 
        return ProviderHelper.notfound(request);
  }

  private void setEditDetail(RequestContext request) throws IOException {
    Target target = request.getTarget();
    String feed = target.getParameter("feed");
    String id = target.getParameter("entry");
    Entry entry = (Entry) request.getDocument().getRoot();
    entry.setEdited(new Date());
    Link link = entry.getEditLink();
    Map<String,Object> params = new HashMap<String,Object>();
    params.put("feed", feed);
    params.put("entry", id);
    String href = request.absoluteUrlFor("entry", params);
    if (link == null) {
      entry.addLink(href, "edit");
    } else {
      link.setHref(href);
    }
  }
  
  public ResponseContext postEntry(
    RequestContext request) {
      Target target = request.getTarget();
      String feed = target.getParameter("feed");
      Session session = new Session(host,port);
      Database db = session.getDatabase(feed);
      try {
        CharArrayWriter cwriter = new CharArrayWriter();
        setEditDetail(request);
        request.getDocument().getRoot().writeTo("json", cwriter);
        String json = new String(cwriter.toCharArray());
        JSONObject obj = JSONObject.fromObject(json);
        String key = createKey(request);
        Document doc = null;
        try {
          doc = db.getDocument(key);
        } catch (Exception e) {}
        if (doc != null) { 
          return ProviderHelper.conflict(
            request, 
            "Entry with that key already exists");
        } else {
          doc = new Document(obj);
          doc.setId(key);
          db.saveDocument(doc);
          doc = db.getDocument(key);
          if (doc != null) {
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("feed", feed);
            params.put("entry", key);
            String urlFor = request.absoluteUrlFor("entry", params);
            return new JsonObjectResponseContext(
              request.getAbdera(),
              config,
              doc)
                .setStatus(201)
                .setEntityTag(doc.getRev())
                .setLocation(urlFor);
          } else { 
            return ProviderHelper.servererror(request, null);
          }
        }
      } catch (IOException e) {
        return ProviderHelper.servererror(request, e);
      }
  }
  
  private String createKey(RequestContext request) throws IOException {
    String slug = request.getSlug();
    if (slug == null) {
      slug = ((Entry)request.getDocument().getRoot()).getTitle();
    }
    return Sanitizer.sanitize(slug, "", true, Normalizer.Form.D);
  }
  
  public ResponseContext getEntry(
    RequestContext request) {
      Target target = request.getTarget();
      String feed = target.getParameter("feed");
      String entry = target.getParameter("entry");
      Session session = new Session(host,port);
      Database db = session.getDatabase(feed);
      Document doc = null;
      try {
        doc = db.getDocument(entry);
      } catch (Exception e) {}
      if (doc != null)
        return new JsonObjectResponseContext(
          request.getAbdera(),
          config,
          doc)
            .setStatus(200)
            .setEntityTag(doc.getRev());
      else 
        return ProviderHelper.notfound(request);
  }
  
  public ResponseContext putEntry(
    RequestContext request) {
      Target target = request.getTarget();
      String feed = target.getParameter("feed");
      Session session = new Session(host,port);
      Database db = session.getDatabase(feed);
      try {
        CharArrayWriter cwriter = new CharArrayWriter();
        setEditDetail(request);
        request.getDocument().getRoot().writeTo("json", cwriter);
        String json = new String(cwriter.toCharArray());
        JSONObject obj = JSONObject.fromObject(json);
        String key = target.getParameter("entry");
        Document doc = null;
        try {
          doc = db.getDocument(key);
        } catch (Exception e) {}
        if (doc == null) { 
          return ProviderHelper.notfound(request);
        } else {
          db.deleteDocument(doc);
          doc = new Document(obj);
          doc.setId(key);
          db.saveDocument(doc);
          doc = db.getDocument(key);
          if (doc != null)
            return new JsonObjectResponseContext(
              request.getAbdera(),
              config,
              doc)
                .setStatus(200)
                .setEntityTag(doc.getRev());
          else 
            return ProviderHelper.servererror(request, null);
        }
      } catch (IOException e) {
        return ProviderHelper.servererror(request, e);
      }
  }

  public ResponseContext getCategories(
    RequestContext request) {
      return ProviderHelper.notfound(request);
  }
    
  public ResponseContext extensionRequest(
    RequestContext request) {
    return ProviderHelper.notfound(request);
  }

  private static class DocumentComparator 
    implements Comparator<Document> {
      public int compare(
        Document doc1, 
        Document doc2) {
          String ed1 = doc1.getString("edited");
          String ed2 = doc2.getString("edited");
          Date d1 = AtomDate.parse(ed1);
          Date d2 = AtomDate.parse(ed2);
          if (d1.before(d2)) return 1;
          else if (d1.after(d2)) return -1;
          else return 0;
      }
  }
}
