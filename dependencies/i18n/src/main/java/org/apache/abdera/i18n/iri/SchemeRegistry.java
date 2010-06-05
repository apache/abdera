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
package org.apache.abdera.i18n.iri;

import java.util.HashMap;
import java.util.Map;

/**
 * Static registry of custom IRI schemes.
 */
public final class SchemeRegistry {

    private static SchemeRegistry registry;

    public static synchronized SchemeRegistry getInstance() {
        if (registry == null)
            registry = new SchemeRegistry();
        return registry;
    }

    private final Map<String, Scheme> schemes;

    SchemeRegistry() {
        schemes = new HashMap<String, Scheme>();
        schemes.put(HttpScheme.NAME, new HttpScheme());
        schemes.put(HttpsScheme.NAME, new HttpsScheme());
        schemes.put(FtpScheme.NAME, new FtpScheme());
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean register(String schemeClass) throws ClassNotFoundException, IllegalAccessException,
        InstantiationException {
        Class<Scheme> klass = (Class<Scheme>)Thread.currentThread().getContextClassLoader().loadClass(schemeClass);
        return register(klass);
    }

    public synchronized boolean register(Class<Scheme> schemeClass) throws IllegalAccessException,
        InstantiationException {
        Scheme scheme = schemeClass.newInstance();
        return register(scheme);
    }

    public synchronized boolean register(Scheme scheme) {
        String name = scheme.getName();
        if (schemes.get(name) == null) {
            schemes.put(name.toLowerCase(), scheme);
            return true;
        } else
            return false;
    }

    public Scheme getScheme(String scheme) {
        if (scheme == null)
            return null;
        Scheme s = schemes.get(scheme.toLowerCase());
        return (s != null) ? s : new DefaultScheme(scheme);
    }

}
