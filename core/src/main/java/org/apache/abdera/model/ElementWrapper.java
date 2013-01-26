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
package org.apache.abdera.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.writer.WriterOptions;

/**
 * Base implementation used for static extensions.
 */
@SuppressWarnings("unchecked")
public abstract class ElementWrapper implements Element {

    private Element internal;

    protected ElementWrapper(Element internal) {
        this.internal = internal;
    }

    protected ElementWrapper(Factory factory, QName qname) {
        Element el = factory.newElement(qname);
        internal = (el instanceof ElementWrapper) ? ((ElementWrapper)el).getInternal() : el;
    }

    public <T extends Base> T addComment(String value) {
        internal.addComment(value);
        return (T)this;
    }

    public Object clone() {
        try {
            ElementWrapper wrapper = (ElementWrapper)super.clone();
            wrapper.internal = (Element)internal.clone();
            return wrapper;
        } catch (CloneNotSupportedException e) {
            // won't happen
            return null;
        }
    }

    public <T extends Element> T declareNS(String uri, String prefix) {
        internal.declareNS(uri, prefix);
        return (T)this;
    }

    public void discard() {
        internal.discard();
    }

    public List<QName> getAttributes() {
        return internal.getAttributes();
    }

    public String getAttributeValue(QName qname) {
        return internal.getAttributeValue(qname);
    }

    public String getAttributeValue(String name) {
        return internal.getAttributeValue(name);
    }

    public IRI getBaseUri() {
        return internal.getBaseUri();
    }

    public <T extends Element> Document<T> getDocument() {
        return internal.getDocument();
    }

    public List<QName> getExtensionAttributes() {
        return internal.getExtensionAttributes();
    }

    public Factory getFactory() {
        return internal.getFactory();
    }

    public <T extends Element> T getFirstChild() {
        return (T)internal.getFirstChild();
    }

    public <T extends Element> T getFirstChild(QName qname) {
        return (T)internal.getFirstChild(qname);
    }

    public String getLanguage() {
        return internal.getLanguage();
    }

    public Lang getLanguageTag() {
        return internal.getLanguageTag();
    }

    public Locale getLocale() {
        return internal.getLocale();
    }

    public <T extends Element> T getNextSibling() {
        return (T)internal.getNextSibling();
    }

    public <T extends Element> T getNextSibling(QName qname) {
        return (T)internal.getNextSibling(qname);
    }

    public <T extends Base> T getParentElement() {
        return (T)internal.getParentElement();
    }

    public <T extends Element> T getPreviousSibling() {
        return (T)internal.getPreviousSibling();
    }

    public <T extends Element> T getPreviousSibling(QName qname) {
        return (T)internal.getPreviousSibling(qname);
    }

    public QName getQName() {
        return internal.getQName();
    }

    public IRI getResolvedBaseUri() {
        return internal.getResolvedBaseUri();
    }

    public String getText() {
        return internal.getText();
    }

    public <T extends Element> T removeAttribute(QName qname) {
        internal.removeAttribute(qname);
        return (T)this;
    }

    public <T extends Element> T removeAttribute(String name) {
        internal.removeAttribute(name);
        return (T)this;
    }

    public <T extends Element> T setAttributeValue(QName qname, String value) {
        internal.setAttributeValue(qname, value);
        return (T)this;
    }

    public <T extends Element> T setAttributeValue(String name, String value) {
        internal.setAttributeValue(name, value);
        return (T)this;
    }

    public <T extends Element> T setBaseUri(IRI base) {
        internal.setBaseUri(base);
        return (T)this;
    }

    public <T extends Element> T setBaseUri(String base) {
        internal.setBaseUri(base);
        return (T)this;
    }

    public <T extends Element> T setLanguage(String language) {
        internal.setLanguage(language);
        return (T)this;
    }

    @SuppressWarnings("deprecation")
    public <T extends Element> T setParentElement(Element parent) {
        internal.setParentElement(parent);
        return (T)this;
    }

    public void setText(String text) {
        internal.setText(text);
    }

    public <T extends Element> T setText(DataHandler handler) {
        internal.setText(handler);
        return (T)this;
    }

    public void writeTo(OutputStream out) throws IOException {
        internal.writeTo(out);
    }

    public void writeTo(Writer writer) throws IOException {
        internal.writeTo(writer);
    }

    public boolean equals(Object other) {
        if (other instanceof ElementWrapper)
            other = ((ElementWrapper)other).getInternal();
        return internal.equals(other);
    }

    public String toString() {
        return internal.toString();
    }

    public int hashCode() {
        return internal.hashCode();
    }

    public Element getInternal() {
        return internal;
    }

    public <T extends Element> List<T> getElements() {
        return internal.getElements();
    }

    public Map<String, String> getNamespaces() {
        return internal.getNamespaces();
    }

    public boolean getMustPreserveWhitespace() {
        return internal.getMustPreserveWhitespace();
    }

    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        internal.setMustPreserveWhitespace(preserve);
        return (T)this;
    }

    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        internal.writeTo(out, options);
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out, WriterOptions options)
        throws IOException {
        internal.writeTo(writer, out, options);
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out) throws IOException {
        internal.writeTo(writer, out);
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out, WriterOptions options) throws IOException {
        internal.writeTo(writer, out, options);
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out) throws IOException {
        internal.writeTo(writer, out);
    }

    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        internal.writeTo(writer, out, options);
    }

    public void writeTo(String writer, OutputStream out) throws IOException {
        internal.writeTo(writer, out);
    }

    public void writeTo(String writer, Writer out, WriterOptions options) throws IOException {
        internal.writeTo(writer, out, options);
    }

    public void writeTo(String writer, Writer out) throws IOException {
        internal.writeTo(writer, out);
    }

    public void writeTo(Writer out, WriterOptions options) throws IOException {
        internal.writeTo(out, options);
    }

    public WriterOptions getDefaultWriterOptions() {
        return internal.getDefaultWriterOptions();
    }

    public <T extends Base> T complete() {
        internal.complete();
        return (T)this;
    }

    public Iterator<Element> iterator() {
        return internal.iterator();
    }
}
