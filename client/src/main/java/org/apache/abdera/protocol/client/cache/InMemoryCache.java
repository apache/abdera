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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.ClientResponse;

public abstract class InMemoryCache extends AbstractCache {

    protected transient final Map<Object, CachedResponse> cache;

    protected InMemoryCache(Abdera abdera, Map<Object, CachedResponse> map) {
        super(abdera);
        this.cache = map;
    }

    @Override
    protected CachedResponse createCachedResponse(ClientResponse response, Object key) throws IOException {
        return new InMemoryCachedResponse(abdera, this, key, response);
    }

    public Cache clear() {
        cache.clear();
        return this;
    }

    public CachedResponse get(Object key) {
        return cache.get(key);
    }

    public Cache remove(Object key) {
        cache.remove(key);
        return this;
    }

    protected void add(Object key, CachedResponse response) {
        cache.put(key, response);
    }

    public Iterator<Object> iterator() {
        return cache.keySet().iterator();
    }
}
