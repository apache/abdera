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

public class Point extends Position {

    private static final long serialVersionUID = 7540202474168797239L;

    private Coordinate coordinate;

    public Point() {
    }

    public Point(Point point) {
        this.coordinate = point.getCoordinate().clone();
    }

    public Point(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Point(double latitude, double longitude) {
        this.coordinate = new Coordinate(latitude, longitude);
    }

    public Point(String value) {
        this.coordinate = new Coordinate(value);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setCoordinate(double latitude, double longitude) {
        this.coordinate = new Coordinate(latitude, longitude);
    }

    public void setCoordinate(String value) {
        this.coordinate = new Coordinate(value);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((coordinate == null) ? 0 : coordinate.hashCode());
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
        final Point other = (Point)obj;
        if (coordinate == null) {
            if (other.coordinate != null)
                return false;
        } else if (!coordinate.equals(other.coordinate))
            return false;
        return true;
    }

    public int compareTo(Position o) {
        if (o == null || !(o instanceof Point) || equals(o))
            return 0;
        return coordinate.compareTo(((Point)o).coordinate);
    }

}
