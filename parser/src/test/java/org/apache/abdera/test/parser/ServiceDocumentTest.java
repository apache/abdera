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
package org.apache.abdera.test.parser;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.FOMMultipartCollection;
import org.junit.Test;

public class ServiceDocumentTest {

    /**
     * Test whether the Service Document includes <accept> for collections.
     */
    @Test
    public void testCollectionAccepts() throws Exception {
        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();
        Service svc = factory.newService();
        Workspace ws = svc.addWorkspace("test-ws");
        Collection coll = ws.addCollection("test-coll", ws.getTitle() + "/test-coll");
        coll.setAcceptsEntry();
        assertTrue("Collection does not accept entries.", coll.acceptsEntry());
        coll.addAccepts("application/apples");
        assertTrue("Collection does not accept apples.", coll.accepts("application/apples"));
        StringWriter sw = new StringWriter();
        svc.writeTo(sw);
        // System.out.println(sw);
        String s = sw.toString();
        assertTrue("Service document does not specify acceptance of entries.", s
            .contains("application/atom+xml; type=entry"));
        assertTrue("Service document does not specify acceptance of apples.", s.contains("application/apples"));
    }

    /**
     * Test whether the <accept> element includes the multipart attribute.
     */
    @Test
    public void testCollectionAcceptsMultipart() throws Exception {
        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();
        Service svc = factory.newService();
        Workspace ws = svc.addWorkspace("test-ws");
        FOMMultipartCollection coll =
            (FOMMultipartCollection)ws.addMultipartCollection("test multipart coll", "/test-coll");
        coll.setAcceptsEntry();
        coll.addAccepts("image/*", "multipart-related");

        assertTrue("Collection does not accept entries.", coll.acceptsEntry());
        assertTrue("Collection does not accept multipart related images", coll.acceptsMultipart("image/*"));

        StringWriter sw = new StringWriter();
        svc.writeTo(sw);

        String s = sw.toString();
        assertTrue("Service document does not specify acceptance of entries.", s
            .contains("application/atom+xml; type=entry"));
        assertTrue("Service document does not specify acceptance of apples.", s.contains("image/*"));
    }
}
