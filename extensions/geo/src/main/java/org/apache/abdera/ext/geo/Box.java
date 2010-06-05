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

public class Box extends Multiple {

    private static final String TWO_COORDINATES = "A box must have two coordinates";
    private static final long serialVersionUID = 3994252648307511152L;

    public Box() {
        super();
    }

    public Box(Multiple multiple) {
        super(multiple);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(Point lowerCorner, Point upperCorner) {
        super(lowerCorner, upperCorner);
    }

    public Box(Coordinate... coordinates) {
        super(coordinates);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(Coordinates coordinates) {
        super(coordinates);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(String value) {
        super(value);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(Multiple... multiples) {
        super(multiples);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(Point... points) {
        super(points);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Box(double... values) {
        super(values);
        if (this.coordinates.size() != 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        super.setCoordinates(coordinates);
        if (this.coordinates.size() > 2)
            throw new IllegalArgumentException(TWO_COORDINATES);
    }

    public Coordinate getUpperCorner() {
        return coordinates.size() > 1 ? coordinates.get(1) : null;
    }

    public Coordinate getLowerCorner() {
        return coordinates.size() > 0 ? coordinates.get(0) : null;
    }
}
