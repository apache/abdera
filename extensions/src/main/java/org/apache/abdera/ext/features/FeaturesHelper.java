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

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.ext.features.Feature.Status;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.util.MimeTypeHelper;

/**
 * Implementation of the current APP Features Draft
 * (http://www.ietf.org/internet-drafts/draft-snell-atompub-feature-05.txt)
 */
public final class FeaturesHelper {

  public static final String FNS = "http://purl.org/atompub/features/1.0";
  public static final QName FEATURE = new QName(FNS, "feature","f");
  public static final QName TYPE = new QName(FNS, "type", "f");
  
  private static final String FEATURE_BASE                 = "http://www.w3.org/2007/app/";
  public static final String FEATURE_DRAFTS                = FEATURE_BASE + "drafts";
  public static final String FEATURE_XHTML_CONTENT         = FEATURE_BASE + "xhtml-content";
  public static final String FEATURE_HTML_CONTENT          = FEATURE_BASE + "html-content";
  public static final String FEATURE_TEXT_CONTENT          = FEATURE_BASE + "text-content";
  public static final String FEATURE_XML_CONTENT           = FEATURE_BASE + "xml-content";
  public static final String FEATURE_BINARY_CONTENT        = FEATURE_BASE + "binary-content";
  public static final String FEATURE_REF_CONTENT           = FEATURE_BASE + "ref-content";
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
  public static final String FEATURE_SCHEDULED_PUBLISHING  = FEATURE_BASE + "scheduled-publishing";
  public static final String FEATURE_THREADING             = ThreadConstants.THR_NS;

  
  private static final String ABDERA_FEATURE_BASE = "http://incubator.apache.org/abdera/features/";
  
  /**
   * Indicates that the collection will accept digitally signed entries
   * If marked as "required", the collection will only accept digitally signed entries
   */
  public static final String ABDERA_FEATURE_SIGNATURE = ABDERA_FEATURE_BASE + "signature";
  
  /**
   * Indicates that the collection will preserve XML digital signatures contained
   * in member resources 
   */
  public static final String ABDERA_FEATURE_PRESERVE_SIGNATURE = ABDERA_FEATURE_BASE + "preserve-signature";
  
  /**
   * Indicates that the collection supports the use of the Atom Bidi Attribute.
   * If marked as "required", the collection will only accept entries that contain the bidi attribute
   */
  public static final String ABDERA_FEATURE_BIDI = ABDERA_FEATURE_BASE + "bidi";
  
  /**
   * Indicates that the collection supports the use of Diffie-Hellman key exchange
   * for XML encrypted requests
   */
  public static final String ABDERA_FEATURE_DHENCREQUEST = ABDERA_FEATURE_BASE + "dhenc-request";
  
  /**
   * Indicates that the collection supports the use of Diffie-Hellman key exchange
   * for XML encrypted responses
   */
  public static final String ABDERA_FEATURE_DHENCRESPONSE = ABDERA_FEATURE_BASE + "dhenc-response";
  
  /**
   * Indicates that the collection will add it's own digital signature to the 
   * collection feed and member resources
   */
  public static final String ABDERA_FEATURE_SIGNED_RESPONSE = ABDERA_FEATURE_BASE + "response-signature";
  
  /**
   * Indicates that the collection supports the use of Geo extensions (see the
   * org.apache.abdera.ext.geo Package)
   */
  public static final String ABDERA_FEATURE_GEO = ABDERA_FEATURE_BASE + "geo";
  
  /**
   * Indicates that the collection supports the use of the Feed paging standard.
   * (ftp://ftp.rfc-editor.org/in-notes/internet-drafts/draft-nottingham-atompub-feed-history-11.txt)
   * See the org.apache.abdera.ext.history Package)
   */
  public static final String ABDERA_FEATURE_PAGING = ABDERA_FEATURE_BASE + "paging";
  
  /**
   * Indicates that the collection supports the use of the Simple Sharing Extensions
   * (see the org.apache.abdera.ext.sharing Package)
   */
  public static final String ABDERA_FEATURE_SHARING = ABDERA_FEATURE_BASE + "sharing";
  
  /**
   * Indicates that the collection supports the GoogleLogin auth scheme
   * (see the org.apache.abdera.ext.gdata Package)
   */
  public static final String ABDERA_FEATURE_GOOGLELOGIN = ABDERA_FEATURE_BASE + "googlelogin";
  
  /**
   * Indicates that the collection supports the WSSE auth scheme
   * (see the org.apache.abdera.ext.wsse Package)
   */
  public static final String ABDERA_FEATURE_WSSE = ABDERA_FEATURE_BASE + "wsse";
  
  
  
  
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
  
  public static Status getFeatureStatus(Collection collection, String feature) {
    Feature f = getFeature(collection,feature);
    return f != null ? f.getStatus() : Status.UNSPECIFIED;
  }
  
  public static Feature[] getSupportedFeatures(Collection collection) {
    return getFeatures(collection, Status.SUPPORTED);
  }
  
  public static Feature[] getUnsupportedFeatures(Collection collection) {
    return getFeatures(collection, Status.UNSUPPORTED);
  }
  
  public static Feature[] getRequiredFeatures(Collection collection) {
    return getFeatures(collection, Status.REQUIRED);
  }
  
  public static Feature[] getFeatures(Collection collection, Status status) {
    if (status == null) status = Status.SUPPORTED;
    List<Feature> list = new ArrayList<Feature>();
    List<Feature> features = collection.getExtensions(FEATURE);
    for (Feature feature : features) {
      if (status == feature.getStatus()) {
        list.add(feature);
      }
    }
    return list.toArray(new Feature[list.size()]);
  }
  
  /**
   * Add the specified features to the collection
   */
  public static Feature[] addFeatures(
    Collection collection, 
    String... features) {
      List<Feature> list = new ArrayList<Feature>();
      for (String feature : features)
        list.add(addFeature(collection,feature));
      return list.toArray(new Feature[list.size()]);
  }
  
  /**
   * Add the specified features to the collection
   */
  public static Feature[] addFeatures(
    Collection collection,
    Status status,
    String... features) {
      List<Feature> list = new ArrayList<Feature>();
      for (String feature : features)
        list.add(addFeature(collection,feature, status));
      return list.toArray(new Feature[list.size()]);
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
        null, null, null);
  }
  
  /**
   * Add the specified feature to the collection
   * @param collection The collection
   * @param feature The IRI of the feature to add 
   */
  public static Feature addFeature(
    Collection collection, 
    String feature, 
    Status status) {
      return addFeature(
        collection, 
        feature, 
        status, 
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
    Status status,
    String href,
    String label) {
    if (getFeature(collection, feature) != null) 
      throw new IllegalArgumentException("Feature already specified");
    Factory factory = collection.getFactory();
    Feature el = 
      (Feature)factory.newExtensionElement(
        FeaturesHelper.FEATURE, collection);
    collection.declareNS(FNS, "f");
    el.setRef(new IRI(feature).toString());
    el.setStatus(status);
    if (href != null) el.setHref(new IRI(href).toString());
    if (label != null) el.setLabel(label);
    return el;
  }
  
  /**
   * Select a Collection from the service document
   */
  public static Collection[] select(Service service, Selector selector) {
    return select(service, new Selector[] {selector});
  }
  
  /**
   * Select a Collection from the service document
   */
  public static Collection[] select(Service service, Selector... selectors) {
    List<Collection> list = new ArrayList<Collection>();
    for (Workspace workspace : service.getWorkspaces()) {
      Collection[] collections = select(workspace, selectors);
      for (Collection collection : collections)
        list.add(collection);
    }
    return list.toArray(new Collection[list.size()]);
  }
  
  /**
   * Select a Collection from the Workspace
   */
  public static Collection[] select(Workspace workspace, Selector selector) {
    return select(workspace, new Selector[] {selector});
  }
  
  /**
   * Select a Collection from the Workspace
   */
  public static Collection[] select(Workspace workspace, Selector... selectors) {
    List<Collection> list = new ArrayList<Collection>();
    for (Collection collection : workspace.getCollections()) {
      boolean accept = true;
      for (Selector selector : selectors) {
        if (!selector.select(collection)) {
          accept = false;
          break;
        }
      }
      if (accept) list.add(collection);
    }
    return list.toArray(new Collection[list.size()]);
  }
  
  public static void addType(Feature feature, String mediaRange) {
    addType(feature, new String[] {mediaRange});
  }
  
  public static void addType(Feature feature, String... mediaRanges) {
    mediaRanges = MimeTypeHelper.condense(mediaRanges);
    for (String mediaRange : mediaRanges) {
      try {
        feature.addSimpleExtension(TYPE, new MimeType(mediaRange).toString());
      } catch (MimeTypeParseException e) {}
    }
  }
  
  public static String[] getTypes(Feature feature) {
    List<String> list = new ArrayList<String>();
    for (Element type : feature.getExtensions(TYPE)) {
      String value = type.getText();
      if (value != null) {
        value = value.trim();
        try {
          list.add(new MimeType(value).toString());
        } catch (MimeTypeParseException e) {}
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
}
