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
package org.apache.abdera.ext.license;

import java.util.List;

import javax.activation.MimeTypeParseException;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

/**
 * Implementation of the Atom License Extension I-D
 */
public final class LicenseHelper {

  public static final String UNSPECIFIED_LICENSE = "http://purl.org/atompub/license#unspecified";
  
  LicenseHelper() {}
  
  public static List<Link> getLicense(
    Base base, 
    boolean inherited) {
      List<Link> links = null;
      if (base instanceof Source) {
        links = ((Source)base).getLinks(Link.REL_LICENSE);
      } else if (base instanceof Entry) {
        Entry entry = (Entry)base;
        Source source = entry.getSource();
        Base parent = entry.getParentElement();
        links = entry.getLinks(Link.REL_LICENSE);
        if (inherited && (links == null || links.size() == 0) && source != null) {
          links = getLicense(source, false);
        }
        if (inherited && (links == null || links.size() == 0) && parent != null) {
          links = getLicense(parent, false);
        }
      }
      return links;
  }  
  
  public static List<Link> getLicense(
    Base base) {
      return getLicense(base, false);
  }
  
  public static boolean hasUnspecifiedLicense(
    Base base, 
    boolean inherited) {
      try {
        return hasLicense(base, UNSPECIFIED_LICENSE, inherited);
      } catch (IRISyntaxException i) {
        // not going to happen
        return false;
      }
  }
  
  public static boolean hasUnspecifiedLicense(
    Base base) {
      return hasUnspecifiedLicense(base, false);
  }
  
  public static boolean hasLicense(
    Base base, 
    String iri, 
    boolean inherited)
      throws IRISyntaxException {
    List<Link> links = getLicense(base, inherited);
    IRI check = new IRI(iri);
    boolean answer = false;
    if (links != null) {
      for (Link link : links) {
        if (link.getResolvedHref().equals(check)) {
          answer = true;
          break;
        }
      }
    }
    return answer;
  }
  
  public static boolean hasLicense(
    Base base, 
    String iri) 
      throws IRISyntaxException {
    return hasLicense(base, iri, false);
  }
  
  public static boolean hasLicense(
    Base base, 
    boolean inherited) {
      List<Link> links = getLicense(base, inherited);
      return (links != null && links.size() > 0);
  }
  
  public static boolean hasLicense(
    Base base) {
      return hasLicense(base, false);
  }
  
  public static Link addUnspecifiedLicense(
    Base base) {
      try {
        if (hasUnspecifiedLicense(base)) 
          throw new IllegalStateException("Unspecified license already added");
        if (hasLicense(base)) 
          throw new IllegalStateException("Other licenses are already added.");
        return addLicense(base, UNSPECIFIED_LICENSE);
      } catch (IRISyntaxException i) {
        // not going to happen
        return null;
      }
  }
  
  public static Link addLicense(
    Base base, 
    String iri) 
      throws IRISyntaxException {
    try {
      return addLicense(base, iri, null, null, null);
    } catch (MimeTypeParseException m) {
      // not going to happen
      return null;
    }
  }
  
  public static Link addLicense(
    Base base,
    String iri,
    String title) 
    throws IRISyntaxException {
    try {
      return addLicense(base, iri, null, title, null);
    } catch (MimeTypeParseException m) {
      // not going to happen
      return null;
    }
  }
  
  public static Link addLicense(
    Base base,
    String iri,
    String type,
    String title,
    String hreflang) 
      throws IRISyntaxException,
             MimeTypeParseException {
    if (hasLicense(base, iri))
      throw new IllegalStateException("License '" + iri + "' has already been added");
    if (hasUnspecifiedLicense(base))
      throw new IllegalStateException("Unspecified license already added");
    if (base instanceof Source) {
      return ((Source)base).addLink((new IRI(iri)).toString(), Link.REL_LICENSE, type, title, hreflang, -1);
    } else if (base instanceof Entry) {
      return ((Entry)base).addLink((new IRI(iri)).toString(), Link.REL_LICENSE, type, title, hreflang, -1);
    }
    return null;
  }
  
}
