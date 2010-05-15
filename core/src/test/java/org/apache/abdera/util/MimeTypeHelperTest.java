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
package org.apache.abdera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.easymock.EasyMock;
import org.junit.Test;

public class MimeTypeHelperTest {
    
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

}
