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

import java.util.Arrays;
import java.util.Iterator;

public final class FilterChain {

    private final Iterator<Filter> filters;
    private final Provider provider;

    public FilterChain(Provider provider, RequestContext request) {
        this.provider = provider;
        this.filters = Arrays.asList(provider.getFilters(request)).iterator();
    }

    /**
     * Invoke the next filter in the chain. If there are no more filters in the chain, pass the request context on to
     * the Provider for processing.
     */
    public ResponseContext next(RequestContext request) {
        return filters.hasNext() ? filters.next().filter(request, this) : provider.process(request);
    }

}
