package org.apache.abdera.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultServiceContext;
import org.apache.abdera.protocol.server.impl.ServiceProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.SingletonProviderManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.writer.Writer;
import org.apache.jackrabbit.core.TransientRepository;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import junit.framework.TestCase;

public class JcrCollectionProviderTest extends TestCase {

  private Server server;
  private DefaultServiceContext abderaServiceContext;
  private Repository repository;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    abderaServiceContext = new DefaultServiceContext();

    SingletonProviderManager pm = new SingletonProviderManager();
    abderaServiceContext.setProviderManager(pm);

    ServiceProvider sp = new ServiceProvider();

    SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();

    repository = new TransientRepository();
    
    JcrCollectionProvider cp = new JcrCollectionProvider();
    cp.setTitle("My Entries");
    cp.setAuthor("Apache Abdera");
    cp.setCollectionNodePath("entries");         
    cp.setRepository(repository);
    cp.setCredentials(new SimpleCredentials("username", "pass".toCharArray()));
    cp.initialize();

    Map<String, CollectionProvider> contentProviders = new HashMap<String, CollectionProvider>();
    contentProviders.put("acme/feed", cp);

    wi.setCollectionProviders(contentProviders);

    List<WorkspaceInfo> workspaces = new ArrayList<WorkspaceInfo>();
    workspaces.add(wi);
    sp.setWorkspaces(workspaces);
    pm.setProvider(sp);
    abderaServiceContext.setTargetResolver(sp);
    initializeJetty();
  }

  public void testCustomerProvider() throws Exception {
    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();

    AbderaClient client = new AbderaClient(abdera);

    String base = "http://localhost:9002/";

    // Testing of entry creation
    IRI colUri = new IRI(base).resolve("acme/feed");
    Entry entry = factory.newEntry();
    entry.setTitle("Some Entry");
    entry.setUpdated(new Date());
    entry.addAuthor("Dan Diephouse");
    entry.setId(factory.newUuidUri());
    entry.setSummary("This is my entry.");
    entry.setContent("This is my entry. It's swell.");

    RequestOptions opts = new RequestOptions();
    opts.setContentType("application/atom+xml;type=entry");
    ClientResponse res = client.post(colUri.toString(), entry, opts);
    assertEquals(201, res.getStatus());

    //prettyPrint(abdera, res.getDocument());

    IRI location = res.getLocation();
    assertEquals(colUri + "/Some_Entry", location.toString());

    // GET the entry
    res = client.get(location.toString());
    assertEquals(200, res.getStatus());

    //prettyPrint(abdera, res.getDocument());
    org.apache.abdera.model.Document<Entry> entry_doc = res.getDocument();
    Entry entry2 = entry_doc.getRoot();

    assertEquals(entry.getTitle(), entry2.getTitle());
    assertEquals(entry.getSummary(), entry2.getSummary());
    assertEquals(entry.getContent(), entry2.getContent());

    List<Person> authors = entry2.getAuthors();
    assertEquals(1, authors.size());

    entry = entry2;
    entry.setSummary("New Summary");
    entry.setContent("New Content");

    res = client.put(location.toString(), entry, opts);
    assertEquals(204, res.getStatus());

    res = client.get(colUri.toString());
    org.apache.abdera.model.Document<Feed> feed_doc = res.getDocument();
    Feed feed = feed_doc.getRoot();

    assertEquals(1, feed.getEntries().size());
    //prettyPrint(abdera, feed_doc);

    // test 404 not found
    res = client.get(location.toString() + "Invalid");
    assertEquals(404, res.getStatus());
  }

  protected void prettyPrint(Abdera abdera, Base doc) throws IOException {
    Writer writer = abdera.getWriterFactory().getWriter("prettyxml");
    writer.writeTo(doc, System.out);
    System.out.println();
  }
  
  private void clearJcrRepository() {
    try {
      Session session = repository.login(new SimpleCredentials("username", "password".toCharArray()));

      Node node = session.getRootNode();

      for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
        Node child = itr.nextNode();
        if (!child.getName().equals("jcr:system")) {
          child.remove();
        }
      }
      session.save();
      session.logout();
    } catch (PathNotFoundException t) {
    } catch (Throwable t) {
      t.printStackTrace();
    }
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
    clearJcrRepository();

    if (server != null)
      server.stop();
  }

}
