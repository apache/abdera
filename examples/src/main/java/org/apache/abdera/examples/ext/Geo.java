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
import org.apache.abdera.ext.geo.Coordinate;
import org.apache.abdera.ext.geo.GeoHelper;
import org.apache.abdera.ext.geo.Point;
import org.apache.abdera.ext.geo.Position;
import org.apache.abdera.model.Entry;

/**
 * The Geo extensions allow Atom entries to be geotagged using the Georss extensions.
 */
public class Geo {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setTitle("Middle of the Ocean");

        Point point = new Point(new Coordinate(37.0625, -95.677068));
        GeoHelper.addPosition(entry, point);

        Position[] positions = GeoHelper.getPositions(entry);
        for (Position pos : positions) {
            if (pos instanceof Point) {
                Point p = (Point)pos;
                System.out.println(p.getCoordinate());
            }
        }

        // By default, positions are encoded using the simple georss encoding,
        // W3C and GML encodings are also supported
        GeoHelper.addPosition(entry, point, GeoHelper.Encoding.W3C);
        GeoHelper.addPosition(entry, point, GeoHelper.Encoding.GML);
    }

}
