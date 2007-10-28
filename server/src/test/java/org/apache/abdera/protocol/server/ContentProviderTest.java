package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultServiceContext;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.SingletonProviderManager;
import org.apache.abdera.protocol.server.impl.WorkspaceProvider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class ContentProviderTest extends TestCase {

  private Server server;
  private DefaultServiceContext abderaServiceContext;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    abderaServiceContext = new DefaultServiceContext();

    RegexTargetResolver resolver = new RegexTargetResolver();
    resolver.setPattern("/acme(\\?[^#]*)?", TargetType.TYPE_SERVICE);
    resolver.setPattern("/acme/customers(\\?[^#]*)?", TargetType.TYPE_COLLECTION);
    resolver.setPattern("/acme/customers/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY);
    abderaServiceContext.setTargetResolver(resolver);
    
    SingletonProviderManager pm = new SingletonProviderManager();
    abderaServiceContext.setProviderManager(pm);

    WorkspaceProvider wp = new WorkspaceProvider();

    SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
    wi.setId("acme");
    CustomerContentProvider cp = new CustomerContentProvider();
    Map<String, CollectionProvider> contentProviders = new HashMap<String, CollectionProvider>();
    contentProviders.put("customers", cp);
    
    wi.setCollectionProviders(contentProviders);

    List<WorkspaceInfo> workspaces = new ArrayList<WorkspaceInfo>();
    workspaces.add(wi);
    wp.setWorkspaces(workspaces);
    pm.setProvider(wp);

    initializeJetty();
  }

  public void testCustomerProvider() throws Exception {

    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();

    AbderaClient client = new AbderaClient(abdera);

    String base = "http://localhost:9002/";

    // Testing of entry creation
    IRI colUri = new IRI(base).resolve("acme/customers"); // base +
                                                          // docCollection.getHref().toString();
    Entry entry = factory.newEntry();
    entry.setTitle("Hmmm this is ignored right now");
    entry.setUpdated(new Date());
    entry.addAuthor("Acme Industries");
    entry.setId(factory.newUuidUri());
    entry.setSummary("Customer document");
    
    Element customerEl = factory.newElement(new QName("customer"));
    customerEl.setAttributeValue(new QName("name"), "Dan Diephouse");
    entry.setContent(customerEl);

    RequestOptions opts = new RequestOptions();
    opts.setContentType("application/atom+xml;type=entry");
    ClientResponse res = client.post(colUri.toString(), entry, opts);
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
    entry = entry_doc.getRoot();

    res = client.get(colUri + "/foobar");
    assertEquals(404, res.getStatus());
  }

  protected void prettyPrint(Abdera abdera, Base doc) throws IOException {
    abdera.getWriterFactory().getWriter("prettyxml").writeTo(doc, System.out);
    System.out.println();
  }

  private void initializeJetty() throws Exception {

    server = new Server(9002);
    Context root = new Context(server, "/", Context.NO_SESSIONS);
    root.addServlet(new ServletHolder(new AbderaServlet() {

      @Override
      protected ServiceContext createServiceContext() {
        abderaServiceContext.init(getAbdera(), getProperties(getServletConfig()));
        return abderaServiceContext;
      }
    }), "/*");
    server.start();
  }

  @Override
  protected void tearDown() throws Exception {
    if (server != null) server.stop();
  }

}
