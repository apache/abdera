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
package org.apache.abdera.ext.opensearch.server;

import java.util.Map;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

/**
 * The OpenSearchUrlAdapter interface provides the business logic for executing search operations and getting back
 * Atom-based responses augmented with Open Search metadata.
 */
public interface OpenSearchUrlAdapter {

    /**
     * Make the actual search operation based on passed parameters.
     * 
     * @param request The {@link org.apache.abdera.protocol.server.RequestContext} object.
     * @param parameters Search parameters extracted from the request: they are the same parameters reported into the
     *            Open Search URL template.
     */
    ResponseContext search(RequestContext request, Map<String, String> parameters);
}
