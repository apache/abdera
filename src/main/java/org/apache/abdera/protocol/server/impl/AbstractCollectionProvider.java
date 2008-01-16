package org.apache.abdera.protocol.server.impl;

import java.io.IOException;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCollectionProvider 
  extends ProviderSupport implements CollectionProvider {
  private final static Log log = LogFactory.getLog(AbstractEntityCollectionProvider.class);
  
  private String baseMediaIri = "media/";
  
  public AbstractCollectionProvider() {
    super();
  }

  public AbstractCollectionProvider(int defaultpagesize) {
    super(defaultpagesize);
  }
  
  public void begin(RequestContext request) throws ResponseContextException {
  }

  public ResponseContext createEntry(RequestContext request) {
    try {
      MimeType contentType = request.getContentType();
      if (isMediaType(contentType))
        return createMediaEntry(request);
      else
        return createNonMediaEntry(request);
    } catch (ParseException pe) {
      return new EmptyResponseContext(415);
    } catch (ClassCastException cce) {
      return new EmptyResponseContext(415);
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
      return new EmptyResponseContext(500);
    }
  }
  
  public void end(RequestContext request, ResponseContext response) {
  }

  public abstract String getAuthor() throws ResponseContextException;
  
  public String getBaseMediaIri() {
    return baseMediaIri;
  }
  
  public abstract String getId(RequestContext request);
  
  public abstract String getTitle(RequestContext request);

  public void setBaseMediaIri(String baseMediaIri) {
    this.baseMediaIri = baseMediaIri;
  }

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

  protected ResponseContext buildGetEntryResponse(RequestContext request, Entry entry) {
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
  
  protected EntityTag calculateEntityTag(Base base) {
    String id = null;
    String modified = null;
    if (base instanceof Entry) {
      id = ((Entry)base).getId().toString();
      modified = ((Entry)base).getUpdatedElement().getText();
    } else if (base instanceof Feed) {
      id = ((Feed)base).getId().toString();
      modified = ((Feed)base).getUpdatedElement().getText();
    }
    return EntityTag.generate(id, modified);
  }

  /**
   * Create a ResponseContext (or take it from the Exception) for an
   * exception that occurred in the application.
   * @param e
   * @return
   */
  protected ResponseContext createErrorResponse(ResponseContextException e) {
    if (log.isInfoEnabled()) {
      log.info("A ResponseException was thrown.", e);
    } else if (e.getResponseContext() instanceof EmptyResponseContext 
      && ((EmptyResponseContext) e.getResponseContext()).getStatus() >= 500) {
      log.warn("A ResponseException was thrown.", e);
    }
    
    return e.getResponseContext();
  }
  
  protected Feed createFeedBase(RequestContext request) {
    Factory factory = request.getAbdera().getFactory();
    Feed feed = factory.newFeed();
    try {
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addLink("");
      feed.addLink("", "self");
      feed.addAuthor(getAuthor());
    } catch (Exception e) {
    }
    return feed;
  }

  protected abstract ResponseContext createMediaEntry(RequestContext request)
    throws IOException;

  protected abstract ResponseContext createNonMediaEntry(RequestContext request)
    throws IOException ;
  
  @SuppressWarnings("unchecked")
  protected Entry getEntryFromRequest(RequestContext request) throws IOException {
    Abdera abdera = request.getAbdera();
    Parser parser = abdera.getParser();

    Document<Entry> entry_doc = (Document<Entry>)request.getDocument(parser).clone();
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


  protected IRI getMediaIRI(IRI entryBaseIri, String name) {
    return entryBaseIri.resolve(getBaseMediaIri()).resolve(name);
  }

  protected boolean isMediaType(MimeType contentType) {
    String ctype = (contentType != null) ? contentType.toString() : null;
    return ctype != null && !MimeTypeHelper.isAtom(ctype) /*&& !MimeTypeHelper.isXml(ctype)*/;
  }

  @Override
  protected IRI resolveBase(RequestContext request) {
    IRI uri = request.getUri();
    String q = uri.getQuery();
    if (q != null && !"".equals(q)) {
      String iriStr = uri.toString();
      int idx = iriStr.indexOf('?');
      uri = new IRI(iriStr.substring(0, idx));
    }
    
    IRI resolved = request.getBaseUri().resolve(uri);
    
    return resolved;
  }
}
