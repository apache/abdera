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

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.util.Discover;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ServiceManager is used by the AbderaServlet to bootstrap the server instance. There should be little to no reason
 * why an end user would need to use this class directly.
 */
public class ServiceManager {

    public static final String PROVIDER = "org.apache.abdera.protocol.server.Provider";

    private final static Log log = LogFactory.getLog(ServiceManager.class);

    private static ServiceManager INSTANCE = null;
    private static Abdera abdera = null;

    ServiceManager() {
    }

    public static synchronized ServiceManager getInstance() {
        if (INSTANCE == null) {
            log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "ServiceManager"));
            INSTANCE = new ServiceManager();
        }
        return INSTANCE;
    }

    public static synchronized Abdera getAbdera() {
        if (abdera == null) {
            log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "Abdera"));
            abdera = new Abdera();
        }
        return abdera;
    }

    public Provider newProvider(Map<String, String> properties) {
        Abdera abdera = getAbdera();
        String instance = properties.get(PROVIDER);
        if (instance == null) {
            instance = DefaultProvider.class.getName();
        }
        log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "Provider"));
        Provider provider = (Provider)Discover.locate(PROVIDER, instance);
        log.debug(Localizer.sprintf("INITIALIZING.INSTANCE", "Provider"));
        provider.init(abdera, properties);
        return provider;
    }
}
