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
package org.apache.abdera.ext.rss;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;

public class RssFeed extends ExtensibleElementWrapper implements Feed {

    public RssFeed(Element internal) {
        super(internal);
    }

    public RssFeed(Factory factory, QName qname) {
        super(factory, qname);
    }

    @SuppressWarnings("unused")
    private RssChannel getChannel() {
        RssChannel c = super.getExtension(RssConstants.QNAME_CHANNEL);
        if (c == null)
            c = super.getExtension(RssConstants.QNAME_RDF_CHANNEL);
        return c;
    }

    public Feed addEntry(Entry entry) {
        getChannel().addEntry(entry);
        return this;
    }

    public Entry addEntry() {
        return getChannel().addEntry();
    }

    public Source getAsSource() {
        throw new UnsupportedOperationException("Converting to source is not supported");
    }

    public List<Entry> getEntries() {
        return getChannel().getEntries();
    }

    public Entry getEntry(String id) {
        return getChannel().getEntry(id);
    }

    public Feed insertEntry(Entry entry) {
        getChannel().insertEntry(entry);
        return this;
    }

    public Entry insertEntry() {
        return getChannel().insertEntry();
    }

    public Feed sortEntries(Comparator<Entry> comparator) {
        getChannel().sortEntries(comparator);
        return this;
    }

    public Feed sortEntriesByEdited(boolean new_first) {
        getChannel().sortEntriesByEdited(new_first);
        return this;
    }

    public Feed sortEntriesByUpdated(boolean new_first) {
        getChannel().sortEntriesByUpdated(new_first);
        return this;
    }

    public <T extends Source> T addAuthor(Person person) {
        getChannel().addAuthor(person);
        return (T)this;
    }

    public Person addAuthor(String name) {
        return getChannel().addAuthor(name);
    }

    public Person addAuthor(String name, String email, String iri) {
        return getChannel().addAuthor(name, email, iri);
    }

    public <T extends Source> T addCategory(Category category) {
        getChannel().addCategory(category);
        return (T)this;
    }

    public Category addCategory(String term) {
        return getChannel().addCategory(term);
    }

    public Category addCategory(String scheme, String term, String label) {
        return getChannel().addCategory(scheme, term, label);
    }

    public <T extends Source> T addContributor(Person person) {
        throw new UnsupportedOperationException("Contributor's are not supported");
    }

    public Person addContributor(String name) {
        throw new UnsupportedOperationException("Contributor's are not supported");
    }

    public Person addContributor(String name, String email, String iri) {
        throw new UnsupportedOperationException("Contributor's are not supported");
    }

    public <T extends Source> T addLink(Link link) {
        getChannel().addLink(link);
        return (T)this;
    }

    public Link addLink(String href) {
        return getChannel().addLink(href);
    }

    public Link addLink(String href, String rel) {
        return getChannel().addLink(href, rel);
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        return getChannel().addLink(href, rel, type, title, hreflang, length);
    }

    public Link getAlternateLink() {
        return getChannel().getAlternateLink();
    }

    public Link getAlternateLink(String type, String hreflang) {
        return getChannel().getAlternateLink(type, hreflang);
    }

    public IRI getAlternateLinkResolvedHref() {
        return getChannel().getAlternateLinkResolvedHref();
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        return getChannel().getAlternateLinkResolvedHref(type, hreflang);
    }

    public Person getAuthor() {
        return getChannel().getAuthor();
    }

    public List<Person> getAuthors() {
        return getChannel().getAuthors();
    }

    public List<Category> getCategories() {
        return getChannel().getCategories();
    }

    public List<Category> getCategories(String scheme) {
        return getChannel().getCategories(scheme);
    }

    public Collection getCollection() {
        // TODO: should I support this?
        return null;
    }

    public List<Person> getContributors() {
        return getChannel().getContributors();
    }

    public Generator getGenerator() {
        return getChannel().getGenerator();
    }

    public IRI getIcon() {
        return getChannel().getIcon();
    }

    public IRIElement getIconElement() {
        return getChannel().getIconElement();
    }

    public IRI getId() {
        return getChannel().getId();
    }

    public IRIElement getIdElement() {
        return getChannel().getIdElement();
    }

    public Link getLink(String rel) {
        return getChannel().getLink(rel);
    }

    public IRI getLinkResolvedHref(String rel) {
        return getChannel().getLinkResolvedHref(rel);
    }

    public List<Link> getLinks() {
        return getChannel().getLinks();
    }

    public List<Link> getLinks(String rel) {
        return getChannel().getLinks(rel);
    }

    public List<Link> getLinks(String... rel) {
        return getChannel().getLinks(rel);
    }

    public IRI getLogo() {
        return getChannel().getLogo();
    }

    public IRIElement getLogoElement() {
        return getChannel().getLogoElement();
    }

    public String getRights() {
        return getChannel().getRights();
    }

    public Text getRightsElement() {
        return getChannel().getRightsElement();
    }

    public Type getRightsType() {
        return getChannel().getRightsType();
    }

    public Link getSelfLink() {
        return getChannel().getSelfLink();
    }

    public IRI getSelfLinkResolvedHref() {
        return getChannel().getSelfLinkResolvedHref();
    }

    public String getSubtitle() {
        return getChannel().getSubtitle();
    }

    public Text getSubtitleElement() {
        return getChannel().getSubtitleElement();
    }

    public Type getSubtitleType() {
        return getChannel().getSubtitleType();
    }

    public String getTitle() {
        return getChannel().getTitle();
    }

    public Text getTitleElement() {
        return getChannel().getTitleElement();
    }

    public Type getTitleType() {
        return getChannel().getTitleType();
    }

    public Date getUpdated() {
        return getChannel().getUpdated();
    }

    public DateTime getUpdatedElement() {
        return getChannel().getUpdatedElement();
    }

    public String getUpdatedString() {
        return getChannel().getUpdatedString();
    }

    public Date getPublished() {
        return getChannel().getPublished();
    }

    public DateTime getPublisheddElement() {
        return getChannel().getPublishedElement();
    }

    public String getPublishedString() {
        return getChannel().getPublishedString();
    }

    public IRIElement newId() {
        return getChannel().newId();
    }

    public <T extends Source> T setCollection(Collection collection) {
        throw new UnsupportedOperationException("Collection is not supported");
    }

    public <T extends Source> T setGenerator(Generator generator) {
        getChannel().setGenerator(generator);
        return (T)this;
    }

    public Generator setGenerator(String iri, String version, String value) {
        return getChannel().setGenerator(iri, version, value);
    }

    public IRIElement setIcon(String iri) {
        return getChannel().setIcon(iri);
    }

    public <T extends Source> T setIconElement(IRIElement iri) {
        getChannel().setIconElement(iri);
        return (T)this;
    }

    public IRIElement setId(String id) {
        return getChannel().setId(id);
    }

    public IRIElement setId(String id, boolean normalize) {
        return getChannel().setId(id, normalize);
    }

    public <T extends Source> T setIdElement(IRIElement id) {
        getChannel().setIdElement(id);
        return (T)this;
    }

    public IRIElement setLogo(String iri) {
        return getChannel().setLogo(iri);
    }

    public <T extends Source> T setLogoElement(IRIElement iri) {
        getChannel().setLogoElement(iri);
        return (T)this;
    }

    public Text setRights(String value) {
        return getChannel().setRights(value);
    }

    public Text setRights(String value, Type type) {
        return getChannel().setRights(value, type);
    }

    public Text setRights(Div value) {
        return getChannel().setRights(value);
    }

    public Text setRightsAsHtml(String value) {
        return getChannel().setRightsAsHtml(value);
    }

    public Text setRightsAsXhtml(String value) {
        return getChannel().setRightsAsXhtml(value);
    }

    public <T extends Source> T setRightsElement(Text text) {
        getChannel().setRightsElement(text);
        return (T)this;
    }

    public Text setSubtitle(String value) {
        return getChannel().setSubtitle(value);
    }

    public Text setSubtitle(String value, Type type) {
        return getChannel().setSubtitle(value, type);
    }

    public Text setSubtitle(Div value) {
        return getChannel().setSubtitle(value);
    }

    public Text setSubtitleAsHtml(String value) {
        return getChannel().setSubtitleAsHtml(value);
    }

    public Text setSubtitleAsXhtml(String value) {
        return getChannel().setSubtitleAsXhtml(value);
    }

    public <T extends Source> T setSubtitleElement(Text text) {
        getChannel().setSubtitleElement(text);
        return (T)this;
    }

    public Text setTitle(String value) {
        return getChannel().setTitle(value);
    }

    public Text setTitle(String value, Type type) {
        return getChannel().setTitle(value, type);
    }

    public Text setTitle(Div value) {
        return getChannel().setTitle(value);
    }

    public Text setTitleAsHtml(String value) {
        return getChannel().setTitleAsHtml(value);
    }

    public Text setTitleAsXhtml(String value) {
        return getChannel().setTitleAsXhtml(value);
    }

    public <T extends Source> T setTitleElement(Text text) {
        getChannel().setTitleElement(text);
        return (T)this;
    }

    public DateTime setUpdated(Date value) {
        return getChannel().setUpdated(value);
    }

    public DateTime setUpdated(String value) {
        return getChannel().setUpdated(value);
    }

    public <T extends Source> T setUpdatedElement(DateTime dateTime) {
        getChannel().setUpdatedElement(dateTime);
        return (T)this;
    }

    public <T extends ExtensibleElement> T addExtension(Element extension) {
        getChannel().addExtension(extension);
        return (T)this;
    }

    public <T extends Element> T addExtension(QName qname) {
        return (T)getChannel().addExtension(qname);
    }

    public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
        return (T)getChannel().addExtension(namespace, localPart, prefix);
    }

    public Element addSimpleExtension(QName qname, String value) {
        return getChannel().addSimpleExtension(qname, value);
    }

    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
        return getChannel().addSimpleExtension(namespace, localPart, prefix, value);
    }

    public <T extends Element> T getExtension(QName qname) {
        return (T)getChannel().getExtension(qname);
    }

    public <T extends Element> T getExtension(Class<T> _class) {
        return getChannel().getExtension(_class);
    }

    public List<Element> getExtensions() {
        return getChannel().getExtensions();
    }

    public List<Element> getExtensions(String uri) {
        return getChannel().getExtensions(uri);
    }

    public <T extends Element> List<T> getExtensions(QName qname) {
        return getChannel().getExtensions(qname);
    }

    public String getSimpleExtension(QName qname) {
        return getChannel().getSimpleExtension(qname);
    }

    public String getSimpleExtension(String namespace, String localPart, String prefix) {
        return getChannel().getSimpleExtension(namespace, localPart, prefix);
    }

    public String getAttributeValue(String name) {
        return getChannel().getAttributeValue(name);
    }

    public String getAttributeValue(QName qname) {
        return getChannel().getAttributeValue(qname);
    }

    public List<QName> getAttributes() {
        return getChannel().getAttributes();
    }

    public List<QName> getExtensionAttributes() {
        return getChannel().getExtensionAttributes();
    }

    public String getLanguage() {
        return getChannel().getLanguage();
    }

    public <T extends Element> T removeAttribute(QName qname) {
        getChannel().removeAttribute(qname);
        return (T)this;
    }

    public <T extends Element> T setAttributeValue(String name, String value) {
        getChannel().setAttributeValue(name, value);
        return (T)this;
    }

    public <T extends Element> T setAttributeValue(QName qname, String value) {
        getChannel().setAttributeValue(qname, value);
        return (T)this;
    }

    public <T extends Element> T setBaseUri(IRI base) {
        // throw new UnsupportedOperationException("Setting the base uri with xml:base is not supported");
        return (T)this;
    }

    public <T extends Element> T setBaseUri(String base) {
        // throw new UnsupportedOperationException("Setting the base uri with xml:base is not supported");
        return (T)this;
    }

    public <T extends Element> T setLanguage(String language) {
        getChannel().setLanguage(language);
        return (T)this;
    }

    public String getVersion() {
        return super.getAttributeValue("version");
    }

    public Feed getAsFeed() {
        throw new UnsupportedOperationException("Converting to feed is not supported");
    }
}
