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
package org.apache.abdera.protocol.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.protocol.client.util.AutoReleasingInputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;

public class CommonsResponse extends AbstractClientResponse implements ClientResponse {

    private final HttpMethod method;

    protected CommonsResponse(Abdera abdera, HttpMethod method) {
        super(abdera);
        if (method.isRequestSent())
            this.method = method;
        else
            throw new IllegalStateException();
        parse_cc();
        getServerDate();
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    /**
     * Return the request method
     */
    public String getMethod() {
        return method.getName();
    }

    /**
     * Return the status code of the response
     */
    public int getStatus() {
        return method.getStatusCode();
    }

    /**
     * Return the status text of the response
     */
    public String getStatusText() {
        return method.getStatusText();
    }

    /**
     * Release the resources associated with this response
     */
    public void release() {
        method.releaseConnection();
    }

    /**
     * Return the value of the named HTTP header
     */
    public String getHeader(String header) {
        Header h = method.getResponseHeader(header);
        return h != null ? h.getValue() : null;
    }

    /**
     * Return the values of the named HTTP header
     */
    public Object[] getHeaders(String header) {
        Header[] headers = method.getResponseHeaders(header);
        List<Object> values = new ArrayList<Object>();
        for (Header h : headers) {
            values.add(h.getValue());
        }
        return values.toArray(new Object[values.size()]);
    }

    /**
     * Return all of the HTTP headers
     */
    public Map<String, Object[]> getHeaders() {
        Header[] headers = method.getResponseHeaders();
        Map<String, Object[]> map = new HashMap<String, Object[]>();
        for (Header header : headers) {
            Object[] values = map.get(header.getName());
            List<Object> list = values == null ? new ArrayList<Object>() : Arrays.asList(values);
            list.add(header.getValue());
            map.put(header.getName(), list.toArray(new Object[list.size()]));
        }
        return java.util.Collections.unmodifiableMap(map);
    }

    /**
     * Return a listing of HTTP header names
     */
    public String[] getHeaderNames() {
        Header[] headers = method.getResponseHeaders();
        List<String> list = new ArrayList<String>();
        for (Header h : headers) {
            String name = h.getName();
            if (!list.contains(name))
                list.add(name);
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Return the request URI
     */
    public String getUri() {
        try {
            return method.getURI().toString();
        } catch (URIException e) {
        }
        return null; // shouldn't happen
    }

    /**
     * Return the inputstream for reading the content of the response. The InputStream returned will automatically
     * decode Content-Encodings and will automatically release the response when the stream has been read fully.
     */
    public InputStream getInputStream() throws IOException {
        if (in == null ) {
            String ce = getHeader("Content-Encoding");
            in = method.getResponseBodyAsStream();
            if (ce != null && in != null) {
                in = CompressionUtil.getDecodingInputStream(in, ce);
            }
            in = new AutoReleasingInputStream(method, in);
        }
        return in;
    }

}
