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

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class RssContent 
  extends ElementWrapper 
  implements Content {

  public RssContent(Element internal) {
    super(internal);
  }

  public RssContent(Factory factory, QName qname) {
    super(factory, qname);
  }

  public Type getContentType() {
    return Type.HTML;
  }

  public DataHandler getDataHandler() {
    return null;
  }

  public MimeType getMimeType() {
    return null;
  }

  public IRI getResolvedSrc() {
    return null;
  }

  public IRI getSrc() {
    return null;
  }

  public String getValue() {
    return getText();
  }

  public <T extends Element> T getValueElement() {
    return null;
  }

  public String getWrappedValue() {
    return getText();
  }

  public void setContentType(Type type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setDataHandler(DataHandler dataHandler) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setMimeType(String type) throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setSrc(String src) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setValue(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public <T extends Element> void setValueElement(T value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setWrappedValue(String wrappedValue) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

}
