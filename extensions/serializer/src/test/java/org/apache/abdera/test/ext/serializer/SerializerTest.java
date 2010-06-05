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
package org.apache.abdera.test.ext.serializer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.serializer.ConventionSerializationContext;
import org.apache.abdera.ext.serializer.annotation.Author;
import org.apache.abdera.ext.serializer.annotation.ID;
import org.apache.abdera.ext.serializer.annotation.Link;
import org.apache.abdera.ext.serializer.annotation.Published;
import org.apache.abdera.ext.serializer.annotation.Summary;
import org.apache.abdera.ext.serializer.annotation.Title;
import org.apache.abdera.ext.serializer.annotation.Updated;
import org.apache.abdera.ext.serializer.impl.EntrySerializer;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.writer.StreamWriter;
import org.junit.Test;

public class SerializerTest {

    static Date date_now = new Date();
    static Calendar cal_now = Calendar.getInstance();

    @Test
    public void testSimple() throws Exception {
        Abdera abdera = Abdera.getInstance();
        StreamWriter sw = abdera.newStreamWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sw.setOutputStream(out).setAutoIndent(true);
        ConventionSerializationContext c = new ConventionSerializationContext(sw);
        c.setSerializer(MyEntry.class, new EntrySerializer());
        sw.startDocument();
        c.serialize(new MyEntry());
        sw.endDocument();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        assertEquals("tag:example.org,2008:foo", entry.getId().toString());
        assertEquals("This is the title", entry.getTitle());
        assertEquals(date_now, entry.getUpdated());
        assertEquals(cal_now.getTime(), entry.getPublished());
        assertEquals("James", entry.getAuthor().getName());
        assertEquals("this is the summary", entry.getSummary());
        assertEquals("http://example.org/foo", entry.getAlternateLink().getResolvedHref().toString());
    }

    public static class MyEntry {
        public String getId() {
            return "tag:example.org,2008:foo";
        }

        public String getTitle() {
            return "This is the title";
        }

        public String getAuthor() {
            return "James";
        }

        public Date getUpdated() {
            return date_now;
        }

        public Calendar getPublished() {
            return cal_now;
        }

        public String getSummary() {
            return "this is the summary";
        }

        public String getLink() {
            return "http://example.org/foo";
        }
    }

    @Test
    public void testAnnotated() throws Exception {
        Abdera abdera = Abdera.getInstance();
        StreamWriter sw = abdera.newStreamWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sw.setOutputStream(out).setAutoIndent(true);
        ConventionSerializationContext c = new ConventionSerializationContext(sw);
        sw.startDocument();
        c.serialize(new MyAnnotatedEntry());
        sw.endDocument();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        assertEquals("tag:example.org,2008:foo", entry.getId().toString());
        assertEquals("This is the title", entry.getTitle());
        assertEquals(date_now, entry.getUpdated());
        assertEquals(date_now, entry.getPublished());
        assertEquals("James", entry.getAuthor().getName());
        assertEquals("this is the summary", entry.getSummary());
        assertEquals("http://example.org/foo", entry.getAlternateLink().getResolvedHref().toString());
    }

    @org.apache.abdera.ext.serializer.annotation.Entry
    public static class MyAnnotatedEntry {
        @ID
        public String getFoo() {
            return "tag:example.org,2008:foo";
        }

        @Title
        public String getBar() {
            return "This is the title";
        }

        @Author
        public String getBaz() {
            return "James";
        }

        @Updated
        @Published
        public Date getLastModified() {
            return date_now;
        }

        @Summary
        public String getText() {
            return "this is the summary";
        }

        @Link
        public String getUri() {
            return "http://example.org/foo";
        }
    }
}
