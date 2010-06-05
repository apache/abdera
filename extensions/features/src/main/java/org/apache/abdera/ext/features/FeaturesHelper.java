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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

/**
 * Implementation of the current APP Features Draft
 * (http://www.ietf.org/internet-drafts/draft-snell-atompub-feature-08.txt)
 */
public final class FeaturesHelper {

    public enum Status {
        UNSPECIFIED, SPECIFIED
    }

    public static final String FNS = "http://purl.org/atompub/features/1.0";
    public static final QName FEATURE = new QName(FNS, "feature", "f");
    public static final QName FEATURES = new QName(FNS, "features", "f");
    public static final QName TYPE = new QName(FNS, "type", "f");

    private static final String FEATURE_BASE = "http://www.w3.org/2007/app/";
    private static final String ABDERA_FEATURE_BASE = "http://abdera.apache.org/features/";
    private static final String BLOG_FEATURE_BASE = "http://abdera.apache.org/features/blog/";
    public static final String FEATURE_SUPPORTS_DRAFTS = FEATURE_BASE + "supportsDraft";
    public static final String FEATURE_IGNORES_DRAFTS = FEATURE_BASE + "ignoresDraft";

    public static final String FEATURE_SUPPORTS_XHTML_CONTENT = ABDERA_FEATURE_BASE + "supportsXhtmlContent";
    public static final String FEATURE_REQUIRES_XHTML_CONTENT = ABDERA_FEATURE_BASE + "requiresXhtmlContent";
    public static final String FEATURE_SUPPORTS_HTML_CONTENT = ABDERA_FEATURE_BASE + "supportsHtmlContent";
    public static final String FEATURE_REQUIRES_HTML_CONTENT = ABDERA_FEATURE_BASE + "requiresHtmlContent";
    public static final String FEATURE_SUPPORTS_TEXT_CONTENT = ABDERA_FEATURE_BASE + "supportsTextContent";
    public static final String FEATURE_REQUIRES_TEXT_CONTENT = ABDERA_FEATURE_BASE + "requiresTextContent";
    public static final String FEATURE_SUPPORTS_XML_CONTENT = ABDERA_FEATURE_BASE + "supportsXmlContent";
    public static final String FEATURE_REQUIRES_XML_CONTENT = ABDERA_FEATURE_BASE + "requiresXmlContent";
    public static final String FEATURE_SUPPORTS_BINARY_CONTENT = ABDERA_FEATURE_BASE + "supportsBinaryContent";
    public static final String FEATURE_REQUIRES_BINARY_CONTENT = ABDERA_FEATURE_BASE + "requiresBinaryContent";
    public static final String FEATURE_SUPPORTS_REF_CONTENT = ABDERA_FEATURE_BASE + "supportsRefContent";
    public static final String FEATURE_REQUIRES_REF_CONTENT = ABDERA_FEATURE_BASE + "requiresRefContent";
    public static final String FEATURE_SUPPORTS_XHTML_TEXT = ABDERA_FEATURE_BASE + "supportsXhtmlText";
    public static final String FEATURE_REQUIRES_XHTML_TEXT = ABDERA_FEATURE_BASE + "requiresXhtmlText";
    public static final String FEATURE_SUPPORTS_HTML_TEXT = ABDERA_FEATURE_BASE + "supportsHtmlText";
    public static final String FEATURE_REQUIRES_HTML_TEXT = ABDERA_FEATURE_BASE + "requiresHtmlText";
    public static final String FEATURE_SUPPORTS_TEXT_TEXT = ABDERA_FEATURE_BASE + "supportsTextText";
    public static final String FEATURE_REQUIRES_TEXT_TEXT = ABDERA_FEATURE_BASE + "requiresTextText";
    public static final String FEATURE_PRESERVES_SUMMARY = ABDERA_FEATURE_BASE + "preservesSummary";
    public static final String FEATURE_IGNORES_SUMMARY = ABDERA_FEATURE_BASE + "ignoresSummary";
    public static final String FEATURE_PRESERVES_RIGHTS = ABDERA_FEATURE_BASE + "preservesRights";
    public static final String FEATURE_IGNORES_RIGHTS = ABDERA_FEATURE_BASE + "ignoresRights";
    public static final String FEATURE_PRESERVES_AUTHORS = ABDERA_FEATURE_BASE + "preservesAuthors";
    public static final String FEATURE_IGNORES_AUTHORS = ABDERA_FEATURE_BASE + "ignoresAuthors";
    public static final String FEATURE_PRESERVES_CONTRIBUTORS = ABDERA_FEATURE_BASE + "preservesContributors";
    public static final String FEATURE_IGNORES_CONTRIBUTORS = ABDERA_FEATURE_BASE + "ignoresContributors";
    public static final String FEATURE_USES_SLUG = ABDERA_FEATURE_BASE + "usesSlug";
    public static final String FEATURE_IGNORES_SLUG = ABDERA_FEATURE_BASE + "ignoresSlug";
    public static final String FEATURE_PRESERVES_CATEGORIES = ABDERA_FEATURE_BASE + "preservesCategories";
    public static final String FEATURE_MULTIPLE_CATEGORIES = ABDERA_FEATURE_BASE + "multipleCategories";
    public static final String FEATURE_IGNORES_CATEGORIES = ABDERA_FEATURE_BASE + "ignoresCategories";
    public static final String FEATURE_PRESERVES_LINKS = ABDERA_FEATURE_BASE + "preservesLinks";
    public static final String FEATURE_IGNORES_LINKS = ABDERA_FEATURE_BASE + "ignoresLinks";
    public static final String FEATURE_PRESERVES_INFOSET = ABDERA_FEATURE_BASE + "preservesInfoset";
    public static final String FEATURE_PRESERVES_ID = ABDERA_FEATURE_BASE + "preservesId";
    public static final String FEATURE_PRESERVES_DATES = ABDERA_FEATURE_BASE + "preservesDates";
    public static final String FEATURE_PRESERVES_EXTENSIONS = ABDERA_FEATURE_BASE + "preservesExtensions";
    public static final String FEATURE_SCHEDULED_PUBLISHING = ABDERA_FEATURE_BASE + "scheduledPublishing";
    public static final String FEATURE_REQUIRES_PERSON_EMAIL = ABDERA_FEATURE_BASE + "requiresPersonEmail";
    public static final String FEATURE_HIDES_PERSON_EMAIL = ABDERA_FEATURE_BASE + "hidesPersonEmail";
    public static final String FEATURE_REQUIRES_PERSON_URI = ABDERA_FEATURE_BASE + "requiresPersonUri";
    public static final String FEATURE_HIDES_PERSON_URI = ABDERA_FEATURE_BASE + "hidesPersonUri";
    public static final String FEATURE_PRESERVES_LANGUAGE = ABDERA_FEATURE_BASE + "preservesXmlLang";
    public static final String FEATURE_IGNORES_LANGUAGE = ABDERA_FEATURE_BASE + "ignoresXmlLang";
    public static final String FEATURE_SUPPORTS_CONDITIONALS = ABDERA_FEATURE_BASE + "supportsConditionalUpdates";
    public static final String FEATURE_REQUIRES_CONDITIONALS = ABDERA_FEATURE_BASE + "requiresConditionalUpdates";
    public static final String FEATURE_PRESERVES_THREADING = ABDERA_FEATURE_BASE + "preservesThreading";
    public static final String FEATURE_REQUIRES_THREADING = ABDERA_FEATURE_BASE + "requiresThreading";
    public static final String FEATURE_IGNORES_THREADING = ABDERA_FEATURE_BASE + "ignoresThreading";

    /**
     * Indicates that the collection will preserve XML digital signatures contained in member resources
     */
    public static final String FEATURE_PRESERVE_SIGNATURE = ABDERA_FEATURE_BASE + "preservesSignature";

    /**
     * Indicates that the collection will support XML digital signatures contained in member resources but may not
     * preserve those signatures
     */
    public static final String FEATURE_SUPPORTS_SIGNATURE = ABDERA_FEATURE_BASE + "supportsSignature";

    /**
     * Indicates that the collection will ignore XML digital signatures contained in member resources
     */
    public static final String FEATURE_IGNORES_SIGNATURE = ABDERA_FEATURE_BASE + "ignoresSignature";

    /**
     * Indicates that the collection requires member resources to contain valid XML digital signatures
     */
    public static final String FEATURE_REQUIRES_SIGNATURE = ABDERA_FEATURE_BASE + "requiresSignature";

    /**
     * Indicates that the collection will add it's own digital signature to the collection feed and member resources
     */
    public static final String FEATURE_SIGNED_RESPONSE = ABDERA_FEATURE_BASE + "responseSignature";

    /**
     * Indicates that the collection supports the use of the Atom Bidi Attribute.
     */
    public static final String FEATURE_SUPPORTS_BIDI = ABDERA_FEATURE_BASE + "supportsBidi";

    /**
     * Indicates that the collection requires the use of the Atom Bidi Attribute.
     */
    public static final String FEATURE_REQUIRES_BIDI = ABDERA_FEATURE_BASE + "requiresBidi";

    /**
     * Indicates that the collection ignores the use of the Atom Bidi Attribute.
     */
    public static final String FEATURE_IGNORES_BIDI = ABDERA_FEATURE_BASE + "ignoresBidi";

    /**
     * Indicates that the collection supports the use of Geo extensions (see the org.apache.abdera.ext.geo Package)
     */
    public static final String FEATURE_SUPPORTS_GEO = ABDERA_FEATURE_BASE + "supportsGeo";

    /**
     * Indicates that the collection requires the use of Geo extensions (see the org.apache.abdera.ext.geo Package)
     */
    public static final String FEATURE_REQUIRES_GEO = ABDERA_FEATURE_BASE + "requiresGeo";

    /**
     * Indicates that the collection ignores the use of Geo extensions (see the org.apache.abdera.ext.geo Package)
     */
    public static final String FEATURE_IGNORES_GEO = ABDERA_FEATURE_BASE + "ignoresGeo";

    /**
     * Indicates that the collection supports the use of the Simple Sharing Extensions (see the
     * org.apache.abdera.ext.sharing Package)
     */
    public static final String FEATURE_SUPPORTS_SHARING = ABDERA_FEATURE_BASE + "supportsSharing";

    /**
     * Indicates that the collection requires the use of the Simple Sharing Extensions (see the
     * org.apache.abdera.ext.sharing Package)
     */
    public static final String FEATURE_REQUIRES_SHARING = ABDERA_FEATURE_BASE + "requiresSharing";

    /**
     * Indicates that the collection ignores the use of the Simple Sharing Extensions (see the
     * org.apache.abdera.ext.sharing Package)
     */
    public static final String FEATURE_IGNORES_SHARING = ABDERA_FEATURE_BASE + "ignoresSharing";

    /**
     * Indicates that the collection requires the GoogleLogin auth scheme (see the org.apache.abdera.ext.gdata Package)
     */
    public static final String FEATURE_REQUIRES_GOOGLELOGIN = ABDERA_FEATURE_BASE + "requiresGoogleLogin";

    /**
     * Indicates that the collection supports the GoogleLogin auth scheme (see the org.apache.abdera.ext.gdata Package)
     */
    public static final String FEATURE_SUPPORTS_GOOGLELOGIN = ABDERA_FEATURE_BASE + "supportsGoogleLogin";

    /**
     * Indicates that the collection requires the WSSE auth scheme (see the org.apache.abdera.ext.wsse Package)
     */
    public static final String FEATURE_REQUIRES_WSSE = ABDERA_FEATURE_BASE + "requiresWsse";

    /**
     * Indicates that the collection supports the WSSE auth scheme (see the org.apache.abdera.ext.wsse Package)
     */
    public static final String FEATURE_SUPPORTS_WSSE = ABDERA_FEATURE_BASE + "supportsWsse";

    /**
     * Indicates that the collection will remove markup that is considered potentially unsafe from the entry examples of
     * the type of markup that would be removed include scripts and embed
     */
    public static final String FEATURE_FILTERS_MARKUP = BLOG_FEATURE_BASE + "filtersUnsafeMarkup";

    private FeaturesHelper() {
    }

    private static Map<String, Features> featuresCache =
        Collections.synchronizedMap(new WeakHashMap<String, Features>());

    private static Features getCachedFeatures(String iri) {
        return featuresCache.get(iri);
    }

    private static void setCachedFeatures(String iri, Features features) {
        featuresCache.put(iri, features);
    }

    public static void flushCachedFeatures() {
        featuresCache.clear();
    }

    public static Features newFeatures(Abdera abdera) {
        Factory factory = abdera.getFactory();
        Document<Features> doc = factory.newDocument();
        Features features = factory.newElement(FEATURES, doc);
        doc.setRoot(features);
        return features;
    }

    public static Features getFeaturesElement(Collection collection) {
        return getFeaturesElement(collection, true);
    }

    public static Features getFeaturesElement(Collection collection, boolean outofline) {
        Features features = collection.getExtension(FEATURES);
        if (features != null && outofline) {
            if (features.getHref() != null) {
                String iri = features.getResolvedHref().toASCIIString();
                features = getCachedFeatures(iri);
                if (features == null) {
                    Abdera abdera = collection.getFactory().getAbdera();
                    AbderaClient client = new AbderaClient(abdera);
                    ClientResponse resp = client.get(iri);
                    if (resp.getType() == ResponseType.SUCCESS) {
                        Document<Features> doc = resp.getDocument();
                        features = doc.getRoot();
                        setCachedFeatures(iri, features);
                    } else {
                        features = null;
                    }
                }
            }
        }
        return features;
    }

    public static Feature getFeature(Collection collection, String feature) {
        return getFeature(getFeaturesElement(collection), feature);
    }

    /**
     * Returns the specified feature element or null
     */
    public static Feature getFeature(Features features, String feature) {
        if (features == null)
            return null;
        List<Element> list = features.getExtensions(FEATURE);
        for (Element el : list) {
            if (el.getAttributeValue("ref").equals(feature))
                return (Feature)el;
        }
        return null;
    }

    public static Status getFeatureStatus(Collection collection, String feature) {
        return getFeatureStatus(getFeaturesElement(collection), feature);
    }

    public static Status getFeatureStatus(Features features, String feature) {
        if (features == null)
            return Status.UNSPECIFIED;
        Feature f = getFeature(features, feature);
        return f != null ? Status.SPECIFIED : Status.UNSPECIFIED;
    }

    public static Feature[] getFeatures(Collection collection) {
        Features features = getFeaturesElement(collection);
        if (features == null)
            return null;
        List<Feature> list = features.getExtensions(FEATURE);
        return list.toArray(new Feature[list.size()]);
    }

    public static Features addFeaturesElement(Collection collection) {
        if (getFeaturesElement(collection, false) != null)
            throw new IllegalArgumentException("A collection element can only contain one features element");
        return collection.addExtension(FEATURES);
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
            if (accept)
                list.add(collection);
        }
        return list.toArray(new Collection[list.size()]);
    }

}
