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
package org.apache.abdera.protocol.server.test.simple;

import java.io.IOException;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class SimpleProvider extends DefaultProvider {

  public SimpleProvider() {
    super("/atom");
    
    SimpleWorkspaceInfo wkspc = new SimpleWorkspaceInfo();
    wkspc.setTitle("A Simple Workspace");
    wkspc.addCollection(new SimpleAdapter());

    addWorkspace(wkspc);
  }

}
