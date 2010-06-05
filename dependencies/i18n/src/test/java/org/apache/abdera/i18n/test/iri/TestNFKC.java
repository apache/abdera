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

import static org.junit.Assert.assertEquals;

import org.apache.abdera.i18n.text.Normalizer;
import org.junit.Test;

public class TestNFKC extends TestBase {

    @Test
    public void testNFKC() throws Exception {

        // "\xC2\xB5", "\xCE\xBC"
        String s1 = Normalizer.normalize(string(0xC2, 0xB5)).toString();
        String s2 = string(0xCE, 0xBC);
        assertEquals(s2, s1);

        // "\xC2\xAA", "\x61"
        s1 = Normalizer.normalize(string(0xC2, 0xAA)).toString();
        s2 = string(0x61);
        assertEquals(s2, s1);

    }

}
