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
package org.apache.abdera.test.ext.media;

import static org.apache.abdera.ext.media.MediaConstants.CONTENT;
import static org.apache.abdera.ext.media.MediaConstants.GROUP;
import static org.apache.abdera.ext.media.MediaConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.media.MediaContent;
import org.apache.abdera.ext.media.MediaGroup;
import org.apache.abdera.ext.media.MediaTitle;
import org.apache.abdera.ext.media.MediaConstants.Expression;
import org.apache.abdera.ext.media.MediaConstants.Type;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.junit.Test;

public class MediaTest {

    @Test
    public void testMedia() throws Exception {

        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();
        Entry entry = factory.newEntry();
        MediaGroup group = entry.addExtension(GROUP);
        MediaContent content = group.addExtension(CONTENT);
        content.setUrl("http://example.org");
        content.setBitrate(123);
        content.setChannels(2);
        content.setDuration(123);
        content.setExpression(Expression.SAMPLE);
        content.setFilesize(12345);
        content.setFramerate(123);
        content.setLanguage("en");
        MediaTitle title = content.addExtension(TITLE);
        title.setType(Type.PLAIN);
        title.setText("This is a sample");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        entry.writeTo(out);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Parser parser = abdera.getParser();
        Document<Entry> doc = parser.parse(in);
        entry = doc.getRoot();

        group = entry.getExtension(GROUP);
        List<MediaContent> list = entry.getExtensions(CONTENT);
        for (MediaContent item : list) {
            assertEquals("http://example.org", item.getUrl().toString());
            assertEquals(123, item.getBitrate());
            assertEquals(2, item.getChannels());
            assertEquals(123, item.getDuration());
            assertEquals(Expression.SAMPLE, item.getExpression());
            assertEquals(12345, item.getFilesize());
            assertEquals(123, item.getFramerate());
            assertEquals("en", item.getLang());
            title = item.getExtension(TITLE);
            assertNotNull(title);
            assertEquals(Type.PLAIN, title.getType());
            assertEquals("This is a sample", title.getText());
        }
    }

}
