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
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.ServiceUtil;

public class FOMParserFactory 
  implements ParserFactory {

  public Parser getInstance() {
    return Parser.INSTANCE;
  }

  public Parser getInstance(String name) {
    return (name != null) ? 
      loadParsers().get(name) : getInstance();
  }

  private static Map<String,NamedParser> parsers = null;
  
  public static Map<String,NamedParser> loadParsers() {
    if (parsers == null) {
      List<NamedParser> _parsers = 
        ServiceUtil._loadimpls(
          "META-INF/services/org.apache.abdera.parser.NamedParser");
      parsers = new HashMap<String,NamedParser>();
      for (NamedParser parser : _parsers) {
        parsers.put(parser.getName(), parser);
      }
    }
    return parsers;
  }
}
