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
package org.apache.abdera.test.ext.bidi;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.bidi.BidiHelper;
import org.apache.abdera.ext.bidi.BidiHelper.Direction;
import org.apache.abdera.model.Feed;

import junit.framework.TestCase;

public class BidiTest extends TestCase {

  public static void testBidi() throws Exception {
    
    Abdera abdera = new Abdera();
    Feed feed = abdera.getFactory().newFeed();
    feed.setTitle("Testing");
    feed.setSubtitle("Testing");
    BidiHelper.setDirection(Direction.RTL, feed);
    BidiHelper.setDirection(Direction.LTR, feed.getSubtitleElement());
    
    assertNotNull(feed.getAttributeValue("dir"));
    assertEquals(BidiHelper.getDirection(feed), Direction.RTL);
    assertEquals(BidiHelper.getDirection(feed.getTitleElement()), Direction.RTL);
    assertEquals(BidiHelper.getDirection(feed.getSubtitleElement()), Direction.LTR);
    assertEquals(BidiHelper.getBidiElementText(feed.getTitleElement()), BidiHelper.getBidiText(Direction.RTL, "Testing"));
    assertEquals(BidiHelper.getBidiElementText(feed.getSubtitleElement()), BidiHelper.getBidiText(Direction.LTR, "Testing"));
    
  }
  
}
