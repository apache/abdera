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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.filter.TextFilter;


/**
 * @author James M Snell (jasnell@us.ibm.com)
 * 
 * Parser options are used to modify the behavior of the parser. 
 */
public interface ParserOptions {

  /**
   * Returns the factory the parser should use
   */
  Factory getFactory();
  
  /**
   * Sets the factory the parser should use
   */
  void setFactory(Factory factory);
  
  /**
   * Returns the default character set to use for the parsed document
   */
  String getCharset();
  
  /**
   * Sets the character set to use for the parsed document
   */
  void setCharset(String charset);
  
  /**
   * Returns the Parse Filter.  The parse filter is a set of XML QNames that
   * the parse should watch out for.  If the filter is null, the parser will
   * parse all elements in the document.  I the filter is not null, the parser
   * will only pay attention to elements whose QName's appear in the filter list.
   */
  ParseFilter getParseFilter();

  /**
   * Sets the Parse Filter.  The parse filter is a set of XML QNames that
   * the parse should watch out for.  If the filter is null, the parser will
   * parse all elements in the document.  I the filter is not null, the parser
   * will only pay attention to elements whose QName's appear in the filter list.
   */
  void setParseFilter(ParseFilter parseFilter);
  
  /**
   * Returns the TextFilter.  The text filter provides filtering for all
   * character strings encountered during the parse process.
   */
  TextFilter getTextFilter();

  /**
   * Sets the TextFilter.  The text filter provides filtering for all
   * character strings encountered during the parse process.
   */
  void setTextFilter(TextFilter textFilter);
}
