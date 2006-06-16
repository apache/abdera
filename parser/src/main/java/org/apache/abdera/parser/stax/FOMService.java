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
package org.apache.abdera.parser.stax;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMService
  extends FOMExtensibleElement 
  implements Service {
 
  private static final long serialVersionUID = 7982751563668891240L;

  public FOMService(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMService(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMService(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMService(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(SERVICE, parent, factory);
  }

  public FOMService(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(SERVICE, parent, factory, builder);
  }
  
  public List<Workspace> getWorkspaces() {
    return _getChildrenAsSet(WORKSPACE);
  }

  public Workspace getWorkspace(String title) {
    List<Workspace> workspaces = getWorkspaces();
    Workspace workspace = null;
    for (Workspace w : workspaces) {
      if (w.getTitle().equals(title)) {
        workspace = w;
        break;
      }
    }
    return workspace;
  }

  public void setWorkspaces(List<Workspace> workspaces) {
    _setChildrenFromSet(WORKSPACE, workspaces);
  }

  public void addWorkspace(Workspace workspace) {
    addChild((OMElement) workspace);
  }
  
  public Workspace addWorkspace(String title) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Workspace workspace = fomfactory.newWorkspace(this);
    workspace.setTitle(title);
    return workspace;
  }

  public Collection getCollection(String workspace, String collection) {
    Collection col = null;
    Workspace w = getWorkspace(workspace);
    if (w != null) {
      col = w.getCollection(collection);
    }
    return col;
  }

}
