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
package org.apache.abdera.ext.rss;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.DateTimeWrapper;
import org.apache.abdera.model.Element;

public class RssDateTime extends DateTimeWrapper implements DateTime {

    private static String[] masks =
        {"EEE, dd MMM yyyy HH:mm:ss z", "dd MMM yyyy HH:mm z", "dd MMM yyyy", "-yy-MM-dd", "-yy-MM", "-yymm",
         "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSz", "yyyy-MM-dd't'HH:mm:ss.SSSz", // invalid
         "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd't'HH:mm:ss.SSS'z'", // invalid
         "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd't'HH:mm:ssz", // invalid
         "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd't'HH:mm:ss'Z'", // invalid
         "yyyy-MM-dd't'HH:mm:ss'z'", // invalid
         "yyyy-MM-dd'T'HH:mm:ss'z'", // invalid
         "yyyy-MM-dd'T'HH:mm:ss'z'", // invalid
         "yyyy-MM-dd'T'HH:mmz", // invalid
         "yyyy-MM-dd't'HH:mmz", // invalid
         "yyyy-MM-dd'T'HH:mm'Z'", // invalid
         "yyyy-MM-dd't'HH:mm'z'", // invalid
         "yyyy-MM-dd", "yyyy-MM", "yyyyMMdd", "yyMMdd", "yyyy"};

    public RssDateTime(Element internal) {
        super(internal);
    }

    public RssDateTime(Factory factory, QName qname) {
        super(factory, qname);
    }

    @Override
    public AtomDate getValue() {
        return parse(getText());
    }

    @Override
    public DateTime setCalendar(Calendar date) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public DateTime setDate(Date date) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public DateTime setString(String date) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public DateTime setTime(long date) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public DateTime setValue(AtomDate dateTime) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    private AtomDate parse(String value) {
        for (String mask : masks) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(mask);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date d = sdf.parse(value);
                return new AtomDate(d);
            } catch (Exception e) {
            }
        }
        return null;
    }
}
