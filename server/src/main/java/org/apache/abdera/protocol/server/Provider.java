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

import java.util.Map;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;

/**
 * Providers are responsible for processing all requests to the Atompub server.<br>
 * Actual request processing is delegated to {@link RequestProcessor} implementations, depending on the request
 * {@link TargetType}.
 */
public interface Provider {

    /**
     * Initialize the Provider.
     */
    void init(Abdera abdera, Map<String, String> properties);

    /**
     * Retrieve the Abdera instance associated with this provider
     */
    Abdera getAbdera();

    /**
     * Get the specified property
     */
    String getProperty(String name);

    /**
     * Return a listing of all available properties
     */
    String[] getPropertyNames();

    /**
     * Resolve the subject using the Provider's Subject Resolver
     */
    Subject resolveSubject(RequestContext request);

    /**
     * Resolve the target using the Provider's Target Resolver
     */
    Target resolveTarget(RequestContext request);

    /**
     * Construct a URL using to Provider's Target Builder
     */
    String urlFor(RequestContext request, Object key, Object param);

    /**
     * Process the request
     */
    ResponseContext process(RequestContext request);

    /**
     * Return the listing of filters for this request
     */
    Filter[] getFilters(RequestContext request);

    /**
     * Set a map of {@link RequestProcessor}s to register on this provider, overriding already registered ones.
     */
    void setRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors);

    /**
     * Add a map of {@link RequestProcessor}s to register on this provider, without overriding already registered ones.<br>
     * Å
     */
    void addRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors);

    /**
     * Return a map of registered {@link RequestProcessor}s with related {@link TargetType}.
     */
    Map<TargetType, RequestProcessor> getRequestProcessors();
}
