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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Provides an implementation of the Atom Date Construct, which is itself a specialization of the RFC3339 date-time.
 * </p>
 * <p>
 * Accessors on this class are not synchronized.
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
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
 * </pre>
 */
public final class AtomDate implements Cloneable, Serializable {

    private static final long serialVersionUID = -7062139688635877771L;

    private Date value;

    /**
     * Create an AtomDate using the current date and time
     */
    public AtomDate() {
        this(new Date());
    }

    /**
     * Create an AtomDate using the serialized string format (e.g. 2003-12-13T18:30:02Z).
     * 
     * @param value The serialized RFC3339 date/time value
     */
    public AtomDate(String value) {
        this(parse(value));
    }

    /**
     * Create an AtomDate using a java.util.Date
     * 
     * @param value The java.util.Date value
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public AtomDate(Date value) {
        this.value = (Date)value.clone();
    }

    /**
     * Create an AtomDate using a java.util.Calendar.
     * 
     * @param value The java.util.Calendar value
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public AtomDate(Calendar value) {
        this(value.getTime());
    }

    /**
     * Create an AtomDate using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public AtomDate(long value) {
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
     * Sets the value of the Atom date using the serialized string form
     * 
     * @param value The serialized string form of the date
     */
    public AtomDate setValue(String value) {
        this.value = parse(value);
        return this;
    }

    /**
     * Sets the value of the Atom date using java.util.Date
     * 
     * @param date A java.util.Date
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public AtomDate setValue(Date date) {
        this.value = (Date)date.clone();
        return this;
    }

    /**
     * Sets the value of the Atom date using java.util.Calendar
     * 
     * @param calendar a java.util.Calendar
     */
    public AtomDate setValue(Calendar calendar) {
        this.value = calendar.getTime();
        return this;
    }

    /**
     * Sets the value of the Atom date using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @param timestamp The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public AtomDate setValue(long timestamp) {
        this.value = new Date(timestamp);
        return this;
    }

    /**
     * Returns the value of this Atom Date
     * 
     * @return A java.util.Date representing this Atom Date
     */
    public Date getDate() {
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
        } else if (obj instanceof AtomDate) {
            Date d = ((AtomDate)obj).value;
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

    private static final Pattern PATTERN =
        Pattern
            .compile("(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:([Tt])?(?:(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?(?:\\.(\\d{3}))?)?([Zz])?(?:([+-])(\\d{2}):(\\d{2}))?");

    /**
     * Parse the serialized string form into a java.util.Date
     * 
     * @param date The serialized string form of the date
     * @return The created java.util.Date
     */
    public static Date parse(String date) {
        Matcher m = PATTERN.matcher(date);
        if (m.find()) {
            if (m.group(4) == null)
                throw new IllegalArgumentException("Invalid Date Format");
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            int hoff = 0, moff = 0, doff = -1;
            if (m.group(10) != null) {
                doff = m.group(10).equals("-") ? 1 : -1;
                hoff = doff * (m.group(11) != null ? Integer.parseInt(m.group(11)) : 0);
                moff = doff * (m.group(12) != null ? Integer.parseInt(m.group(12)) : 0);
            }
            c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
            c.set(Calendar.MONTH, m.group(2) != null ? Integer.parseInt(m.group(2)) - 1 : 0);
            c.set(Calendar.DATE, m.group(3) != null ? Integer.parseInt(m.group(3)) : 1);
            c.set(Calendar.HOUR_OF_DAY, m.group(5) != null ? Integer.parseInt(m.group(5)) + hoff : 0);
            c.set(Calendar.MINUTE, m.group(6) != null ? Integer.parseInt(m.group(6)) + moff : 0);
            c.set(Calendar.SECOND, m.group(7) != null ? Integer.parseInt(m.group(7)) : 0);
            c.set(Calendar.MILLISECOND, m.group(8) != null ? Integer.parseInt(m.group(8)) : 0);
            return c.getTime();
        } else {
            throw new IllegalArgumentException("Invalid Date Format");
        }
    }

    /**
     * Create the serialized string form from a java.util.Date
     * 
     * @param d A java.util.Date
     * @return The serialized string form of the date
     */
    public static String format(Date date) {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(date);
        sb.append(c.get(Calendar.YEAR));
        sb.append('-');
        int f = c.get(Calendar.MONTH);
        if (f < 9)
            sb.append('0');
        sb.append(f + 1);
        sb.append('-');
        f = c.get(Calendar.DATE);
        if (f < 10)
            sb.append('0');
        sb.append(f);
        sb.append('T');
        f = c.get(Calendar.HOUR_OF_DAY);
        if (f < 10)
            sb.append('0');
        sb.append(f);
        sb.append(':');
        f = c.get(Calendar.MINUTE);
        if (f < 10)
            sb.append('0');
        sb.append(f);
        sb.append(':');
        f = c.get(Calendar.SECOND);
        if (f < 10)
            sb.append('0');
        sb.append(f);
        sb.append('.');
        f = c.get(Calendar.MILLISECOND);
        if (f < 100)
            sb.append('0');
        if (f < 10)
            sb.append('0');
        sb.append(f);
        sb.append('Z');
        return sb.toString();
    }

    /**
     * Create a new Atom Date instance from the serialized string form
     * 
     * @param value The serialized string form of the date
     * @return The created AtomDate
     */
    public static AtomDate valueOf(String value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Date
     * 
     * @param value a java.util.Date
     * @return The created AtomDate
     */
    public static AtomDate valueOf(Date value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance from a java.util.Calendar
     * 
     * @param value A java.util.Calendar
     * @return The created AtomDate
     */
    public static AtomDate valueOf(Calendar value) {
        return new AtomDate(value);
    }

    /**
     * Create a new Atom Date instance using the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * 
     * @param value The number of milliseconds since January 1, 1970, 00:00:00 GMT
     * @return The created AtomDate
     */
    public static AtomDate valueOf(long value) {
        return new AtomDate(value);
    }
}
