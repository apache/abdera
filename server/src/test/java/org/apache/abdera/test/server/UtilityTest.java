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
package org.apache.abdera.test.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.AbstractRequestContext;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleSubjectResolver;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.EntityTag;

public class UtilityTest extends TestCase {

  public static void testEncoding() throws Exception {
    String t = "t\u00e9st";
    String tb1 = EncodingUtil.encode(t);
    String tb2 = EncodingUtil.encode(t, "UTF-8");
    String tb3 = EncodingUtil.encode(t, "UTF-8", EncodingUtil.Codec.B);
    String tb4 = EncodingUtil.encode(t, "UTF-16", EncodingUtil.Codec.B);
    String tq1 = EncodingUtil.encode(t, "UTF-8", EncodingUtil.Codec.Q);
    String tq2 = EncodingUtil.encode(t, "UTF-16", EncodingUtil.Codec.Q);
    
    assertEquals("=?UTF-8?B?dMOpc3Q=?=", tb1);
    assertEquals("=?UTF-8?B?dMOpc3Q=?=", tb2);
    assertEquals("=?UTF-8?B?dMOpc3Q=?=", tb3);
    assertEquals("=?UTF-16?B?/v8AdADpAHMAdA==?=", tb4);
    assertEquals("=?UTF-8?Q?t=C3=A9st?=", tq1);
    assertEquals("=?UTF-16?Q?=FE=FF=00t=00=E9=00s=00t?=", tq2);
    
    assertEquals(EncodingUtil.decode(tb1),t);
    assertEquals(EncodingUtil.decode(tb2),t);
    assertEquals(EncodingUtil.decode(tb3),t);
    assertEquals(EncodingUtil.decode(tb4),t);
    assertEquals(EncodingUtil.decode(tq1),t);
    assertEquals(EncodingUtil.decode(tq2),t);
  }
  
  public static void testServiceManager() throws Exception {
    ServiceManager sm = ServiceManager.getInstance();
    assertNotNull(sm);
    assertNotNull(sm.newServiceContext(new HashMap<String,String>()));
  }
  
  public static void testEntityTag() throws Exception {
    EntityTag etag1 = EntityTag.parse("\"foo\"");
    assertEquals(etag1.getTag(),"foo");
    assertFalse(etag1.isWeak());
    
    EntityTag etag2 = EntityTag.parse("W/\"foo\"");
    assertEquals(etag2.getTag(),"foo");
    assertTrue(etag2.isWeak());
    
    assertFalse(EntityTag.matches(etag1, etag2));
    
    EntityTag[] tags = EntityTag.parseTags("\"foo\", W/\"bar\"");
    assertTrue(EntityTag.matchesAny(etag1, tags));
    
    assertEquals(etag1.toString(), "\"foo\"");
    assertEquals(etag2.toString(), "W/\"foo\"");
  }
  
  public static void testRegexTargetResolver() throws Exception {
    
    RegexTargetResolver r = new RegexTargetResolver();
    r.setPattern("", TargetType.TYPE_SERVICE);
    r.setPattern("/([^/?#]+)", TargetType.TYPE_COLLECTION);
    
    DummyRequestContext drc = null;
    Target target = null;
    
    drc = new DummyRequestContext("/test","http://example.org/");
    target = r.resolve(drc);
    assertNotNull(target);
    assertEquals(target.getType(), TargetType.TYPE_SERVICE);
    
    drc = new DummyRequestContext("/test/foo","http://example.org/");
    target = r.resolve(drc);
    assertNotNull(target);
    assertEquals(target.getType(), TargetType.TYPE_COLLECTION);
    
    drc = new DummyRequestContext("/test/foo/","http://example.org/");
    target = r.resolve(drc);
    assertNull(target);

  }
  
  public static void testSubjectResolver() throws Exception {
    DummyRequestContext drc = new DummyRequestContext("/test","http://example.org/");
    assertNotNull(drc.getSubject());
  }
  
  private static class DummyRequestContext extends AbstractRequestContext {

    protected DummyRequestContext(String request, String base) throws Exception {
      super(
        ServiceManager.getInstance().newServiceContext(new HashMap<String,String>()), 
        "POST", 
        new IRI(request), 
        new IRI(base));
      
      principal = (Principal) getProperty(Property.PRINCIPAL); 
      
      subject = context.getSubjectResolver().resolve(this);
    }

    public Object getAttribute(Scope scope, String name) {
      return null;
    }

    public String[] getAttributeNames(Scope scope) {
      return null;
    }

    public InputStream getInputStream() throws IOException {
      return null;
    }

    public String getParameter(String name) {
      return null;
    }

    public String[] getParameterNames() {
      return null;
    }

    public List<String> getParameters(String name) {
      return null;
    }

    public Object getProperty(Property property) {
      switch (property) {
        case PRINCIPAL: return SimpleSubjectResolver.ANONYMOUS;
      }
      return null;
    }

    public Reader getReader() throws IOException {
      return null;
    }

    public void setAttribute(Scope scope, String name, Object value) {
    }

    public Date getDateHeader(String name) {
      return null;
    }

    public String getHeader(String name) {
      return null;
    }

    public String[] getHeaderNames() {
      return null;
    }

    public String[] getHeaders(String name) {
      return null;
    }

    public boolean isUserInRole(String role) {
      return false;
    }

    public String getContextPath() {
      return "/test";
    }
    
  }
}
