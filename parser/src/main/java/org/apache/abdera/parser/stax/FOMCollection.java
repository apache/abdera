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
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("deprecation")
public class FOMCollection extends FOMExtensibleElement implements Collection {

    private static final String[] ENTRY = {"application/atom+xml;type=\"entry\""};
    private static final String[] EMPTY = new String[0];

    private static final long serialVersionUID = -5291734055253987136L;

    protected FOMCollection(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMCollection(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
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

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Collection setHref(String href) {
        complete();
        if (href != null)
            setAttributeValue(HREF, (new IRI(href).toString()));
        else
            removeAttribute(HREF);
        return this;
    }

    public String[] getAccept() {
        List<String> accept = new ArrayList<String>();
        Iterator<?> i = getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext())
            i = getChildrenWithName(PRE_RFC_ACCEPT);
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String t = e.getText();
            if (t != null) {
                accept.add(t.trim());
            }
        }
        if (accept.size() > 0) {
            String[] list = accept.toArray(new String[accept.size()]);
            return MimeTypeHelper.condense(list);
        } else {
            return EMPTY;
        }
    }

    public Collection setAccept(String mediaRange) {
        return setAccept(new String[] {mediaRange});
    }

    public Collection setAccept(String... mediaRanges) {
        complete();
        if (mediaRanges != null && mediaRanges.length > 0) {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.length == 1 && mediaRanges[0].equals("")) {
                addExtension(ACCEPT);
            } else {
                mediaRanges = MimeTypeHelper.condense(mediaRanges);
                for (String type : mediaRanges) {
                    if (type.equalsIgnoreCase("entry")) {
                        addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                    } else {
                        try {
                            addSimpleExtension(ACCEPT, new MimeType(type).toString());
                        } catch (javax.activation.MimeTypeParseException e) {
                            throw new org.apache.abdera.util.MimeTypeParseException(e);
                        }
                    }
                }
            }
        } else {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public Collection addAccepts(String mediaRange) {
        return addAccepts(new String[] {mediaRange});
    }

    public Collection addAccepts(String... mediaRanges) {
        complete();
        if (mediaRanges != null) {
            for (String type : mediaRanges) {
                if (!accepts(type)) {
                    try {
                        addSimpleExtension(ACCEPT, new MimeType(type).toString());
                    } catch (Exception e) {
                    }
                }
            }
        }
        return this;
    }

    public Collection addAcceptsEntry() {
        return addAccepts("application/atom+xml;type=entry");
    }

    public Collection setAcceptsEntry() {
        return setAccept("application/atom+xml;type=entry");
    }

    public Collection setAcceptsNothing() {
        return setAccept("");
    }

    public boolean acceptsEntry() {
        return accepts("application/atom+xml;type=entry");
    }

    public boolean acceptsNothing() {
        return accepts("");
    }

    public boolean accepts(String mediaType) {
        String[] accept = getAccept();
        if (accept.length == 0)
            accept = ENTRY;
        for (String a : accept) {
            if (MimeTypeHelper.isMatch(a, mediaType))
                return true;
        }
        return false;
    }

    public boolean accepts(MimeType mediaType) {
        return accepts(mediaType.toString());
    }

    public Categories addCategories() {
        complete();
        return ((FOMFactory)getOMFactory()).newCategories(this);
    }

    public Collection addCategories(Categories categories) {
        complete();
        addChild((OMElement)categories);
        return this;
    }

    public Categories addCategories(String href) {
        complete();
        Categories cats = ((FOMFactory)getOMFactory()).newCategories();
        cats.setHref(href);
        addCategories(cats);
        return cats;
    }

    public Categories addCategories(List<Category> categories, boolean fixed, String scheme) {
        complete();
        Categories cats = ((FOMFactory)getOMFactory()).newCategories();
        cats.setFixed(fixed);
        if (scheme != null)
            cats.setScheme(scheme);
        if (categories != null) {
            for (Category category : categories) {
                cats.addCategory(category);
            }
        }
        addCategories(cats);
        return cats;
    }

    public List<Categories> getCategories() {
        List<Categories> list = _getChildrenAsSet(CATEGORIES);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_CATEGORIES);
        return list;
    }

}
