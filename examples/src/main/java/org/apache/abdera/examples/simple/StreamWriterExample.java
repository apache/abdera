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
package org.apache.abdera.examples.simple;

import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

/**
 * Demonstrates the use of the Abdera StreamWriter interface
 */
public class StreamWriterExample {

  public static void main(String... args) {
    
    Abdera abdera = Abdera.getInstance();
    
    StreamWriter out = abdera.newStreamWriter();
    
    out.setOutputStream(System.out,"UTF-8");
    
    out.startDocument();
    out.startFeed();
    
    out.writeId("http://example.org");
    out.writeTitle("<Testing 123>");
    out.writeSubtitle("Foo");
    out.writeAuthor("James", null, null);
    out.writeUpdated(new Date());
      
    out.writeLink("http://example.org/foo");
    out.writeLink("http://example.org/bar","self");
      
    out.writeCategory("foo");
    out.writeCategory("bar");
      
    out.writeLogo("logo");
    out.writeIcon("icon");
    out.writeGenerator("1.0", "http://example.org", "foo");
      
    for (int n = 0; n < 100; n++) {
      out.startEntry();
      
      out.writeId("http://example.org/" + n);
      out.writeTitle("Entry #" + n);
      out.writeUpdated(new Date());
      out.writePublished(new Date());
      out.writeEdited(new Date());
      out.writeSummary("This is text summary");
      out.writeAuthor("James", null, null);
      out.writeContributor("Joe", null, null);
      out.startContent("application/xml");
        out.startElement(new QName("a","b","c"));
          out.startElement(new QName("x","y","z"));
            out.writeElementText("This is a test");
          out.endElement();
        out.endElement();
      out.endContent();
      out.endEntry();
    }
      
    out.endFeed();
    out.endDocument();

    
  }
  
}
