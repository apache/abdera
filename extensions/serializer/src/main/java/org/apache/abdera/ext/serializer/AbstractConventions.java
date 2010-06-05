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
package org.apache.abdera.ext.serializer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.abdera.ext.serializer.annotation.Convention;

public abstract class AbstractConventions implements Conventions, Cloneable, Serializable {

    private final Map<Class<? extends Annotation>, Pattern> conventions =
        new HashMap<Class<? extends Annotation>, Pattern>();
    private final boolean isCaseSensitive;

    protected AbstractConventions() {
        this(false);
    }

    protected AbstractConventions(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public Iterator<Class<? extends Annotation>> iterator() {
        return conventions.keySet().iterator();
    }

    public void setConvention(String pattern, Class<? extends Annotation> annotationType) {
        Pattern regex = isCaseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        conventions.put(annotationType, regex);
    }

    public void setConvention(Class<? extends Annotation> annotationType) {
        Convention conv = annotationType.getAnnotation(Convention.class);
        if (conv == null)
            throw new IllegalArgumentException("No Convention Annotation [" + annotationType + "]");
        setConvention(conv.value(), annotationType);
    }

    public Class<? extends Annotation> matchConvention(AccessibleObject accessor) {
        return matchConvention(accessor, null);
    }

    public Class<? extends Annotation> matchConvention(AccessibleObject accessor, Class<? extends Annotation> expect) {
        if (accessor == null)
            return null;
        String name = ((Member)accessor).getName();
        for (Map.Entry<Class<? extends Annotation>, Pattern> entry : conventions.entrySet()) {
            if (entry.getValue().matcher(name).matches() && (expect == null || expect == entry.getKey()))
                return entry.getKey();
        }
        return null;
    }

    public boolean isMatch(AccessibleObject accessor, Class<? extends Annotation> expect) {
        return matchConvention(accessor, expect) != null;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((conventions == null) ? 0 : conventions.hashCode());
        result = PRIME * result + (isCaseSensitive ? 1231 : 1237);
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
        final AbstractConventions other = (AbstractConventions)obj;
        if (conventions == null) {
            if (other.conventions != null)
                return false;
        } else if (!conventions.equals(other.conventions))
            return false;
        if (isCaseSensitive != other.isCaseSensitive)
            return false;
        return true;
    }

    public Conventions clone() {
        try {
            return (Conventions)super.clone();
        } catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    protected Conventions copy() {
        throw new SerializationException(new CloneNotSupportedException());
    }
}
