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
package org.apache.abdera.ext.geo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Coordinates implements Iterable<Coordinate>, Serializable, Cloneable, Comparable<Coordinates> {

    private static final long serialVersionUID = 1640788106113796699L;
    protected List<Coordinate> coords = new ArrayList<Coordinate>();

    public Coordinates() {
    }

    public Coordinates(Coordinate... coordinates) {
        for (Coordinate coord : coordinates)
            add(coord);
    }

    public Coordinates(Coordinates coordinates) {
        coords.addAll(coordinates.coords);
    }

    public Coordinates(String value) {
        Coordinates cs = parse(value);
        this.coords = cs.coords;
    }

    public synchronized Coordinate get(int n) {
        return coords.get(n);
    }

    public synchronized void add(Coordinates coordinates) {
        coords.addAll(coordinates.coords);
    }

    public synchronized void add(Coordinate coordinate) {
        coords.add(coordinate);
    }

    public synchronized void add(Coordinate... coordinates) {
        for (Coordinate c : coordinates)
            add(c);
    }

    public synchronized void add(double latitude, double longitude) {
        coords.add(new Coordinate(latitude, longitude));
    }

    public synchronized void add(String value) {
        coords.add(new Coordinate(value));
    }

    public synchronized void remove(Coordinate coordinate) {
        if (coords.contains(coordinate))
            coords.remove(coordinate);
    }

    public synchronized void remove(Coordinate... coordinates) {
        for (Coordinate c : coordinates)
            remove(c);
    }

    public synchronized void remove(double latitude, double longitude) {
        remove(new Coordinate(latitude, longitude));
    }

    public synchronized void remove(String value) {
        remove(new Coordinate(value));
    }

    public synchronized boolean contains(double latitude, double longitude) {
        return contains(new Coordinate(latitude, longitude));
    }

    public synchronized boolean contains(String value) {
        return contains(new Coordinate(value));
    }

    public synchronized boolean contains(Coordinate coordinate) {
        return coords.contains(coordinate);
    }

    public synchronized boolean contains(Coordinate... coordinates) {
        for (Coordinate c : coordinates)
            if (!coords.contains(c))
                return false;
        return true;
    }

    public Iterator<Coordinate> iterator() {
        return coords.iterator();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Coordinate coord : coords) {
            if (buf.length() > 0)
                buf.append(" ");
            buf.append(coord);
        }
        return buf.toString();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((coords == null) ? 0 : coords.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Coordinates other = (Coordinates)obj;
        if (coords == null) {
            if (other.coords != null)
                return false;
        } else if (!coords.equals(other.coords))
            return false;
        return true;
    }

    public static Coordinates parse(String value) {
        Coordinates cs = new Coordinates();
        try {
            String[] points = value.trim().split("\\s+");
            for (int n = 0; n < points.length; n = n + 2) {
                double lat = Double.parseDouble(points[n]);
                double lon = Double.parseDouble(points[n + 1]);
                Coordinate c = new Coordinate(lat, lon);
                cs.add(c);
            }
            return cs;
        } catch (Throwable t) {
            throw new RuntimeException("Error parsing coordinate pairs", t);
        }
    }

    public int size() {
        return coords.size();
    }

    public void clear() {
        coords.clear();
    }

    public Coordinates clone() {
        try {
            return (Coordinates)super.clone();
        } catch (CloneNotSupportedException e) {
            return new Coordinates(this); // Not going to happen
        }
    }

    public Coordinates sort() {
        return sort(false);
    }

    public Coordinates sort(boolean reverse) {
        Coordinates c = clone();
        if (reverse)
            Collections.sort(c.coords, Collections.reverseOrder());
        else
            Collections.sort(c.coords);
        return c;
    }

    public int compareTo(Coordinates o) {
        if (o == null || equals(o))
            return 0;
        if (size() < o.size())
            return -1;
        if (o.size() < size())
            return 1;
        for (int n = 0; n < size(); n++) {
            Coordinate c1 = coords.get(n);
            Coordinate c2 = o.coords.get(n);
            int c = c1.compareTo(c2);
            if (c != 0)
                return c;
        }
        return 0;
    }

}
