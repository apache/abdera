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
package org.apache.abdera.test.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.XmlRestrictedCharReader;
import org.easymock.EasyMock;
import org.junit.Test;


public class CoreTest implements Constants {

  @Test
  public void testDefaultConfigurationProperties() {
    Configuration config = new AbderaConfiguration();
    assertEquals("org.apache.abdera.parser.stax.FOMFactory",
      config.getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY)
      );
    assertEquals("org.apache.abdera.parser.stax.FOMParser",
      config.getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER)
      );
    assertEquals("org.apache.abdera.parser.stax.FOMXPath",
      config.getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH)
      );
  }
  
  @Test
  public void testUriNormalization() {
    try {
      assertEquals("http://www.example.org/Bar/%3F/foo/",
        IRI.normalizeString(
          "HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/.") 
        );
      assertEquals("https://www.example.org/Bar/%3F/foo/",
          IRI.normalizeString(
          "HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/.") 
        );
      assertEquals("http://www.example.org:81/Bar/%3F/foo/",
        IRI.normalizeString(
          "HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/.")
        );
      assertEquals("https://www.example.org:444/Bar/%3F/foo/",
        IRI.normalizeString(
          "HTTPs://www.EXAMPLE.org:444/foo/../Bar/%3f/./foo/.") 
        );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testAtomDate() {
    Date now = new Date();
    AtomDate atomNow = AtomDate.valueOf(now);
    String rfc3339 = atomNow.getValue();
    atomNow = AtomDate.valueOf(rfc3339);
    Date parsed = atomNow.getDate();
    assertEquals(now, parsed);
  }
  
  @Test
  public void testAtomDate2() {
    String date = "2007-12-13T14:15:16.123Z";
    AtomDate atomDate = new AtomDate(date);
    Calendar calendar = atomDate.getCalendar();
    atomDate = new AtomDate(calendar);
    assertEquals(date,atomDate.toString());
  }
  
  @Test
  public void testAtomDate3() {
    long date = System.currentTimeMillis();
    AtomDate atomDate = new AtomDate(date);
    Calendar calendar = atomDate.getCalendar();
    atomDate = new AtomDate(calendar);
    assertEquals(date,atomDate.getTime());
  }
  
  @Test
  public void testXmlRestrictedCharReader() throws Exception {
    String s = "\u0001abcdefghijklmnop\u0002";
    StringReader r = new StringReader(s);
    XmlRestrictedCharReader x = new XmlRestrictedCharReader(r);
    char[] chars = new char[s.length()+1];
    int n = x.read(chars);  // the first and last characters should never show up
    assertEquals(s.length()-2,n);
    assertEquals("abcdefghijklmnop",new String(chars,0,n));
  }
  
  @Test
  public void testMimeTypeHelper() throws Exception {
    assertTrue(MimeTypeHelper.isApp("application/atomsvc+xml"));
    assertFalse(MimeTypeHelper.isApp("application/atomserv+xml"));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml"));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"entry\""));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"feed\""));
    assertTrue(MimeTypeHelper.isEntry("application/atom+xml;type=\"entry\""));
    assertTrue(MimeTypeHelper.isFeed("application/atom+xml;type=\"feed\""));
    assertTrue(MimeTypeHelper.isText("text/plain"));
    assertTrue(MimeTypeHelper.isXml("application/xml"));
    
    String[] types = MimeTypeHelper.condense("image/png","image/gif","image/png","image/*");
    assertEquals(1, types.length);
    assertEquals("image/*",types[0]);
    
    assertTrue(MimeTypeHelper.isEntry(MimeTypeHelper.getMimeType(EasyMock.createMock(Entry.class))));
    assertTrue(MimeTypeHelper.isFeed(MimeTypeHelper.getMimeType(EasyMock.createMock(Feed.class))));
  }
  
  @Test
  public void testCompressionUtil() throws Exception {
    String s = "abcdefg";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStream cout = CompressionUtil.getEncodedOutputStream(
      out, CompressionUtil.CompressionCodec.GZIP);
    cout.write(s.getBytes("UTF-8"));
    cout.flush(); cout.close();
    byte[] bytes = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    InputStream cin = CompressionUtil.getDecodingInputStream(
      in, CompressionUtil.CompressionCodec.GZIP);
    byte[] buffer = new byte[20];
    int r = cin.read(buffer);
    String t = new String(buffer, 0, r, "UTF-8");
    assertEquals(s,t);
  }

}
