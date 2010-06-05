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

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbstractClientResponse;

public abstract class CachedResponseBase extends AbstractClientResponse implements CachedResponse {

    protected final Object key;
    protected final Cache cache;
    protected long initial_age;

    protected CachedResponseBase(Abdera abdera, Object key, Cache cache) {
        super(abdera);
        this.key = key;
        this.cache = cache;
    }

    private long calcInitialAge() {
        long age_value = getAge();
        long now = (new Date()).getTime();
        long cachedTime = getCachedTime();
        long date_value = (cachedTime != -1) ? cachedTime : 0;
        long apparent_age = Math.max(0, now - date_value);
        long corrected_received_age = Math.max(apparent_age, age_value);
        return corrected_received_age / 1000;
    }

    public Object getKey() {
        return key;
    }

    public Cache getCache() {
        return cache;
    }

    public void release() {
        if (cache != null) {
            cache.remove(key);
        }
    }

    public long getInitialAge() {
        if (initial_age == -1)
            initial_age = calcInitialAge();
        return initial_age;
    }

    public long getCachedTime() {
        return getServerDate().getTime();
    }

    public long getResidentAge() {
        long now = (new Date()).getTime();
        long init = getCachedTime();
        return Math.max(0, (now - init)) / 1000;
    }

    public long getCurrentAge() {
        return getInitialAge() + getResidentAge();
    }

    public long getFreshnessLifetime() {
        long lifetime = getMaxAge();
        if (lifetime == -1) {
            Date expires_date = getExpires();
            long expires = (expires_date != null) ? expires_date.getTime() : -1;
            long cachedTime = getCachedTime();
            if (expires != -1) {
                lifetime = (expires > cachedTime) ? (expires - cachedTime) / 1000 : 0;
            } // else, expires is not set, return -1 for now. TODO: apply heuristics
        }
        return lifetime;
    }

    public long getHowStale() {
        return (!isFresh()) ? getCurrentAge() - getFreshnessLifetime() : 0;
    }

    public boolean isFresh() {
        long lifetime = getFreshnessLifetime();
        long currentage = getCurrentAge();
        return (lifetime != -1) ? lifetime > currentage : true;
    }

}
