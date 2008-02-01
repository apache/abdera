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

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.junit.Test;

public class XhtmlTest extends Assert {
 
  @Test
  public void testXhtml() throws Exception {
    
    Abdera abdera = new Abdera();
    Entry entry = abdera.newEntry();
    entry.setContentAsXhtml("<p>Test</p>");
    assertNotNull(entry.getContent());
    assertEquals(entry.getContentType(), Content.Type.XHTML);
    Element el = entry.getContentElement().getValueElement();
    assertTrue(el instanceof Div);
    
    entry = abdera.newEntry();
    entry.setContent("<a><b><c/></b></a>", Content.Type.XML);
    assertNotNull(entry.getContent());
    assertEquals(entry.getContentType(), Content.Type.XML);
    assertNotNull(entry.getContentElement().getValueElement());
    
  }
  
}
