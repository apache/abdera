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

import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;

public class FOMParserFactory 
  implements ParserFactory {

  private Abdera abdera = null;
  
  public FOMParserFactory() {
    this.abdera = new Abdera();
  }
  
  public FOMParserFactory(Abdera abdera) {
    this.abdera = abdera;
  }
  
  protected Abdera getAbdera() {
    return abdera;
  }
  
  public Parser getParser() {
    return getAbdera().getParser();
  }

  public Parser getParser(String name) {
    return (name != null) ? 
      loadParsers().get(name) : getParser();
  }

  private Map<String,NamedParser> loadParsers() {
    return getAbdera().getConfiguration().getNamedParsers();
  }
  
}
