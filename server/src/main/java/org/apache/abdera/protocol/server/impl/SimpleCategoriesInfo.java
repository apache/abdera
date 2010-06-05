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
import java.util.Iterator;
import java.util.List;

import org.apache.abdera.model.Categories;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.RequestContext;

public class SimpleCategoriesInfo implements CategoriesInfo, Serializable {

    private static final long serialVersionUID = 8732335394387909260L;

    private final String href;
    private final String scheme;
    private final boolean fixed;
    private final List<CategoryInfo> list = new ArrayList<CategoryInfo>();

    public SimpleCategoriesInfo() {
        this(null, false);
    }

    public SimpleCategoriesInfo(boolean fixed) {
        this(null, fixed);
    }

    public SimpleCategoriesInfo(String href) {
        this.href = href;
        this.scheme = null;
        this.fixed = false;
    }

    public SimpleCategoriesInfo(String scheme, boolean fixed, CategoryInfo... categories) {
        this.href = null;
        this.scheme = scheme;
        this.fixed = fixed;
        addCategoryInfo(categories);
    }

    public String getHref(RequestContext request) {
        return href;
    }

    public String getScheme(RequestContext request) {
        return scheme;
    }

    public boolean isFixed(RequestContext request) {
        return fixed;
    }

    public Iterator<CategoryInfo> iterator() {
        return list.iterator();
    }

    public SimpleCategoriesInfo addCategoryInfo(CategoryInfo... categories) {
        for (CategoryInfo cat : categories)
            list.add(cat);
        return this;
    }

    public SimpleCategoriesInfo setCategoryInfo(CategoryInfo... categories) {
        list.clear();
        return addCategoryInfo(categories);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (fixed ? 1231 : 1237);
        result = prime * result + ((href == null) ? 0 : href.hashCode());
        result = prime * result + ((list == null) ? 0 : list.hashCode());
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SimpleCategoriesInfo other = (SimpleCategoriesInfo)obj;
        if (fixed != other.fixed)
            return false;
        if (href == null) {
            if (other.href != null)
                return false;
        } else if (!href.equals(other.href))
            return false;
        if (list == null) {
            if (other.list != null)
                return false;
        } else if (!list.equals(other.list))
            return false;
        if (scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!scheme.equals(other.scheme))
            return false;
        return true;
    }

    public Categories asCategoriesElement(RequestContext request) {
        Categories cats = request.getAbdera().getFactory().newCategories();
        if (href != null)
            cats.setHref(href);
        else {
            cats.setFixed(fixed);
            cats.setScheme(scheme);
            for (CategoryInfo cat : this)
                cats.addCategory(cat.asCategoryElement(request));
        }
        return cats;
    }
}
