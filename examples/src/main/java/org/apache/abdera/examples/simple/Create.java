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

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.iri.IRI;


public class Create {

  public static void main(String[] args) throws Exception {

    Factory factory = Abdera.getNewFactory();
    Feed feed = factory.newFeed();
    feed.setLanguage("en-US");
    feed.setBaseUri("http://example.org");
    
    feed.setTitle("Example Feed");
    feed.addLink("http://example.org/");
    feed.setUpdated(new Date());
    feed.addAuthor("John Doe");
    feed.setId("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    feed.addContributor("Bob Jones");
    feed.addCategory("example");
    
    // Creates an entry and inserts it at the beginning of the list
    Entry entry = feed.insertEntry();
    entry.setTitle("Atom-Powered Robots Run Amok");
    entry.addLink("http://example.org/2003/12/13/atom03");
    entry.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    entry.setUpdated(new Date());
    entry.setSummary("Some text.");
    
    // Creates an entry and inserts it at the beginning of the list
    Entry entry2 = feed.insertEntry();
    entry2.setTitle("re: Atom-Powered Robots Run Amok");
    entry2.addLink("/2003/12/13/atom03/1");
    entry2.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b");
    entry2.setUpdated(new Date());
    entry2.setSummary("A response");
    
    // Creates an entry and appends it to the end of the list
    Entry entry3 = feed.addEntry();
    entry3.setTitle("<div xmlns=\"http://www.w3.org/1999/xhtml\"><p>Test</p></div>", Text.Type.XHTML);
    entry3.addLink("/2003/12/13/atom03/2");
    entry3.setId("HTTP://www.Example.org/foo/../bar", true); // normalizes the id to the value http://www.example.org/bar
    entry3.setUpdated(new Date());
    entry3.setSummaryAsHtml("<p><a href=\"foo\">Test</a></p>").setBaseUri("http://example.org/site/");
    entry3.setSource(feed.getAsSource());
    
    // Out-of-line content
    Entry entry4 = feed.addEntry();
    entry4.setTitle("re: Atom-Powered Robots Run Amok");
    entry4.addLink("/2003/12/13/atom03/3");
    entry4.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-cafebabecafe");
    entry4.setUpdated(new Date());
    entry4.setSummary("An entry with out-of-line content");
    entry4.setContent(new IRI("http://example.org/0xcafebabe"), "text/html");
    feed.getDocument().writeTo(System.out);
  }

}
