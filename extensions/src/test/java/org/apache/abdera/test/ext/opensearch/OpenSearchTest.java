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
package org.apache.abdera.test.ext.opensearch;

import junit.framework.TestCase;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Document;

import org.apache.abdera.parser.Parser;

import org.apache.abdera.ext.opensearch.TotalResults;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.ItemsPerPage;
import org.apache.abdera.ext.opensearch.StartIndex;

import java.io.InputStream;

public class OpenSearchTest extends TestCase {
  public void testBasics()
  {
    InputStream stream = OpenSearchTest.class.getResourceAsStream("/opensearch.xml");
    Document<Element> doc = Parser.INSTANCE.parse(stream);

    TotalResults tr = doc.getRoot().getFirstChild(OpenSearchConstants.TOTAL_RESULTS);
    assertNotNull(tr);
    assertEquals(tr.getCount(), 47);

    ItemsPerPage ipp = doc.getRoot().getFirstChild(OpenSearchConstants.ITEMS_PER_PAGE);
    assertNotNull(ipp);
    assertEquals(ipp.getCount(), 1);

    StartIndex si = doc.getRoot().getFirstChild(OpenSearchConstants.START_INDEX);
    assertNotNull(si);
    assertEquals(si.getIndex(), 1);
  }
}
