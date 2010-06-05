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

public class Coordinate implements Serializable, Cloneable, Comparable<Coordinate> {

    private static final long serialVersionUID = -916272885213668761L;

    private double latitude = 0.0f;
    private double longitude = 0.0f;

    public Coordinate() {
    }

    public Coordinate(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public Coordinate(String value) {
        Coordinate c = parse(value);
        setLatitude(c.latitude);
        setLongitude(c.longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (Double.compare(longitude, 90.0d) > 0)
            throw new IllegalArgumentException("Latitude > 90.0 degrees");
        if (Double.compare(longitude, -90.0d) < 0)
            throw new IllegalArgumentException("Latitude < 90.0 degrees");
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (Double.compare(longitude, 180.0d) > 0)
            throw new IllegalArgumentException("Longitude > 180.0 degrees");
        if (Double.compare(longitude, -180.0d) < 0)
            throw new IllegalArgumentException("Longitude < 180.0 degrees");
        this.longitude = longitude;
    }

    public String toString() {
        return Double.toString(latitude) + " " + Double.toString(longitude);
    }

    public Coordinate clone() {
        try {
            return (Coordinate)super.clone();
        } catch (CloneNotSupportedException e) {
            return new Coordinate(latitude, longitude); // not going to happen
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = PRIME * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = PRIME * result + (int)(temp ^ (temp >>> 32));
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
        final Coordinate other = (Coordinate)obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        return true;
    }

    public static Coordinate parse(String value) {
        try {
            String[] points = value.trim().split("\\s+", 2);
            double latitude = Double.parseDouble(points[0].trim());
            double longitude = Double.parseDouble(points[1].trim());
            return new Coordinate(latitude, longitude);
        } catch (Throwable t) {
            throw new RuntimeException("Error parsing coordinate pair", t);
        }
    }

    public int compareTo(Coordinate o) {
        if (o == null || equals(o))
            return 0;
        int l1 = Double.compare(latitude, o.latitude);
        int l2 = Double.compare(longitude, o.longitude);
        if (l1 < 0)
            return -1;
        if (l1 == 0 && l2 < -1)
            return -1;
        if (l1 == 0 && l2 == 0)
            return 0;
        return 1;
    }

}
