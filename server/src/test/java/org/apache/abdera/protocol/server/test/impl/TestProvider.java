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
package org.apache.abdera.protocol.server.test.impl;

import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleCollection;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.test.simple.SimpleAdapter;

public class TestProvider 
  extends DefaultProvider {

  public TestProvider() {
    setTargetResolver(
      new RegexTargetResolver()
        .setPattern("/atom(\\?[^#]*)?", TargetType.TYPE_SERVICE)
        .setPattern("/atom/([^/#?]+);categories", TargetType.TYPE_CATEGORIES, "collection")
        .setPattern("/atom/([^/#?;]+)(\\?[^#]*)?", TargetType.TYPE_COLLECTION, "collection")
        .setPattern("/atom/([^/#?]+)/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY, "collection", "entry")
    );        
    SimpleWorkspaceInfo workspace = new SimpleWorkspaceInfo();
    workspace.setTitle("My Blog");
    workspace.addCollection(
      new SimpleCollection(
        new SimpleAdapter(),
        "feed1", 
        "entries1", 
        "/atom/feed1", 
        "application/atom+xml;type=entry"
      )
    );
    workspace.addCollection(
      new SimpleCollection(
        new SimpleAdapter(),
        "feed2", 
        "entries2", 
        "/atom/feed2", 
        "application/atom+xml;type=entry"
      )
    );        
    addWorkspace(workspace);
  }
  
}
