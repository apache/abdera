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
package org.apache.abdera.test.ext.features;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.features.AcceptSelector;
import org.apache.abdera.ext.features.Feature;
import org.apache.abdera.ext.features.FeatureSelector;
import org.apache.abdera.ext.features.FeaturesHelper;
import org.apache.abdera.ext.features.Selector;
import org.apache.abdera.ext.features.XPathSelector;
import org.apache.abdera.ext.features.Feature.Status;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;

public class FeatureTest extends TestCase {

  public static void testFeatures() throws Exception {
    Abdera abdera = new Abdera();
    Collection coll = abdera.getFactory().newCollection();
    FeaturesHelper.addFeature(
      coll, "http://example.com/features/foo", 
      Status.REQUIRED, null, "foo & here");
    FeaturesHelper.addFeature(
      coll, "http://example.com/features/bar", 
      null, null, null);
    FeaturesHelper.addFeature(
      coll, "http://example.com/features/baz",
      Status.UNSUPPORTED);
    
    assertEquals(Status.REQUIRED,FeaturesHelper.getFeatureStatus(
      coll, "http://example.com/features/foo"));
    assertEquals(Status.SUPPORTED, FeaturesHelper.getFeatureStatus(
      coll, "http://example.com/features/bar"));
    assertEquals(Status.UNSUPPORTED, FeaturesHelper.getFeatureStatus(
      coll, "http://example.com/features/baz"));
    assertEquals(Status.UNSPECIFIED,FeaturesHelper.getFeatureStatus(
      coll, "http://example.com/features/pez"));

  }
  
  public static void testSelectors() throws Exception {
    
    Abdera abdera = new Abdera();
    Service service = abdera.newService();
    Workspace workspace = service.addWorkspace("a");
    Collection collection1 = workspace.addCollection("a1","a1");
    collection1.setAcceptsEntry();
    FeaturesHelper.addFeature(collection1, FeaturesHelper.FEATURE_DRAFTS);
    Collection collection2 = workspace.addCollection("a2","a2");
    collection2.setAccept("image/*");
    
    Selector s1 = new FeatureSelector(FeaturesHelper.FEATURE_DRAFTS);
    
    Collection[] collections = FeaturesHelper.select(service, s1);
    
    assertEquals(1,collections.length);
    assertEquals(collections[0],collection1);
    
    Selector s2 = new AcceptSelector("image/png");
    
    collections = FeaturesHelper.select(service,s2);
    
    assertEquals(1,collections.length);
    assertEquals(collections[0],collection2);
    
    Selector s3 = new XPathSelector(
      "f:feature[@ref='http://www.w3.org/2007/app/drafts']");
    
    collections = FeaturesHelper.select(service,s3);
    
    for (Collection c : collections) System.out.println(c);
    
    assertEquals(1,collections.length);
    assertEquals(collections[0],collection1);
  }
  

  public static void testType() throws Exception {
     Abdera abdera = new Abdera();
     Feature feature = abdera.getFactory().newElement(FeaturesHelper.FEATURE);
     FeaturesHelper.addType(feature, "image/jpg","image/gif","image/png","image/*");
     String[] types = FeaturesHelper.getTypes(feature);
     assertEquals(1,types.length);
     assertEquals("image/*", types[0]);
  }
}
