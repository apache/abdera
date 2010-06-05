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
package org.apache.abdera.test.security.filter;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.security.util.filters.SignedRequestFilter;
import org.apache.abdera.security.util.filters.SignedResponseFilter;

public class CustomProvider extends AbstractWorkspaceProvider {

    private final SimpleAdapter adapter;

    private static final String keystoreFile = "/key.jks";
    private static final String keystorePass = "testing";
    private static final String privateKeyAlias = "James";
    private static final String privateKeyPass = "testing";
    private static final String certificateAlias = "James";

    public CustomProvider() {

        this.adapter = new SimpleAdapter();

        RouteManager rm =
            new RouteManager().addRoute("service", "/", TargetType.TYPE_SERVICE).addRoute("collection",
                                                                                          "/:collection",
                                                                                          TargetType.TYPE_COLLECTION)
                .addRoute("entry", "/:collection/:entry", TargetType.TYPE_ENTRY);

        setTargetBuilder(rm);
        setTargetResolver(rm);

        SimpleWorkspaceInfo workspace = new SimpleWorkspaceInfo();
        workspace.setTitle("A Simple Workspace");
        workspace.addCollection(adapter);
        addWorkspace(workspace);

        addFilter(new SignedRequestFilter(), new SignedResponseFilter(keystoreFile, keystorePass, privateKeyAlias,
                                                                      privateKeyPass, certificateAlias, null));
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        return adapter;
    }

}
