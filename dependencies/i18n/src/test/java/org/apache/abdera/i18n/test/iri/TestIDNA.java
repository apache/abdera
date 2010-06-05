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
package org.apache.abdera.i18n.test.iri;

import static org.junit.Assert.assertTrue;

import org.apache.abdera.i18n.iri.IDNA;
import org.junit.Test;

public class TestIDNA extends TestBase {

    @Test
    public void testPunycode() throws Exception {
        String o = "\u00e1\u00e9\u00ed\u00f1\u00f3\u00bd\u00a9";
        String i = "12-uda5tmbya2aq8623e";
        String out = IDNA.toASCII(o);
        String in = IDNA.toUnicode(i);
        assertTrue(out.equalsIgnoreCase("xn--" + i));
        assertTrue(in.equalsIgnoreCase(i));
    }

}
