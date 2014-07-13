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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRIHelper;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMSource extends FOMExtensibleElement implements Source {

    private static final long serialVersionUID = 9153127297531238021L;

    protected FOMSource(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMSource(OMContainer parent, OMFactory factory) throws OMException {
        super(SOURCE, parent, factory);
    }

    protected FOMSource(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMSource(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    public Person getAuthor() {
        return (Person)getFirstChildWithName(AUTHOR);
    }

    public List<Person> getAuthors() {
        return _getChildrenAsSet(AUTHOR);
    }

    public <T extends Source> T addAuthor(Person person) {
        complete();
        addChild((OMElement)person);
        return (T)this;
    }

    public Person addAuthor(String name) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.getOMFactory();
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        return person;
    }

    public Person addAuthor(String name, String email, String uri) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.getOMFactory();
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public List<Category> getCategories() {
        return _getChildrenAsSet(CATEGORY);
    }

    public List<Category> getCategories(String scheme) {
        return FOMHelper.getCategories(this, scheme);
    }

    public <T extends Source> T addCategory(Category category) {
        complete();
        Element el = category.getParentElement();
        if (el != null && el instanceof Categories) {
            Categories cats = category.getParentElement();
            category = (Category)category.clone();
            try {
                if (category.getScheme() == null && cats.getScheme() != null)
                    category.setScheme(cats.getScheme().toString());
            } catch (Exception e) {
                // Do nothing, shouldn't happen
            }
        }
        addChild((OMElement)category);
        return (T)this;
    }

    public Category addCategory(String term) {
        complete();
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Category category = factory.newCategory(this);
        category.setTerm(term);
        return category;
    }

    public Category addCategory(String scheme, String term, String label) {
        complete();
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Category category = factory.newCategory(this);
        category.setTerm(term);
        category.setScheme(scheme);
        category.setLabel(label);
        return category;
    }

    public List<Person> getContributors() {
        return _getChildrenAsSet(CONTRIBUTOR);
    }

    public <T extends Source> T addContributor(Person person) {
        complete();
        addChild((OMElement)person);
        return (T)this;
    }

    public Person addContributor(String name) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.getOMFactory();
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        return person;
    }

    public Person addContributor(String name, String email, String uri) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.getOMFactory();
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public IRIElement getIdElement() {
        return (IRIElement)getFirstChildWithName(ID);
    }

    public <T extends Source> T setIdElement(IRIElement id) {
        complete();
        if (id != null)
            _setChild(ID, (OMElement)id);
        else
            _removeChildren(ID, false);
        return (T)this;
    }

    public IRI getId() {
        IRIElement id = getIdElement();
        return (id != null) ? id.getValue() : null;
    }

    public IRIElement setId(String value) {
        complete();
        return setId(value, false);
    }

    public IRIElement newId() {
        return setId(this.getFactory().newUuidUri(), false);
    }

    public IRIElement setId(String value, boolean normalize) {
        complete();
        if (value == null) {
            _removeChildren(ID, false);
            return null;
        }
        IRIElement id = getIdElement();
        if (id != null) {
            if (normalize)
                id.setNormalizedValue(value);
            else
                id.setValue(value);
            return id;
        } else {
            FOMFactory fomfactory = (FOMFactory)getOMFactory();
            IRIElement iri = fomfactory.newID(this);
            iri.setValue((normalize) ? IRI.normalizeString(value) : value);
            return iri;
        }
    }

    public List<Link> getLinks() {
        return _getChildrenAsSet(LINK);
    }

    public List<Link> getLinks(String rel) {
        return FOMHelper.getLinks(this, rel);
    }

    public List<Link> getLinks(String... rels) {
        return FOMHelper.getLinks(this, rels);
    }

    public <T extends Source> T addLink(Link link) {
        complete();
        addChild((OMElement)link);
        return (T)this;
    }

    public Link addLink(String href) {
        return addLink(href, null);
    }

    public Link addLink(String href, String rel) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        if (rel != null)
            link.setRel(rel);
        return link;
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        link.setRel(rel);
        link.setMimeType(type);
        link.setTitle(title);
        link.setHrefLang(hreflang);
        link.setLength(length);
        return link;
    }

    public Text getRightsElement() {
        return getTextElement(RIGHTS);
    }

    public <T extends Source> T setRightsElement(Text text) {
        complete();
        setTextElement(RIGHTS, text, false);
        return (T)this;
    }

    public Text setRights(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newRights();
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRightsAsHtml(String value) {
        return setRights(value, Text.Type.HTML);
    }

    public Text setRightsAsXhtml(String value) {
        return setRights(value, Text.Type.XHTML);
    }

    public Text setRights(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newRights(type);
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRights(Div value) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newRights(Text.Type.XHTML);
        text.setValueElement(value);
        setRightsElement(text);
        return text;
    }

    public String getRights() {
        return getText(RIGHTS);
    }

    public Text getSubtitleElement() {
        return getTextElement(SUBTITLE);
    }

    public <T extends Source> T setSubtitleElement(Text text) {
        complete();
        setTextElement(SUBTITLE, text, false);
        return (T)this;
    }

    public Text setSubtitle(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newSubtitle();
        text.setValue(value);
        setSubtitleElement(text);
        return text;
    }

    public Text setSubtitleAsHtml(String value) {
        return setSubtitle(value, Text.Type.HTML);
    }

    public Text setSubtitleAsXhtml(String value) {
        return setSubtitle(value, Text.Type.XHTML);
    }

    public Text setSubtitle(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newSubtitle(type);
        text.setValue(value);
        setSubtitleElement(text);
        return text;
    }

    public Text setSubtitle(Div value) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newSubtitle(Text.Type.XHTML);
        text.setValueElement(value);
        setSubtitleElement(text);
        return text;
    }

    public String getSubtitle() {
        return getText(SUBTITLE);
    }

    public Text getTitleElement() {
        return getTextElement(TITLE);
    }

    public <T extends Source> T setTitleElement(Text text) {
        complete();
        setTextElement(TITLE, text, false);
        return (T)this;
    }

    public Text setTitle(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newTitle();
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitleAsHtml(String value) {
        return setTitle(value, Text.Type.HTML);
    }

    public Text setTitleAsXhtml(String value) {
        return setTitle(value, Text.Type.XHTML);
    }

    public Text setTitle(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newTitle(type);
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitle(Div value) {
        FOMFactory factory = (FOMFactory)this.getOMFactory();
        Text text = factory.newTitle(Text.Type.XHTML);
        text.setValueElement(value);
        setTitleElement(text);
        return text;
    }

    public String getTitle() {
        return getText(TITLE);
    }

    public DateTime getUpdatedElement() {
        return (DateTime)getFirstChildWithName(UPDATED);
    }

    public <T extends Source> T setUpdatedElement(DateTime updated) {
        complete();
        if (updated != null)
            _setChild(UPDATED, (OMElement)updated);
        else
            _removeChildren(UPDATED, false);
        return (T)this;
    }

    public String getUpdatedString() {
        DateTime dte = getUpdatedElement();
        return (dte != null) ? dte.getString() : null;
    }

    public Date getUpdated() {
        DateTime dte = getUpdatedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime setUpdated(AtomDate value) {
        complete();
        if (value == null) {
            _removeChildren(UPDATED, false);
            return null;
        }
        DateTime dte = getUpdatedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            FOMFactory fomfactory = (FOMFactory)getOMFactory();
            DateTime dt = fomfactory.newUpdated(this);
            dt.setValue(value);
            return dt;
        }
    }

    public DateTime setUpdated(Date value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public DateTime setUpdated(String value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public Generator getGenerator() {
        return (Generator)getFirstChildWithName(GENERATOR);
    }

    public <T extends Source> T setGenerator(Generator generator) {
        complete();
        if (generator != null)
            _setChild(GENERATOR, (OMElement)generator);
        else
            _removeChildren(GENERATOR, false);
        return (T)this;
    }

    public Generator setGenerator(String uri, String version, String value) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Generator generator = fomfactory.newGenerator(this);
        if (uri != null)
            generator.setUri(uri);
        if (version != null)
            generator.setVersion(version);
        if (value != null)
            generator.setText(value);
        return generator;
    }

    public IRIElement getIconElement() {
        return (IRIElement)getFirstChildWithName(ICON);
    }

    public <T extends Source> T setIconElement(IRIElement iri) {
        complete();
        if (iri != null)
            _setChild(ICON, (OMElement)iri);
        else
            _removeChildren(ICON, false);
        return (T)this;
    }

    public IRIElement setIcon(String value) {
        complete();
        if (value == null) {
            _removeChildren(ICON, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        IRIElement iri = fomfactory.newIcon(this);
        iri.setValue(value);
        return iri;
    }

    public IRI getIcon() {
        IRIElement iri = getIconElement();
        IRI uri = (iri != null) ? iri.getResolvedValue() : null;
        return (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) ? null : uri;
    }

    public IRIElement getLogoElement() {
        return (IRIElement)getFirstChildWithName(LOGO);
    }

    public <T extends Source> T setLogoElement(IRIElement iri) {
        complete();
        if (iri != null)
            _setChild(LOGO, (OMElement)iri);
        else
            _removeChildren(LOGO, false);
        return (T)this;
    }

    public IRIElement setLogo(String value) {
        complete();
        if (value == null) {
            _removeChildren(LOGO, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        IRIElement iri = fomfactory.newLogo(this);
        iri.setValue(value);
        return iri;
    }

    public IRI getLogo() {
        IRIElement iri = getLogoElement();
        IRI uri = (iri != null) ? iri.getResolvedValue() : null;
        return (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) ? null : uri;
    }

    public Link getLink(String rel) {
        List<Link> self = getLinks(rel);
        Link link = null;
        if (self.size() > 0)
            link = self.get(0);
        return link;
    }

    public Link getSelfLink() {
        return getLink(Link.REL_SELF);
    }

    public Link getAlternateLink() {
        return getLink(Link.REL_ALTERNATE);
    }

    public IRI getLinkResolvedHref(String rel) {
        Link link = getLink(rel);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Text.Type getRightsType() {
        Text text = getRightsElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getSubtitleType() {
        Text text = getSubtitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getTitleType() {
        Text text = getTitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Collection getCollection() {
        Collection coll = getFirstChild(COLLECTION);
        if (coll == null)
            coll = getFirstChild(PRE_RFC_COLLECTION);
        return coll;
    }

    public <T extends Source> T setCollection(Collection collection) {
        complete();
        if (collection != null) {
            _removeChildren(PRE_RFC_COLLECTION, true);
            _setChild(COLLECTION, (OMElement)collection);
        } else {
            _removeChildren(COLLECTION, false);
        }
        return (T)this;
    }

    public Link getAlternateLink(String type, String hreflang) {
        return selectLink(getLinks(Link.REL_ALTERNATE), type, hreflang);
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = getAlternateLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Feed getAsFeed() {
        FOMFeed feed = (FOMFeed)((FOMFactory)getOMFactory()).newFeed();
        for (Iterator i = this.getChildElements(); i.hasNext();) {
            FOMElement child = (FOMElement)i.next();
            if (!child.getQName().equals(ENTRY)) {
                feed.addChild((OMNode)child.clone());
            }
        }
        try {
            if (this.getBaseUri() != null) {
                feed.setBaseUri(this.getBaseUri());
            }
        } catch (Exception e) {
        }
        return feed;
    }

}
