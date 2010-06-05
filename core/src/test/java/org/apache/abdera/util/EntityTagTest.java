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

import org.apache.abdera.util.EntityTag;
import org.junit.Test;

public class EntityTagTest {

    @Test
    public void testEntityTag() throws Exception {
        String[] tags = {"hello", "\"hello\"", "W/\"hello\"", "*"};
        EntityTag[] etags = new EntityTag[tags.length];
        for (int n = 0; n < tags.length; n++) {
            etags[n] = new EntityTag(tags[n]);
        }
        assertFalse(etags[0].isWeak());
        assertFalse(etags[0].isWild());
        assertFalse(etags[1].isWeak());
        assertFalse(etags[1].isWild());
        assertTrue(etags[2].isWeak());
        assertFalse(etags[2].isWild());
        assertFalse(etags[3].isWeak());
        assertTrue(etags[3].isWild());
        assertEquals("hello", etags[0].getTag());
        assertEquals("hello", etags[1].getTag());
        assertEquals("hello", etags[2].getTag());
        assertEquals("*", etags[3].getTag());
        assertEquals(tags[1], etags[0].toString());
        assertEquals(tags[1], etags[1].toString());
        assertEquals(tags[2], etags[2].toString());
        assertEquals(tags[3], etags[3].toString());

        assertTrue(EntityTag.matches(etags[3], etags[0]));
        assertTrue(EntityTag.matches(etags[3], etags[1]));
        assertTrue(EntityTag.matches(etags[3], etags[2]));
        assertTrue(EntityTag.matches(etags[3], etags[3]));

        assertTrue(EntityTag.matches(etags[0], etags[1]));
        assertFalse(EntityTag.matches(etags[0], etags[2]));

        assertTrue(EntityTag.matchesAny(etags[3], new EntityTag[] {etags[0], etags[1], etags[2]}));
        assertTrue(EntityTag.matchesAny(etags[0], new EntityTag[] {etags[3], etags[1], etags[2]}));
        assertTrue(EntityTag.matchesAny(etags[1], new EntityTag[] {etags[0], etags[3], etags[2]}));
        assertTrue(EntityTag.matchesAny(etags[2], new EntityTag[] {etags[0], etags[1], etags[3]}));

        java.util.Arrays.sort(etags);
        assertEquals(tags[3], etags[0].toString());
        assertEquals(tags[1], etags[1].toString());
        assertEquals(tags[1], etags[2].toString());
        assertEquals(tags[2], etags[3].toString());
        EntityTag etag = EntityTag.generate("a", "b", "c", "d");
        assertEquals("\"e2fc714c4727ee9395f324cd2e7f331f\"", etag.toString());
    }

}
