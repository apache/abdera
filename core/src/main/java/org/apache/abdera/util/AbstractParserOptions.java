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

public abstract class AbstractParserOptions 
  implements ParserOptions {

  protected Factory factory = null;
  protected String charset = null;
  protected ParseFilter parseFilter = null;
  protected TextFilter textFilter = null;
  protected boolean detect = true;
  protected boolean ignoredtd = false;
  protected boolean ignorecomments = false;
  protected boolean ignorepi = false;
  protected boolean ignorespace = false;

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
  
  public boolean getAutodetectCharset() {
    return this.detect;
  }
  
  public void setAutodetectCharset(boolean detect) {
    this.detect = detect;
  }
  
  public void setIgnoreDoctype(boolean ignore) {
    this.ignoredtd = ignore;
  }
  public void setIgnoreComments(boolean ignore) {
    this.ignorecomments = ignore;
  }
  public void setIgnoreWhitespace(boolean ignore) {
    this.ignorespace = ignore;
  }
  public void setIgnoreProcessingInstructions(boolean ignore) {
    this.ignorepi = ignore;
  }
  public boolean getIgnoreDoctype() {
    return this.ignoredtd;
  }
  public boolean getIgnoreComments() {
    return this.ignorecomments;
  }
  public boolean getIgnoreWhitespace() {
    return this.ignorespace;
  }
  public boolean getIgnoreProcessingInstructions() {
    return this.ignorepi;
  }
}
