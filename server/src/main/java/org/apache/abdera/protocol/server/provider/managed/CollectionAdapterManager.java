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
package org.apache.abdera.protocol.server.provider.managed;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.CollectionAdapter;

public class CollectionAdapterManager {

    public static Logger logger = Logger.getLogger(CollectionAdapterManager.class.getName());

    // maps a feed id to an adapter instance
    protected static Map<String, CollectionAdapter> adapterInstanceMap = new HashMap<String, CollectionAdapter>();

    protected final Abdera abdera;
    protected final ServerConfiguration config;

    public CollectionAdapterManager(Abdera abdera, ServerConfiguration config) {
        this.abdera = abdera;
        this.config = config;
    }

    public CollectionAdapter getAdapter(String feedId) throws Exception {
        FeedConfiguration feedConfiguration = config.loadFeedConfiguration(feedId);
        return createAdapterInstance(feedConfiguration, abdera);
    }

    public Map<String, FeedConfiguration> listAdapters() throws Exception {
        Map<String, FeedConfiguration> results = new HashMap<String, FeedConfiguration>();
        Enumeration<URL> e =
            Thread.currentThread().getContextClassLoader().getResources(config.getFeedConfigLocation());
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            File file = new File(url.toURI());
            if (!file.exists()) {
                throw new RuntimeException("Could not convert properties path to a File! \"" + file.getAbsolutePath()
                    + "\" does not exist.");
            }
            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });
            if (files != null) {
                for (File _file : files) {
                    String name = _file.getName();
                    int i = name.indexOf(config.getFeedConfigSuffix());
                    String id = i > -1 ? name.substring(0, i) : null;
                    if (id != null) {
                        FeedConfiguration feedConfiguration = loadFeedInfo(id);
                        if (null != feedConfiguration)
                            results.put(id, feedConfiguration);
                    }
                }
            }
        }
        return results;
    }

    protected FeedConfiguration loadFeedInfo(String feedId) throws Exception {
        return config.loadFeedConfiguration(feedId);
    }

    protected static synchronized CollectionAdapter createAdapterInstance(FeedConfiguration config, Abdera abdera)
        throws Exception {
        CollectionAdapter basicAdapter = adapterInstanceMap.get(config.getFeedId());
        if (basicAdapter != null) {
            return basicAdapter;
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?> adapterClass = cl.loadClass(config.getAdapterClassName());
        Constructor<?>[] ctors = adapterClass.getConstructors();
        for (Constructor<?> element : ctors) {
            logger.finest("Public constructor found: " + element.toString());
        }
        Constructor<?> c = adapterClass.getConstructor(new Class[] {Abdera.class, FeedConfiguration.class});
        c.setAccessible(true);
        CollectionAdapter adapterInstance = (CollectionAdapter)c.newInstance(abdera, config);

        // put this adapter instance in adapterInstanceMap
        adapterInstanceMap.put(config.getFeedId(), adapterInstance);
        return adapterInstance;
    }

}
