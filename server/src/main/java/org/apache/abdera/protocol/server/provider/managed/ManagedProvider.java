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

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;

/**
 * The ManagedProvider uses *.properties files discovered in the webapp classpath to configure CollectionAdapter
 * instances. The ManagedWorkspace implementation will automatically discover the *.properties files and will use those
 * to create the appropriate CollectionAdapter objects. Properties files must be located in the classpath at
 * /abdera/adapter/*.properties. Refer to the Abdera Server Implementation Guide for additional details
 */
public abstract class ManagedProvider extends AbstractWorkspaceProvider {

    protected abstract ServerConfiguration getServerConfiguration(RequestContext request);

    protected ManagedProvider() {
        addWorkspace(new ManagedWorkspace(this));
    }

    public CollectionAdapterManager getCollectionAdapterManager(RequestContext request) {
        return new CollectionAdapterManager(abdera, getServerConfiguration(request));
    }

}
