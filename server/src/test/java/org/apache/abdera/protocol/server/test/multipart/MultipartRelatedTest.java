package org.apache.abdera.protocol.server.test.multipart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.HashMap;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.processors.MultipartRelatedServiceRequestProcessor;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.After;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

@SuppressWarnings("serial")
public class MultipartRelatedTest {

    private int port;
    private Server server;

    private void initializeJetty(String contextPath) throws Exception {
        port = PortAllocator.allocatePort();
        server = new Server(port);
        Context root = new Context(server, contextPath, Context.NO_SESSIONS);
        root.addServlet(new ServletHolder(new AbderaServlet() {
            @Override
            protected Provider createProvider() {
                DefaultProvider provider = new DefaultProvider("/");
                provider.addRequestProcessors(new HashMap<TargetType, RequestProcessor>() {
                    {
                        put(TargetType.TYPE_SERVICE, new MultipartRelatedServiceRequestProcessor());
                    }
                });

                MultipartRelatedAdapter ca = new MultipartRelatedAdapter();
                ca.setHref("media");

                SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
                wi.setTitle("multimedia/related Workspace");
                wi.addCollection(ca);

                provider.addWorkspace(wi);

                provider.init(getAbdera(), null);
                return provider;
            }
        }), "/*");
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();
    }

    @Test
    public void testServiceDocument() throws Exception {
        initializeJetty("/");
        AbderaClient client = new AbderaClient(new Abdera());

        ClientResponse res = client.get("http://localhost:" + port + "/");
        assertEquals(200, res.getStatus());
        StringWriter sw = new StringWriter();
        res.getDocument().writeTo(sw);

        assertTrue(sw.toString().contains("accept alternate=\"multipart-related\">image/png"));
        assertTrue(sw.toString().contains("accept>video/*"));
        assertTrue(sw.toString().contains("accept>image/jpg"));
    }

    @Test
    public void testPostMedia() throws Exception {
        execTest(201, "image/png");
    }

    @Test
    public void testPostMediaInvalidContentType() throws Exception {
        // collection doesn't accept multipart files with this content type
        execTest(415, "image/jpg");
    }

    private void execTest(int status, String contentType) throws Exception {
        initializeJetty("/");

        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();

        AbderaClient client = new AbderaClient(abdera);

        Entry entry = factory.newEntry();

        entry.setTitle("my image");
        entry.addAuthor("david");
        entry.setId("tag:apache.org,2008:234534344");
        entry.setSummary("multipart test");
        entry.setContent(new IRI("cid:234234@example.com"), contentType);

        ClientResponse res =
            client.post("http://localhost:" + port + "/media", entry, this.getClass().getResourceAsStream("info.png"));
        assertEquals(status, res.getStatus());
    }
}
