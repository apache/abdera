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
package org.apache.abdera.test.parser.stax;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.junit.Test;

/**
 * Test possible concurrency issues.
 * 
 * @version $Id$
 */
public class ConcurrencyTest {

    private static final int N_THREADS = 100;

    /**
     * Test for a concurrency issue that caused a ConcurrentModificationException in Abdera 0.1.0 but seems to be fixed
     * in 0.2. We leave the test here to prevent possible regressions.
     */
    @Test
    public void testSetContentMT() throws Exception {
        Thread t[] = new Thread[N_THREADS];
        final boolean failed[] = new boolean[t.length];
        for (int i = 0; i < t.length; ++i) {
            final int j = i;
            failed[i] = false;
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        setContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                        failed[j] = true;
                        fail(e.toString());
                    }
                }
            };
            t[i] = new Thread(r);
            t[i].start();
        }
        for (int i = 0; i < t.length; ++i) {
            t[i].join();
            if (failed[i]) {
                fail("Thread " + t[i] + " failed.");
            }
        }
    }

    private void setContent() throws Exception {
        // For Abdera 0.1.0 this would be:
        // Parser parser = Factory.INSTANCE.newParser();
        Parser parser = Abdera.getNewParser();
        InputStream is = ParserTest.class.getResourceAsStream("/entry.xml");
        Document<Entry> doc = parser.parse(is);
        Entry entry = doc.getRoot();
        Content content = entry.getFactory().newContent(Content.Type.XML);
        content.setValue("<some><xml>document</xml></some>");
        content.setMimeType("application/xml");
        entry.setContentElement(content);
    }
}
