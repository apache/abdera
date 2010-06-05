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
package org.apache.abdera.protocol.server.filters;

import java.util.Arrays;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.RequestContextWrapper;

/**
 * Abdera Filter implementation that supports the use of the X-HTTP-Method-Override header used by GData.
 */
public class MethodOverrideFilter implements Filter {

    private String[] methods;

    public MethodOverrideFilter() {
        this("DELETE", "PUT");
    }

    public MethodOverrideFilter(String... methods) {
        setMethods(methods);
    }

    public String[] getMethods() {
        return methods;
    }

    public void setMethods(String... methods) {
        this.methods = methods;
        Arrays.sort(methods);
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        return chain.next(new MethodOverrideRequestContext(request));
    }

    private class MethodOverrideRequestContext extends RequestContextWrapper {

        private final String method;

        public MethodOverrideRequestContext(RequestContext request) {
            super(request);
            String method = super.getMethod();
            String xheader = getHeader("X-HTTP-Method-Override");
            if (xheader == null)
                xheader = getHeader("X-Method-Override");
            if (xheader != null)
                xheader = xheader.toUpperCase().trim();
            if (method.equals("POST") && xheader != null && Arrays.binarySearch(methods, xheader) > -1) {
                method = xheader;
            }
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
