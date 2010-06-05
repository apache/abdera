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
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;

public class RssSource extends ExtensibleElementWrapper implements Source {

    private Link self = null;

    public RssSource(Element internal) {
        super(internal);
        self = new RssLink(internal);
    }

    public RssSource(Factory factory, QName qname) {
        super(factory, qname);
        self = new RssLink(factory, qname);
    }

    public <T extends Source> T addAuthor(Person person) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addAuthor(String name) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addAuthor(String name, String email, String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T addCategory(Category category) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Category addCategory(String term) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Category addCategory(String scheme, String term, String label) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T addContributor(Person person) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addContributor(String name) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Person addContributor(String name, String email, String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T addLink(Link link) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href, String rel) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link getAlternateLink(String type, String hreflang) {
        return getAlternateLink();
    }

    public IRI getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Person getAuthor() {
        return null;
    }

    public List<Person> getAuthors() {
        return null;
    }

    public List<Category> getCategories() {
        return null;
    }

    public List<Category> getCategories(String scheme) {
        return null;
    }

    public Collection getCollection() {
        return null;
    }

    public List<Person> getContributors() {
        return null;
    }

    public Generator getGenerator() {
        return null;
    }

    public IRI getIcon() {
        return null;
    }

    public IRIElement getIconElement() {
        return null;
    }

    public IRI getId() {
        return null;
    }

    public IRIElement getIdElement() {
        return null;
    }

    public Link getLink(String rel) {
        return null;
    }

    public IRI getLinkResolvedHref(String rel) {
        return null;
    }

    public List<Link> getLinks() {
        return null;
    }

    public List<Link> getLinks(String rel) {
        return null;
    }

    public List<Link> getLinks(String... rel) {
        return null;
    }

    public IRI getLogo() {
        return null;
    }

    public IRIElement getLogoElement() {
        return null;
    }

    public String getRights() {
        return null;
    }

    public Text getRightsElement() {
        return null;
    }

    public Type getRightsType() {
        return null;
    }

    public Link getSelfLink() {
        return getAlternateLink();
    }

    public IRI getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public String getSubtitle() {
        return null;
    }

    public Text getSubtitleElement() {
        return null;
    }

    public Type getSubtitleType() {
        return null;
    }

    public String getTitle() {
        return getText();
    }

    public Text getTitleElement() {
        return null;
    }

    public Type getTitleType() {
        return Type.HTML;
    }

    public Date getUpdated() {
        return null;
    }

    public DateTime getUpdatedElement() {
        return null;
    }

    public String getUpdatedString() {
        return null;
    }

    public IRIElement newId() {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setCollection(Collection collection) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setGenerator(Generator generator) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Generator setGenerator(String iri, String version, String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setIcon(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setIconElement(IRIElement iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setId(String id) {
        return null;
    }

    public IRIElement setId(String id, boolean normalize) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setIdElement(IRIElement id) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setLogo(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setLogoElement(IRIElement iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRights(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRightsAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setRightsAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setRightsElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitle(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitleAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setSubtitleAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setSubtitleElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(String value, Type type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(Div value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitleAsHtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitleAsXhtml(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setTitleElement(Text text) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public DateTime setUpdated(Date value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public DateTime setUpdated(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public <T extends Source> T setUpdatedElement(DateTime dateTime) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Text setTitle(String value) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link getAlternateLink() {
        return self;
    }

    public Feed getAsFeed() {
        throw new UnsupportedOperationException("Converting to feed is not supported");
    }

}
