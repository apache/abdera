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
package org.apache.abdera.protocol.client.cache;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

public interface Cache extends Iterable<Object> {

    public static enum Disposition {
        STALE, FRESH, TRANSPARENT
    }

    /**
     * Get the disposition of a specific item
     */
    Disposition disposition(Object key, RequestOptions options);

    /**
     * Get a specific item from the cache
     */
    CachedResponse get(Object key);

    /**
     * Clear all items from the cache
     */
    Cache clear();

    /**
     * Remove a specific item from the cache
     */
    Cache remove(Object key);

    /**
     * Update the cached item
     */
    ClientResponse update(Object key, RequestOptions options, ClientResponse response, ClientResponse cached_response);

}
