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
package org.apache.abdera.test.converter;

import java.net.URL;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.converter.ConventionConversionContext;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.annotation.Category;
import org.apache.abdera.converter.annotation.Feed;
import org.apache.abdera.converter.annotation.Generator;
import org.apache.abdera.converter.annotation.Link;
import org.apache.abdera.converter.annotation.Rel;
import org.apache.abdera.converter.annotation.Scheme;
import org.apache.abdera.converter.annotation.TextType;
import org.apache.abdera.model.Text;

public class ConverterTest extends TestCase {

  public static void testConverter() throws Exception {
    
    Abdera abdera = new Abdera();
    ConversionContext context = new ConventionConversionContext(abdera);
    
    FooFeed fooFeed = new FooFeed();
    
    Object object = context.convert(fooFeed);
    
    System.out.println(object);
    
  }
  
  @Feed
  public static class FooFeed {
    public String id = "http://example.org/1";
    @TextType(Text.Type.XHTML) public String title = "The title";
    public Date updated = new Date();
    @TextType(Text.Type.HTML) public String subtitle = "The subtitle";
    public String author = "james";
    @Rel("enclosure") public String link = "foo";
    @Link @Rel("related") public String related = "bar";
    public String[] categories = new String[] {"baz","joe"};
    @Category @Scheme("http://example.org/type") public String type = "Foo";
    @Generator(version="1.0",uri="http://example.org") public String generator = "Bob";
    
    public FooEntry[] entries = new FooEntry[] { new FooEntry() };
  }
  
  public static class FooEntry {
    public String id = "http://example.org/1/1";
    @TextType(Text.Type.XHTML) public String title = "The title";
    public Date updated = new Date();
    @TextType(Text.Type.HTML) public String summary = "The summary";    
    public String author = "james";    
    @Rel("enclosure") public String link = "foo";
    @Link @Rel("related") public String related = "bar";    
    public String category = "baz";    
    @Category @Scheme("http://example.org/type") public String type = "Foo";
    
    //@MediaType("text/html") public IRI content = new IRI("http://example.com");
    
    public DataHandler content = new DataHandler(new URLDataSource(getUrl()));
    
    private static URL getUrl() {
      try {
        return new URL("http://www.snellspace.com/wp");
      } catch (Exception e) {
        return null;
      }
    }
  }
}
