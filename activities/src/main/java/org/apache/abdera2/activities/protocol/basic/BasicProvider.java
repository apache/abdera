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
package org.apache.abdera2.activities.protocol.basic;

import org.apache.abdera2.activities.protocol.managed.BasicServerConfiguration;
import org.apache.abdera2.activities.protocol.managed.ManagedProvider;
import org.apache.abdera2.activities.protocol.managed.ServerConfiguration;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.CollectionAdapter;
import org.apache.abdera2.common.protocol.RouteManager;
import org.apache.abdera2.common.protocol.TargetType;

/**
 * Provider implementation intended to be used with BasicAdapter implementations
 */
public class BasicProvider extends ManagedProvider {

    public static final String PARAM_FEED = "stream";
    public static final String PARAM_ENTRY = "activity";

    public BasicProvider() {
        super();
        init();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void init() {
        RouteManager routeManager =
            new RouteManager()
              .addRoute(
                "stream",
                "/:stream",
                TargetType.TYPE_COLLECTION)
              .addRoute(
                "activity", 
                "/:stream/:activity", 
                TargetType.TYPE_ENTRY);
        setTargetBuilder(
            routeManager);
        setTargetResolver(
            routeManager);
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        try {
            return getCollectionAdapterManager(request)
              .getAdapter(request.getTarget().getParameter(PARAM_FEED));
        } catch (Exception e) {
            return null;
        }
    }

    protected ServerConfiguration getServerConfiguration(RequestContext request) {
        return new BasicServerConfiguration(request);
    }

}
