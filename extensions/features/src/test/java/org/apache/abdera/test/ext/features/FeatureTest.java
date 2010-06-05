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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.features.AcceptSelector;
import org.apache.abdera.ext.features.Feature;
import org.apache.abdera.ext.features.FeatureSelector;
import org.apache.abdera.ext.features.Features;
import org.apache.abdera.ext.features.FeaturesHelper;
import org.apache.abdera.ext.features.Selector;
import org.apache.abdera.ext.features.XPathSelector;
import org.apache.abdera.ext.features.FeaturesHelper.Status;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.junit.Test;

public class FeatureTest {

    @Test
    public void testFeaturesDocument() throws Exception {
        Abdera abdera = Abdera.getInstance();
        Features features = FeaturesHelper.newFeatures(abdera);
        assertNotNull(features);
        assertNotNull(features.getDocument());
        Document<Features> doc = features.getDocument();
        assertTrue(doc.getRoot() instanceof Features);
    }

    @Test
    public void testFeatures() throws Exception {
        Abdera abdera = Abdera.getInstance();
        Collection coll = abdera.getFactory().newCollection();
        Features features = FeaturesHelper.addFeaturesElement(coll);
        features.addFeature("http://example.com/features/foo", null, "foo & here");
        features.addFeature("http://example.com/features/bar", null, null);

        assertEquals(Status.SPECIFIED, FeaturesHelper.getFeatureStatus(coll, "http://example.com/features/foo"));
        assertEquals(Status.SPECIFIED, FeaturesHelper.getFeatureStatus(coll, "http://example.com/features/bar"));
        assertEquals(Status.UNSPECIFIED, FeaturesHelper.getFeatureStatus(coll, "http://example.com/features/baz"));
        assertEquals(Status.UNSPECIFIED, FeaturesHelper.getFeatureStatus(coll, "http://example.com/features/pez"));

    }

    @Test
    public void testSelectors() throws Exception {

        Abdera abdera = Abdera.getInstance();
        Service service = abdera.newService();
        Workspace workspace = service.addWorkspace("a");
        Collection collection1 = workspace.addCollection("a1", "a1");
        collection1.setAcceptsEntry();
        Features features = FeaturesHelper.addFeaturesElement(collection1);
        features.addFeature(FeaturesHelper.FEATURE_SUPPORTS_DRAFTS);
        Collection collection2 = workspace.addCollection("a2", "a2");
        collection2.setAccept("image/*");

        Selector s1 = new FeatureSelector(FeaturesHelper.FEATURE_SUPPORTS_DRAFTS);

        Collection[] collections = FeaturesHelper.select(service, s1);

        assertEquals(1, collections.length);
        assertEquals(collections[0], collection1);

        Selector s2 = new AcceptSelector("image/png");

        collections = FeaturesHelper.select(service, s2);

        assertEquals(1, collections.length);
        assertEquals(collections[0], collection2);

        Selector s3 = new XPathSelector("f:features/f:feature[@ref='" + FeaturesHelper.FEATURE_SUPPORTS_DRAFTS + "']");

        collections = FeaturesHelper.select(service, s3);

        assertEquals(1, collections.length);
        assertEquals(collections[0], collection1);
    }

    @Test
    public void testType() throws Exception {
        Abdera abdera = Abdera.getInstance();
        Feature feature = abdera.getFactory().newElement(FeaturesHelper.FEATURE);
        feature.addType("image/jpg", "image/gif", "image/png", "image/*");
        String[] types = feature.getTypes();
        assertEquals(1, types.length);
        assertEquals("image/*", types[0]);
    }
}
