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
package org.apache.abdera.ext.opensearch;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

public class Query {

  public enum Role {
    CORRECTION,
    EXAMPLE,
    RELATED,
    REQUEST,
    SUBSET,
    SUPERSET;
  }
  
  private final Element internal;
  
  public Query(Element internal) {
    this.internal = internal;
  }
  
  public Query(Abdera abdera) {
    internal = abdera.getFactory().newElement(OpenSearchV11Helper.QUERY);
  }
  
  public Query(ExtensibleElement parent) {
    parent.declareNS(OpenSearchV11Helper.OPENSEARCH_NS, OpenSearchV11Helper.OS_PREFIX);
    internal = parent.addExtension(OpenSearchV11Helper.QUERY);
  }
  
  public void setParent(ExtensibleElement parent) {
    parent.addExtension(internal);
  }
  
  public void discard() {
    internal.discard();
  }
  
  public Role getRole() {
    String role = internal.getAttributeValue("role");
    if (role == null) return null;
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (Exception e) {
      return null; // role is likely an extension. we don't currently support extension roles
    }
  }
  
  public void setRole(Role role) {
    if (role != null) {
      internal.setAttributeValue("role", role.name().toLowerCase());
    } else {
      internal.removeAttribute(new QName("role"));
    }
  }
  
  public String getTitle() {
    return internal.getAttributeValue("title");
  }
  
  public void setTitle(String title) {
    if (title != null) {
      if (title.length() > 256) throw new IllegalArgumentException("Title too long (max 256 characters)");
      internal.setAttributeValue("title", title);
    } else {
      internal.removeAttribute(new QName("title"));
    }
  }

  public int getTotalResults() {
    String val = internal.getAttributeValue("totalResults");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setTotalResults(int totalResults) {
    if (totalResults > -1) {
      internal.setAttributeValue("totalResults", String.valueOf(totalResults));
    } else {
      internal.removeAttribute(new QName("totalResults"));
    }
  }
  
  public String getSearchTerms() {
    return internal.getAttributeValue("searchTerms");
  }
  
  public void setSearchTerms(String terms) {
    if (terms != null) {
      internal.setAttributeValue("searchTerms", terms);
    } else {
      internal.removeAttribute(new QName("searchTerms"));
    }
  }
  
  public int getCount() {
    String val = internal.getAttributeValue("count");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setCount(int count) {
    if (count > -1) {
      internal.setAttributeValue("count", String.valueOf(count));
    } else {
      internal.removeAttribute(new QName("count"));
    }
  }
  
  public int getStartIndex() {
    String val = internal.getAttributeValue("startIndex");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setStartIndex(int startIndex) {
    if (startIndex > -1) {
      internal.setAttributeValue("startIndex", String.valueOf(startIndex));
    } else {
      internal.removeAttribute(new QName("startIndex"));
    }
  }
  
  public int getStartPage() {
    String val = internal.getAttributeValue("startPage");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setStartPage(int startPage) {
    if (startPage > -1) {
      internal.setAttributeValue("startPage", String.valueOf(startPage));
    } else {
      internal.removeAttribute(new QName("startPage"));
    }
  }
  
  public String getLanguage() {
    return internal.getAttributeValue("language");
  }
  
  public void setLanguage(String language) {
    if (language != null) {
      internal.setAttributeValue("language", language);
    } else {
      internal.removeAttribute(new QName("language"));
    }
  }
  
  public String getInputEncoding() {
    return internal.getAttributeValue("inputEncoding");
  }
  
  public void setInputEncoding(String encoding) {
    if (encoding != null) {
      internal.setAttributeValue("inputEncoding", encoding);
    } else {
      internal.removeAttribute(new QName("inputEncoding"));
    }
  }

  public String getOutputEncoding() {
    return internal.getAttributeValue("outputEncoding");
  }
  
  public void setOutputEncoding(String encoding) {
    if (encoding != null) {
      internal.setAttributeValue("outputEncoding", encoding);
    } else {
      internal.removeAttribute(new QName("outputEncoding"));
    }
  }
  
  public String getAttribute(QName qname) {
    return internal.getAttributeValue(qname);
  }
  
  public void setAttribute(QName qname, String value) {
    if (value != null) {
      internal.setAttributeValue(qname, value);
    } else {
      internal.removeAttribute(qname);
    }
  }
  
  @Override
  public String toString() {
    return internal.toString();
  }
  
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Query)) return false;
    Query query = (Query) other;
    return (internal.equals(query.internal));
  }
  
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = super.hashCode();
    result = PRIME * result + ((internal == null) ? 0 : internal.hashCode());
    return result;
  }

}
