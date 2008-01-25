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
package org.apache.abdera.protocol.server.provider.basic;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.protocol.server.impl.CollectionAdapterManager;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;

public class BasicProvider 
  extends AbstractWorkspaceProvider {

  public static final String PARAM_FEED = "feed";
  public static final String PARAM_ENTRY = "entry";
  
  public BasicProvider() {
    setTargetResolver(
      new RegexTargetResolver()
        .setPattern("/", TargetType.TYPE_SERVICE)
        .setPattern("/" + "([^/#?]+)", TargetType.TYPE_COLLECTION, PARAM_FEED)
        .setPattern("/" + "([^/#?]+)/([^/#?]+)", TargetType.TYPE_ENTRY, PARAM_FEED, PARAM_ENTRY)
    );
    setTargetBuilder(
      new TemplateTargetBuilder()
        .setTemplate(TargetType.TYPE_SERVICE, "{target_base}/")
        .setTemplate(TargetType.TYPE_COLLECTION, "{target_base}/{" + PARAM_FEED + "}")
        .setTemplate(TargetType.TYPE_ENTRY, "{target_base}/{" + PARAM_FEED + "}/{" + PARAM_ENTRY + "}")
    );
    addWorkspace(new BasicWorkspace(this));
  }

  protected CollectionAdapterManager cam;
  
  protected CollectionAdapterManager getCollectionAdapterManager(
    Abdera abdera) {
      if (cam == null) 
        cam = new CollectionAdapterManager(abdera);
    return cam;
  }
  
  public CollectionAdapter getCollectionAdapter(RequestContext request) {
    try {
      return getCollectionAdapterManager(request.getAbdera())
        .getAdapter(
          request.getTarget()
            .getParameter(
                BasicProvider.PARAM_FEED));
    } catch (Exception e) {
      return null;
    }
  }
  
}
