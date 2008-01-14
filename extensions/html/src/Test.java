import org.apache.abdera.Abdera;
import org.apache.abdera.ext.html.HtmlCleaner;
import org.apache.abdera.model.Entry;

//import org.apache.abdera.ext.html.HtmlHelper;
//import org.apache.abdera.model.Element;

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

public class Test {

  public static void main(String... args) throws Exception {
    
   Abdera abdera = Abdera.getInstance();
   Entry entry = abdera.newEntry();
   entry.setContentAsXhtml(HtmlCleaner.parse("<p>test<br>foo"));
   System.out.println(entry);
  }
   
}
