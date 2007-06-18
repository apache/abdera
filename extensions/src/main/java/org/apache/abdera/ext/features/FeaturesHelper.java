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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;

/**
 * Implementation of the current APP Features Draft
 * (http://www.ietf.org/internet-drafts/draft-snell-atompub-feature-05.txt)
 */
public final class FeaturesHelper {

  public static final String FNS = "http://purl.org/atompub/features/1.0";
  public static final QName FEATURE = new QName(FNS, "feature","f");
  
  private static final String FEATURE_BASE                 = "http://www.w3.org/2007/app/";
  public static final String FEATURE_DRAFTS                = FEATURE_BASE + "drafts";
  public static final String FEATURE_XHTML_CONTENT         = FEATURE_BASE + "xhtml-content";
  public static final String FEATURE_HTML_CONTENT          = FEATURE_BASE + "html-content";
  public static final String FEATURE_TEXT_CONTENT          = FEATURE_BASE + "text-content";
  public static final String FEATURE_XML_CONTENT           = FEATURE_BASE + "xml-content";
  public static final String FEATURE_BINARY_CONTENT        = FEATURE_BASE + "binary-content";
  public static final String FEATURE_REF_CONTENT           = FEATURE_BASE + "src-content";
  public static final String FEATURE_XHTML_TITLE           = FEATURE_BASE + "xhtml-title";
  public static final String FEATURE_HTML_TITLE            = FEATURE_BASE + "html-title";
  public static final String FEATURE_TEXT_TITLE            = FEATURE_BASE + "text-title";
  public static final String FEATURE_XHTML_SUMMARY         = FEATURE_BASE + "xhtml-summary";
  public static final String FEATURE_HTML_SUMMARY          = FEATURE_BASE + "html-summary";
  public static final String FEATURE_TEXT_SUMMARY          = FEATURE_BASE + "text-summary";
  public static final String FEATURE_AUTO_SUMMARY          = FEATURE_BASE + "auto-summary";
  public static final String FEATURE_XHTML_RIGHTS          = FEATURE_BASE + "xhtml-rights";
  public static final String FEATURE_HTML_RIGHTS           = FEATURE_BASE + "html-rights";
  public static final String FEATURE_TEXT_RIGHTS           = FEATURE_BASE + "text-rights";
  public static final String FEATURE_AUTH_AUTHOR           = FEATURE_BASE + "auth-author";
  public static final String FEATURE_SLUG                  = FEATURE_BASE + "slug";
  public static final String FEATURE_MULTIPLE_CATEGORIES   = FEATURE_BASE + "multiple-categories";
  public static final String FEATURE_MULTIPLE_AUTHORS      = FEATURE_BASE + "multiple-authors";
  public static final String FEATURE_MULTIPLE_CONTRIBUTORS = FEATURE_BASE + "multiple-contributors";
  public static final String FEATURE_PRESERVE_INFOSET      = FEATURE_BASE + "preserve-infoset";
  public static final String FEATURE_PRESERVE_ID           = FEATURE_BASE + "preserve-id";
  public static final String FEATURE_PRESERVE_DATES        = FEATURE_BASE + "preserve-dates";
  public static final String FEATURE_PRESERVE_EXTENSIONS   = FEATURE_BASE + "preserve-extensions";
  public static final String FEATURE_PRESERVE_LINKS        = FEATURE_BASE + "preserve-links";
  public static final String FEATURE_PRESERVE_RIGHTS       = FEATURE_BASE + "preserve-rights";
  public static final String FEATURE_THREADING             = ThreadConstants.THR_NS;

  
  private FeaturesHelper() {}
  
  /**
   * Returns the specified feature element or null
   */
  public static Feature getFeature(
    Collection collection,
    String feature) {
      List<Element> list = collection.getExtensions(FEATURE);
      for (Element el : list) {
        if (el.getAttributeValue("ref").equals(feature))
          return (Feature) el;
      }
      return null;
  }
  
  /**
   * Returns true if the collection contains the specified feature element
   */
  public static boolean supportsFeature(
    Collection collection,
    String feature) { 
      return supportsFeature(collection, new String[] {feature});
  }
  
  /**
   * Returns true if the collection contains the specified feature element(s)
   */
  public static boolean supportsFeature(
    Collection collection, 
    String... features) {
      List<Element> list = collection.getExtensions(FEATURE);
      return check(list,features) == features.length;
  }
  
  
  private static int check(List<Element> exts, String... refvals) {
    int c = 0;
    for (String refval : refvals) {
      for (Element el : exts) {
        String ref = el.getAttributeValue("ref");
        if (ref.equals(refval)) {
          c++;
          break;
        }
      }
    }
    return c;
  }
  
  /**
   * Add the specified feature to the collection
   * @param collection The collection
   * @param feature The IRI of the feature to add 
   */
  public static Feature addFeature(
    Collection collection, 
    String feature) {
      return addFeature(
        collection, 
        feature, 
        false, null, null);
  }
  
  /**
   * Add the specified feature to the collection
   * @param collection The collection
   * @param feature The IRI of the feature to add 
   */
  public static Feature addFeature(
    Collection collection, 
    String feature, 
    boolean required) {
      return addFeature(
        collection, 
        feature, 
        required, 
        null, null);
  }
  
  /**
   * Add the specified feature to the collection
   * @param collection The collection
   * @param feature The IRI of the feature to add 
   * @param required True if the feature is required
   * @param href An IRI pointing to a human readable resource describing the feature
   * @param label A human readable label for the feature
   */
  public static Feature addFeature(
    Collection collection, 
    String feature,
    boolean required,
    String href,
    String label) {
    if (supportsFeature(collection, feature)) 
      throw new IllegalArgumentException("Feature already supported");
    Factory factory = collection.getFactory();
    Feature el = 
      (Feature)factory.newExtensionElement(
        FeaturesHelper.FEATURE, collection);
    collection.declareNS(FNS, "f");
    el.setAttributeValue("ref", (new IRI(feature)).toString());
    if (required) el.setAttributeValue("required", "yes");
    if (href != null) el.setAttributeValue("href", (new IRI(href)).toString());
    if (label != null) el.setAttributeValue("label", label);
    return el;
  }
  
}
