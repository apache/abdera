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

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("deprecation")
public class FOMService extends FOMExtensibleElement implements Service {

    private static final long serialVersionUID = 7982751563668891240L;

    public FOMService() {
        super(Constants.SERVICE, new FOMDocument<Service>(), new FOMFactory());
        declareAtomNs();
    }

    protected FOMService(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
        declareAtomNs();
    }

    protected FOMService(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        declareAtomNs();
    }

    protected FOMService(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMService(OMContainer parent, OMFactory factory) throws OMException {
        super(SERVICE, parent, factory);
        declareAtomNs();
    }

    private void declareAtomNs() {
        declareDefaultNamespace(APP_NS);
        declareNamespace(ATOM_NS, "atom");
    }

    public List<Workspace> getWorkspaces() {
        List<Workspace> list = _getChildrenAsSet(WORKSPACE);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_WORKSPACE);
        return list;
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

    public Service addWorkspace(Workspace workspace) {
        complete();
        addChild((OMElement)workspace);
        return this;
    }

    public Workspace addWorkspace(String title) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
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

    public Collection getCollectionThatAccepts(MimeType... types) {
        Collection collection = null;
        for (Workspace workspace : getWorkspaces()) {
            collection = workspace.getCollectionThatAccepts(types);
            if (collection != null)
                break;
        }
        return collection;
    }

    public Collection getCollectionThatAccepts(String... types) {
        Collection collection = null;
        for (Workspace workspace : getWorkspaces()) {
            collection = workspace.getCollectionThatAccepts(types);
            if (collection != null)
                break;
        }
        return collection;
    }

    public List<Collection> getCollectionsThatAccept(MimeType... types) {
        List<Collection> collections = new ArrayList<Collection>();
        for (Workspace workspace : getWorkspaces()) {
            List<Collection> colls = workspace.getCollectionsThatAccept(types);
            collections.addAll(colls);
        }
        return collections;
    }

    public List<Collection> getCollectionsThatAccept(String... types) {
        List<Collection> collections = new ArrayList<Collection>();
        for (Workspace workspace : getWorkspaces()) {
            List<Collection> colls = workspace.getCollectionsThatAccept(types);
            collections.addAll(colls);
        }
        return collections;
    }

}
