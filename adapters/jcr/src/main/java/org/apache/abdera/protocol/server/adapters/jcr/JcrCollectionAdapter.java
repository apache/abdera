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
package org.apache.abdera.protocol.server.adapters.jcr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.jcr.Credentials;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.Workspace;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.abdera.protocol.util.PoolManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;

/**
 * Adapter implementation that uses a JCR Repository to store Atompub collection entries. The adapter is intended to be
 * used with the DefaultProvider implementation.
 */
public class JcrCollectionAdapter extends AbstractEntityCollectionAdapter<Node> {

    private final static Log log = LogFactory.getLog(JcrCollectionAdapter.class);

    private static final String TITLE = "title";

    private static final String SUMMARY = "summary";

    private static final String UPDATED = "updated";

    private static final String AUTHOR = "author";

    private static final String AUTHOR_EMAIL = "author.email";

    private static final String AUTHOR_LANGUAGE = "author.language";

    private static final String AUTHOR_NAME = "author.name";

    private static final String CONTENT = "content";

    private static final String SESSION_KEY = "jcrSession";

    private static final String MEDIA = "media";

    private static final String CONTENT_TYPE = "contentType";

    private static final String NAMESPACE = "http://abdera.apache.org";

    private String collectionNodePath;

    private String id;

    private String title;

    private String author;

    private String collectionNodeId;

    private Repository repository;

    private Credentials credentials;

    private int maxActiveSessions = 100;

    private PoolManager<Session> sessionPool;

    public void setCollectionNodePath(String collectionNodePath) {
        this.collectionNodePath = collectionNodePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Logs into the repository and posts a node for the collection if one does not exist. Also, this will set up the
     * session pool.
     * 
     * @throws RepositoryException
     */
    public void initialize() throws Exception {
        Session session = repository.login(credentials);

        Node collectionNode = null;
        try {
            collectionNode = session.getRootNode().getNode(collectionNodePath);
        } catch (PathNotFoundException e) {
            collectionNode = session.getRootNode().addNode(collectionNodePath);
            collectionNode.addMixin("mix:referenceable");
            session.save();
        }

        this.collectionNodeId = collectionNode.getUUID();
        this.id = "urn:" + collectionNodeId;

        Workspace workspace = session.getWorkspace();

        // Get the NodeTypeManager from the Workspace.
        // Note that it must be cast from the generic JCR NodeTypeManager to the
        // Jackrabbit-specific implementation.
        JackrabbitNodeTypeManager jntmgr = (JackrabbitNodeTypeManager)workspace.getNodeTypeManager();
        if (!jntmgr.hasNodeType("abdera:entry")) {
            InputStream in = getClass().getResourceAsStream("/org/apache/abdera/jcr/nodeTypes.xml");
            try {
                // register the node types and any referenced namespaces
                jntmgr.registerNodeTypes(in, JackrabbitNodeTypeManager.TEXT_XML);
            } finally {
                in.close();
            }
        }

        session.logout();

        sessionPool = new SessionPoolManager(maxActiveSessions, repository, credentials);
    }

    @Override
    public void start(RequestContext request) throws ResponseContextException {
        try {
            Session session = (Session)sessionPool.get(request);

            request.setAttribute(Scope.REQUEST, SESSION_KEY, session);
        } catch (Exception e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public void end(RequestContext request, ResponseContext response) {
        // Logout of the JCR session
        Session session = getSession(request);
        if (session != null) {
            try {
                sessionPool.release(session);
            } catch (Exception e) {
                log.warn("Could not return Session to pool!", e);
            }
        }
    }

    @Override
    public String getContentType(Node entry) {
        try {
            return getStringOrNull(entry, CONTENT_TYPE);
        } catch (ResponseContextException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isMediaEntry(Node entry) throws ResponseContextException {
        try {
            return entry.hasProperty(MEDIA);
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public Node postMedia(MimeType mimeType, String slug, InputStream inputStream, RequestContext request)
        throws ResponseContextException {
        if (slug == null) {
            throw new ResponseContextException("A slug header must be supplied.", 500);
        }
        Node n = postEntry(slug, null, null, new Date(), null, null, request);

        try {
            n.setProperty(MEDIA, inputStream);
            n.setProperty(CONTENT_TYPE, mimeType.toString());

            String summary = postSummaryForEntry(n);
            if (summary != null) {
                n.setProperty(SUMMARY, summary);
            }

            getSession(request).save();

            return n;
        } catch (RepositoryException e) {
            try {
                getSession(request).refresh(false);
            } catch (Throwable t) {
                log.warn(t);
            }
            throw new ResponseContextException(500, e);
        }
    }

    /**
     * post a summary for an entry. Used when a media entry is postd so you have the chance to post a meaningful summary
     * for consumers of the feed.
     * 
     * @param n
     * @return
     */
    protected String postSummaryForEntry(Node n) {
        return null;
    }

    @Override
    public Node postEntry(String title,
                          IRI id,
                          String summary,
                          Date updated,
                          List<Person> authors,
                          Content content,
                          RequestContext request) throws ResponseContextException {
        Session session = getSession(request);
        try {

            Node collectionNode = session.getNodeByUUID(collectionNodeId);
            String resourceName = Sanitizer.sanitize(title, "-");
            return postEntry(title, summary, updated, authors, content, session, collectionNode, resourceName, 0);
        } catch (RepositoryException e) {
            try {
                session.refresh(false);
            } catch (Throwable t) {
                log.warn(t);
            }
            throw new ResponseContextException(500, e);
        }
    }

    protected Node postEntry(String title,
                             String summary,
                             Date updated,
                             List<Person> authors,
                             Content content,
                             Session session,
                             Node collectionNode,
                             String resourceName,
                             int num) throws ResponseContextException, RepositoryException {
        try {
            String name = resourceName;
            if (num > 0) {
                name = name + "_" + num;
            }
            Node entry = collectionNode.addNode(name, "abdera:entry");

            entry.addMixin("mix:referenceable");

            mapEntryToNode(entry, title, summary, updated, authors, content, session);

            session.save();

            return entry;
        } catch (ItemExistsException e) {
            return postEntry(title, summary, updated, authors, content, session, collectionNode, resourceName, ++num);
        }
    }

    protected Node mapEntryToNode(Node entry,
                                  String title,
                                  String summary,
                                  Date updated,
                                  List<Person> authors,
                                  Content content,
                                  Session session) throws ResponseContextException, RepositoryException {
        if (title == null) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("Entry title cannot be empty.");
            throw new ResponseContextException(ctx);
        }

        entry.setProperty(TITLE, title);

        if (summary != null) {
            entry.setProperty(SUMMARY, summary);
        }

        Calendar upCal = Calendar.getInstance();
        upCal.setTime(updated);
        entry.setProperty(UPDATED, upCal);

        if (authors != null) {
            for (Person p : authors) {
                Node addNode = entry.addNode(AUTHOR);
                addNode.setProperty(AUTHOR_EMAIL, p.getEmail());
                addNode.setProperty(AUTHOR_LANGUAGE, p.getLanguage());
                addNode.setProperty(AUTHOR_NAME, p.getName());
            }
        }

        if (content != null) {
            switch (content.getContentType()) {
                case TEXT:
                    entry.setProperty(CONTENT, content.getText());
                    entry.setProperty(CONTENT_TYPE, Type.TEXT.toString());
                    break;
                case XHTML:
                    entry.setProperty(CONTENT, asString(content));
                    entry.setProperty(CONTENT_TYPE, Type.XHTML.toString());
                    break;
                default:
                    throw new ResponseContextException("Invalid content element type.", 500);
            }
        }

        if (summary != null) {
            entry.setProperty(SUMMARY, summary);
        }

        return entry;
    }

    private String asString(Content content2) throws ResponseContextException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            content2.<Element> getFirstChild().writeTo(bos);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
        return new String(bos.toByteArray());
    }

    protected Session getSession(RequestContext request) {
        return (Session)request.getAttribute(Scope.REQUEST, SESSION_KEY);
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Session session = getSession(request);
        try {
            getNode(session, resourceName).remove();
            session.save();
        } catch (RepositoryException e) {
            try {
                session.refresh(false);
            } catch (Throwable t) {
                log.warn(t);
            }
            throw new ResponseContextException(500, e);
        }
    }

    private Node getNode(Session session, String resourceName) throws ResponseContextException, RepositoryException {
        try {
            return session.getNodeByUUID(collectionNodeId).getNode(resourceName);
        } catch (PathNotFoundException e) {
            throw new ResponseContextException(404);
        }
    }

    /** Recursively outputs the contents of the given node. */
    public static void dump(Node node) throws RepositoryException {
        // First output the node path
        System.out.println(node.getPath());
        // Skip the virtual (and large!) jcr:system subtree
        if (node.getName().equals("jcr:system")) {
            return;
        }

        // Then output the properties
        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (property.getDefinition().isMultiple()) {
                // A multi-valued property, print all values
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    System.out.println(property.getPath() + " = " + values[i].getString());
                }
            } else {
                // A single-valued property
                System.out.println(property.getPath() + " = " + property.getString());
            }
        }

        // Finally output all the child nodes recursively
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            dump(nodes.nextNode());
        }
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return author;
    }

    @Override
    public List<Person> getAuthors(Node entry, RequestContext request) throws ResponseContextException {
        try {
            ArrayList<Person> authors = new ArrayList<Person>();
            for (NodeIterator nodes = entry.getNodes(); nodes.hasNext();) {
                Node n = nodes.nextNode();

                if (n.getName().equals(AUTHOR)) {
                    Person author = request.getAbdera().getFactory().newAuthor();
                    author.setName(getStringOrNull(entry, AUTHOR_NAME));
                    author.setEmail(getStringOrNull(entry, AUTHOR_EMAIL));
                    author.setLanguage(getStringOrNull(entry, AUTHOR_LANGUAGE));
                    authors.add(author);
                }
            }
            return authors;
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public Object getContent(Node entry, RequestContext request) throws ResponseContextException {

        String typeStr = getStringOrNull(entry, CONTENT_TYPE);
        Factory factory = Abdera.getInstance().getFactory();
        String textContent = getStringOrNull(entry, CONTENT);
        Type type = Type.valueOf(typeStr);
        Content content = factory.newContent(type);
        switch (type) {
            case TEXT:
                content.setValue(textContent);
                return content;
            case XHTML:
                content.setWrappedValue(textContent);
                return content;
            default:
        }
        return null;
    }

    @Override
    public Iterable<Node> getEntries(RequestContext request) throws ResponseContextException {
        ArrayList<Node> entries = new ArrayList<Node>();

        Session session = getSession(request);
        try {
            Node n = session.getNodeByUUID(collectionNodeId);
            for (NodeIterator nodes = n.getNodes(); nodes.hasNext();) {
                entries.add(nodes.nextNode());
            }
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }

        return entries;
    }

    @Override
    public Node getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        try {
            return getNode(getSession(request), resourceName);
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public String getId(RequestContext request) {
        return id;
    }

    @Override
    public String getId(Node entry) throws ResponseContextException {
        try {
            return "urn:" + entry.getUUID();
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public String getMediaName(Node entry) throws ResponseContextException {
        return getName(entry);
    }

    public InputStream getMediaStream(Node entry) throws ResponseContextException {
        try {
            Value value = getValueOrNull(entry, MEDIA);
            return (value != null) ? value.getStream() : null;
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }

    }

    @Override
    public String getName(Node entry) throws ResponseContextException {
        try {
            return entry.getName();
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public Text getSummary(Node entry, RequestContext request) throws ResponseContextException {
        Text summary = request.getAbdera().getFactory().newSummary();
        summary.setText(getStringOrNull(entry, SUMMARY));
        return summary;
    }

    public String getTitle(RequestContext request) {
        return title;
    }

    @Override
    public String getTitle(Node entry) throws ResponseContextException {
        return getStringOrNull(entry, TITLE);
    }

    @Override
    public Date getUpdated(Node entry) throws ResponseContextException {
        Calendar updated = getDateOrNull(entry, UPDATED);
        return (updated != null) ? updated.getTime() : null;
    }

    @Override
    public void putEntry(Node entry,
                         String title,
                         Date updated,
                         List<Person> authors,
                         String summary,
                         Content content,
                         RequestContext request) throws ResponseContextException {
        Session session = getSession(request);
        try {
            mapEntryToNode(entry, title, summary, updated, authors, content, session);
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public static String getStringOrNull(Node node, String propName) throws ResponseContextException {
        try {
            Value v = getValueOrNull(node, propName);
            return (v != null) ? v.getString() : null;
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public ResponseContext getCategories(RequestContext request) {
        return null;
    }

    public static Calendar getDateOrNull(Node node, String propName) throws ResponseContextException {
        try {
            Value v = getValueOrNull(node, propName);
            return (v != null) ? v.getDate() : null;
        } catch (RepositoryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public static Value getValueOrNull(Node node, String propName) throws RepositoryException {
        return node.hasProperty(propName) ? node.getProperty(propName).getValue() : null;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setMaxActiveSessions(int maxActiveSessions) {
        this.maxActiveSessions = maxActiveSessions;
    }

}
