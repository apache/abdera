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
package org.apache.abdera2.common.protocol;

import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.text.Slug;
import org.apache.abdera2.common.http.QualityHelper;
import org.apache.abdera2.common.http.QualityHelper.QToken;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProviderHelper {
    public final static Log log = LogFactory.getLog(ProviderHelper.class);

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
    public static ResponseContext createErrorResponse(Provider provider, final int code, final String message) {
        return createErrorResponse(provider, code, message, null);
    }

    /**
     * Returns an Error document based on the StreamWriter
     */
    public static ResponseContext createErrorResponse(Provider provider,
                                                              final int code,
                                                              final String message,
                                                              final Throwable t) {
        return (ResponseContext)provider.createErrorResponse(code, message, t);
    }

    /**
     * Return a server error
     */
    public static ResponseContext servererror(RequestContext request, String reason, Throwable t) {
        log.info(Localizer.get("SERVER_ERROR"), t);
        return createErrorResponse(request.getProvider(), 500, reason, t);
    }

    /**
     * Return a server error
     */
    public static  ResponseContext servererror(RequestContext request, Throwable t) {;
        return servererror(request, "Server Error", t);
    }

    /**
     * Return an unauthorized error
     */
    public static  ResponseContext unauthorized(RequestContext request, String reason) {
        log.debug(Localizer.get("UNAUTHORIZED"));
        return createErrorResponse(request.getProvider(), 401, reason);
    }

    public static  ResponseContext unauthorized(RequestContext request) {
        return unauthorized(request, "Unauthorized");
    }

    /**
     * Return an unauthorized error
     */
    public static  ResponseContext forbidden(RequestContext request, String reason) {
        log.debug(Localizer.get("FORBIDDEN"));
        return createErrorResponse(request.getProvider(), 403, reason);
    }

    public static  ResponseContext forbidden(RequestContext request) {
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
    public static  ResponseContext notfound(RequestContext request, String reason) {
        log.debug(Localizer.get("UNKNOWN"));
        return createErrorResponse(request.getProvider(), 404, reason);
    }

    public static  ResponseContext notfound(RequestContext request) {
        return notfound(request, "Not Found");
    }

    /**
     * Return a 405 method not allowed error
     */
    public static  ResponseContext notallowed(RequestContext request, String reason, String... methods) {
        log.debug(Localizer.get("NOT.ALLOWED"));
        ResponseContext resp = createErrorResponse(request.getProvider(), 405, reason);
        resp.setAllow(methods);
        return resp;
    }

    public static  ResponseContext notallowed(RequestContext request, String... methods) {
        return notallowed(request, "Method Not Allowed", methods);
    }

    public static  ResponseContext notallowed(RequestContext request) {
        return notallowed(request, getDefaultMethods(request));
    }

    /**
     * Return a 400 bad request error
     */
    public static  ResponseContext badrequest(RequestContext request, String reason) {
        log.debug(Localizer.get("BAD.REQUEST"));
        return createErrorResponse(request.getProvider(), 400, reason);
    }

    public static  ResponseContext badrequest(RequestContext request) {
        return badrequest(request, "Bad Request");
    }

    /**
     * Return a 409 conflict error
     */
    public static  ResponseContext conflict(RequestContext request, String reason) {
        log.debug(Localizer.get("CONFLICT"));
        return createErrorResponse(request.getProvider(), 409, reason);
    }

    public static  ResponseContext conflict(RequestContext request) {
        return conflict(request, "Conflict");
    }

    /**
     * Return a service unavailable error
     */
    public static  ResponseContext unavailable(RequestContext request, String reason) {
        log.debug(Localizer.get("UNAVAILABLE"));
        return createErrorResponse(request.getProvider(), 503, reason);
    }

    public static  ResponseContext unavailable(RequestContext request) {
        return unavailable(request, "Service Unavailable");
    }

    public static  ResponseContext notmodified(RequestContext request, String reason) {
        log.debug(Localizer.get("NOT.MODIFIED"));
        return new EmptyResponseContext(304, reason);
    }

    public static  ResponseContext notmodified(RequestContext request) {
        return notmodified(request, "Not Modified");
    }

    public static  ResponseContext preconditionfailed(RequestContext request, String reason) {
        log.debug(Localizer.get("PRECONDITION.FAILED"));
        return createErrorResponse(request.getProvider(), 412, reason);
    }

    public static  ResponseContext preconditionfailed(RequestContext request) {
        return preconditionfailed(request, "Precondition Failed");
    }

    /**
     * Return a 415 media type not-supported error
     */
    public static  ResponseContext notsupported(RequestContext request, String reason) {
        log.debug(Localizer.get("NOT.SUPPORTED"));
        return createErrorResponse(request.getProvider(), 415, reason);
    }

    public static  ResponseContext notsupported(RequestContext request) {
        return notsupported(request, "Media Type Not Supported");
    }

    /**
     * Return a 423 locked error
     */
    public static  ResponseContext locked(RequestContext request, String reason) {
        log.debug(Localizer.get("LOCKED"));
        return createErrorResponse(request.getProvider(), 423, reason);
    }

    public static  ResponseContext locked(RequestContext request) {
        return locked(request, "Locked");
    }

    /**
     * Sanitize the value of a Slug header. Any non alphanumeric characters in the slug are replaced with an underscore
     */
    public static String sanitizeSlug(String slug) {
        if (slug == null)
            throw new IllegalArgumentException(Localizer.get("SLUG.NOT.NULL"));
        String sanitized = Slug.create(slug).toString();
        log.debug(Localizer.sprintf("SLUG.SANITIZED", slug, sanitized));
        return sanitized;
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

    public static String[] getAcceptableTypes(RequestContext request) {
        String accept = request.getAccept();
        QToken[] tokens = QualityHelper.orderByQ(accept);
        String[] res = new String[tokens.length];
        for (int n = 0; n < tokens.length; n++)
          res[n] = tokens[n].token();
        return res;
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
