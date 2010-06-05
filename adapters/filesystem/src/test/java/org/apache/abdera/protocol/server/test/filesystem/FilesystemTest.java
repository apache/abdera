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
package org.apache.abdera.protocol.server.test.filesystem;

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
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.provider.basic.BasicProvider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

@Ignore
public class FilesystemTest {

    private static Server server;
    private static Abdera abdera = Abdera.getInstance();
    private static AbderaClient client = new AbderaClient();

    @BeforeClass
    public static void setUp() throws Exception {
        if (server == null) {
            server = new Server(9002);
            Context context = new Context(server, "/", Context.SESSIONS);
            ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
            servletHolder.setInitParameter(ServiceManager.PROVIDER, BasicProvider.class.getName());
            context.addServlet(servletHolder, "/*");
            server.start();
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
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.APP_MEDIA_TYPE));
        Document<Service> doc = resp.getDocument();
        Service service = doc.getRoot();
        assertEquals(1, service.getWorkspaces().size());
        Workspace workspace = service.getWorkspace("Abdera");
        assertEquals(2, workspace.getCollections().size());
        Collection collection = workspace.getCollection("Filesystem Feed");
        assertNotNull(collection);
        assertTrue(collection.acceptsEntry());
        assertEquals("http://localhost:9002/fs", collection.getResolvedHref().toString());
    }

    @Test
    public void testGetFeed() {
        ClientResponse resp = client.get("http://localhost:9002/fs");
        assertNotNull(resp);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.ATOM_MEDIA_TYPE));
        Document<Feed> doc = resp.getDocument();
        Feed feed = doc.getRoot();
        assertEquals("http://localhost:9002/fs", feed.getId().toString());
        assertEquals("Filesystem Feed", feed.getTitle());
        assertEquals("james", feed.getAuthor().getName());
        assertEquals(0, feed.getEntries().size());
        resp.release();
    }

    @Test
    public void testPostEntry() {
        Entry entry = abdera.newEntry();
        entry.setId("http://localhost:9002/fs/foo");
        entry.setTitle("test entry");
        entry.setContent("Test Content");
        entry.addLink("http://example.org");
        entry.setUpdated(new Date());
        entry.addAuthor("James");
        ClientResponse resp = client.post("http://localhost:9002/fs", entry);
        assertNotNull(resp);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertEquals(201, resp.getStatus());
        assertEquals("http://localhost:9002/fs/test_entry", resp.getLocation().toString());
        resp = client.get("http://localhost:9002/fs");
        Document<Feed> feed_doc = resp.getDocument();
        Feed feed = feed_doc.getRoot();
        assertEquals(1, feed.getEntries().size());
        resp.release();
    }

    @Test
    public void testPostMedia() {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
        RequestOptions options = client.getDefaultRequestOptions();
        options.setContentType("application/octet-stream");
        ClientResponse resp = client.post("http://localhost:9002/fs", in, options);
        assertEquals(ResponseType.CLIENT_ERROR, resp.getType());
        assertEquals(415, resp.getStatus());
        resp.release();
    }

    @Test
    public void testPutEntry() {
        ClientResponse resp = client.get("http://localhost:9002/fs/test_entry");
        Document<Entry> doc = resp.getDocument();
        Entry entry = (Entry)doc.getRoot().clone();
        entry.setTitle("This is the modified title");
        resp.release();
        resp = client.put("http://localhost:9002/fs/test_entry", entry);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertEquals(200, resp.getStatus());
        resp.release();
        resp = client.get("http://localhost:9002/fs/test_entry");
        doc = resp.getDocument();
        entry = doc.getRoot();
        assertEquals("This is the modified title", entry.getTitle());
        resp.release();
        resp = client.get("http://localhost:9002/fs");
        Document<Feed> feed_doc = resp.getDocument();
        Feed feed = feed_doc.getRoot();
        assertEquals(1, feed.getEntries().size());
        resp.release();
    }

    @Test
    public void testDeleteEntry() {
        ClientResponse resp = client.delete("http://localhost:9002/fs/test_entry");
        assertEquals(ResponseType.SUCCESS, resp.getType());
        resp.release();
        resp = client.get("http://localhost:9002/fs");
        Document<Feed> feed_doc = resp.getDocument();
        Feed feed = feed_doc.getRoot();
        assertEquals(0, feed.getEntries().size());
        resp.release();
    }
}
