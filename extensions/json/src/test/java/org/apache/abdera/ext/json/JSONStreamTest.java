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
package org.apache.abdera.ext.json;


import org.apache.abdera.ext.json.JSONStream;
import org.apache.abdera.Abdera;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.junit.Test;
import org.junit.Assert;
import junit.framework.TestCase;


/**
 * JSONStreamTest
 *
 * @author David Calavera
 * @since 11/01/08
 */
public class JSONStreamTest extends TestCase {

	@Test
	public void testJSONStreamContent() throws Exception {
	    Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();

        entry.setContent(new IRI("http://example.org/xml"), "text/xml");

        Writer json = abdera.getWriterFactory().getWriter("json");
        entry.writeTo(json, System.out);
    }
}