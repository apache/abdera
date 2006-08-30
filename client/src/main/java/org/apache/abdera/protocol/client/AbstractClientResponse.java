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
package org.apache.abdera.protocol.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.util.AbstractResponse;
import org.apache.abdera.protocol.util.CacheControlUtil;

public abstract class AbstractClientResponse
  extends AbstractResponse
  implements ClientResponse {

  protected InputStream in = null;
  protected Date response_date = null;
  protected Date now = new Date(); 
  protected Abdera abdera = null;
  protected Parser parser = null;
  
  protected synchronized Parser getParser() {
    if (parser == null) {
      if (abdera == null) abdera = new Abdera();
      parser = abdera.getParser();
    }
    return parser;
  }
  
  public <T extends Element>Document<T> getDocument() 
    throws ParseException {
      return getDocument(getParser());
  }
  
  public <T extends Element>Document<T> getDocument(
    ParserOptions options) 
      throws ParseException {
    return getDocument(getParser(), options);
  }

  public <T extends Element>Document<T> getDocument(
    Parser parser) 
      throws ParseException {
    return getDocument(parser, parser.getDefaultParserOptions());
  }
  
  public <T extends Element>Document<T> getDocument(
    Parser parser, 
    ParserOptions options) 
      throws ParseException {
    try {
      return parser.parse(getInputStream(), getUri(), options);
    } catch (Exception e) {
      throw new ParseException(e);
    }
  }
  
  public InputStream getInputStream() throws IOException {
    return in;
  }

  public void setInputStream(InputStream in) {
    this.in = in;
  }

  public Date getServerDate() {
    if (response_date == null) {
      Date date = getDateHeader("Date");
      response_date = (date != null) ? date : now;
    }
    return response_date;
  }
  
  protected void parse_cc() {
    String cc = getHeader("Cache-Control");
    if (cc != null)
      CacheControlUtil.parseCacheControl(cc, this);
  }

}
