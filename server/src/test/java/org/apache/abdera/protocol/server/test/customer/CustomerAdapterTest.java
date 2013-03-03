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
package org.apache.abdera.protocol.server.test.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.After;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class CustomerAdapterTest {

    private int port;
    private Server server;
    private DefaultProvider customerProvider;

    private void setupAbdera(String base) throws Exception {
        customerProvider = new DefaultProvider(base);

        CustomerAdapter ca = new CustomerAdapter();
        ca.setHref("customers");

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Customer Workspace");
        wi.addCollection(ca);

        customerProvider.addWorkspace(wi);
    }

    @Test
    public void testCustomerProvider() throws Exception {
        setupAbdera("/");
        initializeJetty("/");

        runTests("/");
    }

    @Test
    public void testCustomerProviderWithNonRootContextPath() throws Exception {
        setupAbdera("/");
        initializeJetty("/foo");

        runTests("/foo/");
    }

    private void runTests(String base) throws IOException {
        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();

        AbderaClient client = new AbderaClient(abdera);

        String uri = "http://localhost:" + port + base;

        // Service document test.

        ClientResponse res = client.get(uri);
        assertNotNull(res);
        try {
            assertEquals(200, res.getStatus());
            assertEquals(ResponseType.SUCCESS, res.getType());
            assertTrue(MimeTypeHelper.isMatch(res.getContentType().toString(), Constants.APP_MEDIA_TYPE));

            Document<Service> doc = res.getDocument();
            Service service = doc.getRoot();
            assertEquals(1, service.getWorkspaces().size());

            Workspace workspace = service.getWorkspaces().get(0);
            assertEquals(1, workspace.getCollections().size());

            // Keep the loop in case we add other collections to the test.

            for (Collection collection : workspace.getCollections()) {
                if (collection.getTitle().equals("Acme Customer Database")) {
                    String expected = uri + "customers";
                    String actual = collection.getResolvedHref().toString();
                    assertEquals(expected, actual);
                }
            }
        } finally {
            res.release();
        }

        // Testing of entry creation
        IRI colUri = new IRI(uri).resolve("customers");

        Entry entry = factory.newEntry();
        entry.setTitle("This is ignored right now");
        entry.setUpdated(new Date());
        entry.addAuthor("Acme Industries");
        entry.setId(factory.newUuidUri());
        entry.setSummary("Customer document");

        Element customerEl = factory.newElement(new QName("customer"));
        customerEl.setAttributeValue(new QName("name"), "Dan Diephouse");
        entry.setContent(customerEl);

        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/atom+xml;type=entry");
        res = client.post(colUri.toString() + "?test=foo", entry, opts);
        assertEquals(201, res.getStatus());

        // prettyPrint(abdera, res.getDocument());

        IRI location = res.getLocation();
        assertEquals(uri + "customers/1001-Dan_Diephouse", location.toString());

        // GET the entry
        res = client.get(location.toString());
        assertEquals(200, res.getStatus());
        res.release();

        // prettyPrint(abdera, res.getDocument());
        org.apache.abdera.model.Document<Entry> entry_doc = res.getDocument();
        // prettyPrint(abdera, entry_doc);
        entry = entry_doc.getRoot();
        assertEquals(uri + "customers/1001-Dan_Diephouse", entry_doc.getRoot().getEditLinkResolvedHref().toString());

        // HEAD
        res = client.head(location.toString());
        assertEquals(200, res.getStatus());
        assertEquals(0, res.getContentLength());
        res.release();

        // Try invalid resources
        res = client.get(colUri + "/foobar");
        assertEquals(404, res.getStatus());
        res.release();

        res = client.head(colUri + "/foobar");
        assertEquals(404, res.getStatus());
        assertEquals(0, res.getContentLength());
        res.release();

        IRI badColUri = new IRI(uri).resolve("customersbad");
        // GET the service doc
        res = client.get(colUri.toString());
        assertEquals(200, res.getStatus());
        res.release();
        res = client.get(badColUri.toString());
        assertEquals(404, res.getStatus());
        res.release();
    }

    protected void prettyPrint(Abdera abdera, Base doc) throws IOException {
        WriterFactory factory = abdera.getWriterFactory();
        Writer writer = factory.getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
    }

    @SuppressWarnings("serial")
    private void initializeJetty(String contextPath) throws Exception {
        port = PortAllocator.allocatePort();
        server = new Server(port);
        Context root = new Context(server, contextPath, Context.NO_SESSIONS);
        root.addServlet(new ServletHolder(new AbderaServlet() {

            @Override
            protected Provider createProvider() {
                customerProvider.init(getAbdera(), null);
                return customerProvider;
            }
        }), "/*");
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();
    }

}
