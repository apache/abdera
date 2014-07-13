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
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("deprecation")
public class FOMWorkspace extends FOMExtensibleElement implements Workspace {

    private static final long serialVersionUID = -421749865550509424L;

    protected FOMWorkspace(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMWorkspace(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMWorkspace(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    protected FOMWorkspace(OMContainer parent, OMFactory factory) throws OMException {
        super(WORKSPACE, parent, factory);
    }

    public String getTitle() {
        Text title = this.getFirstChild(TITLE);
        return (title != null) ? title.getValue() : null;
    }

    private Text setTitle(String title, Text.Type type) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Text text = fomfactory.newText(PREFIXED_TITLE, type);
        text.setValue(title);
        this._setChild(PREFIXED_TITLE, (OMElement)text);
        return text;
    }

    public Text setTitle(String title) {
        return setTitle(title, Text.Type.TEXT);
    }

    public Text setTitleAsHtml(String title) {
        return setTitle(title, Text.Type.HTML);
    }

    public Text setTitleAsXHtml(String title) {
        return setTitle(title, Text.Type.XHTML);
    }

    public Text getTitleElement() {
        return getFirstChild(TITLE);
    }

    public List<Collection> getCollections() {
        List<Collection> list = _getChildrenAsSet(COLLECTION);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_COLLECTION);
        return list;
    }

    public Collection getCollection(String title) {
        List<Collection> cols = getCollections();
        Collection col = null;
        for (Collection c : cols) {
            if (c.getTitle().equals(title)) {
                col = c;
                break;
            }
        }
        return col;
    }

    public Workspace addCollection(Collection collection) {
        complete();
        addChild((OMElement)collection);
        return this;
    }

    public Collection addCollection(String title, String href) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Collection collection = fomfactory.newCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    public Collection addMultipartCollection(String title, String href) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Collection collection = fomfactory.newMultipartCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    public Collection getCollectionThatAccepts(MimeType... types) {
        Collection collection = null;
        for (Collection coll : getCollections()) {
            int matches = 0;
            for (MimeType type : types)
                if (coll.accepts(type))
                    matches++;
            if (matches == types.length) {
                collection = coll;
                break;
            }
        }
        return collection;
    }

    public Collection getCollectionThatAccepts(String... types) {
        Collection collection = null;
        for (Collection coll : getCollections()) {
            int matches = 0;
            for (String type : types)
                if (coll.accepts(type))
                    matches++;
            if (matches == types.length) {
                collection = coll;
                break;
            }
        }
        return collection;
    }

    public List<Collection> getCollectionsThatAccept(MimeType... types) {
        List<Collection> collections = new ArrayList<Collection>();
        for (Collection coll : getCollections()) {
            int matches = 0;
            for (MimeType type : types)
                if (coll.accepts(type))
                    matches++;
            if (matches == types.length) {
                collections.add(coll);
            }
        }
        return collections;
    }

    public List<Collection> getCollectionsThatAccept(String... types) {
        List<Collection> collections = new ArrayList<Collection>();
        for (Collection coll : getCollections()) {
            int matches = 0;
            for (String type : types)
                if (coll.accepts(type))
                    matches++;
            if (matches == types.length) {
                collections.add(coll);
            }
        }
        return collections;
    }

}
