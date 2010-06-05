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
package org.apache.abdera.ext.json;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

/**
 * Servlet that will do an HTTP GET to retrieve an Atom document then convert that into a JSON doc. The URL pattern is
 * simple: http://.../servlet/path/{url}
 */
@SuppressWarnings("unchecked")
public class JSONServlet extends HttpServlet {

    private static final long serialVersionUID = 1414392196430276024L;

    private Abdera getAbdera() {
        ServletContext sc = getServletContext();
        Abdera abdera = null;
        synchronized (sc) {
            abdera = (Abdera)sc.getAttribute(Abdera.class.getName());
            if (abdera == null) {
                abdera = new Abdera();
                sc.setAttribute(Abdera.class.getName(), abdera);
            }
        }
        return abdera;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = request.getPathInfo();
        if (url != null && url.length() > 1)
            url = URLDecoder.decode(url, "UTF-8");
        else {
            response.sendError(400);
            return;
        }
        url = url.substring(1);

        Abdera abdera = getAbdera();
        AbderaClient client = new AbderaClient(abdera);

        RequestOptions options = client.getDefaultRequestOptions();
        if (request.getHeader("If-Match") != null)
            options.setIfMatch(request.getHeader("If-Match"));
        if (request.getHeader("If-None-Match") != null)
            options.setIfNoneMatch(request.getHeader("If-None-Match"));
        if (request.getHeader("If-Modified-Since") != null)
            options.setIfNoneMatch(request.getHeader("If-Modified-Since"));
        if (request.getHeader("If-Unmodified-Since") != null)
            options.setIfNoneMatch(request.getHeader("If-Unmodified-Since"));

        ClientResponse resp = client.get(url);
        switch (resp.getType()) {
            case SUCCESS:
                try {
                    Document doc = resp.getDocument();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    if (doc.getEntityTag() != null)
                        response.setHeader("ETag", doc.getEntityTag().toString());
                    if (doc.getLanguage() != null)
                        response.setHeader("Content-Language", doc.getLanguage());
                    if (doc.getLastModified() != null)
                        response.setDateHeader("Last-Modified", doc.getLastModified().getTime());
                    OutputStream out = response.getOutputStream();
                    doc.writeTo("json", out);
                } catch (Exception e) {
                    response.sendError(500);
                    return;
                }
            case CLIENT_ERROR:
            case SERVER_ERROR:
                response.sendError(resp.getStatus(), resp.getStatusText());
                return;
        }
    }

}
