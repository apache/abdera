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

import java.util.Map;

import javax.security.auth.Subject;

/**
 * Providers are responsible for processing all requests to the Atompub server.<br>
 * Actual request processing is delegated to {@link AtompubRequestProcessor} implementations, depending on the request
 * {@link TargetType}.
 */
@SuppressWarnings("rawtypes")
public interface Provider
  extends TargetBuilder {

    /**
     * Initialize the Provider.
     */
    void init(Map<String, String> properties);

    /**
     * Get the specified property
     */
    String getProperty(String name);
    
    /**
     * Return a listing of all available properties
     */
    Iterable<String> getPropertyNames();

    /**
     * Resolve the subject using the Provider's Subject Resolver
     */
    Subject resolveSubject(RequestContext request);

    /**
     * Resolve the target using the Provider's Target Resolver
     */
    Target resolveTarget(RequestContext request);

    /**
     * Process the request
     */
    <S extends ResponseContext>S process(RequestContext request);

    /**
     * Return the listing of filters for this request
     */
    Iterable<Filter> getFilters(RequestContext request);

    <S extends ResponseContext>S createErrorResponse(int code, String message, Throwable t);
    
    /**
     * Set a map of {@link AtompubRequestProcessor}s to register on this provider, overriding already registered ones.
     */
    void setRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors);

    /**
     * Add a map of {@link AtompubRequestProcessor}s to register on this provider, without overriding already registered ones.
     */
    void addRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors);

    /**
     * Return a map of registered {@link AtompubRequestProcessor}s with related {@link TargetType}.
     */
    Map<TargetType, RequestProcessor> getRequestProcessors();
}
