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
 * <p>A simple compound parse filter that allows us to apply multiple 
 * parse filters to a single parse operation.</p>
 * 
 * <pre>
 * CompoundParseFilter filter = 
 *   new CompoundParseFilter(
 *     CompoundParseFilter.Condition.ACCEPTABLE_TO_ALL,
 *     new SafeContentWhiteListParseFilter(),
 *     new MyWhiteListParseFilter(),
 *     new MySomeOtherKindOfParseFilter()
 *   );
 * options.setParseFilter(filter);
 * </pre>
 */
public class CompoundParseFilter 
  extends ParseFilter {

  public enum Condition {
    ACCEPTABLE_TO_ALL, 
    ACCEPTABLE_TO_ANY, 
    UNACCEPTABLE_TO_ALL,
    UNACCEPTABLE_TO_ANY,
  };
  
  private Condition condition = Condition.ACCEPTABLE_TO_ANY;
  private static final ParseFilter[] empty = new ParseFilter[0];
  protected ParseFilter[] filters = null;
  
  public CompoundParseFilter(Condition condition, ParseFilter... filters) {
    this.filters = filters;
    this.condition = condition;
  }
  
  public CompoundParseFilter(ParseFilter... filters) {
    this.filters = filters;
  }
  
  private ParseFilter[] getFilters() {
    return (filters != null) ? filters : empty;
  }
  
  @Override
  public boolean acceptable(QName qname) {
    for (ParseFilter filter : getFilters()) {
      if (filter.acceptable(qname)) {
        switch(condition) {
          case ACCEPTABLE_TO_ANY:
            return true;
          case UNACCEPTABLE_TO_ALL:
            return false;
        }
      } else {
        switch(condition) {
          case ACCEPTABLE_TO_ALL:
            return false;
          case UNACCEPTABLE_TO_ANY:
            return true;
        }
      }
    }
    return true;
  }

  @Override
  public boolean acceptableAttribute(QName qname, QName attribute) {
    for (ParseFilter filter : getFilters()) {
      if (filter.acceptableAttribute(qname,attribute)) {
        switch(condition) {
          case ACCEPTABLE_TO_ANY:
            return true;
          case UNACCEPTABLE_TO_ALL:
            return false;
        }
      } else {
        switch(condition) {
          case ACCEPTABLE_TO_ALL:
            return false;
          case UNACCEPTABLE_TO_ANY:
            return true;
        }
      }
    }
    return true;
  }

  @Override
  public void add(QName qname) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAttribute(QName parent, QName attribute) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(QName qname) {
    for (ParseFilter filter : getFilters()) {
      if (filter.contains(qname)) return true;
    }
    return false;
  }

  @Override
  public boolean containsAttribute(QName qname, QName attribute) {
    for (ParseFilter filter : getFilters()) {
      if (filter.containsAttribute(qname,attribute)) return true;
    }
    return false;
  }

}
