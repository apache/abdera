package org.apache.abdera.protocol.server.content;

import java.io.InputStream;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

public interface CollectionProvider<T> {
    
    String getTitle();
    
    ResponseContext getFeed(RequestContext request, Feed feed);

    ResponseContext createEntry(RequestContext request);

    ResponseContext getMedia(RequestContext request);
    
    ResponseContext deleteEntry(RequestContext request);

    ResponseContext getEntry(RequestContext request, IRI entryBaseIri);

    ResponseContext getFeed(RequestContext request);

    ResponseContext updateEntry(RequestContext request, IRI entryBaseIri);
    
}
