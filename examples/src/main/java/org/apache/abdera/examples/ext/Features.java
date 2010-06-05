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
package org.apache.abdera.examples.ext;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.features.Feature;
import org.apache.abdera.ext.features.FeatureSelector;
import org.apache.abdera.ext.features.FeaturesHelper;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;

/**
 * The Atompub Features Extension is described in an IETF I-D and is used to communicate information about features that
 * are supported, required or unsupported by an Atompub collection.
 */
public class Features {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();
        Service service = abdera.newService();
        Workspace workspace = service.addWorkspace("My workspace");
        Collection collection = workspace.addCollection("My collection", "foo");

        // Specify which features are supported by the collection
        org.apache.abdera.ext.features.Features features = FeaturesHelper.addFeaturesElement(collection);
        features.addFeature(FeaturesHelper.FEATURE_SUPPORTS_DRAFTS);
        features.addFeature(FeaturesHelper.FEATURE_REQUIRES_TEXT_TEXT);
        features.addFeature(FeaturesHelper.FEATURE_IGNORES_SLUG);
        features.addFeature(FeaturesHelper.FEATURE_SUPPORTS_BIDI);

        // Get the support status of a specific feature
        System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_SUPPORTS_DRAFTS));
        System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_REQUIRES_TEXT_TEXT));
        System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_IGNORES_SLUG));
        System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_SUPPORTS_BIDI));
        System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_SUPPORTS_GEO));

        Feature[] fs = FeaturesHelper.getFeatures(collection);
        for (Feature feature : fs) {
            System.out.println("\t" + feature.getRef());
        }

        // Select a collection by feature
        Collection[] selectedCollections =
            FeaturesHelper.select(service, new FeatureSelector(FeaturesHelper.FEATURE_SUPPORTS_DRAFTS,
                                                               FeaturesHelper.FEATURE_SUPPORTS_BIDI));
        System.out.println("Selected Collections:");
        for (Collection selected : selectedCollections)
            System.out.println("\t" + selected.getTitle());

    }

}
