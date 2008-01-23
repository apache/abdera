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
package org.apache.abdera.protocol.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;

public class SimpleCollection 
  implements CollectionInfo, 
             Serializable {
  
  private static final long serialVersionUID = 8026455829158149510L;
  
  private final CollectionAdapter adapter;
  private final String id;
  private final String title;
  private final String href;
  private final String[] accepts;
  private final List<CategoriesInfo> catinfos = new ArrayList<CategoriesInfo>();
  
  public SimpleCollection(
    CollectionAdapter adapter,
    String id,
    String title,
    String href,
    String... accepts) {
      this.adapter = adapter;
      this.id = id;
      this.title = title;
      this.accepts = accepts;
      this.href = href;
  }
  
  public boolean isCollectionFor(RequestContext request) {
    // TODO Auto-generated method stub
    return false;
  }

  public String[] getAccepts(RequestContext request) {
    return accepts;
  }
  
  public String getHref(RequestContext request) {
    return href;
  }
  
  public String getTitle(RequestContext request) {
    return title;
  }
  
  public boolean isAdapterFor(RequestContext request) {
    return request.getTarget().getParameter("collection").equals(this.id);
  }

  public CollectionAdapter getCollectionAdapter(RequestContext request) {
    return adapter;
  }

  public CategoriesInfo[] getCategoriesInfo(RequestContext request) {
    return catinfos.toArray(new CategoriesInfo[catinfos.size()]);
  }
  
  public void addCategoriesInfo(CategoriesInfo... catinfos) {
    for (CategoriesInfo catinfo : catinfos)
      this.catinfos.add(catinfo);
  }
  
  public void setCategoriesInfo(CategoriesInfo...catinfos) {
    this.catinfos.clear();
    addCategoriesInfo(catinfos);
  }
}
