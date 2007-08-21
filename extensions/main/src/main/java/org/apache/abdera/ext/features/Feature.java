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
package org.apache.abdera.ext.features;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

public class Feature 
  extends ExtensibleElementWrapper {
  
  public enum Status {
    UNSUPPORTED,
    UNSPECIFIED,
    SUPPORTED, 
    REQUIRED,  
  }
  
  public Feature(Element internal) {
    super(internal);
  }
  
  public Feature(Factory factory) {
    super(factory, FeaturesHelper.FEATURE);
  }
  
  public IRI getRef()  {
      String ref = getAttributeValue("ref");
      return (ref != null) ? new IRI(ref) : null;
  }
  
  public Status getStatus() {
    String status = getAttributeValue("status");
    return status != null ? Status.valueOf(status.toUpperCase()) : Status.SUPPORTED;
  }
  
  public void setStatus(Status status) {
    if (status != null && status != Status.SUPPORTED) {
      if (status != Status.UNSPECIFIED) {
        setAttributeValue("status",status.name().toLowerCase());
      } else {
        throw new IllegalArgumentException(
          "Cannot set the status to unspecified");
      }
    } else {
      removeAttribute(new QName("status"));
    }
  }
  
  public IRI getHref()  {
      String href = getAttributeValue("href");
      return (href != null) ? new IRI(href) : null;
  }
  
  public String getLabel() {
    return getAttributeValue("label");
  }
  
  public void setRef(String ref) {
      if (ref == null) throw new IllegalArgumentException();
      setAttributeValue("ref", (new IRI(ref)).toString());
  }
  
  public void setHref(String href)  {
      if (href != null)
        setAttributeValue("href", (new IRI(href)).toString());
      else 
        removeAttribute(new QName("href"));
  }
  
  public void setLabel(String label) {
    if (label != null)
      setAttributeValue("label", label);
    else 
      removeAttribute(new QName("label"));
  }
  
}
