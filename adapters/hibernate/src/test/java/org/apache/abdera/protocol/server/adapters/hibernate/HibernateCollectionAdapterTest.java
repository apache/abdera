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
package org.apache.abdera.protocol.server.adapters.hibernate;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.provider.basic.BasicProvider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import static org.junit.Assert.*;

public class HibernateCollectionAdapterTest {

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
	
	@Test
	public void testGetFeed() {
		ClientResponse resp = client.get("http://localhost:9002/hibernate");
		assertNotNull(resp);
		assertEquals(ResponseType.SUCCESS, resp.getType());
	    assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(), Constants.ATOM_MEDIA_TYPE));
	    Document<Feed> doc = resp.getDocument();
	    Feed feed = doc.getRoot();
	    assertEquals("http://localhost:9002/hibernate", feed.getId().toString());	    
	    assertEquals("david", feed.getAuthor().getName());
	    assertEquals(0, feed.getEntries().size());
	    resp.release();	    
	}
	
	@Test
	public void testCreateEntry() {		
		Entry entry = abdera.newEntry();
		entry.setId("foo");
		entry.setTitle("test entry");
		entry.setContent("Test Content");
		entry.addLink("http://example.org");
		entry.setUpdated(new Date());
		entry.addAuthor("david");
		ClientResponse resp = client.post("http://localhost:9002/hibernate", entry);
		assertNotNull(resp);
		assertEquals(ResponseType.SUCCESS, resp.getType());
		assertEquals(201, resp.getStatus());
		assertEquals("http://localhost:9002/hibernate/foo", resp.getLocation().toString());
		resp = client.get("http://localhost:9002/hibernate");
		Document<Feed> feed_doc = resp.getDocument();
		Feed feed = feed_doc.getRoot();
		assertEquals(feed.getEntries().size(), 1);
		resp.release();
	}
	
	@Test
	public void testUpdateEntry() {		
		ClientResponse resp = client.get("http://localhost:9002/hibernate/foo");
	    Document<Entry> doc = resp.getDocument();
	    Entry entry = (Entry) doc.getRoot().clone();
	    entry.setTitle("This is the modified title");
	    resp.release();
	    resp = client.put("http://localhost:9002/hibernate/foo", entry);
	    assertEquals(ResponseType.SUCCESS, resp.getType());
	    assertEquals(200, resp.getStatus());
	    resp.release();
	    resp = client.get("http://localhost:9002/hibernate/foo");
	    doc = resp.getDocument();
	    entry = doc.getRoot();
	    assertEquals("This is the modified title", entry.getTitle());
	    resp.release();
	    resp = client.get("http://localhost:9002/hibernate");
	    Document<Feed> feed_doc = resp.getDocument();
	    Feed feed = feed_doc.getRoot();
	    assertEquals(1, feed.getEntries().size());
	    resp.release();
	}
	
	@Test
	public void testDeleteEntry() {		
		ClientResponse resp = client.delete("http://localhost:9002/hibernate/foo");
		assertEquals(ResponseType.SUCCESS, resp.getType());
		resp.release();
		resp = client.get("http://localhost:9002/hibernate");
		Document<Feed> feed_doc = resp.getDocument();
		Feed feed = feed_doc.getRoot();
		assertEquals(0, feed.getEntries().size());
		resp.release();
	}

}
