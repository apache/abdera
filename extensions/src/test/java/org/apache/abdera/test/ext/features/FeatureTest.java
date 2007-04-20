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
import org.apache.abdera.ext.features.Feature;
import org.apache.abdera.ext.features.FeaturesHelper;
import org.apache.abdera.model.Collection;

public class FeatureTest extends TestCase {

  public static void testFeatures() throws Exception {
    Abdera abdera = new Abdera();
    Collection coll = abdera.getFactory().newCollection();
    FeaturesHelper.addFeature(
      coll, "http://example.com/features/foo", 
      true, null, "foo & here");
    FeaturesHelper.addFeature(
      coll, "http://example.com/features/bar", 
      false, null, null);
    assertTrue(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/foo"));
    assertTrue(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/bar"));
    assertTrue(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/foo",
            "http://example.com/features/bar"));
    assertFalse(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/foo",
            "http://example.com/features/pez"));
    assertFalse(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/pez",
            "http://example.com/features/foo"));
    assertFalse(FeaturesHelper.supportsFeature(
      coll, "http://example.com/features/pez"));
    Feature f = FeaturesHelper.getFeature(
      coll, "http://example.com/features/foo");
    assertTrue(f.isRequired());
  }
  
}
