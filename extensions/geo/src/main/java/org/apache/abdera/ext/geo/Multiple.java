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

import java.util.Iterator;

public abstract class Multiple extends Position implements Iterable<Coordinate> {

    protected Coordinates coordinates;

    public Multiple() {
    }

    public Multiple(Multiple... multiples) {
        this.coordinates = new Coordinates();
        for (Multiple m : multiples)
            coordinates.add(m.getCoordinates());
    }

    public Multiple(Multiple multiple) {
        this(multiple.getCoordinates().clone());
    }

    public Multiple(Point point) {
        this(point.getCoordinate().clone());
    }

    public Multiple(Point... points) {
        this.coordinates = new Coordinates();
        for (Point p : points)
            coordinates.add(p.getCoordinate());
    }

    public Multiple(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Multiple(Coordinate... coordinates) {
        this.coordinates = new Coordinates(coordinates);
    }

    public Multiple(String value) {
        this.coordinates = new Coordinates(value);
    }

    public Multiple(double... values) {
        this.coordinates = new Coordinates();
        for (int n = 0; n < values.length; n = n + 2) {
            Coordinate c = new Coordinate(values[n], values[n + 1]);
            this.coordinates.add(c);
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Iterator<Coordinate> iterator() {
        return coordinates.iterator();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((coordinates == null) ? 0 : coordinates.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Multiple other = (Multiple)obj;
        if (coordinates == null) {
            if (other.coordinates != null)
                return false;
        } else if (!coordinates.equals(other.coordinates))
            return false;
        return true;
    }

    public int compareTo(Position o) {
        if (o == null || !this.getClass().isInstance(o) || equals(o))
            return 0;
        return coordinates.compareTo(((Multiple)o).coordinates);
    }

    protected void verify179Rule() {
        for (Coordinate c1 : getCoordinates()) {
            for (Coordinate c2 : getCoordinates()) {
                check179(c1.getLatitude(), c2.getLatitude());
                check179(c1.getLongitude(), c2.getLongitude());
            }
        }
    }

    private void check179(double d1, double d2) {
        if (Math.abs(Math.max(d1, d2)) - Math.abs(Math.min(d1, d2)) > 179)
            throw new RuntimeException("Values are greater than 179 degrees");
    }
}
