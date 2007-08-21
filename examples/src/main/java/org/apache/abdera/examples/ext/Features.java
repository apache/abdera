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
 * The Atompub Features Extension is described in an IETF I-D and is used to
 * communicate information about features that are supported, required or 
 * unsupported by an Atompub collection.
 */
public class Features {

  public static void main(String... args) throws Exception {
    
    Abdera abdera = new Abdera();
    Service service = abdera.newService();
    Workspace workspace = service.addWorkspace("My workspace");
    Collection collection = workspace.addCollection("My collection", "foo");
    
    // Specify which features are supported by the collection
    FeaturesHelper.addFeature(collection, FeaturesHelper.FEATURE_DRAFTS);
    FeaturesHelper.addFeature(collection, FeaturesHelper.FEATURE_TEXT_TITLE, Feature.Status.REQUIRED);
    FeaturesHelper.addFeature(collection, FeaturesHelper.FEATURE_SLUG, Feature.Status.UNSUPPORTED);
    FeaturesHelper.addFeature(collection, FeaturesHelper.ABDERA_FEATURE_BIDI);
    
    // Get the support status of a specific feature
    System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_DRAFTS));
    System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_TEXT_TITLE));
    System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.FEATURE_SLUG));
    System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.ABDERA_FEATURE_BIDI));
    System.out.println(FeaturesHelper.getFeatureStatus(collection, FeaturesHelper.ABDERA_FEATURE_GEO));
    
    
    // Listing features by support status
    Feature[] features = null;
    features = FeaturesHelper.getRequiredFeatures(collection);
    System.out.println("Required:");
    for (Feature feature : features) {
      System.out.println("\t" + feature.getRef());
    }
    
    features = FeaturesHelper.getSupportedFeatures(collection);
    System.out.println("Supported:");
    for (Feature feature : features) {
      System.out.println("\t" + feature.getRef());
    }
    
    features = FeaturesHelper.getUnsupportedFeatures(collection);
    System.out.println("Unsupported:");
    for (Feature feature : features) {
      System.out.println("\t" + feature.getRef());
    }
    
    
    // Select a collection by feature support status
    Collection[] selectedCollections = 
      FeaturesHelper.select(
        service, 
        new FeatureSelector(
          FeaturesHelper.FEATURE_DRAFTS, 
          FeaturesHelper.ABDERA_FEATURE_BIDI));
    System.out.println("Selected Collections:");
    for (Collection selected : selectedCollections) 
      System.out.println("\t" + selected.getTitle());
    
  }
  
}
