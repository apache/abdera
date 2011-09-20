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
package org.apache.abdera2.activities.protocol.managed;

import org.apache.abdera2.activities.protocol.AbstractActivitiesCollectionAdapter;
import org.apache.abdera2.common.protocol.CollectionAdapter;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContextException;

public abstract class ManagedCollectionAdapter 
  extends AbstractActivitiesCollectionAdapter 
  implements CollectionAdapter {

    protected final FeedConfiguration config;

    protected ManagedCollectionAdapter(FeedConfiguration config) {
        this.config = config;
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
