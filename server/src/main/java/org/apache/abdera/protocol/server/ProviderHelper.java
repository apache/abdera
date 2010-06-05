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
package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.WriterFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides support methods for {@link Provider}
 */
public class ProviderHelper {
    private final static Log log = LogFactory.getLog(ProviderHelper.class);

    private ProviderHelper() {
    }

    public static int getPageSize(RequestContext request, String pagesizeparam, int defaultpagesize) {
        int size = defaultpagesize;
        try {
            String _ps = request.getParameter(pagesizeparam);
            size = (_ps != null) ? Math.min(Math.max(Integer.parseInt(_ps), 0), defaultpagesize) : defaultpagesize;
        } catch (Exception e) {
        }
        log.debug(Localizer.sprintf("PAGE.SIZE", size));
        return size;
    }

    public static int getOffset(RequestContext request, String pageparam, int pageSize) {
        int offset = 0;
        try {
            String _page = request.getParameter(pageparam);
            int page = (_page != null) ? Integer.parseInt(_page) : 1;
            page = Math.max(page, 1) - 1;
            offset = pageSize * page;
        } catch (Exception e) {
        }
        log.debug(Localizer.sprintf("OFFSET", offset));
        return offset;
    }

    /**
     * Returns an Error document based on the StreamWriter
     */
    public static AbstractResponseContext createErrorResponse(Abdera abdera, final int code, final String message) {
        return createErrorResponse(abdera, code, message, null);
    }

    /**
     * Returns an Error document based on the StreamWriter
     */
    public static AbstractResponseContext createErrorResponse(Abdera abdera,
                                                              final int code,
                                                              final String message,
                                                              final Throwable t) {
        AbstractResponseContext rc = new StreamWriterResponseContext(abdera) {
            protected void writeTo(StreamWriter sw) throws IOException {
                Error.create(sw, code, message, t);
            }
        };
        rc.setStatus(code);
        rc.setStatusText(message);
        return rc;
    }

    /**
     * Return a server error
     */
    public static ResponseContext servererror(RequestContext request, String reason, Throwable t) {
        log.info(Localizer.get("SERVER_ERROR"), t);
        return createErrorResponse(request.getAbdera(), 500, reason, t);
    }

    /**
     * Return a server error
     */
    public static ResponseContext servererror(RequestContext request, Throwable t) {
        return servererror(request, "Server Error", t);
    }

    /**
     * Return an unauthorized error
     */
    public static ResponseContext unauthorized(RequestContext request, String reason) {
        log.debug(Localizer.get("UNAUTHORIZED"));
        return createErrorResponse(request.getAbdera(), 401, reason);
    }

    public static ResponseContext unauthorized(RequestContext request) {
        return unauthorized(request, "Unauthorized");
    }

    /**
     * Return an unauthorized error
     */
    public static ResponseContext forbidden(RequestContext request, String reason) {
        log.debug(Localizer.get("FORBIDDEN"));
        return createErrorResponse(request.getAbdera(), 403, reason);
    }

    public static ResponseContext forbidden(RequestContext request) {
        return forbidden(request, "Forbidden");
    }

    /**
     * Return a 204 No Content response
     */
    public static ResponseContext nocontent(String reason) {
        return new EmptyResponseContext(204, reason);
    }

    public static ResponseContext nocontent() {
        return nocontent("Not Content");
    }

    /**
     * Return a 404 not found error
     */
    public static ResponseContext notfound(RequestContext request, String reason) {
        log.debug(Localizer.get("UNKNOWN"));
        return createErrorResponse(request.getAbdera(), 404, reason);
    }

    public static ResponseContext notfound(RequestContext request) {
        return notfound(request, "Not Found");
    }

    /**
     * Return a 405 method not allowed error
     */
    public static ResponseContext notallowed(RequestContext request, String reason, String... methods) {
        log.debug(Localizer.get("NOT.ALLOWED"));
        AbstractResponseContext resp = createErrorResponse(request.getAbdera(), 405, reason);
        resp.setAllow(methods);
        return resp;
    }

    public static ResponseContext notallowed(RequestContext request, String... methods) {
        return notallowed(request, "Method Not Allowed", methods);
    }

    public static ResponseContext notallowed(RequestContext request) {
        return notallowed(request, getDefaultMethods(request));
    }

    /**
     * Return a 400 bad request error
     */
    public static ResponseContext badrequest(RequestContext request, String reason) {
        log.debug(Localizer.get("BAD.REQUEST"));
        return createErrorResponse(request.getAbdera(), 400, reason);
    }

    public static ResponseContext badrequest(RequestContext request) {
        return badrequest(request, "Bad Request");
    }

    /**
     * Return a 409 conflict error
     */
    public static ResponseContext conflict(RequestContext request, String reason) {
        log.debug(Localizer.get("CONFLICT"));
        return createErrorResponse(request.getAbdera(), 409, reason);
    }

    public static ResponseContext conflict(RequestContext request) {
        return conflict(request, "Conflict");
    }

    /**
     * Return a service unavailable error
     */
    public static ResponseContext unavailable(RequestContext request, String reason) {
        log.debug(Localizer.get("UNAVAILABLE"));
        return createErrorResponse(request.getAbdera(), 503, reason);
    }

    public static ResponseContext unavailable(RequestContext request) {
        return unavailable(request, "Service Unavailable");
    }

    public static ResponseContext notmodified(RequestContext request, String reason) {
        log.debug(Localizer.get("NOT.MODIFIED"));
        return new EmptyResponseContext(304, reason);
    }

    public static ResponseContext notmodified(RequestContext request) {
        return notmodified(request, "Not Modified");
    }

    public static ResponseContext preconditionfailed(RequestContext request, String reason) {
        log.debug(Localizer.get("PRECONDITION.FAILED"));
        return createErrorResponse(request.getAbdera(), 412, reason);
    }

    public static ResponseContext preconditionfailed(RequestContext request) {
        return preconditionfailed(request, "Precondition Failed");
    }

    /**
     * Return a 415 media type not-supported error
     */
    public static ResponseContext notsupported(RequestContext request, String reason) {
        log.debug(Localizer.get("NOT.SUPPORTED"));
        return createErrorResponse(request.getAbdera(), 415, reason);
    }

    public static ResponseContext notsupported(RequestContext request) {
        return notsupported(request, "Media Type Not Supported");
    }

    /**
     * Return a 423 locked error
     */
    public static ResponseContext locked(RequestContext request, String reason) {
        log.debug(Localizer.get("LOCKED"));
        return createErrorResponse(request.getAbdera(), 423, reason);
    }

    public static ResponseContext locked(RequestContext request) {
        return locked(request, "Locked");
    }

    /**
     * Return a document
     */
    @SuppressWarnings("unchecked")
    public static ResponseContext returnBase(Base base, int status, Date lastModified) {
        log.debug(Localizer.get("RETURNING.DOCUMENT"));
        BaseResponseContext response = new BaseResponseContext(base);
        response.setStatus(status);
        if (lastModified != null)
            response.setLastModified(lastModified);
        // response.setContentType(MimeTypeHelper.getMimeType(base));
        Document doc = base instanceof Document ? (Document)base : ((Element)base).getDocument();
        if (doc.getEntityTag() != null) {
            response.setEntityTag(doc.getEntityTag());
        } else if (doc.getLastModified() != null) {
            response.setLastModified(doc.getLastModified());
        }
        return response;
    }

    /**
     * Sanitize the value of a Slug header. Any non alphanumeric characters in the slug are replaced with an underscore
     */
    public static String sanitizeSlug(String slug) {
        if (slug == null)
            throw new IllegalArgumentException(Localizer.get("SLUG.NOT.NULL"));
        String sanitized = Sanitizer.sanitize(slug);
        log.debug(Localizer.sprintf("SLUG.SANITIZED", slug, sanitized));
        return sanitized;
    }

    /**
     * Check to see if the entry is minimally valid according to RFC4287. This is not a complete check. It just verifies
     * that the appropriate elements are present and that their values can be accessed.
     */
    public static boolean isValidEntry(Entry entry) {
        try {
            IRI id = entry.getId();
            if (id == null || id.toString().trim().length() == 0 || !id.isAbsolute())
                return false;
            if (entry.getTitle() == null)
                return false;
            if (entry.getUpdated() == null)
                return false;
            if (entry.getAuthor() == null && (entry.getSource() != null && entry.getSource().getAuthor() == null))
                return false;
            Content content = entry.getContentElement();
            if (content == null) {
                if (entry.getAlternateLink() == null)
                    return false;
            } else {
                if ((content.getSrc() != null || content.getContentType() == Content.Type.MEDIA) && entry
                    .getSummaryElement() == null) {
                    log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", false));
                    return false;
                }
            }
        } catch (Exception e) {
            log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", false));
            return false;
        }
        log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", true));
        return true;
    }

    /**
     * Return false if the element contains any extension elements that are not supported
     */
    public static boolean checkElementNamespaces(Element element, List<String> ignore) {
        List<QName> attrs = element.getExtensionAttributes();
        for (QName qname : attrs) {
            String ns = qname.getNamespaceURI();
            if (!ignore.contains(ns))
                return false;
        }
        if (element instanceof ExtensibleElement) {
            ExtensibleElement ext = (ExtensibleElement)element;
            List<Element> extensions = ext.getExtensions();
            for (Element el : extensions) {
                QName qname = el.getQName();
                String ns = qname.getNamespaceURI();
                if (!ignore.contains(ns))
                    return false;
                if (!checkElementNamespaces(el, ignore))
                    return false;
            }
        }
        return true;
    }

    public static boolean beforeOrEqual(Date d1, Date d2) {
        long l1 = d1.getTime() / 1000; // drop milliseconds
        long l2 = d2.getTime() / 1000; // drop milliseconds
        return l1 <= l2;
    }

    public static IRI resolveBase(RequestContext request) {
        return request.getBaseUri().resolve(request.getUri());
    }

    public static String combine(String... vals) {
        StringBuilder buf = new StringBuilder();
        for (String val : vals) {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(val);
        }
        return buf.toString();
    }

    public static String[] getDefaultMethods(RequestContext request) {
        TargetType type = request.getTarget().getType();
        if (type == null)
            return new String[0];
        if (type == TargetType.TYPE_COLLECTION)
            return new String[] {"GET", "HEAD", "OPTIONS", "POST"};
        if (type == TargetType.TYPE_CATEGORIES)
            return new String[] {"GET", "HEAD", "OPTIONS"};
        if (type == TargetType.TYPE_ENTRY)
            return new String[] {"DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"};
        if (type == TargetType.TYPE_MEDIA)
            return new String[] {"DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"};
        if (type == TargetType.TYPE_SERVICE)
            return new String[] {"GET", "HEAD", "OPTIONS"};
        return new String[] {"GET", "HEAD", "OPTIONS"};
    }

    public static boolean defaultCheckMethod(RequestContext request, String[] methods) {
        return (java.util.Arrays.binarySearch(methods, request.getMethod()) >= 0);
    }

    public static boolean isAtom(RequestContext request) {
        MimeType mt = request.getContentType();
        String ctype = (mt != null) ? mt.toString() : null;
        return ctype != null && MimeTypeHelper.isAtom(ctype);
    }

    private static class QTokenComparator implements Comparator<QToken> {
        public int compare(QToken o1, QToken o2) {
            if (o1.qvalue > o2.qvalue)
                return -1;
            if (o1.qvalue < o2.qvalue)
                return 1;
            return 0;
        }
    }

    private static class QToken {
        String token;
        double qvalue = 1.0;

        QToken(String token, double qvalue) {
            this.token = token;
            this.qvalue = qvalue;
        }
    }

    /**
     * <p>
     * Utility method for parsing HTTP content negotiation headers and sorting their tokens according to their q
     * parameter values.
     * </p>
     * <p>
     * e.g. Accept: audio/*; q=0.2, audio/basic, audio/mpeg; q=0.1
     * </p>
     * <p>
     * would sort into:
     * </p>
     * 
     * <pre>
     *   audio/basic
     *   audio/*
     *   audio/mpeg
     * </pre>
     */
    public static String[] orderByQ(String header) {
        if (header == null || header.length() == 0)
            return new String[0];
        String[] tokens = header.split(",");
        QToken[] qtokens = new QToken[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            String[] qvalues = token.trim().split(";");
            String t = qvalues[0];
            if (qvalues.length > 1) {
                for (int n = 1; n < qvalues.length; n++) {
                    String[] v = qvalues[n].trim().split("=");
                    if (v[0].trim().equals("q")) {
                        double qv = Double.parseDouble(v[1]);
                        qtokens[i] = new QToken(t, qv);
                        break;
                    }
                }
            }
            if (qtokens[i] == null)
                qtokens[i] = new QToken(t, 1.0);
        }
        Arrays.sort(qtokens, new QTokenComparator());
        tokens = new String[qtokens.length];
        for (int n = 0; n < qtokens.length; n++) {
            tokens[n] = qtokens[n].token;
        }
        return tokens;
    }

    /**
     * Returns an appropriate NamedWriter instance given an appropriately formatted HTTP Accept header. The header will
     * be parsed and sorted according to it's q parameter values. The first named writer capable of supporting the
     * specified type, in order of q-value preference, will be returned. The results on this are not always predictable.
     * For instance, if the Accept header says "application/*" it could end up with either the JSON writer or the
     * PrettyXML writer, or any other writer that supports any writer that supports a specific form of "application/*".
     * It's always best to be very specific in the Accept headers.
     */
    public static NamedWriter getAcceptableNamedWriter(Abdera abdera, String accept_header) {
        String[] sorted_accepts = orderByQ(accept_header);
        WriterFactory factory = abdera.getWriterFactory();
        if (factory == null)
            return null;
        for (String accept : sorted_accepts) {
            NamedWriter writer = (NamedWriter)factory.getWriterByMediaType(accept);
            if (writer != null)
                return writer;
        }
        return null;
    }

    public static NamedWriter getNamedWriter(Abdera abdera, String mediatype) {
        WriterFactory factory = abdera.getWriterFactory();
        if (factory == null)
            return null;
        NamedWriter writer = (NamedWriter)factory.getWriterByMediaType(mediatype);
        return writer;
    }

    public static EntityTag calculateEntityTag(Base base) {
        String id = null;
        String modified = null;
        if (base instanceof Entry) {
            Entry entry = (Entry)base;
            id = entry.getId().toString();
            modified = AtomDate.format(entry.getEdited() != null ? entry.getEdited() : entry.getUpdated());
        } else if (base instanceof Feed) {
            Feed feed = (Feed)base;
            id = feed.getId().toString();
            modified = AtomDate.format(feed.getUpdated());
        } else if (base instanceof Document) {
            return calculateEntityTag(((Document<?>)base).getRoot());
        }
        return EntityTag.generate(id, modified);
    }

    public static String getEditUriFromEntry(Entry entry) {
        String editUri = null;
        List<Link> editLinks = entry.getLinks("edit");
        if (editLinks != null) {
            for (Link link : editLinks) {
                // if there is more than one edit link, we should not automatically
                // assume that it's always going to point to an Atom document
                // representation.
                if (link.getMimeType() != null) {
                    if (MimeTypeHelper.isMatch(link.getMimeType().toString(), Constants.ATOM_MEDIA_TYPE)) {
                        editUri = link.getResolvedHref().toString();
                        break;
                    }
                } else {
                    // edit link with no type attribute is the right one to use
                    editUri = link.getResolvedHref().toString();
                    break;
                }
            }
        }
        return editUri;
    }

    public static String[] getAcceptableTypes(RequestContext request) {
        String accept = request.getAccept();
        return orderByQ(accept);
    }

    public static boolean isPreferred(RequestContext request, String s1, String s2) {
        return isPreferred(getAcceptableTypes(request), s1, s2);
    }

    public static boolean isPreferred(String[] accepts, String s1, String s2) {
        int i1 = accepts.length, i2 = accepts.length;
        for (int n = 0; n < accepts.length; n++) {
            if (MimeTypeHelper.isMatch(s1, accepts[n])) {
                i1 = n;
                break;
            }
        }
        for (int n = 0; n < accepts.length; n++) {
            if (MimeTypeHelper.isMatch(s2, accepts[n])) {
                i2 = n;
                break;
            }
        }
        return i1 < i2;
    }
}
