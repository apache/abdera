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
package org.apache.abdera2.activities.protocol;

import java.util.Map;

import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.TypeAdapter;
import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.protocol.AbstractServiceManager;
import org.apache.abdera2.common.protocol.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ServiceManager is used by the AbderaServlet to bootstrap the server 
 * instance. There should be little to no reason why an end user would need 
 * to use this class directly.
 */
public class ActivitiesServiceManager
  extends AbstractServiceManager<Provider> {

    private final static Log log = LogFactory.getLog(ActivitiesServiceManager.class);

    public ActivitiesServiceManager() {}

    public static IO getIO(TypeAdapter<?>... adapters) {
      return IO.get(adapters);
    }
    
    public Provider newProvider(Map<String, String> properties) {
        String instance = properties.get(PROVIDER);
        if (instance == null)
            instance = DefaultActivitiesProvider.class.getName();
        log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "Provider"));
        Provider provider = Discover.locate(Provider.class, instance);
        log.debug(Localizer.sprintf("INITIALIZING.INSTANCE", "Provider"));
        provider.init(properties);
        return provider;
    }
    
}
