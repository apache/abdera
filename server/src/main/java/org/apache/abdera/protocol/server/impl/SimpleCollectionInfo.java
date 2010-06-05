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
package org.apache.abdera.protocol.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.abdera.model.Collection;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;

public class SimpleCollectionInfo implements CollectionInfo, Serializable {

    private static final long serialVersionUID = 8026455829158149510L;

    private final String title;
    private final String href;
    private final String[] accepts;
    private final List<CategoriesInfo> catinfos = new ArrayList<CategoriesInfo>();

    public SimpleCollectionInfo(String title, String href, String... accepts) {
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

    public CategoriesInfo[] getCategoriesInfo(RequestContext request) {
        return catinfos.toArray(new CategoriesInfo[catinfos.size()]);
    }

    public void addCategoriesInfo(CategoriesInfo... catinfos) {
        for (CategoriesInfo catinfo : catinfos)
            this.catinfos.add(catinfo);
    }

    public void setCategoriesInfo(CategoriesInfo... catinfos) {
        this.catinfos.clear();
        addCategoriesInfo(catinfos);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(accepts);
        result = prime * result + ((catinfos == null) ? 0 : catinfos.hashCode());
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
        final SimpleCollectionInfo other = (SimpleCollectionInfo)obj;
        if (!Arrays.equals(accepts, other.accepts))
            return false;
        if (catinfos == null) {
            if (other.catinfos != null)
                return false;
        } else if (!catinfos.equals(other.catinfos))
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

    public Collection asCollectionElement(RequestContext request) {
        Collection collection = request.getAbdera().getFactory().newCollection();
        collection.setHref(href);
        collection.setTitle(title);
        collection.setAccept(accepts);
        for (CategoriesInfo catsinfo : this.catinfos) {
            collection.addCategories(catsinfo.asCategoriesElement(request));
        }
        return collection;
    }
}
