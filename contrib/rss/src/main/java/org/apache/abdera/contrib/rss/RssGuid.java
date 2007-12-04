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
package org.apache.abdera.contrib.rss;

import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;

public class RssGuid 
  extends ExtensibleElementWrapper 
  implements IRIElement, Link {

  public RssGuid(Element internal) {
    super(internal);
  }

  public RssGuid(Factory factory, QName qname) {
    super(factory, qname);
  }

  public IRI getResolvedValue() {
    return getValue();
  }

  public IRI getValue() {
    String t = getText();
    return (t != null) ? new IRI(t) : null;
  }

  public IRIElement setNormalizedValue(String iri) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public IRIElement setValue(String iri) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }
  
  public boolean isPermalink() {
    String v = getAttributeValue("isPermaLink");
    if (v == null) getAttributeValue("ispermalink");
    return (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes"));
  }

  
  public IRI getHref() {
    return getValue();
  }

  public String getHrefLang() {
    return null;
  }

  public long getLength() {
    return -1;
  }

  public MimeType getMimeType() {
    return null;
  }

  public String getRel() {
    return Link.REL_ALTERNATE;
  }

  public IRI getResolvedHref() {
    return getValue();
  }

  public String getTitle() {
    return null;
  }

  public Link setHref(String href) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link setHrefLang(String lang) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link setLength(long length) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link setMimeType(String type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link setRel(String rel) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link setTitle(String title) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public <T extends ExtensibleElement>T addExtension(Element extension) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public <T extends Element> T addExtension(QName qname) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Element addSimpleExtension(QName qname, String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public <T extends Element> T getExtension(QName qname) {
    return null;
  }

  public <T extends Element> T getExtension(Class<T> _class) {
    return null;
  }

  public List<Element> getExtensions() {
    return null;
  }

  public List<Element> getExtensions(String uri) {
    return null;
  }

  public <T extends Element> List<T> getExtensions(QName qname) {
    return null;
  }

  public String getSimpleExtension(QName qname) {
    return null;
  }

  public String getSimpleExtension(String namespace, String localPart, String prefix) {
    return null;
  }

}
