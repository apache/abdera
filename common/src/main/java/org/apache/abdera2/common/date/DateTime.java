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
package org.apache.abdera2.common.date;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * Provides an implementation of the RFC 3339 date-time.
 * </p>
 * <p>
 * Accessors on this class are not synchronized.
 * </p>
 */
public final class DateTime implements Cloneable, Serializable {

    private static final long serialVersionUID = 3491429176989953135L;
    private final Date value;

    /**
     * Create an AtomDate using the current date and time
     */
    public DateTime() {
        this(new Date());
    }

    /**
     * Create an AtomDate using the serialized string format (e.g. 2003-12-13T18:30:02Z).
     * 
     * @param value The serialized RFC3339 date/time value
     */
    public DateTime(String value) {
        this(parse(value));
    }

    /**
     * Create an AtomDate using a java.util.Date
     * 
     * @param value The java.util.Date value
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public DateTime(Date value) {
        this.value = (Date)value.clone();
    }

    /**
     * Create an AtomDate using a java.util.Calendar.
     * 
     * @param value The java.util.Calendar value
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public DateTime(Calendar value) {
        this(value.getTime());
    }

    /**
     * Create an AtomDate using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public DateTime(long value) {
        this(new Date(value));
    }

    /**
     * Return the serialized string form of the Atom date
     * 
     * @return the serialized string form of the date as specified by RFC4287
     */
    public String getValue() {
        return format(value);
    }

  /**
     * Returns the value of this Atom Date
     * 
     * @return A java.util.Date representing this Atom Date
     */
    public Date getDate() {
      // copy it in case the user decides to change the value since 
      // date objects themselves are not immutable
        return (Date)value.clone();
    }

    /**
     * Returns the value of this Atom Date as a java.util.Calendar
     * 
     * @return A java.util.Calendar representing this Atom Date
     */
    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        return cal;
    }

    /**
     * Returns the value of this Atom Date as the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getTime() {
        return value.getTime();
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean answer = false;
        if (obj instanceof Date) {
            Date d = (Date)obj;
            answer = (this.value.equals(d));
        } else if (obj instanceof String) {
            Date d = parse((String)obj);
            answer = (this.value.equals(d));
        } else if (obj instanceof Calendar) {
            Calendar c = (Calendar)obj;
            answer = (this.value.equals(c.getTime()));
        } else if (obj instanceof DateTime) {
            Date d = ((DateTime)obj).value;
            answer = (this.value.equals(d));
        }
        return answer;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private static boolean sep(char c) {
      return "-:.TtZz+".indexOf(c) > -1;
    }
    
    public static Date parse(String date) {
      if (date == null || date.length() == 0)
        throw new IllegalArgumentException();
      int nexttoken = 1;
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND,0);
      cal.set(Calendar.MINUTE,0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.DATE,1);
      cal.set(Calendar.MONTH,0);
      cal.set(Calendar.YEAR,0);
      for (int s = 0, r = 0; r < date.length(); r++) {
        char c = date.charAt(r);
        if (sep(c) || r+1==date.length()) {
          if (nexttoken == -1) {
            String sub = date.substring(s-1);
            TimeZone tz = TimeZone.getTimeZone("GMT"+sub);
            cal.setTimeZone(tz);
            break;
          } else {
            String sub = date.substring(s,r);
            s = r+1;
            int val = Integer.parseInt(sub);
            switch(nexttoken) {
            case 1:
              cal.set(Calendar.YEAR, val);
              nexttoken = c == '-' ? 2 : c == 'T' ? 8 : c == '.' ? 64 : -1;
              break;
            case 2:
              cal.set(Calendar.MONTH, val-1);
              nexttoken = c == '-' ? 4 : c == 'T' ? 8 : c == '.' ? 64 : -1;
              break;
            case 4:
              cal.set(Calendar.DATE, val);
              nexttoken = c == 'T' || c == 't' ? 8 : -1;
              break;
            case 8:
              cal.set(Calendar.HOUR_OF_DAY, val);
              nexttoken = c == ':' ? 16 : c == '.' ? 64 : -1;
              break;
            case 16:
              cal.set(Calendar.MINUTE, val);
              nexttoken = c == ':' ? 32 : c == '.' ? 64 : -1;
              break;
            case 32:
              cal.set(Calendar.SECOND, val); 
              nexttoken = c == '.' ? 64 : -1;
              break;
            case 64:
              cal.set(Calendar.MILLISECOND, val);
              nexttoken = -1;
              break;
            }
          }
        }
      }
      return cal.getTime();
    }

    /**
     * Create the serialized string form from a java.util.Date
     * 
     * @param d A java.util.Date
     * @return The serialized string form of the date
     */
    public static String format(Date date) {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      c.setTime(date);
      return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03dZ",
          c.get(Calendar.YEAR),
          c.get(Calendar.MONTH)+1,
          c.get(Calendar.DATE),
          c.get(Calendar.HOUR_OF_DAY),
          c.get(Calendar.MINUTE), 
          c.get(Calendar.SECOND),
          c.get(Calendar.MILLISECOND));
    }
    
    public static String formatNow() {
      return format(new Date());
    }

    /**
     * Create a new Atom Date instance from the serialized string form
     * 
     * @param value The serialized string form of the date
     * @return The created AtomDate
     */
    public static DateTime valueOf(String value) {
        return new DateTime(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Date
     * 
     * @param value a java.util.Date
     * @return The created AtomDate
     */
    public static DateTime valueOf(Date value) {
        return new DateTime(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Calendar
     * 
     * @param value A java.util.Calendar
     * @return The created AtomDate
     */
    public static DateTime valueOf(Calendar value) {
        return new DateTime(value);
    }

    /**
     * Create a new Atom Date instance using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @return The created AtomDate
     */
    public static DateTime valueOf(long value) {
        return new DateTime(value);
    }
}
