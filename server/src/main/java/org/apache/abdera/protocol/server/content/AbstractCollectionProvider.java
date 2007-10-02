package org.apache.abdera.protocol.server.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractResponseContext;
import org.apache.abdera.protocol.server.impl.BaseResponseContext;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.MediaResponseContext;
import org.apache.abdera.protocol.server.impl.ProviderSupport;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;

public abstract class AbstractCollectionProvider<T> extends ProviderSupport
  implements CollectionProvider<T> {

  private String baseMediaIri = "media/";
  
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
      e.printStackTrace();
      return new EmptyResponseContext(500);
    }
  }

  protected boolean isMediaType(MimeType contentType) {
    String ctype = (contentType != null) ? contentType.toString() : null;
    return ctype != null && !MimeTypeHelper.isAtom(ctype) /*&& !MimeTypeHelper.isXml(ctype)*/;
  }
  
  public abstract T createEntry(String title, String summary, Content content);
  
  public T createMediaEntry(MimeType mimeType, String slug, InputStream inputStream) {
    throw new UnsupportedOperationException();
  }
  
  public ResponseContext deleteEntry(RequestContext request) {
    String id = getEntryID(request);
    if (id != null) {

      deleteEntry(id);

      return new EmptyResponseContext(204);
    } else {
      // TODO: is this right?
      return new EmptyResponseContext(404);
    }
  }

  public abstract void deleteEntry(String resourceName);

  public abstract String getAuthor();

  public String getBaseMediaIri() {
    return baseMediaIri;
  }
  
  public abstract Object getContent(T entry);
  
  // GET, POST, PUT, DELETE
  
  public String getContentType(T entry) {
    throw new UnsupportedOperationException();
  }
  
  public abstract Iterable<T> getEntries();
  
  public ResponseContext getEntry(RequestContext request, IRI entryBaseIri) {
    Entry entry = getEntryFromCollectionProvider(entryBaseIri,
                                                 request);
    if (entry != null) {
      Feed feed = createDocumentsFeed(request.getAbdera());
      entry.setSource(feed.getAsSource());
      Document<Entry> entry_doc = entry.getDocument();
      AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entry_doc);
      rc.setEntityTag(calculateEntityTag(entry));
      return rc;
    } else {
      return new EmptyResponseContext(404);
    }
  }
  
  public abstract T getEntry(String resourceName);
  
  public abstract T getEntryFromId(String id);


  public ResponseContext getFeed(RequestContext request) {
    Abdera abdera = request.getAbdera();
    Feed feed = createDocumentsFeed(abdera);
    
    return getFeed(request, feed);
  }
  
  public ResponseContext getFeed(RequestContext request, Feed feed) {
    feed.setUpdated(new Date());

    for (T entryObj : getEntries()) {
      Entry e = feed.addEntry();

      addEntryDetails(request, e, resolveBase(request), entryObj);
      addContent(e, entryObj);
    }

    Document<Feed> document = feed.getDocument();
    AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(document);
    rc.setEntityTag(calculateEntityTag(document.getRoot()));
    return rc;
  }

  public abstract String getId();
  /**
   * Gets the UUID for the specified entry.
   * @param entry
   * @return
   */
  public abstract String getId(T entry);

  public ResponseContext getMedia(RequestContext request) {
    try {
      String id = getEntryID(request);
      T entryObj = getEntry(id);

      if (entryObj == null) {
        return new EmptyResponseContext(404);
      }

      MediaResponseContext ctx = new MediaResponseContext(getMediaStream(entryObj), 
                                                          getUpdated(entryObj), 
                                                          200);
      ctx.setContentType(getContentType(entryObj));

      return ctx;
    } catch (ParseException pe) {
      return new EmptyResponseContext(415);
    } catch (ClassCastException cce) {
      return new EmptyResponseContext(415);
    } catch (Exception e) {
      return new EmptyResponseContext(400);
    }
  }
  public String getMediaName(T entry) {
    throw new UnsupportedOperationException();
  }

  public InputStream getMediaStream(T gdoc) {
    throw new UnsupportedOperationException();
  }
  
  public abstract String getName(T entry);

  public abstract String getTitle();

  public abstract String getTitle(T entry);

  public abstract Date getUpdated(T entry);

  public boolean isMediaEntry(T entry) {
    return false;
  }

  public void setBaseMediaIri(String baseMediaIri) {
    this.baseMediaIri = baseMediaIri;
  }

  public ResponseContext updateEntry(RequestContext request, IRI feedUri) {
    Abdera abdera = request.getAbdera();
    Entry orig_entry = getEntryFromCollectionProvider(feedUri, request);
    if (orig_entry != null) {
      try {
        MimeType contentType = request.getContentType();
        if (contentType != null && !MimeTypeHelper.isAtom(contentType.toString()))
          return new EmptyResponseContext(415);

        Entry entry = getEntryFromRequest(request);
        if (entry != null) {
          if (!entry.getId().equals(orig_entry.getId()))
            return new EmptyResponseContext(409);

          if (!isValidEntry(entry))
            return new EmptyResponseContext(400);

          entry.setUpdated(new Date());
          // TODO: is this really necessary?
          // entry.getIdElement().setValue(factory.newUuidUri());
          entry.addLink(feedUri.resolve(entry.getId().toString()).toString(), "edit");

          // entryObjProvider.updateEntry(entry)

          Feed feed = createDocumentsFeed(abdera);
          feed.insertEntry(entry);
          feed.setUpdated(new Date());
          return new EmptyResponseContext(204);
        } else {
          return new EmptyResponseContext(400);
        }
      } catch (ParseException pe) {
        return new EmptyResponseContext(415);
      } catch (ClassCastException cce) {
        return new EmptyResponseContext(415);
      } catch (Exception e) {
        return new EmptyResponseContext(400);
      }
    } else {
      return new EmptyResponseContext(404);
    }
  }

  public abstract T updateEntry(T entry, Content content);

  protected void addContent(Entry e, T doc) {
    Object content = getContent(doc);

    if (content instanceof Content) {
      e.setContentElement((Content)content);
    } else if (content instanceof String) {
      e.setContent((String)content);
    }
  }

  protected void addEntryDetails(RequestContext request, Entry e, 
                               IRI entryBaseIri, T entryObj) {
    IRI entryIri = entryBaseIri.resolve(getName(entryObj));
    e.addLink(entryIri.toString(), "edit");
    e.setId(getId(entryObj));
    e.setTitle(getTitle(entryObj));
    e.setUpdated(getUpdated(entryObj));
  }

  protected void addMediaContent(IRI entryBaseIri, Entry entry, T doc) {
    String name = getMediaName(doc);
    IRI mediaIri = getMediaIRI(entryBaseIri, name);
    mediaIri = entryBaseIri.resolve(mediaIri);

    entry.setContent(mediaIri, getContentType(doc));
    entry.addLink(mediaIri.toString(), "edit-media");
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
  protected Feed createDocumentsFeed(Abdera abdera) {
    Factory factory = abdera.getFactory();
    Feed feed = factory.newFeed();
    try {
      feed.setId(getId());
      feed.setTitle(getTitle());
      feed.addLink("");
      feed.addLink("", "self");
      feed.addAuthor(getAuthor());
    } catch (Exception e) {
    }
    return feed;
  }

  protected ResponseContext createMediaEntry(RequestContext request) {
    try {
      T doc = createMediaEntry(request.getContentType(), request.getSlug(), request.getInputStream());

      IRI baseIri = resolveBase(request);
      IRI entryIri = getEntryBaseFromFeedIRI(baseIri);

      Entry entry = request.getAbdera().getFactory().newEntry();

      addEntryDetails(request, entry, entryIri, doc);

      addMediaContent(entryIri, entry, doc);

      BaseResponseContext<Entry> rc = new BaseResponseContext<Entry>(entry);
      rc.setLocation(entryIri.resolve(entry.getEditLinkResolvedHref()).toString());
      rc.setContentLocation(rc.getLocation().toString());
      rc.setEntityTag(calculateEntityTag(entry));
      rc.setStatus(201);
      return rc;
    } catch (IOException e) {
      return new EmptyResponseContext(500);
    }
  }

  protected ResponseContext createNonMediaEntry(RequestContext request)
    throws IOException {
    Entry entry = getEntryFromRequest(request);
    if (entry != null) {
      if (!isValidEntry(entry))
        return new EmptyResponseContext(400);

      entry.setUpdated(new Date());
      
      T entryObj = createEntry(entry.getTitle(), entry.getSummary(), 
                                        entry.getContentElement());
      entry.getIdElement().setValue(getId(entryObj));
      
      IRI entryBaseUri = getEntryBaseFromFeedIRI(resolveBase(request));
      
      IRI entryIri = entryBaseUri.resolve(getName(entryObj));
      entry.addLink(entryIri.toString(), "edit");

      BaseResponseContext<Entry> rc = new BaseResponseContext<Entry>(entry);
      rc.setLocation(entryIri.resolve(entry.getEditLinkResolvedHref()).toString());
      rc.setContentLocation(rc.getLocation().toString());
      rc.setEntityTag(calculateEntityTag(entry));
      rc.setStatus(201);
      return rc;
    } else {
      return new EmptyResponseContext(400);
    }
  }

  protected IRI getEntryBaseFromFeedIRI(IRI baseIri) {
    return new IRI(baseIri + "/");
  }

  protected Entry getEntryFromCollectionProvider(IRI feedIri, RequestContext request) {

    String id = getEntryID(request);
    T entryObj = getEntry(id);

    if (entryObj == null) {
      return null;
    }

    Abdera abdera = request.getAbdera();
    Factory factory = abdera.getFactory();
    Entry entry = factory.newEntry();

    addEntryDetails(request, entry, feedIri, entryObj);

    if (isMediaEntry(entryObj)) {
      addMediaContent(feedIri, entry, entryObj);
    } else {
      addContent(entry, entryObj);
    }

    return entry;
  }

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
    String path = request.getUri().toString();
    String[] segments = path.split("/");
    return segments[segments.length - 1];
  }

  protected IRI getMediaIRI(IRI entryBaseIri, String name) {
    return entryBaseIri.resolve(getBaseMediaIri()).resolve(name);
  }

  @Override
  protected IRI resolveBase(RequestContext request) {
    return request.getBaseUri().resolve(request.getUri());
  }
}
