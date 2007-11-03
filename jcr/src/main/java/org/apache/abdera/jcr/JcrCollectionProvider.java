package org.apache.abdera.jcr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JcrCollectionProvider extends AbstractCollectionProvider<Node> {

  private final static Log log = LogFactory.getLog(JcrCollectionProvider.class);
  
  private static final String TITLE = "title";

  private static final String SUMMARY = "summary";

  private static final String ENTRY = "entry";

  private static final String UPDATED = "updated";

  private static final String AUTHOR = "author";

  private static final String AUTHOR_EMAIL = "author.email";

  private static final String AUTHOR_LANGUAGE = "author.language";

  private static final String AUTHOR_NAME = "author.name";

  private static final String CONTENT = "content";

  private static final String RESOURCE_NAME = "resourceName";

  private static final String SESSION = "jcrSession";

  private static final String MEDIA = "media";

  private static final String CONTENT_TYPE = "contentType";

  private Repository repository;

  private String collectionNodePath;

  private String id;

  private String title;

  private String author;

  private Credentials credentials;

  private String collectionNodeId;

  public JcrCollectionProvider(String title, String author, String collectionNodePath, Repository repository,
                               Credentials credentials) throws RepositoryException {
    super();
    this.title = title;
    this.author = author;
    this.collectionNodePath = collectionNodePath;
    this.credentials = credentials;
    this.repository = repository;
  }

  public void initialize() throws RepositoryException {
    Session session = createSession();

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

    session.logout();
  }

  @Override
  public void begin(RequestContext request) throws ResponseContextException {
    super.begin(request);

    try {
      Session session = createSession();

      request.setAttribute(Scope.REQUEST, SESSION, session);
    } catch (RepositoryException e) {
      throw new ResponseContextException(500, e);
    }
  }

  @Override
  public void end(RequestContext request, ResponseContext response) {
    // Logout of the JCR session
    Session session = getSession(request);
    if (session != null) {
     // session.logout();
    }

    super.end(request, response);
  }

  protected Session createSession() throws RepositoryException {
    return repository.login(credentials);
  }


  @Override
  public String getContentType(Node entry) {
    return getStringOrNull(entry, CONTENT_TYPE);
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
  public Node createMediaEntry(MimeType mimeType, String slug, 
                               InputStream inputStream, RequestContext request)
    throws ResponseContextException {
    if (slug == null) {
      throw new ResponseContextException("A slug header must be supplied.", 500);
    }
    Node n = createEntry(slug, null, null, new Date(), null, null, request);
    
    try {
      n.setProperty(MEDIA, inputStream);
      n.setProperty(CONTENT_TYPE, mimeType.toString());

      String summary = createSummaryForEntry(n);
      if (summary != null) {
        n.setProperty(SUMMARY, summary);
      }

      getSession(request).save();
      
      return n;
    } catch (RepositoryException e) {
      try {
        n.remove();
      } catch (Throwable t) {
        log.warn(t);
      }
      throw new ResponseContextException(500, e);
    }
  }
  
  /**
   * Create a summary for an entry. Used when a media entry is created
   * so you have the chance to create a meaningful summary for consumers
   * of the feed.
   * 
   * @param n
   * @return
   */
  protected String createSummaryForEntry(Node n) {
    return null;
  }

  @Override
  public Node createEntry(String title, IRI id, String summary, Date updated, List<Person> authors,
                          Content content, RequestContext request) throws ResponseContextException {
    
    Node entry = null;
    try {
      Session session = getSession(request);

      Node collectionNode = session.getNodeByUUID(collectionNodeId);
      entry = collectionNode.addNode(ENTRY);
      entry.addMixin("mix:referenceable");

      mapEntryToNode(entry, title, summary, updated, authors, content, session);
      
      session.save();
      
      return entry;
    } catch (RepositoryException e) {
      try {
        if (entry != null) entry.remove();
      } catch (Throwable t) {
        log.warn(t);
      }
      
      throw new ResponseContextException(500, e);
    }
  }

  private Node mapEntryToNode(Node entry, String title, String summary, Date updated, List<Person> authors,
                              Content content, Session session) throws ResponseContextException,
    ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException,
    ItemExistsException, PathNotFoundException, AccessDeniedException, InvalidItemStateException,
    NoSuchNodeTypeException {
    if (title == null) {
      EmptyResponseContext ctx = new EmptyResponseContext(500);
      ctx.setStatusText("Entry title cannot be empty.");
      throw new ResponseContextException(ctx);
    }

    entry.setProperty(TITLE, title);

    // TODO: figure out a full proof way to check for entries with this same
    // resource name
    String resourceName = EncodingUtil.sanitize(title);
    entry.setProperty(RESOURCE_NAME, resourceName);

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
      entry.setProperty(CONTENT, content.getText());
    }

    if (summary != null) {
      entry.setProperty(SUMMARY, summary);
    }
    
    return entry;
  }

  private Session getSession(RequestContext request) {
    return (Session)request.getAttribute(Scope.REQUEST, SESSION);
  }

  @Override
  public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
    try {
      Session session = getSession(request);

      Node node = getNode(session, resourceName);

      node.remove();
    } catch (RepositoryException e) {
      throw new ResponseContextException(500, e);
    }

  }

  private Node getNode(Session session, String resourceName) throws ResponseContextException,
    RepositoryException {
    QueryManager qm = session.getWorkspace().getQueryManager();

    StringBuffer qStr = new StringBuffer();
    qStr.append("//*[@jcr:uuid='").append(collectionNodeId).append("']/entry[@").append(RESOURCE_NAME)
      .append("='").append(resourceName).append("']");
    
    Query query = qm.createQuery(qStr.toString(), Query.XPATH);

    QueryResult execute = query.execute();

    NodeIterator nodes = execute.getNodes();
    if (!nodes.hasNext()) {
      throw new ResponseContextException(404);
    }

    return nodes.nextNode();
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
                  System.out.println(
                      property.getPath() + " = " + values[i].getString());
              }
          } else {
              // A single-valued property
              System.out.println(
                  property.getPath() + " = " + property.getString());
          }
      }

      // Finally output all the child nodes recursively
      NodeIterator nodes = node.getNodes();
      while (nodes.hasNext()) {
          dump(nodes.nextNode());
      }
  }

  @Override
  public String getAuthor() throws ResponseContextException {
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
    return getStringOrNull(entry, CONTENT);
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
  public Node getEntryFromId(String id, RequestContext request) throws ResponseContextException {
    if (!id.startsWith("urn:")) {
      EmptyResponseContext res = new EmptyResponseContext(404);
      res.setStatusText("Invalid entry id.");
      throw new ResponseContextException(res);
    }
    
    id = id.substring(4);
    
    try {
      return getSession(request).getNodeByUUID(id);
    } catch (RepositoryException e) {
      throw new ResponseContextException(500, e);
    }
  }

  @Override
  public String getId() {
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
      
      if (value == null) return null;
      
      return value.getStream();
    } catch (PathNotFoundException e) {
      return null;
    } catch (RepositoryException e) {
      throw new ResponseContextException(500, e);
    }
    
  }
  
  @Override
  public String getName(Node entry) throws ResponseContextException {
    return getStringOrNull(entry, RESOURCE_NAME);
  }

  @Override
  public Text getSummary(Node entry, RequestContext request) {
    Text summary = request.getAbdera().getFactory().newSummary();
    summary.setText(getStringOrNull(entry, SUMMARY));
    return summary;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getTitle(Node entry) throws ResponseContextException {
    return getStringOrNull(entry, TITLE);
  }

  @Override
  public Date getUpdated(Node entry) throws ResponseContextException {
    return getDateOrNull(entry, UPDATED).getTime();
  }

  @Override
  public void updateEntry(Node entry, String title, Date updated,
                          List<Person> authors, String summary,
                          Content content, RequestContext request) throws ResponseContextException {
    Session session = getSession(request);
    try {
      mapEntryToNode(entry, title, summary, updated, authors, content, session);
    } catch (RepositoryException e) {
      throw new ResponseContextException(500, e);
    }
  }

  public static String getStringOrNull(Node node, String propName) {
    try {
      Value v = getValueOrNull(node, propName);
      if (v != null) {
        return v.getString();
      }
    } catch (ValueFormatException e) {
      throw new RuntimeException(e);
    } catch (RepositoryException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public static Calendar getDateOrNull(Node node, String propName) {
    try {
      Value v = getValueOrNull(node, propName);
      if (v != null) {
        return v.getDate();
      }
    } catch (ValueFormatException e) {
      throw new RuntimeException(e);
    } catch (RepositoryException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public static Value getValueOrNull(Node node, String propName) throws PathNotFoundException,
    RepositoryException {
    Property p = null;
    try {
      p = node.getProperty(propName);
    } catch (PathNotFoundException e) {
      return null;
    }

    if (p == null) {
      return null;
    }

    return p.getValue();
  }

}
