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
package org.apache.abdera.protocol.server.test.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
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
import org.apache.abdera.protocol.server.provider.basic.BasicProvider;
import org.apache.abdera.protocol.server.test.JettyServer;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class BasicTest {

  private static JettyServer server;
  private static Abdera abdera = Abdera.getInstance();
  private static AbderaClient client = new AbderaClient();

  @BeforeClass
  public static void setUp() throws Exception {
    if (server == null) {
      server = new JettyServer();
      server.start(BasicProvider.class);
    }
  }
  
  @AfterClass
  public static void tearDown() throws Exception {
    server.stop();
  }

  protected void prettyPrint(Base doc) throws IOException {
    WriterFactory factory = abdera.getWriterFactory();
    Writer writer = factory.getWriter("prettyxml");
    writer.writeTo(doc, System.out);
    System.out.println();
  }

  @Test
  public void testGetService() {
    ClientResponse resp = client.get("http://localhost:9002/");
    assertNotNull(resp);
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.APP_MEDIA_TYPE));
    Document<Service> doc = resp.getDocument();
    Service service = doc.getRoot();
    assertEquals(service.getWorkspaces().size(), 1);
    Workspace workspace = service.getWorkspace("Abdera");
    assertEquals(workspace.getCollections().size(), 1);
    Collection collection = workspace.getCollection("title for any sample feed");
    assertNotNull(collection);
    assertTrue(collection.acceptsEntry());
    assertEquals(collection.getResolvedHref().toString(), "http://localhost:9002/sample");
    resp.release();
  }
  @Test
  public void testGetFeed() {
    ClientResponse resp = client.get("http://localhost:9002/sample");
    assertNotNull(resp);
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.FEED_MEDIA_TYPE));    
    Document<Feed> doc = resp.getDocument();    
    Feed feed = doc.getRoot();
    assertEquals(feed.getId().toString(), "http://localhost:9002/sample");
    assertEquals(feed.getTitle(), "title for any sample feed");
    assertEquals(feed.getAuthor().getName(), "rayc");
    assertEquals(feed.getEntries().size(), 0);
    resp.release();
  }
  @Test
  public void testPostEntry() {
    Entry entry = abdera.newEntry();
    entry.setId("http://localhost:9002/sample/foo");
    entry.setTitle("test entry");
    entry.setContent("Test Content");
    entry.addLink("http://example.org");
    entry.setUpdated(new Date());
    entry.addAuthor("James");
    ClientResponse resp = client.post("http://localhost:9002/sample", entry);
    assertNotNull(resp);
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    assertEquals(resp.getStatus(), 201);
    assertEquals(resp.getLocation().toString(), "http://localhost:9002/sample/foo");
    resp.release();
    resp = client.get("http://localhost:9002/sample");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 1);
    resp.release();
  }
  @Test
  public void testPostMedia() {
    ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
    RequestOptions options = client.getDefaultRequestOptions();
    options.setContentType("application/octet-stream");
    ClientResponse resp = client.post("http://localhost:9002/sample", in, options);
    assertEquals(resp.getType(), ResponseType.CLIENT_ERROR);
    assertEquals(resp.getStatus(), 405);
    resp.release();
  }
  @Test
  public void testPutEntry() {
    ClientResponse resp = client.get("http://localhost:9002/sample/foo");
    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.ENTRY_MEDIA_TYPE));
    Document<Entry> doc = resp.getDocument();
    Entry entry = doc.getRoot();
    entry.setTitle("This is the modified title");
    resp.release();
    resp = client.put("http://localhost:9002/sample/foo", entry);
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    assertEquals(resp.getStatus(), 200);
    resp.release();
    resp = client.get("http://localhost:9002/sample/foo");
    doc = resp.getDocument();
    entry = doc.getRoot();
    assertEquals(entry.getTitle(), "This is the modified title");
    resp.release();
    resp = client.get("http://localhost:9002/sample");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 1);
    resp.release();
  }

  @Test
  public void testDeleteEntry() {
    ClientResponse resp = client.delete("http://localhost:9002/sample/foo");
    assertEquals(resp.getType(), ResponseType.SUCCESS);
    resp.release();
    resp = client.get("http://localhost:9002/sample");
    Document<Feed> feed_doc = resp.getDocument();
    Feed feed = feed_doc.getRoot();
    assertEquals(feed.getEntries().size(), 0);
    resp.release();
  }
}
