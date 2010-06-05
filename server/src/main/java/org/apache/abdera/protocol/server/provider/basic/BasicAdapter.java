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
package org.apache.abdera.protocol.server.provider.basic;

import java.util.Date;
import java.util.logging.Logger;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ManagedCollectionAdapter;
import org.apache.abdera.util.MimeTypeHelper;

/**
 * The BasicAdapter provides a simplistic interface for working with Atompub collections with a restricted set of
 * options/features. The idea of the basic adapter is to make it easy to provide a minimally capable Atompub server
 */
public abstract class BasicAdapter extends ManagedCollectionAdapter {

    public static Logger logger = Logger.getLogger(BasicAdapter.class.getName());

    protected BasicAdapter(Abdera abdera, FeedConfiguration config) {
        super(abdera, config);
    }

    public String getProperty(String key) throws Exception {
        Object val = config.getProperty(key);
        if (val == null) {
            logger.warning("Cannot find property " + key + "in Adapter properties file for feed " + config.getFeedId());
            throw new RuntimeException();
        }
        if (val instanceof String)
            return (String)val;
        throw new RuntimeException();
    }

    protected Feed createFeed() throws Exception {
        Feed feed = abdera.newFeed();
        feed.setId(config.getFeedUri());
        feed.setTitle(config.getFeedTitle());
        feed.setUpdated(new Date());
        feed.addAuthor(config.getFeedAuthor());
        return feed;
    }

    protected void addEditLinkToEntry(Entry entry) throws Exception {
        if (ProviderHelper.getEditUriFromEntry(entry) == null) {
            entry.addLink(entry.getId().toString(), "edit");
        }
    }

    protected void setEntryIdIfNull(Entry entry) throws Exception {
        // if there is no id in Entry, assign one.
        if (entry.getId() != null) {
            return;
        }
        String uuidUri = abdera.getFactory().newUuidUri();
        String[] segments = uuidUri.split(":");
        String entryId = segments[segments.length - 1];
        entry.setId(createEntryIdUri(entryId));
    }

    protected String createEntryIdUri(String entryId) throws Exception {
        return config.getFeedUri() + "/" + entryId;
    }

    private ResponseContext createOrUpdateEntry(RequestContext request, boolean createFlag) {
        try {
            MimeType mimeType = request.getContentType();
            String contentType = mimeType == null ? null : mimeType.toString();
            if (contentType != null && !MimeTypeHelper.isAtom(contentType) && !MimeTypeHelper.isXml(contentType))
                return ProviderHelper.notsupported(request);
            Abdera abdera = request.getAbdera();
            Parser parser = abdera.getParser();
            Entry inputEntry = (Entry)request.getDocument(parser).getRoot();
            Target target = request.getTarget();
            String entryId = !createFlag ? target.getParameter(BasicProvider.PARAM_ENTRY) : null;
            Entry newEntry = createFlag ? createEntry(inputEntry) : updateEntry(entryId, inputEntry);
            if (newEntry != null) {
                Document<Entry> newEntryDoc = newEntry.getDocument();
                String loc = newEntry.getEditLinkResolvedHref().toString();
                return ProviderHelper.returnBase(newEntryDoc, createFlag ? 201 : 200, null).setLocation(loc);
            } else {
                return ProviderHelper.notfound(request);
            }
        } catch (Exception e) {
            return ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public ResponseContext postEntry(RequestContext request) {
        return createOrUpdateEntry(request, true);
    }

    public ResponseContext deleteEntry(RequestContext request) {
        Target target = request.getTarget();
        String entryId = target.getParameter(BasicProvider.PARAM_ENTRY);
        try {
            return deleteEntry(entryId) ? ProviderHelper.nocontent() : ProviderHelper.notfound(request);
        } catch (Exception e) {
            return ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public ResponseContext putEntry(RequestContext request) {
        return createOrUpdateEntry(request, false);
    }

    public ResponseContext getEntry(RequestContext request) {
        Target target = request.getTarget();
        String entryId = target.getParameter(BasicProvider.PARAM_ENTRY);
        try {
            Entry entry = getEntry(entryId);
            return entry != null ? ProviderHelper.returnBase(entry.getDocument(), 200, null) : ProviderHelper
                .notfound(request);
        } catch (Exception e) {
            return ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public ResponseContext getFeed(RequestContext request) {
        try {
            Feed feed = getFeed();
            return feed != null ? ProviderHelper.returnBase(feed.getDocument(), 200, null) : ProviderHelper
                .notfound(request);
        } catch (Exception e) {
            return ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public ResponseContext extensionRequest(RequestContext request) {
        return ProviderHelper.notallowed(request, ProviderHelper.getDefaultMethods(request));
    }

    public ResponseContext getCategories(RequestContext request) {
        return ProviderHelper.notfound(request);
    }

    public abstract Feed getFeed() throws Exception;

    public abstract Entry getEntry(Object entryId) throws Exception;

    public abstract Entry createEntry(Entry entry) throws Exception;

    public abstract Entry updateEntry(Object entryId, Entry entry) throws Exception;

    public abstract boolean deleteEntry(Object entryId) throws Exception;

}
