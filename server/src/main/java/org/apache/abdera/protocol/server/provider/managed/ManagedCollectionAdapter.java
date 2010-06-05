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

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;

public abstract class ManagedCollectionAdapter extends AbstractCollectionAdapter implements CollectionAdapter {

    protected final FeedConfiguration config;
    protected final Abdera abdera;

    protected ManagedCollectionAdapter(Abdera abdera, FeedConfiguration config) {
        this.config = config;
        this.abdera = abdera;
    }

    public Abdera getAbdera() {
        return this.abdera;
    }

    public FeedConfiguration getConfiguration() {
        return this.config;
    }

    public String getAuthor(RequestContext request) throws ResponseContextException {
        return config.getFeedAuthor();
    }

    public String getId(RequestContext request) {
        return config.getFeedId();
    }

    public String getTitle(RequestContext request) {
        return config.getFeedTitle();
    }
}
