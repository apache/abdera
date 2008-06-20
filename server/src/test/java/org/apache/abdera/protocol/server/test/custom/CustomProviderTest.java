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
package org.apache.abdera.protocol.server.test.custom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomProviderTest extends Assert {

  private static JettyServer server;
  private static Abdera abdera = Abdera.getInstance();
  private static AbderaClient client = new AbderaClient();
  
  private static String BASE = "http://localhost:9002/atom";
  
  @BeforeClass
  public static void setUp() throws Exception {
    try {
      server = new JettyServer();
      server.start(CustomProvider.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception {
    server.stop();
  }

  @Test
  public void testGetService() throws IOException {
    ClientResponse resp = client.get(BASE);
    assertNotNull(resp);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.APP_MEDIA_TYPE));
    Document<Service> doc = resp.getDocument();
try {
prettyPrint(doc);
} catch (Exception e) {}
    Service service = doc.getRoot();
    prettyPrint(service);
    assertEquals(service.getWorkspaces().size(), 1);
    Workspace workspace = service.getWorkspaces().get(0);
    assertEquals(workspace.getCollections().size(), 1);
    Collection collection = workspace.getCollections().get(0);
    assertEquals(collection.getResolvedHref().toString(), BASE + "/feed");
    assertEquals(collection.getTitle().toString(), "A simple feed");
    resp.release();
  }

  @Test
  public void testGetCategories() {
    ClientResponse resp = client.get(BASE + "/feed;categories");
    assertNotNull(resp);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.CAT_MEDIA_TYPE));
    Document<Categories> doc = resp.getDocument();
    Categories cats = doc.getRoot();
    assertEquals(cats.getCategories().size(), 3);
    assertEquals(cats.getCategories().get(0).getTerm(), "foo");
    assertEquals(cats.getCategories().get(1).getTerm(), "bar");
    assertEquals(cats.getCategories().get(2).getTerm(), "baz");
    assertFalse(cats.isFixed());
  }

  @Test
  public void testGetFeed() throws Exception {
    ClientResponse resp = client.get(BASE + "/feed");
    assertNotNull(resp);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.ATOM_MEDIA_TYPE));
    Document<Feed> doc = resp.getDocument();
    Feed feed = doc.getRoot();
    assertEquals("tag:example.org,2008:feed", feed.getId().toString());
    assertEquals(feed.getTitle(), "A simple feed");
    assertEquals(feed.getAuthor().getName(), "Simple McGee");
    assertEquals(feed.getEntries().size(), 0);
    resp.release();
  }

  protected void prettyPrint(Base doc) throws IOException {
//      WriterFactory writerFactory = abdera.getWriterFactory();
//      Writer writer = writerFactory.getWriter("prettyxml");
//      writer.writeTo(doc, System.out);
//      System.out.println();
  }


  @Test
  public void testPostEntry() {
    Entry entry = abdera.newEntry();
    entry.setId(BASE + "/feed/entries/1");
    entry.setTitle("test entry");
    entry.setContent("Test Content");
    entry.addLink("http://example.org");
    entry.setUpdated(new Date());
    entry.addAuthor("James");
    ClientResponse resp = client.post(BASE + "/feed", entry);
    assertNotNull(resp);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    assertEquals(resp.getStatus(), 201);
    assertNotNull(resp.getLocation());
    resp.release();
    resp = client.get(BASE + "/feed");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 1);
  }

  @Test
  public void testPostMedia() {
    ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
    RequestOptions options = client.getDefaultRequestOptions();
    options.setContentType("application/octet-stream");
    ClientResponse resp = client.post(BASE + "/feed", in, options);
    assertEquals(resp.getType(), ResponseType.CLIENT_ERROR);
    assertEquals(resp.getStatus(), 405);
    resp.release();
  }

  @Test
  public void testPutEntry() throws IOException {
    ClientResponse resp = client.get(BASE + "/feed");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    prettyPrint(feed);
    Entry entry = feed.getEntries().get(0);
    String edit = entry.getEditLinkResolvedHref().toString();
    resp.release();
    resp = client.get(edit);
    Document<Entry> doc = resp.getDocument();
    doc.writeTo(System.out);
    prettyPrint(doc.getRoot());
    entry = doc.getRoot();
    entry.setTitle("This is the modified title");
    resp.release();
    resp = client.put(edit, entry);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    assertEquals(resp.getStatus(), 204);
    resp.release();
    resp = client.get(edit);
    doc = resp.getDocument();
    entry = doc.getRoot();
    assertEquals(entry.getTitle(), "This is the modified title");
    resp.release();
    resp = client.get(BASE + "/feed");
    feed_doc = resp.getDocument();
    feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 1);
    resp.release();
  }

  @Test
  public void testDeleteEntry() {
    ClientResponse resp = client.get(BASE + "/feed");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    String edit = entry.getEditLinkResolvedHref().toString();
    resp.release();
    resp = client.delete(edit);
    assertEquals(ResponseType.SUCCESS, resp.getType());
    resp.release();
    resp = client.get(BASE + "/feed");
    feed_doc = resp.getDocument();
    feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 0);
    resp.release();
  }
}
