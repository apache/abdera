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

import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.abdera.filter.TextFilter;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;

public class TextFilterExample {

  public static void main(String[] args) throws Exception {
    
    // First create the text filter
    TextFilter filter = new TextFilter() {
      @Override
      public String filterText(String text, Element element) {
        ExtensionElement ee = (ExtensionElement) element;
        QName qname = ee.getQName();
        Base elparent = element.getParentElement();
        if (Constants.NAME.equals(qname)) {
          text = "Jane Doe";
        } else if (Constants.TITLE.equals(qname) && elparent instanceof Entry) {
          text = text.replaceAll("Amok", "Crazy");
        }
        return text;
      }
    };
    
    // Set the filter using the ParserOptions
    ParserOptions options = Parser.INSTANCE.getDefaultParserOptions();
    options.setTextFilter(filter);
    
    // Parse!
    URL url = TextFilterExample.class.getResource("/simple.xml");
    InputStream in = url.openStream();
    Document<Feed> doc = Parser.INSTANCE.parse(in, url.toURI(), options);
    Feed feed = doc.getRoot();
    System.out.println(feed.getAuthor().getName());          // Jane Doe
    System.out.println(feed.getEntries().get(0).getTitle()); // Atom-Powered Robots Run Crazy
    
  }

}
