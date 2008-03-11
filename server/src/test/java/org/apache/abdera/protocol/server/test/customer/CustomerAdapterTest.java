package org.apache.abdera.protocol.server.test.customer;

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
import org.junit.After;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import junit.framework.Assert;

public class CustomerAdapterTest extends Assert {

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

  @Test
  public void testCustomerProviderWithNonRootBase() throws Exception {
    setupAbdera("/:base/");
    initializeJetty("/");

    runTests("/base/");
  }

  @Test
  public void testCustomerProviderWithNonRootContextBaseAndBase() throws Exception {
    setupAbdera("/:base/");
    initializeJetty("/foo");

    runTests("/foo/base/");
  }

  private void runTests(String base) throws IOException {
    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();

    AbderaClient client = new AbderaClient(abdera);

    String uri = "http://localhost:9002" + base;

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

    prettyPrint(abdera, res.getDocument());

    IRI location = res.getLocation();
    assertEquals(base + "customers/1001-Dan_Diephouse", location.toString());

    // GET the entry
    res = client.get(colUri.resolve(location.toString()).toString());
    assertEquals(200, res.getStatus());
    res.release();

    // prettyPrint(abdera, res.getDocument());
    org.apache.abdera.model.Document<Entry> entry_doc = res.getDocument();
    prettyPrint(abdera, entry_doc);
    entry = entry_doc.getRoot();
    assertEquals(uri + "customers/1001-Dan_Diephouse", entry_doc.getRoot().getEditLinkResolvedHref().toString());

    // HEAD
    res = client.head(colUri.resolve(location.toString()).toString());
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

    server = new Server(9002);
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
