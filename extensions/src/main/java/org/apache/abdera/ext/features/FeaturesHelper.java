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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.g14n.iri.IRI;
import org.apache.abdera.g14n.iri.IRISyntaxException;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;

/**
 * Implementation of the current APP Features Draft
 * (http://www.ietf.org/internet-drafts/draft-snell-atompub-feature-01.txt)
 */
public final class FeaturesHelper {

  public static final String FNS = "http://purl.org/atompub/features/1.0";
  public static final QName FEATURE = new QName(FNS, "feature","f");
  public static final QName CONTROL = new QName(FNS, "control","f");
  
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
   * Returns the specified control element or null
   */
  public static Control getControl(
    Collection collection,
    String control) {
      List<Element> list = collection.getExtensions(CONTROL);
      for (Element el : list) {
        if (el.getAttributeValue("ref").equals(control))
          return (Control)el;
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
  
  /**
   * Returns true if the collection contains the specified control element
   */
  public static boolean supportsControl(
    Collection collection,
    String control) { 
      return supportsControl(collection, new String[] {control});
  }
  
  /**
   * Returns true if the collection contains the specified control element(s)
   */
  public static boolean supportsControl(
    Collection collection, 
    String... controls) {
      List<Element> list = collection.getExtensions(CONTROL);
      return check(list, controls) == controls.length;
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
   * @param required True if the feature is required
   * @param href An IRI pointing to a human readable resource describing the feature
   * @param label A human readable label for the feature
   */
  public static Feature addFeature(
    Collection collection, 
    String feature,
    boolean required,
    String href,
    String label) 
      throws IRISyntaxException {
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
  
  /**
   * Add the specified control to the collection
   * @param collection The collection
   * @param feature The IRI of the control to add 
   * @param required True if the control is required
   * @param href An IRI pointing to a human readable resource describing the control
   * @param label A human readable label for the control
   */
  public static Control addControl(
    Collection collection,
    String control,
    boolean required,
    String href,
    String label) 
      throws IRISyntaxException {
    if (supportsControl(collection, control)) 
      throw new IllegalArgumentException("Control already supported");
    Factory factory = collection.getFactory();
    Control el = 
      (Control)factory.newExtensionElement(
        FeaturesHelper.CONTROL, collection);
    collection.declareNS(FNS, "f");
    el.setAttributeValue("ref", (new IRI(control)).toString());
    if (required) el.setAttributeValue("required", "yes");
    if (href != null) el.setAttributeValue("href", (new IRI(href)).toString());
    if (label != null) el.setAttributeValue("label", label);
    return el;
  }
}
