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
package org.apache.abdera.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Link;


public final class ThreadHelper {

  ThreadHelper() {}
  
  public static int getCount(Link link) {
    String val = link.getAttributeValue(Constants.THRCOUNT);
    return (val != null) ? Integer.parseInt(val) : 0;
  }
  
  @SuppressWarnings("deprecation")
  public static AtomDate getUpdated(Link link) {
    String val = link.getAttributeValue(Constants.THRUPDATED);
    if (val == null) // thr:when was updated to thr:updated, some old impls may still be using thr:when
      val = link.getAttributeValue(Constants.THRWHEN);
    return (val != null) ? AtomDate.valueOf(val) : null;
  }
  
  public void setCount(Link link, int count) {
    link.setAttributeValue(Constants.THRCOUNT, String.valueOf(count).trim());
  }
  
  public void setUpdated(Link link, Date when) {
    link.setAttributeValue(Constants.THRUPDATED, AtomDate.valueOf(when).getValue());
  }
  
  public void setUpdated(Link link, Calendar when) {
    link.setAttributeValue(Constants.THRUPDATED, AtomDate.valueOf(when).getValue());
  }
  
  public void setUpdated(Link link, long when) {
    link.setAttributeValue(Constants.THRUPDATED, AtomDate.valueOf(when).getValue());
  }
  
  public void setUpdated(Link link, String when) {
    link.setAttributeValue(Constants.THRUPDATED, AtomDate.valueOf(when).getValue());
  }
}
