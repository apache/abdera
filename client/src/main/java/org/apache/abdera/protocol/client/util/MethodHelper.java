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
package org.apache.abdera.protocol.client.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class MethodHelper {

    public static enum HopByHop {
        Connection, KeepAlive, ProxyAuthenticate, ProxyAuthorization, TE, Trailers, TransferEncoding, Upgrade;
    }

    public static enum Method {
        GET, POST, PUT, DELETE, OPTIONS, TRACE, HEAD, OTHER;

        public static Method fromString(String method) {
            try {
                return Method.valueOf(method.toUpperCase());
            } catch (Exception e) {
                return OTHER;
            }
        }
    }

    public static Map<String, Object[]> getCacheableHeaders(ClientResponse response) {
        Map<String, Object[]> map = new HashMap<String, Object[]>();
        String[] headers = response.getHeaderNames();
        for (String header : headers) {
            if (MethodHelper.isCacheableHeader(header, response)) {
                Object[] list = response.getHeaders(header);
                map.put(header, list);
            }
        }
        return map;
    }

    public static boolean isCacheableHeader(String header, ClientResponse response) {
        return !isNoCacheOrPrivate(header, response) && !isHopByHop(header);
    }

    public static boolean isNoCacheOrPrivate(String header, ClientResponse response) {
        String[] no_cache_headers = response.getNoCacheHeaders();
        String[] private_headers = response.getPrivateHeaders();
        return contains(no_cache_headers, header) || contains(private_headers, header);
    }

    private static boolean contains(String[] headers, String header) {
        if (headers != null) {
            for (String h : headers) {
                if (h.equals(header))
                    return true;
            }
        }
        return false;
    }

    /**
     * We don't cache hop-by-hop headers TODO: There may actually be other hop-by-hop headers we need to filter out.
     * They'll be listed in the Connection header. see Section 14.10 of RFC2616 (last paragraph)
     */
    public static boolean isHopByHop(String header) {
        try {
            HopByHop.valueOf(header.replaceAll("-", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static EntityEnclosingMethod getMethod(EntityEnclosingMethod method, RequestEntity entity) {
        if (entity != null)
            method.setRequestEntity(entity);
        return method;
    }

    public static HttpMethod createMethod(String method, String uri, RequestEntity entity, RequestOptions options) {
        if (method == null)
            return null;
        Method m = Method.fromString(method);
        Method actual = null;
        HttpMethod httpMethod = null;
        if (options.isUsePostOverride()) {
            if (m.equals(Method.PUT)) {
                actual = m;
            } else if (m.equals(Method.DELETE)) {
                actual = m;
            }
            if (actual != null)
                m = Method.POST;
        }
        switch (m) {
            case GET:
                httpMethod = new GetMethod(uri);
                break;
            case POST:
                httpMethod = getMethod(new PostMethod(uri), entity);
                break;
            case PUT:
                httpMethod = getMethod(new PutMethod(uri), entity);
                break;
            case DELETE:
                httpMethod = new DeleteMethod(uri);
                break;
            case HEAD:
                httpMethod = new HeadMethod(uri);
                break;
            case OPTIONS:
                httpMethod = new OptionsMethod(uri);
                break;
            case TRACE:
                httpMethod = new TraceMethod(uri);
                break;
            default:
                httpMethod = getMethod(new ExtensionMethod(method, uri), entity);
        }
        if (actual != null) {
            httpMethod.addRequestHeader("X-HTTP-Method-Override", actual.name());
        }
        initHeaders(options, httpMethod);

        // by default use expect-continue is enabled on the client
        // only disable if explicitly disabled
        if (!options.isUseExpectContinue())
            httpMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);

        // should we follow redirects, default is true
        if (!(httpMethod instanceof EntityEnclosingMethod))
            httpMethod.setFollowRedirects(options.isFollowRedirects());

        return httpMethod;
    }

    private static void initHeaders(RequestOptions options, HttpMethod method) {
        String[] headers = options.getHeaderNames();
        for (String header : headers) {
            Object[] values = options.getHeaders(header);
            for (Object value : values) {
                method.addRequestHeader(header, value.toString());
            }
        }
        String cc = options.getCacheControl();
        if (cc != null && cc.length() != 0)
            method.setRequestHeader("Cache-Control", cc);
        if (options.getAuthorization() != null)
            method.setDoAuthentication(false);
    }

    public static final class ExtensionMethod extends EntityEnclosingMethod {
        private String method = null;

        public ExtensionMethod(String method, String uri) {
            super(method);
            try {
                this.setURI(new URI(uri, false));
            } catch (Exception e) {
            }
            this.method = method;
        }

        @Override
        public String getName() {
            return method;
        }
    }

    public static RequestOptions createDefaultRequestOptions() {
        RequestOptions options = new RequestOptions();
        options.setAcceptEncoding("gzip", "deflate");
        options.setAccept("application/atom+xml;type=entry",
                          "application/atom+xml;type=feed",
                          "application/atom+xml",
                          "application/atomsvc+xml",
                          "application/atomcat+xml",
                          "application/xml;q=0.5",
                          "text/xml;q=0.5",
                          "*/*;q=0.01");
        options.setAcceptCharset("utf-8", "*;q=0.5");
        return options;
    }
}
