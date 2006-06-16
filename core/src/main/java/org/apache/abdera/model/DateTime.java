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
package org.apache.abdera.model;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>An element conforming to the Atom Date Construct.  The data type 
 * implementation for this element is provided by the AtomDate class.</p>
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface DateTime 
  extends  ExtensionElement {
  
  /**
   * Returns the content value of the element as an AtomDate object 
   */
  AtomDate getValue();
  
  /**
   * Returns the content value of the element as a java.util.Date object
   */
  Date getDate();

  /**
   * Returns the content value of the element as a java.util.Calendar object
   */
  Calendar getCalendar();
  
  /**
   * Returns the content value of the element as a long (equivalent to 
   * calling DateTimeElement().getDate().getTime()
   */
  long getTime();
  
  /**
   * Returns the content value of the element as a string conforming to 
   * RFC-3339
   */
  String getString();
  
  /**
   * Sets the content value of the element
   */
  void setValue(AtomDate dateTime);
  
  /**
   * Sets the content value of the element
   */
  void setDate(Date date);
  
  /**
   * Sets the content value of the element
   */
  void setCalendar(Calendar date);
  
  /**
   * Sets the content value of the element
   */
  void setTime(long date);
  
  /**
   * Sets the content value of the element
   */
  void setString(String date);
}
