package org.apache.abdera.protocol.server.impl;

import static org.apache.abdera.protocol.server.ProviderHelper.calculateEntityTag;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.MediaCollectionAdapter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Transactional;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCollectionAdapter 
  implements CollectionAdapter, 
             MediaCollectionAdapter,
             Transactional, 
             CollectionInfo {

  private final static Log log = LogFactory.getLog(AbstractEntityCollectionAdapter.class);
  
  private String href;
  private Map<String,Object> hrefParams = new HashMap<String,Object>();
 
  public AbstractCollectionAdapter() {
    super();
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
    hrefParams.put("collection", href);
  }
  
  public String getHref(RequestContext request) {
    return request.urlFor("feed", hrefParams);
  }
  
  public void compensate(RequestContext request, Throwable t) {
  }

  public void end(RequestContext request, ResponseContext response) {
  }

  public void start(RequestContext request) throws ResponseContextException {
  }

  public String[] getAccepts(RequestContext request) {
    return new String[] { "application/atom+xml;type=entry" };
  }

  public CategoriesInfo[] getCategoriesInfo(RequestContext request) {
    return null;
  }

  public ResponseContext getCategories(RequestContext request) {
      return null;
  }

  public ResponseContext deleteMedia(RequestContext request) {
    return ProviderHelper.notallowed(request);
  }

  public ResponseContext getMedia(RequestContext request) {
    return ProviderHelper.notallowed(request);
  }

  public ResponseContext putMedia(RequestContext request) {
    return ProviderHelper.notallowed(request);
  }

  public ResponseContext postMedia(RequestContext request) {
    return ProviderHelper.notallowed(request);
  }

  public abstract String getAuthor() throws ResponseContextException;

  public abstract String getId(RequestContext request);
  
  protected ResponseContext buildCreateEntryResponse(IRI entryIri, Entry entry) {
    BaseResponseContext<Entry> rc = new BaseResponseContext<Entry>(entry);
    rc.setLocation(entryIri.resolve(entry.getEditLinkResolvedHref()).toString());
    rc.setContentLocation(rc.getLocation().toString());
    rc.setEntityTag(calculateEntityTag(entry));
    rc.setStatus(201);
    return rc;
  }

  protected ResponseContext buildCreateMediaEntryResponse(IRI entryIri, Entry entry) {
    return buildCreateEntryResponse(entryIri, entry);
  }

  protected ResponseContext buildGetEntryResponse(RequestContext request, Entry entry) throws ResponseContextException {
    Feed feed = createFeedBase(request);
    entry.setSource(feed.getAsSource());
    Document<Entry> entry_doc = entry.getDocument();
    AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entry_doc);
    rc.setEntityTag(calculateEntityTag(entry));
    return rc;
  }

  protected ResponseContext buildGetFeedResponse(Feed feed) {
    Document<Feed> document = feed.getDocument();
    AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(document);
    rc.setEntityTag(calculateEntityTag(document.getRoot()));
    return rc;
  }

  /**
   * Create a ResponseContext (or take it from the Exception) for an exception
   * that occurred in the application.
   * 
   * @param e
   * @return
   */
  protected ResponseContext createErrorResponse(ResponseContextException e) {
    if (log.isInfoEnabled()) {
      log.info("A ResponseException was thrown.", e);
    } else if (e.getResponseContext() instanceof EmptyResponseContext
               && ((EmptyResponseContext)e.getResponseContext()).getStatus() >= 500) {
      log.warn("A ResponseException was thrown.", e);
    }

    return e.getResponseContext();
  }

  protected Feed createFeedBase(RequestContext request) throws ResponseContextException {
    Factory factory = request.getAbdera().getFactory();
    Feed feed = factory.newFeed();
    feed.setId(getId(request));
    feed.setTitle(getTitle(request));
    feed.addLink("");
    feed.addLink("", "self");
    feed.addAuthor(getAuthor());
    feed.setUpdated(new Date());
    return feed;
  }
  
  @SuppressWarnings("unchecked")
  protected Entry getEntryFromRequest(RequestContext request) throws ResponseContextException {
    Abdera abdera = request.getAbdera();
    Parser parser = abdera.getParser();

    Document<Entry> entry_doc;
    try {
      entry_doc = (Document<Entry>)request.getDocument(parser).clone();
    } catch (ParseException e) {
      throw new ResponseContextException(500, e);
    } catch (IOException e) {
      throw new ResponseContextException(500, e);
    }
    if (entry_doc == null) {
      return null;
    }
    return entry_doc.getRoot();
  }

  protected String getEntryID(RequestContext request) {
    String path = request.getTargetPath();
    int q = path.indexOf("?");
    if (q != -1) {
      path = path.substring(0, q);
    }
    String[] segments = path.split("/");
    String id = segments[segments.length - 1];
    return UrlEncoding.decode(id);
  }

  public ResponseContext extensionRequest(RequestContext request) {
    return ProviderHelper.notallowed(request, getMethods(request));
  }

  private String[] getMethods(RequestContext request) {
    return ProviderHelper.getDefaultMethods(request);
  }

  public Collection asCollectionElement(RequestContext request) {
    Collection collection = request.getAbdera().getFactory().newCollection();
    collection.setHref(getHref(request));
    collection.setTitle(getTitle(request));
    collection.setAccept(getAccepts(request));
    for (CategoriesInfo catsinfo : getCategoriesInfo(request)) {
      collection.addCategories(catsinfo.asCategoriesElement(request));
    }
    return collection;
  }
}
