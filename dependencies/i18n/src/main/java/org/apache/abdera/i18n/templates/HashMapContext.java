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
package org.apache.abdera.i18n.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Context implementation based on a HashMap
 */
@SuppressWarnings("unchecked")
public final class HashMapContext extends HashMap<String, Object> implements Context {

    private static final long serialVersionUID = 2206000974505975049L;

    private boolean isiri = false;
    private boolean normalizing = false;

    public HashMapContext() {
    }

    public HashMapContext(Map<String, Object> map) {
        super(map);
    }

    public HashMapContext(Map<String, Object> map, boolean isiri) {
        super(map);
        this.isiri = isiri;
    }

    public <T> T resolve(String var) {
        return (T)get(var);
    }

    public boolean isIri() {
        return isiri;
    }

    public void setIri(boolean isiri) {
        this.isiri = isiri;
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isiri ? 1231 : 1237);
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
        final HashMapContext other = (HashMapContext)obj;
        if (isiri != other.isiri)
            return false;
        return true;
    }

    public boolean isNormalizing() {
        return normalizing;
    }

    public void setNormalizing(boolean normalizing) {
        this.normalizing = normalizing;
    }

}
