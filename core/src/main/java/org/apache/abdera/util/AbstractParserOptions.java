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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.filter.TextFilter;
import org.apache.abdera.parser.ParserOptions;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class AbstractParserOptions 
  implements ParserOptions {

  protected Factory factory = null;
  protected String charset = null;
  protected ParseFilter parseFilter = null;
  protected TextFilter textFilter = null;

  protected abstract void initFactory();
  protected abstract void checkFactory(Factory factory);
  
  public Factory getFactory() {
    if (factory == null) initFactory();
    return factory;
  }
  
  public void setFactory(Factory factory) {
    checkFactory(factory);
    this.factory = factory;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public ParseFilter getParseFilter() {
    return parseFilter;
  }
  public void setParseFilter(ParseFilter parseFilter) {
    this.parseFilter = parseFilter;
  }
  
  public TextFilter getTextFilter() {
    return textFilter;
  }
  
  public void setTextFilter(TextFilter textFilter) {
    this.textFilter = textFilter;
  }
}
