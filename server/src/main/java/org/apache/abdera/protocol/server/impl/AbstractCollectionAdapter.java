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
package org.apache.abdera.protocol.server.impl;

import static org.apache.abdera.protocol.server.ProviderHelper.calculateEntityTag;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.AtomDate;
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
import org.apache.abdera.util.EntityTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base CollectionAdapter implementation that provides a number of helper utility methods for adapter implementations.
 */
public abstract class AbstractCollectionAdapter implements CollectionAdapter, MediaCollectionAdapter, Transactional,
    CollectionInfo {

    private final static Log log = LogFactory.getLog(AbstractEntityCollectionAdapter.class);

    private String href;
    private Map<String, Object> hrefParams = new HashMap<String, Object>();

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
        return new String[] {"application/atom+xml;type=entry"};
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

    public ResponseContext headMedia(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext optionsMedia(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext putMedia(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext postMedia(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext headEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext optionsEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public abstract String getAuthor(RequestContext request) throws ResponseContextException;

    public abstract String getId(RequestContext request);

    /**
     * Creates the ResponseContext for a newly created entry. By default, a BaseResponseContext is returned. The
     * Location, Content-Location, Etag and status are set appropriately.
     */
    protected ResponseContext buildCreateEntryResponse(String link, Entry entry) {
        BaseResponseContext<Entry> rc = new BaseResponseContext<Entry>(entry);
        rc.setLocation(link);
        rc.setContentLocation(rc.getLocation().toString());
        rc.setEntityTag(calculateEntityTag(entry));
        rc.setStatus(201);
        return rc;
    }

    /**
     * Creates the ResponseContext for a newly created entry. By default, a BaseResponseContext is returned. The
     * Location, Content-Location, Etag and status are set appropriately.
     */
    protected ResponseContext buildPostMediaEntryResponse(String link, Entry entry) {
        return buildCreateEntryResponse(link, entry);
    }

    /**
     * Creates the ResponseContext for a GET entry request. By default, a BaseResponseContext is returned. The Entry
     * will contain an appropriate atom:source element and the Etag header will be set.
     */
    protected ResponseContext buildGetEntryResponse(RequestContext request, Entry entry)
        throws ResponseContextException {
        Feed feed = createFeedBase(request);
        entry.setSource(feed.getAsSource());
        Document<Entry> entry_doc = entry.getDocument();
        AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entry_doc);
        rc.setEntityTag(calculateEntityTag(entry));
        return rc;
    }

    /**
     * Creates the ResponseContext for a HEAD entry request. By default, an EmptyResponseContext is returned. The Etag
     * header will be set.
     */
    protected ResponseContext buildHeadEntryResponse(RequestContext request, String id, Date updated)
        throws ResponseContextException {
        EmptyResponseContext rc = new EmptyResponseContext(200);
        rc.setEntityTag(EntityTag.generate(id, AtomDate.format(updated)));
        return rc;
    }

    /**
     * Creates the ResponseContext for a GET feed request. By default, a BaseResponseContext is returned. The Etag
     * header will be set.
     */
    protected ResponseContext buildGetFeedResponse(Feed feed) {
        Document<Feed> document = feed.getDocument();
        AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(document);
        rc.setEntityTag(calculateEntityTag(document.getRoot()));
        return rc;
    }

    /**
     * Create a ResponseContext (or take it from the Exception) for an exception that occurred in the application.
     * 
     * @param e
     * @return
     */
    protected ResponseContext createErrorResponse(ResponseContextException e) {
        if (log.isDebugEnabled()) {
            log.debug("A ResponseException was thrown.", e);
        } else if (e.getResponseContext() instanceof EmptyResponseContext && ((EmptyResponseContext)e
            .getResponseContext()).getStatus() >= 500) {
            log.warn("A ResponseException was thrown.", e);
        }

        return e.getResponseContext();
    }

    /**
     * Create the base feed for the requested collection.
     */
    protected Feed createFeedBase(RequestContext request) throws ResponseContextException {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.setId(getId(request));
        feed.setTitle(getTitle(request));
        feed.addLink("");
        feed.addLink("", "self");
        feed.addAuthor(getAuthor(request));
        feed.setUpdated(new Date());
        return feed;
    }

    /**
     * Retrieves the FOM Entry object from the request payload.
     */
    @SuppressWarnings("unchecked")
    protected Entry getEntryFromRequest(RequestContext request) throws ResponseContextException {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();

        Document<Entry> entry_doc;
        try {
            entry_doc = (Document<Entry>)request.getDocument(parser).clone();
        } catch (ParseException e) {
            throw new ResponseContextException(400, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
        if (entry_doc == null) {
            return null;
        }
        return entry_doc.getRoot();
    }

    /**
     * Get's the name of the specific resource requested
     */
    protected String getResourceName(RequestContext request) {
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
