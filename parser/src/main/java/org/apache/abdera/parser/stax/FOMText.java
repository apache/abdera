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

import javax.xml.namespace.QName;

import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMText 
  extends FOMElement 
  implements Text {

  private static final long serialVersionUID = 5177795905116574120L;
  protected Type type = Type.TEXT;
  
  public FOMText(QName qname, Text.Type type) {
    super(qname);
    init(type);
  }
  
  protected FOMText(
    Type type,
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
    init(type);
  }
  
  protected FOMText(
    Type type, 
    QName qname, 
    OMContainer parent, 
    OMFactory factory)
      throws OMException {
    super(qname, parent, factory);
    init(type);
  }

  protected FOMText(
    Type type, 
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder)
      throws OMException {
    super(qname, parent, factory, builder);
    init(type);
  }
  
  private void init(Type type) {
    this.type = type;
    if (Type.TEXT.equals(type))
      setAttributeValue(TYPE, "text");
    else if (Type.HTML.equals(type)) 
      setAttributeValue(TYPE, "html");
    else if (Type.XHTML.equals(type))
      setAttributeValue(TYPE, "xhtml");
    else 
      removeAttribute(TYPE);
  }
  
  public final Type getTextType() {
    return type;
  }

  public void setTextType(Type type) {
    init(type);
  }
  
  public Div getValueElement() {
    return (Div)this.getFirstChildWithName(Constants.DIV);
  }

  public void setValueElement(Div value) {    
    if (value != null) {
      if (this.getFirstChildWithName(Constants.DIV) != null)
        this.getFirstChildWithName(Constants.DIV).discard();
      init(Text.Type.XHTML);
      this.setFirstChild((OMElement)value);
    } else
      _removeAllChildren();
  }

  public String getValue() {
    String val = null;
    if (Type.TEXT.equals(type)) {
      val = getText();
    } else if (Type.HTML.equals(type)) {
      val = getText();
    } else if (Type.XHTML.equals(type)) {
      FOMDiv div = (FOMDiv) this.getFirstChildWithName(Constants.DIV);
      val = (div != null) ? div.getInternalValue() : null;
    }
    return val;
  }

  public void setText(String value) {
    init(Text.Type.TEXT);
    super.setText(value);
  }
  
  public void setValue(String value) {
    if (value != null) {
      if (Type.TEXT.equals(type)) {
        super.setText(value);
      } else if (Type.HTML.equals(type)) {
        super.setText(value);
      } else if (Type.XHTML.equals(type)) {
        IRI baseUri = null;
        value = "<div xmlns=\"" + XHTML_NS + "\">" + value + "</div>";
        Element element = null;
        try {
          baseUri = getResolvedBaseUri();
          element = _parse(value, baseUri);
        } catch (Exception e) {}
        if (element != null && element instanceof Div)
          setValueElement((Div)element);
      }
    } else 
      _removeAllChildren();
  }

  public String getWrappedValue() {
    if (Type.XHTML.equals(type)) {
      return this.getFirstChildWithName(Constants.DIV).toString();
    } else {
      return getValue();
    }
  }

  public void setWrappedValue(String wrappedValue) {
    if (Type.XHTML.equals(type)) {
      IRI baseUri = null;
      Element element = null;
      try {
        baseUri = getResolvedBaseUri();
        element = _parse(wrappedValue, baseUri);
      } catch (Exception e) {}
      
      if (element != null && element instanceof Div)
        setValueElement((Div)element);
    } else {
      setValue(wrappedValue);
    }
  }

  @Override
  public IRI getBaseUri() {
      if (Type.XHTML.equals(type)) {
        Element el = getValueElement();
        if (el != null) {
          if (el.getAttributeValue(BASE) != null) {
            if (getAttributeValue(BASE) != null)
              return super.getBaseUri().resolve(
                el.getAttributeValue(BASE));
            else
              return _getUriValue(el.getAttributeValue(BASE));
          }
        }
      }
      return super.getBaseUri();
  }

  @Override
  public IRI getResolvedBaseUri() {
      if (Type.XHTML.equals(type)) {
        Element el = getValueElement();
        if (el != null) {
          if (el.getAttributeValue(BASE) != null) {
            return super.getResolvedBaseUri().resolve(
              el.getAttributeValue(BASE));
          }
        }
      }
      return super.getResolvedBaseUri();
  }
  
  @Override
  public String getLanguage() {
    if (Type.XHTML.equals(type)) {
      Element el = getValueElement();
      if (el != null && el.getAttributeValue(LANG) != null)
        return el.getAttributeValue(LANG);
    }
    return super.getLanguage();
  }

  @Override
  public Object clone() {
    FOMText text = (FOMText) super.clone();
    text.type = type;
    return text;
  }

}
