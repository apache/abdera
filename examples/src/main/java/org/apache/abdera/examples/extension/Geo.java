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
package org.apache.abdera.examples.extension;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;

/**
 * Simple example showing the use of the dynamic extension APIs
 */
public class Geo {

    private static final String GEO_NS = "http://www.postneo.com/icbm/";
    private static final String GEO_PFX = "icbm";
    private static final QName LAT = new QName(GEO_NS, "latitude", GEO_PFX);
    private static final QName LONG = new QName(GEO_NS, "longitude", GEO_PFX);

    public static void main(String[] args) throws Exception {

        Abdera abdera = new Abdera();
        Entry entry = abdera.getFactory().newEntry();

        // Set the extension value
        entry.addSimpleExtension(LAT, "39.02980");
        entry.addSimpleExtension(LONG, "-77.07929");

        // Get the extension value
        System.out.println(entry.getSimpleExtension(LAT));
        System.out.println(entry.getSimpleExtension(LONG));

    }

}
