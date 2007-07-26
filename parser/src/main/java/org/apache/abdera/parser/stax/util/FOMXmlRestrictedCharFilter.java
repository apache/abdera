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
package org.apache.abdera.parser.stax.util;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.util.XmlRestrictedCharFilter;

public final class FOMXmlRestrictedCharFilter 
  extends XmlRestrictedCharFilter {

  public FOMXmlRestrictedCharFilter(
    Reader in) {
      this(new FOMXmlVersionReader(in));
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionReader in) {
      super(in,getMode(in.getVersion()));
  }
  
  public FOMXmlRestrictedCharFilter(
    Reader in, 
    char replacement) {
      this(new FOMXmlVersionReader(in), replacement);
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionReader in, 
    char replacement) {
      super(in,getMode(in.getVersion()), replacement);
  }

  public FOMXmlRestrictedCharFilter(
    InputStream in) {
      this(new FOMXmlVersionInputStream(in));
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionInputStream in) {
      super(in,getMode(in.getVersion()));
  }
  
  public FOMXmlRestrictedCharFilter(
    InputStream in, 
    char replacement) {
      this(new FOMXmlVersionInputStream(in), replacement);
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionInputStream in, 
    char replacement) {
      super(in,getMode(in.getVersion()), replacement);
  }

  public FOMXmlRestrictedCharFilter(
    InputStream in, 
    String charset) 
      throws UnsupportedEncodingException {
    this(new FOMXmlVersionInputStream(in),charset);
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionInputStream in, 
    String charset) 
      throws UnsupportedEncodingException {
    super(in,charset,getMode(in.getVersion()));
  }
  
  public FOMXmlRestrictedCharFilter(
    InputStream in, 
    String charset, 
    char replacement) 
      throws UnsupportedEncodingException {
    this(new FOMXmlVersionInputStream(in), charset, replacement);
  }
  
  public FOMXmlRestrictedCharFilter(
    FOMXmlVersionInputStream in, 
    String charset, 
    char replacement) 
      throws UnsupportedEncodingException {
    super(in,charset, getMode(in.getVersion()), replacement);
  }
  
  private static Mode getMode(String version) {
    return version == null ? Mode.XML10 :
           version.equals("1.0") ? Mode.XML10 :
           version.equals("1.1") ? Mode.XML11 : 
           Mode.XML10;
  }
  
}
