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

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.license.LicenseHelper;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 * The Atom License extension is described in Experimental RFC4946 and provides a way of associating copyright licenses
 * with feeds and entries. This is useful when working with things like Creative Commons licenses.
 */
public class License {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();
        Entry entry = feed.addEntry();

        // Add a license to the feed
        LicenseHelper.addLicense(feed, "http://example.org/foo", "Foo");

        // does the feed have a license link?
        System.out.println(LicenseHelper.hasLicense(feed));

        // does the feed have a specific license link?
        System.out.println(LicenseHelper.hasLicense(feed, "http://example.org/foo"));

        // since the entry does not have a license, it inherits the feeds
        System.out.println(LicenseHelper.hasLicense(entry));
        System.out.println(LicenseHelper.hasLicense(entry, "http://example.org/foo"));

        // list the licenses
        List<Link> licenses = LicenseHelper.getLicense(entry);
        for (Link link : licenses) {
            System.out.println(link.getResolvedHref());
        }

        // Add an unspecified license to the entry
        LicenseHelper.addUnspecifiedLicense(entry);

        // now the entry does not inherit the feeds license
        System.out.println(LicenseHelper.hasLicense(entry, "http://example.org/foo"));

    }

}
