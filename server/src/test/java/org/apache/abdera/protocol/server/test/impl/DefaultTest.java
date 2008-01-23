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
package org.apache.abdera.protocol.server.test.impl;

import java.io.ByteArrayInputStream;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.server.test.JettyServer;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;

public class DefaultTest 
  extends TestCase {

  private static JettyServer server;
  private static Abdera abdera = Abdera.getInstance();
  private static AbderaClient client = new AbderaClient();

  public DefaultTest() {
    try {
      if (server == null) {
        server = new JettyServer();
        server.start(TestProvider.class);
      }
    } catch (Exception e) {}
  }
  
  private int count = 7;
  
  @Override protected void tearDown() throws Exception {
    if (--count == 0) 
      server.stop();
  }
  
  public void testGetService() {
    ClientResponse resp = client.get("http://localhost:8080/atom");
    assertNotNull(resp);
    assertEquals(resp.getType(),ResponseType.SUCCESS);
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.APP_MEDIA_TYPE));
    Document<Service> doc = resp.getDocument();
    Service service = doc.getRoot();
    assertEquals(service.getWorkspaces().size(),1);
    Workspace workspace = service.getWorkspaces().get(0);
    assertEquals(workspace.getCollections().size(),2);
    Collection collection = workspace.getCollections().get(0);
    assertEquals(collection.getResolvedHref().toString(), "http://localhost:8080/atom/feed1");
    assertEquals(collection.getTitle().toString(), "entries1");
    collection = workspace.getCollections().get(1);
    assertEquals(collection.getResolvedHref().toString(), "http://localhost:8080/atom/feed2");
    assertEquals(collection.getTitle().toString(), "entries2");
    resp.release();
  }
  
  public void testGetCategories() {
    ClientResponse resp = client.get("http://localhost:8080/atom/feed1;categories");
    assertNotNull(resp);
    assertEquals(resp.getType(),ResponseType.SUCCESS);
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.CAT_MEDIA_TYPE));
    Document<Categories> doc = resp.getDocument();
    Categories cats = doc.getRoot();
    assertEquals(cats.getCategories().size(), 3);
    assertEquals(cats.getCategories().get(0).getTerm(), "foo");
    assertEquals(cats.getCategories().get(1).getTerm(), "bar");
    assertEquals(cats.getCategories().get(2).getTerm(), "baz");
    assertFalse(cats.isFixed());
  }
  
  public void testGetFeed() {
    ClientResponse resp = client.get("http://localhost:8080/atom/feed1");
    assertNotNull(resp);
    assertEquals(resp.getType(),ResponseType.SUCCESS);
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.ATOM_MEDIA_TYPE));
    Document<Feed> doc = resp.getDocument();
    Feed feed = doc.getRoot();
    assertEquals(feed.getId().toString(), "tag:example.org,2006:feed");
    assertEquals(feed.getTitle(), "Simple");
    assertEquals(feed.getAuthor().getName(), "Simple");
    assertEquals(feed.getEntries().size(), 0);
    resp.release();
  }
  
  public void testPostEntry() {
    Entry entry = abdera.newEntry();
    entry.setId("http://localhost:8080/atom/feed1/entries/1");
    entry.setTitle("test entry");
    entry.setContent("Test Content");
    entry.addLink("http://example.org");
    entry.setUpdated(new Date());
    entry.addAuthor("James");
    ClientResponse resp = client.post("http://localhost:8080/atom/feed1", entry);
    assertNotNull(resp);
    assertEquals(resp.getType(),ResponseType.SUCCESS);
    assertEquals(resp.getStatus(), 201);
    assertNotNull(resp.getLocation());
    resp.release();
    resp = client.get("http://localhost:8080/atom/feed1");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(),1);
  }
  
  public void testPostMedia() {
    ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01,0x02,0x03,0x04});
    RequestOptions options = client.getDefaultRequestOptions();
    options.setContentType("application/octet-stream");
    ClientResponse resp = client.post("http://localhost:8080/atom/feed1", in, options);
    assertEquals(resp.getType(),ResponseType.CLIENT_ERROR);
    assertEquals(resp.getStatus(), 415);
    resp.release();
  }
  
  public void testPutEntry() {
    ClientResponse resp = client.get("http://localhost:8080/atom/feed1");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    String edit = entry.getEditLinkResolvedHref().toString();
    resp.release();
    resp = client.get(edit);
    Document<Entry> doc = resp.getDocument();
    entry = doc.getRoot();
    entry.setTitle("This is the modified title");
    resp.release();
    resp = client.put(edit, entry);
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    assertEquals(resp.getStatus(), 204);
    resp.release();
    resp = client.get(edit);
    doc = resp.getDocument();
    entry = doc.getRoot();
    assertEquals(entry.getTitle(), "This is the modified title");
    resp.release();
    resp = client.get("http://localhost:8080/atom/feed1");
    feed_doc = resp.getDocument();
    feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(),1);
    resp.release();
  }
  
  public void testDeleteEntry() {
    ClientResponse resp = client.get("http://localhost:8080/atom/feed1");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    String edit = entry.getEditLinkResolvedHref().toString();
    resp.release();
    resp = client.delete(edit);    
    assertEquals(resp.getType(),ResponseType.SUCCESS);
    resp.release();
    resp = client.get("http://localhost:8080/atom/feed1");
    feed_doc = resp.getDocument();
    feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(),0);
    resp.release();
  }
}
