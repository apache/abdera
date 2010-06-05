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
package org.apache.abdera.protocol.server.provider.managed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.abdera.protocol.server.RequestContext;

public abstract class AbstractServerConfiguration extends ServerConfiguration {

    private final String host;
    private final int port;
    private final boolean secure;

    protected AbstractServerConfiguration(RequestContext request) {
        Object ohost = request.getProperty(RequestContext.Property.SERVERNAME);
        Object oport = request.getProperty(RequestContext.Property.SERVERPORT);
        Object osec = request.getProperty(RequestContext.Property.SECURE);
        host = ohost != null ? (String)ohost : "localhost";
        port = oport != null ? ((Integer)oport).intValue() : 9002;
        secure = osec != null ? ((Boolean)osec).booleanValue() : false;
    }

    @Override
    public String getAdapterConfigLocation() {
        return "abdera/adapter/config/";
    }

    @Override
    public String getFeedConfigLocation() {
        return "abdera/adapter/";
    }

    @Override
    public String getFeedConfigSuffix() {
        return ".properties";
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getServerUri() {
        StringBuilder buf = new StringBuilder();
        buf.append(secure ? "https://" : "http://");
        buf.append(host);
        if (port != 80) {
            buf.append(":");
            buf.append(port);
        }
        return buf.toString();
    }

    @Override
    public FeedConfiguration loadFeedConfiguration(String feedId) throws IOException {
        String fileName = getFeedConfigLocation() + feedId + getFeedConfigSuffix();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (in == null)
            throw new FileNotFoundException();
        Properties props = new Properties();
        props.load(in);
        in.close();
        return FeedConfiguration.getFeedConfiguration(feedId, props, this);
    }
}
