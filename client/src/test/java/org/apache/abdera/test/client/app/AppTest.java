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
package org.apache.abdera.test.client.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.Response;
import org.apache.abdera.test.client.JettyTest;
import org.apache.abdera.util.MimeTypeHelper;
import org.mortbay.jetty.servlet.ServletHandler;

/**
 * Test to make sure that we can operate as a simple APP client
 */
@SuppressWarnings("serial")
public class AppTest extends JettyTest {

  private static Abdera abdera = new Abdera();
  
  private static Factory getFactory() {
    return abdera.getFactory();
  }
  
  private static Parser getParser() {
    return abdera.getParser();
  }
  
  private static ServletHandler handler = 
    JettyTest.getServletHandler(
      ServiceDocumentServlet.class.getName(), "/service",
      CollectionServlet.class.getName(), "/collections/*"
    );
  
  private static AppTest INSTANCE = null;
  
  private static Document<Service> init_service_document(String base) {
    try {
      Service service = getFactory().newService();
      Workspace workspace = service.addWorkspace("Test");
      workspace.addCollection("Entries", base + "/collections/entries").setAccept("entry");
      workspace.addCollection("Other", base + "/collections/other").setAccept("text/plain");
      Document<Service> doc = service.getDocument();
      return doc;
    } catch (Exception e) {}
    return null;
  }
  
  private static Document<Feed> init_entries_document(String base) {
    try {
      Feed feed = getFactory().newFeed();
      feed.setId(base + "/collections/entries");
      feed.setTitle("Entries");
      feed.setUpdated(new Date());
      feed.addLink(base + "/collections/entries");
      feed.addLink(base + "/collections/entries", "self");
      feed.addAuthor("James");
      Document<Feed> doc = feed.getDocument();
      return doc;
    } catch (Exception e) {}
    return null;
  }
    
  public static class ServiceDocumentServlet extends HttpServlet {
    private Document<Service> service = 
      init_service_document(AppTest.INSTANCE.getBase());
    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response) 
        throws ServletException, IOException {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/atomserv+xml; charset=utf-8");
      service.writeTo(response.getOutputStream());
    }
  }
  
  /**
   * this implements a very simple (and quite buggy) APP server. It's just
   * enough for us to test the client behaviors.  I'm sure it could be 
   * greatly improved.
   */
  public static class CollectionServlet extends HttpServlet {
    protected Document<Feed> feed = init_entries_document(AppTest.INSTANCE.getBase());
    protected Map<String,String> media = new HashMap<String,String>();
    
    private String[] tokens = null;
    private final static int COLLECTION = 0;
    private final static int ENTRY = 1;
    private final static int MEDIA = 2;
    
    private int getTargetType(HttpServletRequest request) {
      tokens = request.getRequestURI().split("/");
      if (tokens[2].equals("entries") && tokens.length == 3) return COLLECTION;
      if (tokens[2].equals("entries") && tokens.length == 4) return ENTRY;
      if (tokens[2].equals("media") && tokens.length == 4) return MEDIA;
      return -1;
    }
    
    private int getTarget() {
      return (tokens.length != 4) ? -1 : Integer.parseInt(tokens[3]);
    }
    
    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response) 
        throws ServletException, IOException {
      int t = getTargetType(request);
      switch(t) {
        case COLLECTION:
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentType("application/atom+xml; charset=utf-8");
          feed.writeTo(response.getOutputStream());
          break;
        case ENTRY:
          try {
            Entry entry = feed.getRoot().getEntries().get(getTarget());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/atom+xml; charset=utf-8");
            entry.writeTo(response.getOutputStream());
          } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            break;
          }
          break;
        case MEDIA:
          try {
            String m = media.get(AppTest.INSTANCE.getBase() + "/collections/entries/" + getTarget());
            if (m != null) {
              response.setStatus(HttpServletResponse.SC_OK);
              response.setContentType("text/plain");
              response.getWriter().write(m);
              break;
            } else {
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
          } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
          }
      }
    }
    protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response)
        throws ServletException, IOException {
      int t = getTargetType(request);
      switch(t) {
        case COLLECTION:
        try {
          if (MimeTypeHelper.isMatch(
            request.getContentType(), 
            "application/atom+xml")) {
            MimeType type = new MimeType(request.getContentType());
            String charset = type.getParameter("charset");
            String uri = AppTest.INSTANCE.getBase() + "/collections/entries";
            ParserOptions options = getParser().getDefaultParserOptions();
            options.setCharset(charset);
            Document doc = getParser().parse(request.getInputStream(), uri, options);
            if (doc.getRoot() instanceof Entry) {
              Entry entry = (Entry) doc.getRoot().clone();
              String newID = AppTest.INSTANCE.getBase() + "/collections/entries/" + feed.getRoot().getEntries().size();
              entry.setId(newID);
              entry.setUpdated(new Date());
              entry.addLink(entry.getId().toString(), "edit");
              entry.addLink(entry.getId().toString(), "self");
              feed.getRoot().insertEntry(entry);
              response.setStatus(HttpServletResponse.SC_CREATED);
              response.setHeader("Location", entry.getId().toString());
              response.setHeader("Content-Location", entry.getId().toString());
              entry.writeTo(response.getOutputStream());
              return;
            }
          }
          if (MimeTypeHelper.isMatch(request.getContentType(), "text/plain")) {
            int n = feed.getRoot().getEntries().size();
            String media = read(request.getInputStream());
            Entry entry = getFactory().newEntry();
            String newID = AppTest.INSTANCE.getBase() + "/collections/entries/" + n;
            String slug = request.getHeader("Slug");
            entry.setId(newID);
            entry.setTitle(slug);
            entry.setUpdated(new Date());
            entry.setSummary(slug);
            entry.addLink(entry.getId().toString(), "edit");
            entry.addLink(AppTest.INSTANCE.getBase() + "/collections/media/" + n, "edit-media").setMimeType("text/plain");
            entry.addLink(entry.getId().toString(), "self");
            entry.setContent(new URI(AppTest.INSTANCE.getBase() + "/collections/media/" + n), "text/plain");
            feed.getRoot().insertEntry(entry);
            this.media.put(entry.getId().toString(), media);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setHeader("Location", entry.getId().toString());
            response.setHeader("Content-Location", entry.getId().toString());
            entry.writeTo(response.getOutputStream());
            return;
          }
          response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        } catch (Exception e) {}
        break;
        default:
          response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      }
    }
    
    protected void doPut(
        HttpServletRequest request,
        HttpServletResponse response)
          throws ServletException, IOException {
        int t = getTargetType(request);
        int target = getTarget();
        switch(t) {
          case ENTRY:
            try {
              if (MimeTypeHelper.isMatch(request.getContentType(), "application/atom+xml")) {
                Entry entry = feed.getRoot().getEntries().get(target);
                MimeType type = new MimeType(request.getContentType());
                String charset = type.getParameter("charset");
                String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
                ParserOptions options = getParser().getDefaultParserOptions();
                options.setCharset(charset);
                Document doc = getParser().parse(request.getInputStream(), uri, options);
                if (doc.getRoot() instanceof Entry) {
                  Entry newentry = (Entry) doc.getRoot().clone();
                  if (newentry.getId().equals(entry.getId())) {
                    newentry.setUpdated(new Date());
                    entry.discard();
                    feed.getRoot().insertEntry(newentry);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                  } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot change atom:id");
                    return;
                  }
                }
              }
              response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            } catch (Exception e) {}
            break;
          case MEDIA:
            if (MimeTypeHelper.isMatch(request.getContentType(), "text/plain")) {
              String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
              String media = read(request.getInputStream());
              this.media.put(uri,media);
              Entry entry = feed.getRoot().getEntries().get(target);
              entry.setUpdated(new Date());
              response.setStatus(HttpServletResponse.SC_NO_CONTENT);
              return;
            }
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            break;
          default:
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    
    protected void doDelete(
        HttpServletRequest request,
        HttpServletResponse response)
          throws ServletException, IOException {
        int t = getTargetType(request);
        int target = getTarget();
        switch(t) {
          case ENTRY:
          case MEDIA:
            String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
            Entry entry = feed.getRoot().getEntries().get(target);
            entry.discard();
            media.remove(uri);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
          default:
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);   
        }
    }
  }
  
  private static String read(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int m = -1;
    while ((m = in.read()) != -1) {
      out.write(m);
    }
    String resp = new String(out.toByteArray());
    return resp.trim();
  }
  
  public AppTest() {
    super(1);
    AppTest.INSTANCE = this;
  }
  
  protected ServletHandler getServletHandler() {
    return AppTest.handler;
  }  
  
  public void testAppClient() throws Exception {
    
    Client client = new CommonsClient();
    Entry entry = getFactory().newEntry();
    
    // do the introspection step
    Response response = client.get("http://localhost:8080/service");
    assertEquals(200,response.getStatus());
    Document<Service> service_doc = response.getDocument();
    assertNotNull(service_doc);
    assertEquals(1,service_doc.getRoot().getWorkspaces().size());
    Workspace workspace = service_doc.getRoot().getWorkspace("Test");
    assertNotNull(workspace);
    for (Collection c: workspace.getCollections()) {
      assertNotNull(c.getTitle());
      assertNotNull(c.getHref());
    }
    
    String col_uri = getBase() + "/collections/entries";
    
    // post a new entry
    response = client.post(col_uri, entry);
    assertEquals(201, response.getStatus());
    assertNotNull(response.getLocation());
    assertNotNull(response.getContentLocation());
    
    String self_uri = response.getLocation();
    
    // get the collection to see if our entry is there
    response = client.get(col_uri);
    assertEquals(200, response.getStatus());
    Document<Feed> feed_doc = response.getDocument();
    assertEquals(feed_doc.getRoot().getEntries().size(), 1);
    
    // get the entry to see if we can get it
    response = client.get(self_uri);
    assertEquals(200, response.getStatus());
    Document<Entry> entry_doc = response.getDocument();
    assertEquals(entry_doc.getRoot().getId().toString(), self_uri); // this isn't always true in real life, but for our tests they are the same
    
    // get the edit uri from the entry
    String edit_uri = entry_doc.getRoot().getEditLink().getHref().toString();
    
    // change the entry
    Document<Entry> doc = response.getDocument();
    entry = (Entry) doc.getRoot().clone();
    entry.setTitle("New title");
    
    // submit the changed entry back to the server
    response = client.put(edit_uri, entry);
    assertEquals(204, response.getStatus());

    // check to see if the entry was modified properly
    response = client.get(self_uri);
    assertEquals(200, response.getStatus());
    entry_doc = response.getDocument();
    assertEquals(entry_doc.getRoot().getTitle(), "New title");
    
    // delete the entry
    response = client.delete(edit_uri);
    assertEquals(204, response.getStatus());
    
    // is it gone?
    response = client.get(self_uri);
    assertEquals(404, response.getStatus());
    
    // YAY! We're a working APP client
    
    // Now let's try to do a media post
    
    // Post the media resource
    RequestOptions options = new RequestOptions();
    options.setContentType("text/plain");
    response = client.post(col_uri, new ByteArrayInputStream("test".getBytes()), options);
    assertEquals(201, response.getStatus());
    assertNotNull(response.getLocation());
    assertNotNull(response.getContentLocation());
    
    self_uri = response.getLocation();

    // was an entry created?
    response = client.get(self_uri);
    assertEquals(200, response.getStatus());
    entry_doc = response.getDocument();
    assertEquals(entry_doc.getRoot().getId().toString(), self_uri); // this isn't always true in real life, but for our tests they are the same
    
    // get the right links from the entry
    edit_uri = entry_doc.getRoot().getEditLink().getHref().toString();
    String edit_media = entry_doc.getRoot().getLink("edit-media").getHref().toString();
    String media = entry_doc.getRoot().getContentElement().getSrc().toString();
    
    // edit the entry
    doc = response.getDocument();
    entry = (Entry) doc.getRoot().clone();
    entry.setTitle("New title");
    
    // submit the changes
    response = client.put(edit_uri, entry);
    assertEquals(204, response.getStatus());

    // get the media resource
    response = client.get(media);
    assertEquals(200, response.getStatus());
    String mediavalue = read(response.getInputStream());
    assertEquals(mediavalue,"test");
    
    // edit the media resource
    response = client.put(edit_media, new ByteArrayInputStream("TEST".getBytes()), options);
    assertEquals(204, response.getStatus());

    // was the resource changed?
    response = client.get(media);
    assertEquals(200, response.getStatus());
    mediavalue = read(response.getInputStream());
    assertEquals(mediavalue,"TEST");
    
    // delete the entry
    response = client.delete(edit_uri);
    assertEquals(204, response.getStatus());
    
    // is the entry gone?
    response = client.get(self_uri);
    assertEquals(404, response.getStatus());

    // is the media resource gone?
    options.setNoCache(true); // need to force revalidation to check
    response = client.get(media, options);
    assertEquals(404, response.getStatus());
    
    // YAY! We can handle media link entries 
    
  }
}
