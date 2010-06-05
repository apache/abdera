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
package org.apache.abdera.util.filter;

import org.apache.abdera.filter.ParseFilter;

public abstract class AbstractParseFilter implements ParseFilter {

    private static final long serialVersionUID = -1866308276050148524L;

    private static final byte COMMENTS = 1;
    private static final byte WHITESPACE = 2;
    private static final byte PI = 4;

    protected byte flags = 0;

    private void toggle(boolean s, byte flag) {
        if (s)
            flags |= flag;
        else
            flags &= ~flag;
    }

    private boolean check(byte flag) {
        return (flags & flag) == flag;
    }

    public ParseFilter setIgnoreComments(boolean ignore) {
        toggle(ignore, COMMENTS);
        return this;
    }

    public ParseFilter setIgnoreWhitespace(boolean ignore) {
        toggle(ignore, (byte)WHITESPACE);
        return this;
    }

    public ParseFilter setIgnoreProcessingInstructions(boolean ignore) {
        toggle(ignore, (byte)PI);
        return this;
    }

    public boolean getIgnoreComments() {
        return check(COMMENTS);
    }

    public boolean getIgnoreProcessingInstructions() {
        return check(PI);
    }

    public boolean getIgnoreWhitespace() {
        return check(WHITESPACE);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
