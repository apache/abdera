package org.apache.abdera.protocol.server.test.customer;

import java.io.IOException;
import java.util.Date;

import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.junit.After;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

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
    
    initializeJetty();
  }

  @Test
  public void testCustomerProvider() throws Exception {
    setupAbdera("/");
    
    String base = "http://localhost:9002/";

    runTests(base);
  }
  
  @Test
  public void testCustomerProviderWithDifferentBase() throws Exception {
    setupAbdera("/base/");
    
    String base = "http://localhost:9002/base/";

    runTests(base);
  }

  private void runTests(String base) throws IOException {
    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();

    AbderaClient client = new AbderaClient(abdera);

    // Testing of entry creation
    IRI colUri = new IRI(base).resolve("customers"); 
                                                          
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
    ClientResponse res = client.post(colUri.toString() + "?test=foo", entry, opts);
    assertEquals(201, res.getStatus());

    // prettyPrint(abdera, res.getDocument());

    IRI location = res.getLocation();
    assertEquals(colUri + "/1001-Dan_Diephouse", 
                 location.toString());

    // GET the entry
    res = client.get(location.toString());
    assertEquals(200, res.getStatus());

    // prettyPrint(abdera, res.getDocument());
    org.apache.abdera.model.Document<Entry> entry_doc = res.getDocument();
    prettyPrint(abdera, entry_doc);
    entry = entry_doc.getRoot();

    res = client.get(colUri + "/foobar");
    assertEquals(404, res.getStatus());
  }

  protected void prettyPrint(Abdera abdera, Base doc) throws IOException {
    WriterFactory factory = abdera.getWriterFactory();
    Writer writer = factory.getWriter("prettyxml");
    writer.writeTo(doc, System.out);
    System.out.println();
  }

  @SuppressWarnings("serial") 
  private void initializeJetty() throws Exception {

    server = new Server(9002);
    Context root = new Context(server, "/", Context.NO_SESSIONS);
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
    if (server != null) server.stop();
  }

}
