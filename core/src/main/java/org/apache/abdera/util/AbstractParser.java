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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;


/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class AbstractParser 
  implements Parser {

  public <T extends Element>Document<T> parse(
    InputStream in) 
      throws ParseException {
    return parse(in, (URI)null, getDefaultParserOptions());
  }

  public <T extends Element>Document<T> parse(
    InputStream in, 
    URI base) 
      throws ParseException {
    return parse(in, base, getDefaultParserOptions());
  }

  public <T extends Element>Document<T> parse(
    InputStream in, 
    String base) 
      throws ParseException, 
             URISyntaxException {
    return parse(in, new URI(base), getDefaultParserOptions());
  }
  
  public <T extends Element>Document<T> parse(
    InputStream in, 
    String base, 
    ParserOptions options) 
      throws ParseException, 
             URISyntaxException {
    return parse(in, new URI(base), options);
  }
  
  public abstract ParserOptions getDefaultParserOptions();
  
}
