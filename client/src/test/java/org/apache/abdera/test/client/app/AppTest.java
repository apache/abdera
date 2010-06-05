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
package org.apache.abdera.test.client.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.test.client.JettyUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.WriterOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test to make sure that we can operate as a simple APP client
 */
@SuppressWarnings("serial")
public class AppTest {

    protected static void getServletHandler(String... servletMappings) {
        for (int n = 0; n < servletMappings.length; n = n + 2) {
            String name = servletMappings[n];
            String root = servletMappings[n + 1];
            JettyUtil.addServlet(name, root);
        }
    }

    protected String getBase() {
        return "http://localhost:" + JettyUtil.getPort();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        getServletHandler();
        JettyUtil.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JettyUtil.stop();
    }

    private static Abdera abdera = new Abdera();

    private static Factory getFactory() {
        return abdera.getFactory();
    }

    private static Parser getParser() {
        return abdera.getParser();
    }

    private static AppTest INSTANCE = null;

    private static Document<Service> init_service_document(String base) {
        try {
            Service service = getFactory().newService();
            Workspace workspace = service.addWorkspace("Test");
            workspace.addCollection("Entries", base + "/collections/entries").setAcceptsEntry();
            workspace.addCollection("Other", base + "/collections/other").setAccept("text/plain");
            Document<Service> doc = service.getDocument();
            return doc;
        } catch (Exception e) {
        }
        return null;
    }

    private static Document<Feed> init_entries_document(String base) {
        try {
            Feed feed = getFactory().newFeed();
            feed.setId(base + "/collections/entries");
            feed.setTitle("Entries");
            feed.setUpdated(new Date());
            feed.addLink(base + "/collections/entries");
            feed.addLink(base + "/collections/entries", "self");
            feed.addAuthor("James");
            Document<Feed> doc = feed.getDocument();
            return doc;
        } catch (Exception e) {
        }
        return null;
    }

    public static class ServiceDocumentServlet extends HttpServlet {
        private Document<Service> service = init_service_document(AppTest.INSTANCE.getBase());

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/atomsvc+xml; charset=utf-8");
            WriterOptions options = service.getDefaultWriterOptions();
            options.setCharset("UTF-8");
            service.writeTo(response.getOutputStream(), options);
        }
    }

    /**
     * this implements a very simple (and quite buggy) APP server. It's just enough for us to test the client behaviors.
     * I'm sure it could be greatly improved.
     */
    public static class CollectionServlet extends HttpServlet {
        protected Document<Feed> feed = init_entries_document(AppTest.INSTANCE.getBase());
        protected Map<String, String> media = new HashMap<String, String>();

        private String[] tokens = null;
        private final static int COLLECTION = 0;
        private final static int ENTRY = 1;
        private final static int MEDIA = 2;

        private int getTargetType(HttpServletRequest request) {
            tokens = request.getRequestURI().split("/");
            if (tokens[2].equals("entries") && tokens.length == 3)
                return COLLECTION;
            if (tokens[2].equals("entries") && tokens.length == 4)
                return ENTRY;
            if (tokens[2].equals("media") && tokens.length == 4)
                return MEDIA;
            return -1;
        }

        private int getTarget() {
            return (tokens.length != 4) ? -1 : Integer.parseInt(tokens[3]);
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            int t = getTargetType(request);
            switch (t) {
                case COLLECTION:
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/atom+xml; charset=utf-8");
                    WriterOptions options = feed.getDefaultWriterOptions();
                    options.setCharset("UTF-8");
                    feed.writeTo(response.getOutputStream(), options);
                    break;
                case ENTRY:
                    try {
                        Entry entry = feed.getRoot().getEntries().get(getTarget());
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/atom+xml; charset=utf-8");
                        options = entry.getDefaultWriterOptions();
                        options.setCharset("UTF-8");
                        entry.writeTo(response.getOutputStream(), options);
                    } catch (Exception e) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        break;
                    }
                    break;
                case MEDIA:
                    try {
                        String m = media.get(AppTest.INSTANCE.getBase() + "/collections/entries/" + getTarget());
                        if (m != null) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("text/plain");
                            response.getWriter().write(m);
                            break;
                        } else {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
                    } catch (Exception e) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
            }
        }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            int t = getTargetType(request);
            switch (t) {
                case COLLECTION:
                    try {
                        if (MimeTypeHelper.isMatch(request.getContentType(), "application/atom+xml;type=entry")) {
                            MimeType type = new MimeType(request.getContentType());
                            String charset = type.getParameter("charset");
                            String uri = AppTest.INSTANCE.getBase() + "/collections/entries";
                            ParserOptions options = getParser().getDefaultParserOptions();
                            options.setCharset(charset);
                            Document<?> doc = getParser().parse(request.getInputStream(), uri, options);
                            if (doc.getRoot() instanceof Entry) {
                                Entry entry = (Entry)doc.getRoot().clone();
                                String newID =
                                    AppTest.INSTANCE.getBase() + "/collections/entries/"
                                        + feed.getRoot().getEntries().size();
                                entry.setId(newID);
                                entry.setUpdated(new Date());
                                entry.addLink(entry.getId().toString(), "edit");
                                entry.addLink(entry.getId().toString(), "self");
                                feed.getRoot().insertEntry(entry);
                                response.setStatus(HttpServletResponse.SC_CREATED);
                                response.setHeader("Location", entry.getId().toString());
                                response.setHeader("Content-Location", entry.getId().toString());
                                WriterOptions woptions = entry.getDefaultWriterOptions();
                                woptions.setCharset("UTF-8");
                                entry.writeTo(response.getOutputStream(), woptions);
                                return;
                            }
                        }
                        if (MimeTypeHelper.isMatch(request.getContentType(), "text/plain")) {
                            int n = feed.getRoot().getEntries().size();
                            String media = read(request.getInputStream());
                            Entry entry = getFactory().newEntry();
                            String newID = AppTest.INSTANCE.getBase() + "/collections/entries/" + n;
                            String slug = request.getHeader("Slug");
                            entry.setId(newID);
                            entry.setTitle(slug);
                            entry.setUpdated(new Date());
                            entry.setSummary(slug);
                            entry.addLink(entry.getId().toString(), "edit");
                            entry.addLink(AppTest.INSTANCE.getBase() + "/collections/media/" + n, "edit-media")
                                .setMimeType("text/plain");
                            entry.addLink(entry.getId().toString(), "self");
                            entry.setContent(new IRI(AppTest.INSTANCE.getBase() + "/collections/media/" + n),
                                             "text/plain");
                            feed.getRoot().insertEntry(entry);
                            this.media.put(entry.getId().toString(), media);
                            response.setStatus(HttpServletResponse.SC_CREATED);
                            response.setHeader("Location", entry.getId().toString());
                            response.setHeader("Content-Location", entry.getId().toString());
                            WriterOptions woptions = entry.getDefaultWriterOptions();
                            woptions.setCharset("UTF-8");
                            entry.writeTo(response.getOutputStream(), woptions);
                            return;
                        }
                        response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    } catch (Exception e) {
                    }
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }

        protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            int t = getTargetType(request);
            int target = getTarget();
            switch (t) {
                case ENTRY:
                    try {
                        if (MimeTypeHelper.isMatch(request.getContentType(), "application/atom+xml;type=entry")) {
                            Entry entry = feed.getRoot().getEntries().get(target);
                            MimeType type = new MimeType(request.getContentType());
                            String charset = type.getParameter("charset");
                            String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
                            ParserOptions options = getParser().getDefaultParserOptions();
                            options.setCharset(charset);
                            Document<?> doc = getParser().parse(request.getInputStream(), uri, options);
                            if (doc.getRoot() instanceof Entry) {
                                Entry newentry = (Entry)doc.getRoot().clone();
                                if (newentry.getId().equals(entry.getId())) {
                                    newentry.setUpdated(new Date());
                                    entry.discard();
                                    feed.getRoot().insertEntry(newentry);
                                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                    return;
                                } else {
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot change atom:id");
                                    return;
                                }
                            }
                        }
                        response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    } catch (Exception e) {
                    }
                    break;
                case MEDIA:
                    if (MimeTypeHelper.isMatch(request.getContentType(), "text/plain")) {
                        String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
                        String media = read(request.getInputStream());
                        this.media.put(uri, media);
                        Entry entry = feed.getRoot().getEntries().get(target);
                        entry.setUpdated(new Date());
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                        return;
                    }
                    response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }

        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            int t = getTargetType(request);
            int target = getTarget();
            switch (t) {
                case ENTRY:
                case MEDIA:
                    String uri = AppTest.INSTANCE.getBase() + "/collections/entries/" + target;
                    Entry entry = feed.getRoot().getEntries().get(target);
                    entry.discard();
                    media.remove(uri);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                default:
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    private static String read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int m = -1;
        while ((m = in.read()) != -1) {
            out.write(m);
        }
        String resp = new String(out.toByteArray());
        return resp.trim();
    }

    public AppTest() {
        AppTest.INSTANCE = this;
    }

    protected static void getServletHandler() {
        getServletHandler(ServiceDocumentServlet.class.getName(),
                          "/service",
                          CollectionServlet.class.getName(),
                          "/collections/*");
    }

    @Test
    public void testRequestOptions() throws Exception {
        AbderaClient abderaClient = new AbderaClient();
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setIfModifiedSince(new Date());
        assertNotNull(options.getIfModifiedSince());

        options.set4xxRequestException(true);
        assertTrue(options.is4xxRequestException());

        options.set5xxRequestException(true);
        assertTrue(options.is5xxRequestException());

        options.setAccept("text/plain");
        assertEquals("text/plain", options.getAccept());

        options.setAcceptCharset("UTF-8");
        assertEquals("UTF-8", options.getAcceptCharset());

        options.setAcceptEncoding("gzip");
        assertEquals("gzip", options.getAcceptEncoding());

        options.setAcceptLanguage("en-US");
        assertEquals("en-US", options.getAcceptLanguage());

        options.setAuthorization("auth");
        assertEquals("auth", options.getAuthorization());

        options.setCacheControl("no-cache");
        assertEquals("no-cache", options.getCacheControl());

        options.setContentType("text/plain");
        assertTrue(MimeTypeHelper.isMatch(options.getContentType(), new MimeType("text/plain")));

        options.setEncodedHeader("foo", "UTF-8", "bar");
        assertEquals("bar", options.getDecodedHeader("foo"));

        options.setHeader("foo", "bar");
        assertEquals("bar", options.getHeader("foo"));

        options.setIfMatch("testing");
        assertTrue(EntityTag.matchesAny(new EntityTag("testing"), options.getIfMatch()));

        options.setIfNoneMatch("testing");
        assertTrue(EntityTag.matchesAny(new EntityTag("testing"), options.getIfNoneMatch()));

        options.setSlug("This is the slug");
        assertEquals("This is the slug", options.getSlug());

        options.setUsePostOverride(true);
        assertTrue(options.isUsePostOverride());
    }

    @Test
    public void testAppClient() throws Exception {
        AbderaClient abderaClient = new AbderaClient();
        Entry entry = getFactory().newEntry();
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setHeader("Connection", "close");
        options.setUseExpectContinue(false);

        // do the introspection step
        ClientResponse response = abderaClient.get("http://localhost:" + JettyUtil.getPort() + "/service", options);
        String col_uri;

        try {
            assertEquals(200, response.getStatus());

            Document<Service> service_doc = response.getDocument();
            assertNotNull(service_doc);
            assertEquals(1, service_doc.getRoot().getWorkspaces().size());

            Workspace workspace = service_doc.getRoot().getWorkspace("Test");
            assertNotNull(workspace);

            for (Collection c : workspace.getCollections()) {
                assertNotNull(c.getTitle());
                assertNotNull(c.getHref());
            }

            col_uri = getBase() + "/collections/entries";
        } finally {
            response.release();
        }

        // post a new entry
        response = abderaClient.post(col_uri, entry, options);

        String self_uri;

        try {
            assertEquals(201, response.getStatus());
            assertNotNull(response.getLocation());
            assertNotNull(response.getContentLocation());

            self_uri = response.getLocation().toString();
        } finally {
            response.release();
        }

        // get the collection to see if our entry is there
        response = abderaClient.get(col_uri, options);

        try {
            assertEquals(200, response.getStatus());
            Document<Feed> feed_doc = response.getDocument();
            assertEquals(1, feed_doc.getRoot().getEntries().size());
        } finally {
            response.release();
        }

        // get the entry to see if we can get it
        response = abderaClient.get(self_uri, options);

        String edit_uri;

        try {
            assertEquals(200, response.getStatus());
            Document<Entry> entry_doc = response.getDocument();

            // this isn't always true, but for our tests they are the same
            assertEquals(self_uri, entry_doc.getRoot().getId().toString());

            // get the edit uri from the entry
            edit_uri = entry_doc.getRoot().getEditLink().getHref().toString();

            // change the entry
            Document<Entry> doc = response.getDocument();
            entry = (Entry)doc.getRoot().clone();
            entry.setTitle("New title");
        } finally {
            response.release();
        }

        // submit the changed entry back to the server
        response = abderaClient.put(edit_uri, entry, options);

        try {
            assertEquals(204, response.getStatus());
        } finally {
            response.release();
        }

        // check to see if the entry was modified properly
        response = abderaClient.get(self_uri, options);

        try {
            assertEquals(200, response.getStatus());

            Document<Entry> entry_doc = response.getDocument();
            assertEquals("New title", entry_doc.getRoot().getTitle());
        } finally {
            response.release();
        }

        // delete the entry
        response = abderaClient.delete(edit_uri, options);

        try {
            assertEquals(204, response.getStatus());
        } finally {
            response.release();
        }

        // is it gone?
        response = abderaClient.get(self_uri, options);

        try {
            assertEquals(404, response.getStatus());
        } finally {
            response.release();
        }

        // YAY! We're a working APP client

        // Now let's try to do a media post

        // Post the media resource
        options = abderaClient.getDefaultRequestOptions();
        options.setContentType("text/plain");
        options.setHeader("Connection", "close");
        options.setUseExpectContinue(false);

        response = abderaClient.post(col_uri, new ByteArrayInputStream("test".getBytes()), options);

        try {
            assertEquals(201, response.getStatus());
            assertNotNull(response.getLocation());
            assertNotNull(response.getContentLocation());

            self_uri = response.getLocation().toString();
        } finally {
            response.release();
        }

        // was an entry created?
        options = abderaClient.getDefaultRequestOptions();
        options.setHeader("Connection", "close");
        response = abderaClient.get(self_uri, options);

        String edit_media, media;

        try {
            assertEquals(200, response.getStatus());
            Document<Entry> entry_doc = response.getDocument();

            // this isn't always true, but for our tests they are the same
            assertEquals(self_uri, entry_doc.getRoot().getId().toString());

            // get the right links from the entry
            edit_uri = entry_doc.getRoot().getEditLink().getHref().toString();
            edit_media = entry_doc.getRoot().getLink("edit-media").getHref().toString();
            media = entry_doc.getRoot().getContentElement().getSrc().toString();

            // edit the entry
            Document<Entry> doc = response.getDocument();
            entry = (Entry)doc.getRoot().clone();
            entry.setTitle("New title");
        } finally {
            response.release();
        }

        // submit the changes
        options = abderaClient.getDefaultRequestOptions();
        options.setContentType("application/atom+xml;type=entry");
        options.setHeader("Connection", "close");
        options.setUseExpectContinue(false);

        response = abderaClient.put(edit_uri, entry, options);

        try {
            assertEquals(204, response.getStatus());
        } finally {
            response.release();
        }

        // get the media resource
        response = abderaClient.get(media);

        try {
            assertEquals(200, response.getStatus());

            String mediavalue = read(response.getInputStream());
            assertEquals("test", mediavalue);
        } finally {
            response.release();
        }

        // edit the media resource
        options = abderaClient.getDefaultRequestOptions();
        options.setHeader("Connection", "close");
        options.setContentType("text/plain");
        options.setUseExpectContinue(false);

        response = abderaClient.put(edit_media, new ByteArrayInputStream("TEST".getBytes()), options);

        try {
            assertEquals(204, response.getStatus());
        } finally {
            response.release();
        }

        // was the resource changed?
        response = abderaClient.get(media, options);

        try {
            assertEquals(200, response.getStatus());

            String mediavalue = read(response.getInputStream());
            assertEquals("TEST", mediavalue);
        } finally {
            response.release();
        }

        // delete the entry
        response = abderaClient.delete(edit_uri, options);

        try {
            assertEquals(204, response.getStatus());
        } finally {
            response.release();
        }

        // is the entry gone?
        response = abderaClient.get(self_uri, options);

        try {
            assertEquals(404, response.getStatus());
        } finally {
            response.release();
        }

        // is the media resource gone?
        options.setNoCache(true); // need to force revalidation to check

        response = abderaClient.get(media, options);

        try {
            assertEquals(404, response.getStatus());
        } finally {
            response.release();
        }

        // YAY! We can handle media link entries
    }

    @Test
    public void testEntityTag() throws Exception {
        EntityTag tag1 = new EntityTag("tag");
        EntityTag tag2 = new EntityTag("tag", true); // weak;
        assertFalse(tag1.isWeak());
        assertTrue(tag2.isWeak());
        assertFalse(EntityTag.matches(tag1, tag2));
        assertFalse(EntityTag.matchesAny(tag1, new EntityTag[] {tag2}));
        assertEquals("\"tag\"", tag1.toString());
        assertEquals("W/\"tag\"", tag2.toString());
        tag1 = EntityTag.parse("\"tag\"");
        assertFalse(tag1.isWeak());
        assertEquals("tag", tag1.getTag());
        tag2 = EntityTag.parse("W/\"tag\"");
        assertTrue(tag2.isWeak());
        assertEquals("tag", tag2.getTag());
        EntityTag[] tags = EntityTag.parseTags("\"tag1\", W/\"tag2\"");
        assertFalse(tags[0].isWeak());
        assertEquals("tag1", tags[0].getTag());
        assertTrue(tags[1].isWeak());
        assertEquals("tag2", tags[1].getTag());
    }
}
