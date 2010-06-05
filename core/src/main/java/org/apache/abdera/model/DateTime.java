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
 * <p>
 * An element conforming to the Atom Date Construct. The data type implementation for this element is provided by the
 * AtomDate class.
 * </p>
 */
public interface DateTime extends Element {

    /**
     * Returns the content value of the element as an AtomDate object
     * 
     * @return The Atom Date value of this element
     */
    AtomDate getValue();

    /**
     * Returns the content value of the element as a java.util.Date object
     * 
     * @return The java.util.Date value of this element
     */
    Date getDate();

    /**
     * Returns the content value of the element as a java.util.Calendar object
     * 
     * @return The java.util.Calendar value of this element
     */
    Calendar getCalendar();

    /**
     * Returns the content value of the element as a long (equivalent to calling DateTimeElement().getDate().getTime()
     * 
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    long getTime();

    /**
     * Returns the content value of the element as a string conforming to RFC-3339
     * 
     * @return The serialized string form of this element
     */
    String getString();

    /**
     * Sets the content value of the element
     * 
     * @param dateTime the Atom Date value
     */
    DateTime setValue(AtomDate dateTime);

    /**
     * Sets the content value of the element
     * 
     * @param date The java.util.Date value
     */
    DateTime setDate(Date date);

    /**
     * Sets the content value of the element
     * 
     * @param date The java.util.Calendar value
     */
    DateTime setCalendar(Calendar date);

    /**
     * Sets the content value of the element
     * 
     * @param date the number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    DateTime setTime(long date);

    /**
     * Sets the content value of the element
     * 
     * @param date The serialized string value
     */
    DateTime setString(String date);
}
