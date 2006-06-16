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
package org.apache.abdera.parser;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.ServiceUtil;


/**
 * @author James M Snell (jasnell@us.ibm.com)
 * 
 * The Parser is the interface through which developers parse feed documents.
 * 
 * <code>Document doc = Parser.INSTANCE.parse(inputStream, baseUri, options);</code>
 */
public interface Parser {

  /**
   * The default Parser instance
   */
  public static final Parser INSTANCE = ServiceUtil.newParserInstance();
  
  <T extends Element>Document<T> parse(
    InputStream in) 
      throws ParseException;
  
  <T extends Element>Document<T> parse(
    InputStream in, 
    URI base) 
      throws ParseException;
  
  <T extends Element>Document<T> parse(
    InputStream in, 
    URI base, 
    ParserOptions options) 
      throws ParseException;
  
  <T extends Element>Document<T> parse(
    InputStream in, 
    String base) 
      throws ParseException, URISyntaxException;
  
  <T extends Element>Document<T> parse(
    InputStream in, 
    String base, 
    ParserOptions options) 
      throws ParseException, URISyntaxException;

  ParserOptions getDefaultParserOptions();
}
