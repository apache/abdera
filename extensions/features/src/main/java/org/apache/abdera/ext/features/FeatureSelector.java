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
package org.apache.abdera.ext.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.ext.features.FeaturesHelper.Status;
import org.apache.abdera.model.Collection;

public class FeatureSelector extends AbstractSelector implements Selector {

    private static final long serialVersionUID = -8943638085557912175L;
    private final List<String> features = new ArrayList<String>();

    public FeatureSelector(String... features) {
        for (String feature : features)
            this.features.add(feature);
    }

    public boolean select(Collection collection) {
        for (String feature : features) {
            Status status = FeaturesHelper.getFeatureStatus(collection, feature);
            if (status == Status.SPECIFIED)
                return true;
        }
        return false;
    }

    public String[] getFeatures() {
        return features.toArray(new String[features.size()]);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((features == null) ? 0 : features.hashCode());
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
        final FeatureSelector other = (FeatureSelector)obj;
        if (features == null) {
            if (other.features != null)
                return false;
        } else if (!features.equals(other.features))
            return false;
        return true;
    }

    @Override
    protected Selector copy() {
        return new FeatureSelector(getFeatures());
    }
}
