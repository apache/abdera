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

import junit.framework.TestCase;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.ext.media.MediaGroup;
import org.apache.abdera.ext.media.MediaContent;
import org.apache.abdera.ext.media.MediaTitle;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import static org.apache.abdera.ext.media.MediaConstants.*;

public class MediaTest extends TestCase {

  public static void testMedia() throws Exception {
    
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
      assertEquals(item.getUrl().toString(), "http://example.org");
      assertEquals(item.getBitrate(), 123);
      assertEquals(item.getChannels(), 2);
      assertEquals(item.getDuration(), 123);
      assertEquals(item.getExpression(), Expression.SAMPLE);
      assertEquals(item.getFilesize(), 12345);
      assertEquals(item.getFramerate(), 123);
      assertEquals(item.getLang(), "en");
      title = item.getExtension(TITLE);
      assertNotNull(title);
      assertEquals(title.getType(), Type.PLAIN);
      assertEquals(title.getText(), "This is a sample");
    }
  }
  
}
 