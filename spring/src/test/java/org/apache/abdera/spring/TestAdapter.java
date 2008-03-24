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
package org.apache.abdera.spring;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;

public class TestAdapter extends AbstractCollectionAdapter {

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public String getId(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getHref(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTitle(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext deleteEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext getEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext getFeed(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext postEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext putEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

}
