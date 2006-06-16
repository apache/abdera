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

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Provides an implementation of the Atom Date Construct, 
 * which is itself a specialization of the RFC3339 date-time.</p>
 * 
 * <p>Per RFC4287:</p>
 * 
 * <pre>
 *  3.3.  Date Constructs
 *
 *  A Date construct is an element whose content MUST conform to the
 *  "date-time" production in [RFC3339].  In addition, an uppercase "T"
 *  character MUST be used to separate date and time, and an uppercase
 *  "Z" character MUST be present in the absence of a numeric time zone
 *  offset.
 *
 *  atomDateConstruct =
 *     atomCommonAttributes,
 *     xsd:dateTime
 *
 *  Such date values happen to be compatible with the following
 *  specifications: [ISO.8601.1988], [W3C.NOTE-datetime-19980827], and
 *  [W3C.REC-xmlschema-2-20041028].
 *
 *  Example Date constructs:
 *
 *  &lt;updated>2003-12-13T18:30:02Z&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02.25Z&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02+01:00&lt;/updated>
 *  &lt;updated>2003-12-13T18:30:02.25+01:00&lt;/updated>
 *
 *  Date values SHOULD be as accurate as possible.  For example, it would
 *  be generally inappropriate for a publishing system to apply the same
 *  timestamp to several entries that were published during the course of
 *  a single day.
 *  </pre>
 *  
 * @author James M Snell (jasnell@us.ibm.com)
 */
public final class AtomDate {

  protected Date value = null;
  
  public AtomDate() {}
  
  public AtomDate(String value) { 
    this(parse(value));
  }
  
  public AtomDate(Date value) {
    this.value = value;
  }
  
  public AtomDate(Calendar value) {
    this(value.getTime());
  }
  
  public AtomDate(long value) {
    this(new Date(value));
  }
  
  public String getValue() {
    return format(value);
  }
  
  public void setValue(String value) {
    this.value = parse(value);
  }

  public void setValue(Date date) {
    this.value = date;
  }

  public void setValue(Calendar calendar) {
    this.value = calendar.getTime();
  }
  
  public void setValue(long timestamp) {
    this.value = new Date(timestamp);
  }
  
  public Date getDate() {
    return value;
  }
  
  public Calendar getCalendar() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(value);
    return cal;
  }
  
  public long getTime() {
    return value.getTime();
  }
  
  public String toString() {
    return getValue();
  }
  
  public boolean equals(Object obj) {
    boolean answer = false;
    if (obj instanceof Date) {
      Date d = (Date) obj;
      answer = (this.value.equals(d));
    } else if (obj instanceof String) {
      Date d = parse((String) obj);
      answer = (this.value.equals(d));
    } else if (obj instanceof Calendar) {
      Calendar c = (Calendar) obj;
      answer = (this.value.equals(c.getTime()));
    } else if (obj instanceof AtomDate) {
      Date d = ((AtomDate)obj).value;
      answer = (this.value.equals(d));
    }
    return answer;
  }
  
  private static final String[] masks = {
    "yyyy-MM-dd'T'HH:mm:ss.SSz",
    "yyyy-MM-dd't'HH:mm:ss.SSz",                         // invalid
    "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
    "yyyy-MM-dd't'HH:mm:ss.SS'z'",                       // invalid
    "yyyy-MM-dd'T'HH:mm:ssz",
    "yyyy-MM-dd't'HH:mm:ssz",                            // invalid
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd't'HH:mm:ss'z'",                          // invalid
    "yyyy-MM-dd'T'HH:mmz",                               // invalid
    "yyyy-MM-dd't'HH:mmz",                               // invalid
    "yyyy-MM-dd'T'HH:mm'Z'",                             // invalid
    "yyyy-MM-dd't'HH:mm'z'",                             // invalid
    "yyyy-MM-dd",
    "yyyy-MM",
    "yyyy"
  };
   
  public static Date parse(String date) {
    Date d = null;
    SimpleDateFormat sdf = new SimpleDateFormat();
    for (int n = 0; n < masks.length; n++) {
      try {
        sdf.applyPattern(masks[n]);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf.setLenient(true);
        d = sdf.parse(date, new ParsePosition(0));
        if (d != null) break;
      } catch (Exception e) {}
    }
    if (d == null) 
      throw new IllegalArgumentException();
    return d;
  }
  
  public static String format (Date d) {
    StringBuffer iso8601 = new StringBuffer();
    SimpleDateFormat sdf = new SimpleDateFormat(masks[2]);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdf.format(d, iso8601, new FieldPosition(0));
    return iso8601.toString();
  }

  public static AtomDate valueOf(String value) {
    return new AtomDate(value);
  }
  
  public static AtomDate valueOf(Date value) {
    return new AtomDate(value);
  }
  
  public static AtomDate valueOf(Calendar value) {
    return new AtomDate(value);
  }
  
  public static AtomDate valueOf(long value) {
    return new AtomDate(value);
  }
}
