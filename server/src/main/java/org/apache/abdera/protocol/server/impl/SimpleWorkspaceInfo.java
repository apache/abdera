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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;

public class SimpleWorkspaceInfo implements WorkspaceInfo {

  protected String title;
  protected Set<CollectionInfo> collections;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle(RequestContext request) {
    return title;
  }

  public void addCollection(CollectionInfo ci) {
    getCollections().add(ci);
  }

  public Collection<CollectionInfo> getCollections(RequestContext request) {
    return collections;
  }

  public Set<CollectionInfo> getCollections() {
    if (collections == null) {
      collections = new HashSet<CollectionInfo>();
    }

    return collections;
  }

  public void setCollections(Set<CollectionInfo> collections) {
    this.collections = collections;
  }
}
