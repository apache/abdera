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

import java.util.Iterator;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;

public class SimpleTarget implements Target {

    protected final TargetType type;
    protected final RequestContext context;

    public SimpleTarget(TargetType type, RequestContext context) {
        this.type = type;
        this.context = context;
    }

    public String getIdentity() {
        return context.getUri().toString();
    }

    public String getParameter(String name) {
        return context.getParameter(name);
    }

    public String[] getParameterNames() {
        String[] pn = context.getParameterNames();
        return (pn != null) ? pn : new String[0];
    }

    public Iterator<String> iterator() {
        return java.util.Arrays.asList(getParameterNames()).iterator();
    }

    public TargetType getType() {
        return type;
    }

    public RequestContext getRequestContext() {
        return context;
    }

    public String toString() {
        return getType() + " - " + getIdentity();
    }

    public <T> T getMatcher() {
        return (T)null;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((context == null) ? 0 : context.hashCode());
        result = PRIME * result + ((type == null) ? 0 : type.hashCode());
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
        final SimpleTarget other = (SimpleTarget)obj;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
