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
package org.apache.abdera.protocol.server.adapters.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ManagedCollectionAdapter;

/**
 * Simple Filesystem Adapter that uses a local directory to store Atompub collection entries. As an extension of the
 * ManagedCollectionAdapter class, the Adapter is intended to be used with implementations of the ManagedProvider and
 * are configured using /abdera/adapter/*.properties files. The *.properties file MUST specify the fs.root property to
 * specify the root directory used by the Adapter.
 */
public class FilesystemAdapter extends ManagedCollectionAdapter {

    private final File root;
    private final static FileSorter sorter = new FileSorter();
    private final static Template paging_template = new Template("?{-join|&|count,page}");

    public FilesystemAdapter(Abdera abdera, FeedConfiguration config) {
        super(abdera, config);
        this.root = getRoot();
    }

    private File getRoot() {
        try {
            String root = (String)config.getProperty("fs.root");
            File file = new File(root);
            if (!file.exists())
                file.mkdirs();
            if (!file.isDirectory())
                throw new RuntimeException("Root must be a directory");
            return file;
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException)e;
            throw new RuntimeException(e);
        }
    }

    private Entry getEntry(File entryFile) {
        if (!entryFile.exists() || !entryFile.isFile())
            throw new RuntimeException();
        try {
            FileInputStream fis = new FileInputStream(entryFile);
            Document<Entry> doc = abdera.getParser().parse(fis);
            Entry entry = doc.getRoot();
            return entry;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addPagingLinks(RequestContext request, Feed feed, int currentpage, int count) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("count", count);
        params.put("page", currentpage + 1);
        String next = paging_template.expand(params);
        next = request.getResolvedUri().resolve(next).toString();
        feed.addLink(next, "next");
        if (currentpage > 0) {
            params.put("page", currentpage - 1);
            String prev = paging_template.expand(params);
            prev = request.getResolvedUri().resolve(prev).toString();
            feed.addLink(prev, "previous");
        }
        params.put("page", 0);
        String current = paging_template.expand(params);
        current = request.getResolvedUri().resolve(current).toString();
        feed.addLink(current, "current");
    }

    private void getEntries(RequestContext request, Feed feed, File root) {
        File[] files = root.listFiles();
        Arrays.sort(files, sorter);
        int length = ProviderHelper.getPageSize(request, "count", 25);
        int offset = ProviderHelper.getOffset(request, "page", length);
        String _page = request.getParameter("page");
        int page = (_page != null) ? Integer.parseInt(_page) : 0;
        addPagingLinks(request, feed, page, length);
        if (offset > files.length)
            return;
        for (int n = offset; n < offset + length && n < files.length; n++) {
            File file = files[n];
            Entry entry = getEntry(file);
            feed.addEntry((Entry)entry.clone());
        }
    }

    public ResponseContext getFeed(RequestContext request) {
        Feed feed = abdera.newFeed();
        feed.setId(config.getServerConfiguration().getServerUri() + "/" + config.getFeedId());
        feed.setTitle(config.getFeedTitle());
        feed.addAuthor(config.getFeedAuthor());
        feed.addLink(config.getFeedUri());
        feed.addLink(config.getFeedUri(), "self");
        feed.setUpdated(new Date());
        getEntries(request, feed, root);
        return ProviderHelper.returnBase(feed.getDocument(), 200, null);
    }

    public ResponseContext deleteEntry(RequestContext request) {
        Target target = request.getTarget();
        String key = target.getParameter("entry");
        File file = getFile(key, false);
        if (file.exists())
            file.delete();
        return ProviderHelper.nocontent();
    }

    public ResponseContext getEntry(RequestContext request) {
        Target target = request.getTarget();
        String key = target.getParameter("entry");
        File file = getFile(key, false);
        Entry entry = getEntry(file);
        if (entry != null)
            return ProviderHelper.returnBase(entry.getDocument(), 200, null);
        else
            return ProviderHelper.notfound(request);
    }

    public ResponseContext postEntry(RequestContext request) {
        if (request.isAtom()) {
            try {
                Entry entry = (Entry)request.getDocument().getRoot().clone();
                String key = createKey(request);
                setEditDetail(request, entry, key);
                File file = getFile(key);
                FileOutputStream out = new FileOutputStream(file);
                entry.writeTo(out);
                String edit = entry.getEditLinkResolvedHref().toString();
                return ProviderHelper.returnBase(entry.getDocument(), 201, null).setLocation(edit);
            } catch (Exception e) {
                return ProviderHelper.badrequest(request);
            }
        } else {
            return ProviderHelper.notsupported(request);
        }
    }

    private void setEditDetail(RequestContext request, Entry entry, String key) throws IOException {
        Target target = request.getTarget();
        String feed = target.getParameter("feed");
        String id = key;
        entry.setEdited(new Date());
        Link link = entry.getEditLink();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("feed", feed);
        params.put("entry", id);
        String href = request.absoluteUrlFor("entry", params);
        if (link == null) {
            entry.addLink(href, "edit");
        } else {
            link.setHref(href);
        }
    }

    private File getFile(String key) {
        return getFile(key, true);
    }

    private File getFile(String key, boolean post) {
        File file = new File(root, key);
        if (post && file.exists())
            throw new RuntimeException("File exists");
        return file;
    }

    private String createKey(RequestContext request) throws IOException {
        String slug = request.getSlug();
        if (slug == null) {
            slug = ((Entry)request.getDocument().getRoot()).getTitle();
        }
        return Sanitizer.sanitize(slug, "", true, Normalizer.Form.D);
    }

    public ResponseContext putEntry(RequestContext request) {
        if (request.isAtom()) {
            try {
                Entry entry = (Entry)request.getDocument().getRoot().clone();
                String key = request.getTarget().getParameter("entry");
                setEditDetail(request, entry, key);
                File file = getFile(key, false);
                FileOutputStream out = new FileOutputStream(file);
                entry.writeTo(out);
                String edit = entry.getEditLinkResolvedHref().toString();
                return ProviderHelper.returnBase(entry.getDocument(), 200, null).setLocation(edit);
            } catch (Exception e) {
                return ProviderHelper.badrequest(request);
            }
        } else {
            return ProviderHelper.notsupported(request);
        }
    }

    private static class FileSorter implements Comparator<File> {
        public int compare(File o1, File o2) {
            return o1.lastModified() > o2.lastModified() ? -1 : o1.lastModified() < o2.lastModified() ? 1 : 0;
        }
    }
}
