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
package org.apache.abdera.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractStreamWriter implements StreamWriter {

    protected final Abdera abdera;
    protected final String name;
    protected boolean autoflush = false;
    protected boolean autoclose = false;
    protected boolean autoindent = false;

    protected AbstractStreamWriter(Abdera abdera, String name) {
        this.abdera = abdera;
        this.name = name;
    }

    public StreamWriter setAutoflush(boolean auto) {
        this.autoflush = auto;
        return this;
    }

    public StreamWriter setAutoclose(boolean auto) {
        this.autoclose = auto;
        return this;
    }

    public StreamWriter setChannel(WritableByteChannel channel) {
        return setOutputStream(Channels.newOutputStream(channel));
    }

    public StreamWriter setChannel(WritableByteChannel channel, String charset) {
        return setWriter(Channels.newWriter(channel, charset));
    }

    public String getName() {
        return name;
    }

    public StreamWriter startDocument() {
        return startDocument("1.0");
    }

    public StreamWriter endDocument() {
        return this;
    }

    public StreamWriter startFeed() {
        return startElement(Constants.FEED);
    }

    public StreamWriter endFeed() {
        return endElement();
    }

    public StreamWriter startEntry() {
        return startElement(Constants.ENTRY);
    }

    public StreamWriter endEntry() {
        return endElement();
    }

    public StreamWriter endCategory() {
        return endElement();
    }

    public StreamWriter endContent() {
        return endElement();
    }

    public StreamWriter endLink() {
        return endElement();
    }

    public StreamWriter endPerson() {
        return endElement();
    }

    public StreamWriter endSource() {
        return endElement();
    }

    public StreamWriter endText() {
        return endElement();
    }

    public StreamWriter startLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return startElement(Constants.LINK).writeAttribute("href", iri).writeAttribute("rel", rel)
            .writeAttribute("type", type).writeAttribute("title", title).writeAttribute("hreflang", hreflang)
            .writeAttribute("length", length > -1 ? String.valueOf(length) : null);
    }

    public StreamWriter startPerson(QName qname) {
        return startElement(qname);
    }

    public StreamWriter startSource() {
        return startElement(Constants.SOURCE);
    }

    public StreamWriter startText(QName qname, Text.Type type) {
        return startElement(qname).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter writeDate(QName qname, String date) {
        return startElement(qname).writeElementText(date).endElement();
    }

    public StreamWriter writeIRIElement(QName qname, String iri) {
        return startElement(qname).writeElementText(iri).endElement();
    }

    public StreamWriter writePersonEmail(String email) {
        if (email == null)
            return this;
        return startElement(Constants.EMAIL).writeElementText(email).endElement();
    }

    public StreamWriter writePersonName(String name) {
        if (name == null)
            return this;
        return startElement(Constants.NAME).writeElementText(name).endElement();
    }

    public StreamWriter writePersonUri(String uri) {
        if (uri == null)
            return this;
        return startElement(Constants.URI).writeElementText(uri).endElement();
    }

    public StreamWriter startContent(Content.Type type, String src) {
        return startContent(type.name().toLowerCase(), src);
    }

    public StreamWriter startContent(String type, String src) {
        return startElement(Constants.CONTENT).writeAttribute("type", type).writeAttribute("src", src);
    }

    public StreamWriter startContent(Content.Type type) {
        return startContent(type, null);
    }

    public StreamWriter startContent(String type) {
        return startContent(type, null);
    }

    public StreamWriter startLink(String iri) {
        return startLink(iri, null, null, null, null, -1);
    }

    public StreamWriter startLink(String iri, String rel) {
        return startLink(iri, rel, null, null, null, -1);
    }

    public StreamWriter startLink(String iri, String rel, String type) {
        return startLink(iri, rel, type, null, null, -1);
    }

    public StreamWriter writeCategory(String term) {
        return writeCategory(term, null, null);
    }

    public StreamWriter writeCategory(String term, String scheme) {
        return writeCategory(term, scheme, null);
    }

    public StreamWriter writeCategory(String term, String scheme, String label) {
        return startElement(Constants.CATEGORY).writeAttribute("term", term).writeAttribute("scheme", scheme)
            .writeAttribute("label", label).endElement();
    }

    public StreamWriter writeContent(Content.Type type, String value) {
        return startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(Content.Type type, InputStream value) throws IOException {
        return startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(Content.Type type, DataHandler value) throws IOException {
        return startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(String type, String value) {
        return startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeEdited(Date date) {
        writeDate(Constants.EDITED, date);
        return this;
    }

    public StreamWriter writeId(String iri) {
        return writeIRIElement(Constants.ID, iri);
    }

    public StreamWriter writeIcon(String iri) {
        return writeIRIElement(Constants.ICON, iri);
    }

    public StreamWriter writeLogo(String iri) {
        return writeIRIElement(Constants.LOGO, iri);
    }

    public StreamWriter writeLink(String iri) {
        return writeLink(iri, null, null, null, null, -1);
    }

    public StreamWriter writeLink(String iri, String rel) {
        return writeLink(iri, rel, null, null, null, -1);
    }

    public StreamWriter writeLink(String iri, String rel, String type) {
        return writeLink(iri, rel, type, null, null, -1);
    }

    public StreamWriter writeLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return startLink(iri, rel, type, title, hreflang, length).endLink();
    }

    public StreamWriter writePerson(QName qname, String name, String email, String uri) {
        return startPerson(qname).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public StreamWriter writePublished(Date date) {
        return writeDate(Constants.PUBLISHED, date);
    }

    public StreamWriter writeText(QName qname, Text.Type type, String value) {
        return startText(qname, type).writeElementText(value).endElement();
    }

    public StreamWriter writeUpdated(Date date) {
        return writeDate(Constants.UPDATED, date);
    }

    public StreamWriter writeUpdated(String date) {
        return writeDate(Constants.UPDATED, date);
    }

    public StreamWriter writePublished(String date) {
        return writeDate(Constants.PUBLISHED, date);
    }

    public StreamWriter writeEdited(String date) {
        return writeDate(Constants.EDITED, date);
    }

    public StreamWriter writeDate(QName qname, Date date) {
        return writeDate(qname, AtomDate.format(date));
    }

    public StreamWriter writeId(IRI iri) {
        return writeIRIElement(Constants.ID, iri);
    }

    public StreamWriter writeIcon(IRI iri) {
        return writeIRIElement(Constants.ICON, iri);
    }

    public StreamWriter writeLogo(IRI iri) {
        return writeIRIElement(Constants.LOGO, iri);
    }

    public StreamWriter writeIRIElement(QName qname, IRI iri) {
        return writeIRIElement(qname, iri.toString());
    }

    public StreamWriter writeElementText(InputStream value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r = -1;
        while ((r = value.read(buf)) != -1)
            out.write(buf, 0, r);
        byte[] data = out.toByteArray();
        writeElementText(new String(Base64.encodeBase64(data), "UTF-8"));
        return this;
    }

    public StreamWriter writeElementText(DataHandler value) throws IOException {
        writeElementText(value.getInputStream());
        return this;
    }

    public StreamWriter writeTitle(String value) {
        return writeText(Constants.TITLE, Text.Type.TEXT, value);
    }

    public StreamWriter writeTitle(Text.Type type, String value) {
        return writeText(Constants.TITLE, type, value);
    }

    public StreamWriter writeSubtitle(String value) {
        return writeText(Constants.SUBTITLE, Text.Type.TEXT, value);
    }

    public StreamWriter writeSubtitle(Text.Type type, String value) {
        return writeText(Constants.SUBTITLE, type, value);
    }

    public StreamWriter writeSummary(String value) {
        return writeText(Constants.SUMMARY, Text.Type.TEXT, value);
    }

    public StreamWriter writeSummary(Text.Type type, String value) {
        return writeText(Constants.SUMMARY, type, value);
    }

    public StreamWriter writeRights(String value) {
        return writeText(Constants.RIGHTS, Text.Type.TEXT, value);
    }

    public StreamWriter writeRights(Text.Type type, String value) {
        return writeText(Constants.RIGHTS, type, value);
    }

    public StreamWriter writeAuthor(String name, String email, String uri) {
        return writePerson(Constants.AUTHOR, name, email, uri);
    }

    public StreamWriter writeAuthor(String name) {
        return writeAuthor(name, null, null);
    }

    public StreamWriter startAuthor() {
        return startElement(Constants.AUTHOR);
    }

    public StreamWriter endAuthor() {
        return endElement();
    }

    public StreamWriter writeContributor(String name, String email, String uri) {
        return writePerson(Constants.CONTRIBUTOR, name, email, uri);
    }

    public StreamWriter writeContributor(String name) {
        return writeContributor(name, null, null);
    }

    public StreamWriter startContributor() {
        return startElement(Constants.CONTRIBUTOR);
    }

    public StreamWriter endContributor() {
        return endElement();
    }

    public StreamWriter writeGenerator(String version, String uri, String value) {
        return startElement(Constants.GENERATOR).writeAttribute("version", version).writeAttribute("uri", uri)
            .writeElementText(value).endElement();
    }

    public StreamWriter startGenerator(String version, String uri) {
        return startElement(Constants.GENERATOR).writeAttribute("version", version).writeAttribute("uri", uri);
    }

    public StreamWriter endGenerator() {
        return endElement();
    }

    public StreamWriter startCategory(String term) {
        return startCategory(term, null, null);
    }

    public StreamWriter startCategory(String term, String scheme) {
        return startCategory(term, scheme, null);
    }

    public StreamWriter startCategory(String term, String scheme, String label) {
        return startElement(Constants.CATEGORY).writeAttribute("term", term).writeAttribute("scheme", scheme)
            .writeAttribute("label", label);
    }

    public StreamWriter startService() {
        return startElement(Constants.SERVICE);
    }

    public StreamWriter endService() {
        return endElement();
    }

    public StreamWriter startWorkspace() {
        return startElement(Constants.WORKSPACE);
    }

    public StreamWriter endWorkspace() {
        return endElement();
    }

    public StreamWriter startCollection(String href) {
        return startElement(Constants.COLLECTION).writeAttribute("href", href);
    }

    public StreamWriter endCollection() {
        endElement();
        return this;
    }

    public StreamWriter writeAccepts(String... accepts) {
        for (String accept : accepts) {
            startElement(Constants.ACCEPT).writeElementText(accept).endElement();
        }
        return this;
    }

    public StreamWriter writeAcceptsEntry() {
        return writeAccepts("application/atom+xml;type=entry");
    }

    public StreamWriter writeAcceptsNothing() {
        return writeAccepts("");
    }

    public StreamWriter startCategories() {
        return startCategories(false, null);
    }

    public StreamWriter startCategories(boolean fixed) {
        return startCategories(fixed, null);
    }

    public StreamWriter startCategories(boolean fixed, String scheme) {
        startElement(Constants.CATEGORIES);
        if (fixed) {
            writeAttribute("fixed", "yes");
        }
        if (scheme != null && scheme.length() > 0) {
            writeAttribute("scheme", scheme);
        }
        return this;
    }

    public StreamWriter endCategories() {
        return endElement();
    }

    public StreamWriter startControl() {
        return startElement(Constants.CONTROL);
    }

    public StreamWriter endControl() {
        return endElement();
    }

    public StreamWriter writeDraft(boolean draft) {
        return startElement(Constants.DRAFT).writeElementText(draft ? "yes" : "no").endElement();
    }

    public StreamWriter writeAttribute(String name, String value) {
        if (value == null)
            return this;
        return writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String value) {
        if (value == null)
            return this;
        return writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(QName qname, String value) {
        if (value == null)
            return this;
        return writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter startElement(QName qname) {
        return startElement(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix());
    }

    public StreamWriter startElement(String name) {
        return startElement(name, null, null);
    }

    public StreamWriter startElement(String name, String namespace) {
        return startElement(name, namespace, null);
    }

    public StreamWriter setAutoIndent(boolean indent) {
        this.autoindent = indent;
        return this;
    }

    public StreamWriter writeAttribute(QName qname, Date value) {
        return writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, Date value) {
        return writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, Date value) {
        return writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, Date value) {
        return writeAttribute(name, namespace, prefix, AtomDate.format(value));
    }

    public StreamWriter writeAttribute(QName qname, int value) {
        return writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, int value) {
        return writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, int value) {
        return writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, int value) {
        return writeAttribute(name, namespace, prefix, Integer.toString(value));
    }

    public StreamWriter writeAttribute(QName qname, long value) {
        return writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, long value) {
        return writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, long value) {
        return writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, long value) {
        return writeAttribute(name, namespace, prefix, Long.toString(value));
    }

    public StreamWriter writeAttribute(QName qname, double value) {
        return writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, double value) {
        return writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, double value) {
        return writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, double value) {
        return writeAttribute(name, namespace, prefix, Double.toString(value));
    }

    public StreamWriter writeElementText(Date value) {
        return writeElementText(AtomDate.format(value));
    }

    public StreamWriter writeElementText(int value) {
        return writeElementText(Integer.toString(value));
    }

    public StreamWriter writeElementText(long value) {
        return writeElementText(Long.toString(value));
    }

    public StreamWriter writeElementText(double value) {
        return writeElementText(Double.toString(value));
    }

    public StreamWriter writeBase(String iri) {
        return writeAttribute(Constants.BASE, iri);
    }

    public StreamWriter writeBase(IRI iri) {
        return writeBase(iri.toString());
    }

    public StreamWriter writeLanguage(String lang) {
        return writeAttribute(Constants.LANG, lang);
    }

    public StreamWriter writeLanguage(Lang lang) {
        return writeLanguage(lang.toString());
    }

    public StreamWriter writeLanguage(Locale locale) {
        return writeLanguage(new Lang(locale));
    }

    public StreamWriter writeIRIElement(String name, IRI iri) {
        return startElement(name).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, IRI iri) {
        return startElement(name, namespace).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String prefix, IRI iri) {
        return startElement(name, namespace, prefix).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String prefix, String iri) {
        return startElement(name, namespace, prefix).writeElementText(iri).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String iri) {
        return startElement(name, namespace).writeElementText(iri).endElement();
    }

    public StreamWriter writeIRIElement(String name, String iri) {
        return startElement(name).writeElementText(iri).endElement();
    }

    public StreamWriter writeDate(String name, Date date) {
        return startElement(name).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, Date date) {
        return startElement(name, namespace).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String prefix, Date date) {
        return startElement(name, namespace, prefix).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String date) {
        return startElement(name).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String date) {
        return startElement(name, namespace).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String prefix, String date) {
        return startElement(name, namespace, prefix).writeElementText(date).endElement();
    }

    public StreamWriter startText(String name, String namespace, String prefix, Type type) {
        return startElement(name, namespace, prefix).writeAttribute("type",
                                                                    type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter startText(String name, String namespace, Type type) {
        return startElement(name, namespace).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter startText(String name, Type type) {
        return startElement(name).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter writeText(String name, String namespace, String prefix, Type type, String value) {
        return startText(name, namespace, prefix, type).writeElementText(value).endElement();
    }

    public StreamWriter writeText(String name, String namespace, Type type, String value) {
        return startText(name, namespace, type).writeElementText(value).endElement();
    }

    public StreamWriter writeText(String name, Type type, String value) {
        return startText(name, type).writeElementText(value).endElement();
    }

    public StreamWriter startPerson(String name, String namespace, String prefix) {
        return startElement(name, namespace, prefix);
    }

    public StreamWriter startPerson(String name, String namespace) {
        return startElement(name, namespace);
    }

    public StreamWriter startPerson(String name) {
        return startElement(name);
    }

    public StreamWriter writePerson(String localname,
                                    String namespace,
                                    String prefix,
                                    String name,
                                    String email,
                                    String uri) {
        return startPerson(localname, namespace, prefix).writePersonName(name).writePersonEmail(email)
            .writePersonUri(uri).endPerson();
    }

    public StreamWriter writePerson(String localname, String namespace, String name, String email, String uri) {
        return startPerson(localname, namespace).writePersonName(name).writePersonEmail(email).writePersonUri(uri)
            .endPerson();
    }

    public StreamWriter writePerson(String localname, String name, String email, String uri) {
        return startPerson(localname).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public Appendable append(char c) throws IOException {
        return writeElementText(String.valueOf(c));
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        return append(csq.subSequence(start, end));
    }

    public Appendable append(CharSequence csq) throws IOException {
        return writeElementText(csq.toString());
    }

    public StreamWriter writeElementText(String format, Object... args) {
        new Formatter(this).format(format, args);
        return this;
    }
}
