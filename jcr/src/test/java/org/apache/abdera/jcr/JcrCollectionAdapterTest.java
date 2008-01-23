package org.apache.abdera.jcr;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.writer.Writer;
import org.apache.jackrabbit.core.TransientRepository;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import junit.framework.TestCase;

public class JcrCollectionAdapterTest extends TestCase {

  private Server server;
  private DefaultProvider jcrProvider;
  private Repository repository;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    jcrProvider = new DefaultProvider();

    repository = new TransientRepository();
    
    JcrCollectionAdapter cp = new JcrCollectionAdapter();
    cp.setTitle("My Entries");
    cp.setAuthor("Apache Abdera");
    cp.setCollectionNodePath("entries");         
    cp.setRepository(repository);
    cp.setCredentials(new SimpleCredentials("username", "pass".toCharArray()));
    cp.setHref("acme/feed");
    cp.initialize();
    
    SimpleWorkspaceInfo wkspc = new SimpleWorkspaceInfo();
    wkspc.setTitle("JCR Workspace");
    wkspc.addCollection(cp);
    jcrProvider.addWorkspace(wkspc);
    
    initializeJetty();
  }

  public void testJCRAdapter() throws Exception {
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
      protected Provider createProvider() {
        jcrProvider.init(getAbdera(), getProperties(getServletConfig()));
        return jcrProvider;
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
