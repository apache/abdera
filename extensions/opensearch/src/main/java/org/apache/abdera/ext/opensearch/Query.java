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
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElement;

public class Query 
  extends ElementWrapper {

  public enum Role {
    CORRECTION,
    EXAMPLE,
    RELATED,
    REQUEST,
    SUBSET,
    SUPERSET;
  }
  
  public Query(Element internal) {
    super(internal);
  }
  
  public Query(Factory factory) {
    super(factory, OpenSearchConstants.QUERY);
  }
  
  public Query(Abdera abdera) {
    this(abdera.getFactory());
  }
  
  public Query(ExtensibleElement parent) {
    this(parent.getFactory());
    parent.declareNS(
      OpenSearchConstants.OPENSEARCH_NS, 
      OpenSearchConstants.OS_PREFIX);
  }
  
  public Role getRole() {
    String role = getInternal().getAttributeValue("role");
    if (role == null) return null;
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (Exception e) {
      return null; // role is likely an extension. we don't currently support extension roles
    }
  }
  
  public void setRole(Role role) {
    if (role != null) {
      getInternal().setAttributeValue("role", role.name().toLowerCase());
    } else {
      getInternal().removeAttribute(new QName("role"));
    }
  }
  
  public String getTitle() {
    return getInternal().getAttributeValue("title");
  }
  
  public void setTitle(String title) {
    if (title != null) {
      if (title.length() > 256) throw new IllegalArgumentException("Title too long (max 256 characters)");
      getInternal().setAttributeValue("title", title);
    } else {
      getInternal().removeAttribute(new QName("title"));
    }
  }

  public int getTotalResults() {
    String val = getInternal().getAttributeValue("totalResults");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setTotalResults(int totalResults) {
    if (totalResults > -1) {
      getInternal().setAttributeValue("totalResults", String.valueOf(totalResults));
    } else {
      getInternal().removeAttribute(new QName("totalResults"));
    }
  }
  
  public String getSearchTerms() {
    return getInternal().getAttributeValue("searchTerms");
  }
  
  public void setSearchTerms(String terms) {
    if (terms != null) {
      getInternal().setAttributeValue("searchTerms", terms);
    } else {
      getInternal().removeAttribute(new QName("searchTerms"));
    }
  }
  
  public int getCount() {
    String val = getInternal().getAttributeValue("count");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setCount(int count) {
    if (count > -1) {
      getInternal().setAttributeValue("count", String.valueOf(count));
    } else {
      getInternal().removeAttribute(new QName("count"));
    }
  }
  
  public int getStartIndex() {
    String val = getInternal().getAttributeValue("startIndex");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setStartIndex(int startIndex) {
    if (startIndex > -1) {
      getInternal().setAttributeValue("startIndex", String.valueOf(startIndex));
    } else {
      getInternal().removeAttribute(new QName("startIndex"));
    }
  }
  
  public int getStartPage() {
    String val = getInternal().getAttributeValue("startPage");
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public void setStartPage(int startPage) {
    if (startPage > -1) {
      getInternal().setAttributeValue("startPage", String.valueOf(startPage));
    } else {
      getInternal().removeAttribute(new QName("startPage"));
    }
  }
  
  public String getLanguage() {
    return getInternal().getAttributeValue("language");
  }
  
  public void setLanguage(String language) {
    if (language != null) {
      getInternal().setAttributeValue("language", language);
    } else {
      getInternal().removeAttribute(new QName("language"));
    }
  }
  
  public String getInputEncoding() {
    return getInternal().getAttributeValue("inputEncoding");
  }
  
  public void setInputEncoding(String encoding) {
    if (encoding != null) {
      getInternal().setAttributeValue("inputEncoding", encoding);
    } else {
      getInternal().removeAttribute(new QName("inputEncoding"));
    }
  }

  public String getOutputEncoding() {
    return getInternal().getAttributeValue("outputEncoding");
  }
  
  public void setOutputEncoding(String encoding) {
    if (encoding != null) {
      getInternal().setAttributeValue("outputEncoding", encoding);
    } else {
      getInternal().removeAttribute(new QName("outputEncoding"));
    }
  }
  
  public int hashCode() {
    final int PRIME = 31;
    int result = super.hashCode();
    result = PRIME * result + ((getInternal() == null) ? 0 : getInternal().hashCode());
    return result;
  }

}
