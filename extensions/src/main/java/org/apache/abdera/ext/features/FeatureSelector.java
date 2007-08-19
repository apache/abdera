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

import org.apache.abdera.ext.features.Feature.Status;
import org.apache.abdera.model.Collection;

public class FeatureSelector
  extends AbstractSelector
  implements Selector {

  private static final long serialVersionUID = -8943638085557912175L;
  private final Status minimumStatus;
  private final List<String> features = new ArrayList<String>();
  
  public FeatureSelector(String... features) {
    this(Status.SUPPORTED,features);
  }
  
  public FeatureSelector(Status minimumStatus, String... features) {
    this.minimumStatus = minimumStatus;
    for (String feature : features) this.features.add(feature);
  }
  
  public boolean select(Collection collection) {
    for (String feature : features) {
      Status status = FeaturesHelper.getFeatureStatus(collection, feature);
      if (status != null && status.ordinal() >= minimumStatus.ordinal()) 
        return true;
    }
    return false;
  }

  public String[] getFeatures() {
    return features.toArray(new String[features.size()]);
  }

  public Status getMinimumStatus() {
    return minimumStatus;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((features == null) ? 0 : features.hashCode());
    result = PRIME * result + ((minimumStatus == null) ? 0 : minimumStatus.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final FeatureSelector other = (FeatureSelector) obj;
    if (features == null) {
      if (other.features != null) return false;
    } else if (!features.equals(other.features)) return false;
    if (minimumStatus == null) {
      if (other.minimumStatus != null) return false;
    } else if (!minimumStatus.equals(other.minimumStatus)) return false;
    return true;
  }

}
