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

import javax.xml.namespace.QName;

import org.apache.abdera.filter.ParseFilter;

/**
 * <p>
 * A simple compound parse filter that allows us to apply multiple parse filters to a single parse operation.
 * </p>
 * 
 * <pre>
 * CompoundParseFilter filter =
 *     new CompoundParseFilter(CompoundParseFilter.Condition.ACCEPTABLE_TO_ALL, new SafeContentWhiteListParseFilter(),
 *                             new MyWhiteListParseFilter(), new MySomeOtherKindOfParseFilter());
 * options.setParseFilter(filter);
 * </pre>
 */
public class CompoundParseFilter extends AbstractParseFilter implements ParseFilter {

    private static final long serialVersionUID = -7871289035422204698L;

    public enum Condition {
        ACCEPTABLE_TO_ALL, ACCEPTABLE_TO_ANY, UNACCEPTABLE_TO_ALL, UNACCEPTABLE_TO_ANY;

        byte evaluate(boolean b) {
            if (b) {
                switch (this) {
                    case ACCEPTABLE_TO_ANY:
                        return 1;
                    case UNACCEPTABLE_TO_ALL:
                        return -1;
                }
            } else {
                switch (this) {
                    case ACCEPTABLE_TO_ALL:
                        return -1;
                    case UNACCEPTABLE_TO_ANY:
                        return 1;
                }
            }
            return 0;
        }
    };

    protected final Condition condition;
    protected final ParseFilter[] filters;

    public CompoundParseFilter(Condition condition, ParseFilter... filters) {
        this.filters = filters;
        this.condition = condition;
    }

    public CompoundParseFilter(ParseFilter... filters) {
        this(Condition.ACCEPTABLE_TO_ANY, filters);
    }

    private ParseFilter[] getFilters() {
        return filters;
    }

    public boolean acceptable(QName qname) {
        for (ParseFilter filter : getFilters()) {
            switch (condition.evaluate(filter.acceptable(qname))) {
                case 1:
                    return true;
                case -1:
                    return false;
            }
        }
        return true;
    }

    public boolean acceptable(QName qname, QName attribute) {
        for (ParseFilter filter : getFilters()) {
            switch (condition.evaluate(filter.acceptable(qname, attribute))) {
                case 1:
                    return true;
                case -1:
                    return false;
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
