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

public abstract class Position implements Serializable, Cloneable, Comparable<Position> {

    public static final String DEFAULT_FEATURE_TYPE_TAG = "location";
    public static final String DEFAULT_RELATIONSHIP_TAG = "is-located-at";

    protected String featureTypeTag;
    protected String relationshipTag;
    protected Double elevation;
    protected Double floor;
    protected Double radius;

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public String getFeatureTypeTag() {
        return featureTypeTag;
    }

    public void setFeatureTypeTag(String featureTypeTag) {
        this.featureTypeTag = featureTypeTag;
    }

    public Double getFloor() {
        return floor;
    }

    public void setFloor(Double floor) {
        this.floor = floor;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public String getRelationshipTag() {
        return relationshipTag;
    }

    public void setRelationshipTag(String relationshipTag) {
        this.relationshipTag = relationshipTag;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        // int result = super.hashCode();
        int result = this.getClass().hashCode();
        result = PRIME * result + ((elevation == null) ? 0 : elevation.hashCode());
        result =
            PRIME * result
                + ((featureTypeTag == null) ? DEFAULT_FEATURE_TYPE_TAG.hashCode() : featureTypeTag.hashCode());
        result = PRIME * result + ((floor == null) ? 0 : floor.hashCode());
        result = PRIME * result + ((radius == null) ? 0 : radius.hashCode());
        result =
            PRIME * result
                + ((relationshipTag == null) ? DEFAULT_RELATIONSHIP_TAG.hashCode() : relationshipTag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final Position other = (Position)obj;
        if (elevation == null) {
            if (other.elevation != null)
                return false;
        } else if (!elevation.equals(other.elevation))
            return false;
        if (featureTypeTag == null) {
            if (other.featureTypeTag != null && !other.featureTypeTag.equalsIgnoreCase(DEFAULT_FEATURE_TYPE_TAG))
                return false;
        } else {
            String s = other.featureTypeTag != null ? other.featureTypeTag : DEFAULT_FEATURE_TYPE_TAG;
            if (!featureTypeTag.equalsIgnoreCase(s))
                return false;
        }
        if (floor == null) {
            if (other.floor != null)
                return false;
        } else if (!floor.equals(other.floor))
            return false;
        if (radius == null) {
            if (other.radius != null)
                return false;
        } else if (!radius.equals(other.radius))
            return false;
        if (relationshipTag == null) {
            if (other.relationshipTag != null && !other.relationshipTag.equalsIgnoreCase(DEFAULT_RELATIONSHIP_TAG))
                return false;
        } else {
            String s = other.relationshipTag != null ? other.relationshipTag : DEFAULT_RELATIONSHIP_TAG;
            if (!relationshipTag.equalsIgnoreCase(s))
                return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Position> T clone() {
        try {
            return (T)super.clone();
        } catch (CloneNotSupportedException e) {
            return null; // should never happen
        }
    }
}
