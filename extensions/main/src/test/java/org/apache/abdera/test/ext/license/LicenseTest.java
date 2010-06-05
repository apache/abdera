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
package org.apache.abdera.test.ext.license;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.license.LicenseHelper;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Test;

public class LicenseTest {

    @Test
    public void testLicense() throws Exception {

        String license = "http://example.org";

        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        Entry entry = feed.addEntry();

        LicenseHelper.addLicense(feed, license);

        assertTrue(LicenseHelper.hasLicense(feed, license));
        assertFalse(LicenseHelper.hasLicense(entry, license, false));
        assertTrue(LicenseHelper.hasLicense(entry, license, true));

        assertNotNull(LicenseHelper.addUnspecifiedLicense(entry));

        assertFalse(LicenseHelper.hasLicense(entry, license, true));

        entry = abdera.newEntry();
        entry.setSource(feed.getAsSource());

        assertFalse(LicenseHelper.hasLicense(entry, license, false));
        assertTrue(LicenseHelper.hasLicense(entry, license, true));

        boolean died = false;
        entry = abdera.newEntry();
        LicenseHelper.addLicense(entry, license);
        try {
            // will die because the license already exists
            LicenseHelper.addLicense(entry, license);
        } catch (IllegalStateException e) {
            died = true;
        }
        assertTrue(died);

        died = false;
        try {
            // will die because another license already exists
            LicenseHelper.addUnspecifiedLicense(entry);
        } catch (IllegalStateException e) {
            died = true;
        }
        assertTrue(died);

        died = false;
        entry = abdera.newEntry();
        LicenseHelper.addUnspecifiedLicense(entry);
        try {
            // will die because the unspecified license already exists
            LicenseHelper.addLicense(entry, license);
        } catch (IllegalStateException e) {
            died = true;
        }
        assertTrue(died);

        died = false;
        entry = abdera.newEntry();
        LicenseHelper.addUnspecifiedLicense(entry);
        try {
            // will die because the unspecified license already exists
            LicenseHelper.addUnspecifiedLicense(entry);
        } catch (IllegalStateException e) {
            died = true;
        }
        assertTrue(died);

    }

}
