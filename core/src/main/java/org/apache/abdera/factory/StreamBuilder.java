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
package org.apache.abdera.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.util.AbstractStreamWriter;

/**
 * StreamBuilder is a special implementation of the StreamWriter interface that can be used to create Feed Object Model
 * instances using the StreamWriter interface. StreamBuilder provides an additional method (getBase) for returning the
 * FOM Base element that was built. The StreamWriter methods indent(), flush(), close(), setWriter(), setInputStream,
 * setAutoclose(), setAutoflush(), setAutoIndent(), and setChannel() have no effect on this StreamWriter implementation
 * 
 * <pre>
 * StreamBuilder sw = new StreamBuilder();
 * Entry entry =
 *     sw.startElement(Constants.ENTRY).writeBase(&quot;http://example.org&quot;).writeLanguage(&quot;en-US&quot;)
 *         .writeId(&quot;http://example.org&quot;).writeTitle(&quot;testing&quot;).writeUpdated(new Date()).endElement().getBase();
 * entry.writeTo(System.out);
 * </pre>
 */
@SuppressWarnings("unchecked")
public class StreamBuilder extends AbstractStreamWriter {

    private final Abdera abdera;
    private Base root = null;
    private Base current = null;

    public StreamBuilder() {
        this(Abdera.getInstance());
    }

    public StreamBuilder(Abdera abdera) {
        super(abdera, "fom");
        this.abdera = abdera;
    }

    public <T extends Base> T getBase() {
        return (T)root;
    }

    public StreamBuilder startDocument(String xmlversion, String charset) {
        if (root != null)
            throw new IllegalStateException("Document already started");
        root = abdera.getFactory().newDocument();
        ((Document)root).setCharset(charset);
        current = root;
        return this;
    }

    public StreamBuilder startDocument(String xmlversion) {
        return startDocument(xmlversion, "UTF-8");
    }

    private static QName getQName(String name, String namespace, String prefix) {
        if (prefix != null)
            return new QName(namespace, name, prefix);
        else if (namespace != null)
            return new QName(namespace, name);
        else
            return new QName(name);
    }

    public StreamBuilder startElement(String name, String namespace, String prefix) {
        current = abdera.getFactory().newElement(getQName(name, namespace, prefix), current);
        if (root == null)
            root = current;
        return this;
    }

    public StreamBuilder endElement() {
        current = current instanceof Element ? ((Element)current).getParentElement() : null;
        return this;
    }

    public StreamBuilder writeAttribute(String name, String namespace, String prefix, String value) {
        if (!(current instanceof Element))
            throw new IllegalStateException("Not currently an element");
        ((Element)current).setAttributeValue(getQName(name, namespace, prefix), value);
        return this;
    }

    public StreamBuilder writeComment(String value) {
        current.addComment(value);
        return this;
    }

    public StreamBuilder writeElementText(String value) {
        if (!(current instanceof Element))
            throw new IllegalStateException("Not currently an element");
        Element element = (Element)current;
        String text = element.getText();
        element.setText(text + value);
        return this;
    }

    public StreamBuilder writeId() {
        return writeId(abdera.getFactory().newUuidUri());
    }

    public StreamBuilder writePI(String value) {
        return writePI(value, null);
    }

    public StreamBuilder writePI(String value, String target) {
        if (!(current instanceof Document))
            throw new IllegalStateException("Not currently a document");
        ((Document)current).addProcessingInstruction(target != null ? target : "", value);
        return this;
    }

    public void close() throws IOException {
    }

    public StreamBuilder flush() {
        // non-op
        return this;
    }

    public StreamBuilder indent() {
        // non-op
        return this;
    }

    public StreamBuilder setOutputStream(OutputStream out) {
        // non-op
        return this;
    }

    public StreamBuilder setOutputStream(OutputStream out, String charset) {
        // non-op
        return this;
    }

    public StreamBuilder setWriter(Writer writer) {
        // non-op
        return this;
    }

    public StreamBuilder endAuthor() {
        return (StreamBuilder)super.endAuthor();
    }

    public StreamBuilder endCategories() {
        return (StreamBuilder)super.endCategories();
    }

    public StreamBuilder endCategory() {
        return (StreamBuilder)super.endCategory();
    }

    public StreamBuilder endCollection() {
        return (StreamBuilder)super.endCollection();
    }

    public StreamBuilder endContent() {
        return (StreamBuilder)super.endContent();
    }

    public StreamBuilder endContributor() {
        return (StreamBuilder)super.endContributor();
    }

    public StreamBuilder endControl() {
        return (StreamBuilder)super.endControl();
    }

    public StreamBuilder endDocument() {
        return (StreamBuilder)super.endDocument();
    }

    public StreamBuilder endEntry() {
        return (StreamBuilder)super.endEntry();
    }

    public StreamBuilder endFeed() {
        return (StreamBuilder)super.endFeed();
    }

    public StreamBuilder endGenerator() {
        return (StreamBuilder)super.endGenerator();
    }

    public StreamBuilder endLink() {
        return (StreamBuilder)super.endLink();
    }

    public StreamBuilder endPerson() {
        return (StreamBuilder)super.endPerson();
    }

    public StreamBuilder endService() {
        return (StreamBuilder)super.endService();
    }

    public StreamBuilder endSource() {
        return (StreamBuilder)super.endSource();
    }

    public StreamBuilder endText() {
        return (StreamBuilder)super.endText();
    }

    public StreamBuilder endWorkspace() {
        return (StreamBuilder)super.endWorkspace();
    }

    public StreamBuilder setAutoclose(boolean auto) {
        return (StreamBuilder)super.setAutoclose(auto);
    }

    public StreamBuilder setAutoflush(boolean auto) {
        return (StreamBuilder)super.setAutoflush(auto);
    }

    public StreamBuilder setAutoIndent(boolean indent) {
        return (StreamBuilder)super.setAutoIndent(indent);
    }

    public StreamBuilder setChannel(WritableByteChannel channel, String charset) {
        return (StreamBuilder)super.setChannel(channel, charset);
    }

    public StreamBuilder setChannel(WritableByteChannel channel) {
        return (StreamBuilder)super.setChannel(channel);
    }

    public StreamBuilder startAuthor() {
        return (StreamBuilder)super.startAuthor();
    }

    public StreamBuilder startCategories() {
        return (StreamBuilder)super.startCategories();
    }

    public StreamBuilder startCategories(boolean fixed, String scheme) {
        return (StreamBuilder)super.startCategories(fixed, scheme);
    }

    public StreamBuilder startCategories(boolean fixed) {
        return (StreamBuilder)super.startCategories(fixed);
    }

    public StreamBuilder startCategory(String term, String scheme, String label) {
        return (StreamBuilder)super.startCategory(term, scheme, label);
    }

    public StreamBuilder startCategory(String term, String scheme) {
        return (StreamBuilder)super.startCategory(term, scheme);
    }

    public StreamBuilder startCategory(String term) {
        return (StreamBuilder)super.startCategory(term);
    }

    public StreamBuilder startCollection(String href) {
        return (StreamBuilder)super.startCollection(href);
    }

    public StreamBuilder startContent(String type, String src) {
        return (StreamBuilder)super.startContent(type, src);
    }

    public StreamBuilder startContent(String type) {
        return (StreamBuilder)super.startContent(type);
    }

    public StreamBuilder startContent(Type type, String src) {
        return (StreamBuilder)super.startContent(type, src);
    }

    public StreamBuilder startContent(Type type) {
        return (StreamBuilder)super.startContent(type);
    }

    public StreamBuilder startContributor() {
        return (StreamBuilder)super.startContributor();
    }

    public StreamBuilder startControl() {
        return (StreamBuilder)super.startControl();
    }

    public StreamBuilder startDocument() {
        return (StreamBuilder)super.startDocument();
    }

    public StreamBuilder startElement(QName qname) {
        return (StreamBuilder)super.startElement(qname);
    }

    public StreamBuilder startElement(String name, String namespace) {
        return (StreamBuilder)super.startElement(name, namespace);
    }

    public StreamBuilder startElement(String name) {
        return (StreamBuilder)super.startElement(name);
    }

    public StreamBuilder startEntry() {
        return (StreamBuilder)super.startEntry();
    }

    public StreamBuilder startFeed() {
        return (StreamBuilder)super.startFeed();
    }

    public StreamBuilder startGenerator(String version, String uri) {
        return (StreamBuilder)super.startGenerator(version, uri);
    }

    public StreamBuilder startLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return (StreamBuilder)super.startLink(iri, rel, type, title, hreflang, length);
    }

    public StreamBuilder startLink(String iri, String rel, String type) {
        return (StreamBuilder)super.startLink(iri, rel, type);
    }

    public StreamBuilder startLink(String iri, String rel) {
        return (StreamBuilder)super.startLink(iri, rel);
    }

    public StreamBuilder startLink(String iri) {
        return (StreamBuilder)super.startLink(iri);
    }

    public StreamBuilder startPerson(QName qname) {
        return (StreamBuilder)super.startPerson(qname);
    }

    public StreamBuilder startPerson(String name, String namespace, String prefix) {
        return (StreamBuilder)super.startPerson(name, namespace, prefix);
    }

    public StreamBuilder startPerson(String name, String namespace) {
        return (StreamBuilder)super.startPerson(name, namespace);
    }

    public StreamBuilder startPerson(String name) {
        return (StreamBuilder)super.startPerson(name);
    }

    public StreamBuilder startService() {
        return (StreamBuilder)super.startService();
    }

    public StreamBuilder startSource() {
        return (StreamBuilder)super.startSource();
    }

    public StreamBuilder startText(QName qname, org.apache.abdera.model.Text.Type type) {
        return (StreamBuilder)super.startText(qname, type);
    }

    public StreamBuilder startText(String name, String namespace, String prefix, org.apache.abdera.model.Text.Type type) {
        return (StreamBuilder)super.startText(name, namespace, prefix, type);
    }

    public StreamBuilder startText(String name, String namespace, org.apache.abdera.model.Text.Type type) {
        return (StreamBuilder)super.startText(name, namespace, type);
    }

    public StreamBuilder startText(String name, org.apache.abdera.model.Text.Type type) {
        return (StreamBuilder)super.startText(name, type);
    }

    public StreamBuilder startWorkspace() {
        return (StreamBuilder)super.startWorkspace();
    }

    public StreamBuilder writeAccepts(String... accepts) {
        return (StreamBuilder)super.writeAccepts(accepts);
    }

    public StreamBuilder writeAcceptsEntry() {
        return (StreamBuilder)super.writeAcceptsEntry();
    }

    public StreamBuilder writeAcceptsNothing() {
        return (StreamBuilder)super.writeAcceptsNothing();
    }

    public StreamBuilder writeAttribute(QName qname, Date value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    public StreamBuilder writeAttribute(QName qname, double value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    public StreamBuilder writeAttribute(QName qname, int value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    public StreamBuilder writeAttribute(QName qname, long value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    public StreamBuilder writeAttribute(QName qname, String value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    public StreamBuilder writeAttribute(String name, Date value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    public StreamBuilder writeAttribute(String name, double value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    public StreamBuilder writeAttribute(String name, int value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    public StreamBuilder writeAttribute(String name, long value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, Date value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, double value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, int value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, long value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, String prefix, Date value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, String prefix, double value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, String prefix, int value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, String prefix, long value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    public StreamBuilder writeAttribute(String name, String namespace, String value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    public StreamBuilder writeAttribute(String name, String value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    public StreamBuilder writeAuthor(String name, String email, String uri) {
        return (StreamBuilder)super.writeAuthor(name, email, uri);
    }

    public StreamBuilder writeAuthor(String name) {
        return (StreamBuilder)super.writeAuthor(name);
    }

    public StreamBuilder writeBase(IRI iri) {
        return (StreamBuilder)super.writeBase(iri);
    }

    public StreamBuilder writeBase(String iri) {
        return (StreamBuilder)super.writeBase(iri);
    }

    public StreamBuilder writeCategory(String term, String scheme, String label) {
        return (StreamBuilder)super.writeCategory(term, scheme, label);
    }

    public StreamBuilder writeCategory(String term, String scheme) {
        return (StreamBuilder)super.writeCategory(term, scheme);
    }

    public StreamBuilder writeCategory(String term) {
        return (StreamBuilder)super.writeCategory(term);
    }

    public StreamBuilder writeContent(String type, String value) {
        return (StreamBuilder)super.writeContent(type, value);
    }

    public StreamBuilder writeContent(Type type, DataHandler value) throws IOException {
        return (StreamBuilder)super.writeContent(type, value);
    }

    public StreamBuilder writeContent(Type type, InputStream value) throws IOException {
        return (StreamBuilder)super.writeContent(type, value);
    }

    public StreamBuilder writeContent(Type type, String value) {
        return (StreamBuilder)super.writeContent(type, value);
    }

    public StreamBuilder writeContributor(String name, String email, String uri) {
        return (StreamBuilder)super.writeContributor(name, email, uri);
    }

    public StreamBuilder writeContributor(String name) {
        return (StreamBuilder)super.writeContributor(name);
    }

    public StreamBuilder writeDate(QName qname, Date date) {
        return (StreamBuilder)super.writeDate(qname, date);
    }

    public StreamBuilder writeDate(QName qname, String date) {
        return (StreamBuilder)super.writeDate(qname, date);
    }

    public StreamBuilder writeDate(String name, Date date) {
        return (StreamBuilder)super.writeDate(name, date);
    }

    public StreamBuilder writeDate(String name, String namespace, Date date) {
        return (StreamBuilder)super.writeDate(name, namespace, date);
    }

    public StreamBuilder writeDate(String name, String namespace, String prefix, Date date) {
        return (StreamBuilder)super.writeDate(name, namespace, prefix, date);
    }

    public StreamBuilder writeDate(String name, String namespace, String prefix, String date) {
        return (StreamBuilder)super.writeDate(name, namespace, prefix, date);
    }

    public StreamBuilder writeDate(String name, String namespace, String date) {
        return (StreamBuilder)super.writeDate(name, namespace, date);
    }

    public StreamBuilder writeDate(String name, String date) {
        return (StreamBuilder)super.writeDate(name, date);
    }

    public StreamBuilder writeDraft(boolean draft) {
        return (StreamBuilder)super.writeDraft(draft);
    }

    public StreamBuilder writeEdited(Date date) {
        return (StreamBuilder)super.writeEdited(date);
    }

    public StreamBuilder writeEdited(String date) {
        return (StreamBuilder)super.writeEdited(date);
    }

    public StreamBuilder writeElementText(DataHandler value) throws IOException {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(Date value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(double value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(InputStream value) throws IOException {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(int value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(long value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    public StreamBuilder writeElementText(String format, Object... args) {
        return (StreamBuilder)super.writeElementText(format, args);
    }

    public StreamBuilder writeGenerator(String version, String uri, String value) {
        return (StreamBuilder)super.writeGenerator(version, uri, value);
    }

    public StreamBuilder writeIcon(IRI iri) {
        return (StreamBuilder)super.writeIcon(iri);
    }

    public StreamBuilder writeIcon(String iri) {
        return (StreamBuilder)super.writeIcon(iri);
    }

    public StreamBuilder writeId(IRI iri) {
        return (StreamBuilder)super.writeId(iri);
    }

    public StreamBuilder writeId(String iri) {
        return (StreamBuilder)super.writeId(iri);
    }

    public StreamBuilder writeIRIElement(QName qname, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(qname, iri);
    }

    public StreamBuilder writeIRIElement(QName qname, String iri) {
        return (StreamBuilder)super.writeIRIElement(qname, iri);
    }

    public StreamBuilder writeIRIElement(String name, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, iri);
    }

    public StreamBuilder writeIRIElement(String name, String namespace, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, iri);
    }

    public StreamBuilder writeIRIElement(String name, String namespace, String prefix, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, prefix, iri);
    }

    public StreamBuilder writeIRIElement(String name, String namespace, String prefix, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, prefix, iri);
    }

    public StreamBuilder writeIRIElement(String name, String namespace, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, iri);
    }

    public StreamBuilder writeIRIElement(String name, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, iri);
    }

    public StreamBuilder writeLanguage(Lang lang) {
        return (StreamBuilder)super.writeLanguage(lang);
    }

    public StreamBuilder writeLanguage(Locale locale) {
        return (StreamBuilder)super.writeLanguage(locale);
    }

    public StreamBuilder writeLanguage(String lang) {
        return (StreamBuilder)super.writeLanguage(lang);
    }

    public StreamBuilder writeLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return (StreamBuilder)super.writeLink(iri, rel, type, title, hreflang, length);
    }

    public StreamBuilder writeLink(String iri, String rel, String type) {
        return (StreamBuilder)super.writeLink(iri, rel, type);
    }

    public StreamBuilder writeLink(String iri, String rel) {
        return (StreamBuilder)super.writeLink(iri, rel);
    }

    public StreamBuilder writeLink(String iri) {
        return (StreamBuilder)super.writeLink(iri);
    }

    public StreamBuilder writeLogo(IRI iri) {
        return (StreamBuilder)super.writeLogo(iri);
    }

    public StreamBuilder writeLogo(String iri) {
        return (StreamBuilder)super.writeLogo(iri);
    }

    public StreamBuilder writePerson(QName qname, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(qname, name, email, uri);
    }

    public StreamBuilder writePerson(String localname,
                                     String namespace,
                                     String prefix,
                                     String name,
                                     String email,
                                     String uri) {
        return (StreamBuilder)super.writePerson(localname, namespace, prefix, name, email, uri);
    }

    public StreamBuilder writePerson(String localname, String namespace, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(localname, namespace, name, email, uri);
    }

    public StreamBuilder writePerson(String localname, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(localname, name, email, uri);
    }

    public StreamBuilder writePersonEmail(String email) {
        return (StreamBuilder)super.writePersonEmail(email);
    }

    public StreamBuilder writePersonName(String name) {
        return (StreamBuilder)super.writePersonName(name);
    }

    public StreamBuilder writePersonUri(String uri) {
        return (StreamBuilder)super.writePersonUri(uri);
    }

    public StreamBuilder writePublished(Date date) {
        return (StreamBuilder)super.writePublished(date);
    }

    public StreamBuilder writePublished(String date) {
        return (StreamBuilder)super.writePublished(date);
    }

    public StreamBuilder writeRights(String value) {
        return (StreamBuilder)super.writeRights(value);
    }

    public StreamBuilder writeRights(org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeRights(type, value);
    }

    public StreamBuilder writeSubtitle(String value) {
        return (StreamBuilder)super.writeSubtitle(value);
    }

    public StreamBuilder writeSubtitle(org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeSubtitle(type, value);
    }

    public StreamBuilder writeSummary(String value) {
        return (StreamBuilder)super.writeSummary(value);
    }

    public StreamBuilder writeSummary(org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeSummary(type, value);
    }

    public StreamBuilder writeText(QName qname, org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeText(qname, type, value);
    }

    public StreamBuilder writeText(String name,
                                   String namespace,
                                   String prefix,
                                   org.apache.abdera.model.Text.Type type,
                                   String value) {
        return (StreamBuilder)super.writeText(name, namespace, prefix, type, value);
    }

    public StreamBuilder writeText(String name, String namespace, org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeText(name, namespace, type, value);
    }

    public StreamBuilder writeText(String name, org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeText(name, type, value);
    }

    public StreamBuilder writeTitle(String value) {
        return (StreamBuilder)super.writeTitle(value);
    }

    public StreamBuilder writeTitle(org.apache.abdera.model.Text.Type type, String value) {
        return (StreamBuilder)super.writeTitle(type, value);
    }

    public StreamBuilder writeUpdated(Date date) {
        return (StreamBuilder)super.writeUpdated(date);
    }

    public StreamBuilder writeUpdated(String date) {
        return (StreamBuilder)super.writeUpdated(date);
    }

    public StreamBuilder setPrefix(String prefix, String uri) {
        if (!(current instanceof Element))
            throw new IllegalStateException("Not currently an element");
        ((Element)current).declareNS(uri, prefix);
        return this;
    }

    public StreamBuilder writeNamespace(String prefix, String uri) {
        return setPrefix(prefix, uri);
    }

}
