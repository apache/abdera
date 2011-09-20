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
package org.apache.abdera2.common.protocol;

import java.io.Serializable;
import java.util.Arrays;


public class BasicCollectionInfo 
  implements CollectionInfo, 
             Serializable {

    private static final long serialVersionUID = 8026455829158149510L;

    private final String title;
    private final String href;
    private final String[] accepts;

    public BasicCollectionInfo(String title, String href, String... accepts) {
        this.title = title;
        this.accepts = accepts;
        this.href = href;
    }

    public String[] getAccepts(RequestContext request) {
        return accepts;
    }

    public String getHref(RequestContext request) {
        return href;
    }

    public String getTitle(RequestContext request) {
        return title;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(accepts);
        result = prime * result + ((href == null) ? 0 : href.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BasicCollectionInfo other = (BasicCollectionInfo)obj;
        if (!Arrays.equals(accepts, other.accepts))
            return false;
        if (href == null) {
            if (other.href != null)
                return false;
        } else if (!href.equals(other.href))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

}
