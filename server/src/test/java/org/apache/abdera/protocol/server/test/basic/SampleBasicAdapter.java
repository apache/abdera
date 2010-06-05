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
package org.apache.abdera.protocol.server.test.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.provider.basic.BasicAdapter;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

public class SampleBasicAdapter extends BasicAdapter {

    private static final String ERROR_DUP_ENTRY = "Entry Already Exists";
    private static final String ERROR_INVALID_ENTRY = "No Such Entry in the Feed";

    public static Logger logger = Logger.getLogger(SampleBasicAdapter.class.getName());
    protected HashMap<String, byte[]> entries = new HashMap<String, byte[]>();

    public SampleBasicAdapter(Abdera abdera, FeedConfiguration config) {
        super(abdera, config);
    }

    public Feed getFeed() throws Exception {
        Feed feed = createFeed();

        // get all keys in the feed - with keys in descending order of
        // lastUpdatedDate
        Set<String> keys = entries.keySet();
        for (String s : keys) {
            Entry entry = getEntry(s);
            // TODO: why clone this? Abdera seems to mess up the object
            // if we pass the reference to this object
            feed.addEntry((Entry)entry.clone());
        }
        return feed;
    }

    public Entry getEntry(Object entryId) throws Exception {
        return retrieveEntry((String)entryId);
    }

    public Entry createEntry(Entry entry) throws Exception {
        // entryId may be null. if it is, assign one
        setEntryIdIfNull(entry);
        logger.info("assigning id to Entry: " + entry.getId().toString());
        String entryId = getEntryIdFromUri(entry.getId().toString());

        if (entries.containsKey(entryId)) {
            throw new Exception(ERROR_DUP_ENTRY);
        }
        // add an "updated" element if one was not provided
        if (entry.getUpdated() == null) {
            entry.setUpdated(new Date());
        }
        addEditLinkToEntry(entry);
        storeEntry(entryId, entry);
        logger.finest("returning this entry from sampleadapter.createEntry: " + entry.toString());
        return entry;
    }

    public Entry updateEntry(Object entryId, Entry entry) throws Exception {
        if (!entries.containsKey(entryId)) {
            throw new Exception(ERROR_INVALID_ENTRY);
        }
        entries.remove(entryId);
        // add an "updated" element if one was not provided
        if (entry.getUpdated() == null) {
            entry.setUpdated(new Date());
        }
        addEditLinkToEntry(entry);
        storeEntry((String)entryId, entry);
        logger.finest("returning this entry from sampleadapter.updateEntry: " + entry.toString());
        return entry;
    }

    public boolean deleteEntry(Object entryId) throws Exception {
        if (!entries.containsKey(entryId)) {
            return false;
        }
        entries.remove(entryId);
        return true;
    }

    protected String getEntryIdFromUri(String uri) {
        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }

    protected void storeEntry(String entryId, Entry entry) throws Exception {
        Document<Element> entryDoc = entry.getDocument();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        entryDoc.writeTo(bos);

        // Get the bytes of the serialized object and store in hashmap
        byte[] buf = bos.toByteArray();
        bos.close();
        entries.put(entryId, buf);
    }

    protected Entry retrieveEntry(String entryId) throws Exception {
        // Deserialize from a byte array
        byte[] bytes = entries.get(entryId);
        if (bytes == null) {
            // entry not found
            return null;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Document<Entry> entryDoc = abdera.getParser().parse(in);
        Entry entry = entryDoc.getRoot();
        return entry;
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return config.getFeedAuthor();
    }

    @Override
    public String getId(RequestContext request) {
        return config.getFeedId();
    }

    public String getTitle(RequestContext request) {
        return config.getFeedTitle();
    }
}
