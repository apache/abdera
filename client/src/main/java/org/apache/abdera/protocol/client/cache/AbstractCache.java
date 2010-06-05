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

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.util.CacheControlUtil;

public abstract class AbstractCache implements Cache {

    protected final Abdera abdera;

    protected AbstractCache(Abdera abdera) {
        this.abdera = abdera;
    }

    public Disposition disposition(Object key, RequestOptions options) {
        CachedResponse response = get(key);
        if (response != null && options != null) {
            if (options.isNoCache())
                return Disposition.TRANSPARENT;
            else if (response.isNoCache())
                return Disposition.STALE;
            else if (options != null && options.isOnlyIfCached())
                return Disposition.FRESH;
            else if (response.isMustRevalidate())
                return Disposition.STALE;
            else if (response.getCachedTime() != -1) {
                if (response.isFresh()) {
                    long maxAge = options.getMaxAge();
                    long currentAge = response.getCurrentAge();
                    long minFresh = options.getMinFresh();
                    if (maxAge != -1)
                        return (maxAge > currentAge) ? Disposition.FRESH : Disposition.STALE;
                    if (minFresh != -1)
                        return response.getFreshnessLifetime() < currentAge + minFresh ? Disposition.TRANSPARENT
                            : Disposition.FRESH;
                    return Disposition.FRESH;
                } else {
                    long maxStale = options.getMaxStale();
                    if (maxStale != -1)
                        return maxStale < response.getHowStale() ? Disposition.STALE : Disposition.FRESH;
                    return Disposition.STALE;
                }
            }
        }
        return Disposition.TRANSPARENT;
    }

    protected abstract void add(Object key, CachedResponse response);

    protected abstract CachedResponse createCachedResponse(ClientResponse response, Object key) throws IOException;

    private boolean shouldUpdateCache(ClientResponse response, boolean allowedByDefault) {
        if (allowedByDefault) {
            return !response.isNoCache() && !response.isNoStore() && response.getMaxAge() != 0;
        } else {
            return response.getExpires() != null || response.getMaxAge() > 0
                || response.isMustRevalidate()
                || response.isPublic()
                || response.isPrivate();
        }
    }

    public ClientResponse update(Object key,
                                 RequestOptions options,
                                 ClientResponse response,
                                 ClientResponse cached_response) {
        int status = response.getStatus();
        String uri = response.getUri();
        String method = response.getMethod();
        // if the method changes state on the server, don't cache and
        // clear what we already have
        if (!CacheControlUtil.isIdempotent(method)) {
            remove(uri);
            return response;
        }
        // otherwise, base the decision on the response status code
        switch (status) {
            case 200:
            case 203:
            case 300:
            case 301:
            case 410:
                // rfc2616 says these are cacheable unless otherwise noted
                if (shouldUpdateCache(response, true))
                    return update(key, options, response);
                else
                    remove(uri);
                break;
            case 304:
            case 412:
                // if not revalidated, fall through
                if (cached_response != null)
                    return cached_response;
            default:
                // rfc2616 says are *not* cacheable unless otherwise noted
                if (shouldUpdateCache(response, false))
                    return update(key, options, response);
                else
                    remove(uri);
                break;
        }
        return response;
    }

    private ClientResponse update(Object key, RequestOptions options, ClientResponse response) {
        try {
            CachedResponse cachedResponse = createCachedResponse(response, key);
            add(key, cachedResponse);
            return cachedResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
