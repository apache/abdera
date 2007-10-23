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
package org.apache.abdera.ext.html;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlSerializer;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Div;
import org.xml.sax.InputSource;

public class HtmlHelper {
  
  public static Div parse(String value) {
    return parse(new Abdera(),value);
  }
  
  public static Div parse(InputStream in) {
    return parse(new Abdera(),in);
  }
  
  public static Div parse(InputStream in, String charset) {
    return parse(new Abdera(),in,charset);
  }
  
  public static Div parse(Reader in) {
    return parse(new Abdera(),in);
  }
  
  public static Div parse(Abdera abdera, String value) {
    return parse(abdera, new StringReader(value));
  }
  
  public static Div parse(Abdera abdera, InputStream in) {
    return parse(abdera, in, "UTF-8");
  }
  
  public static Div parse(Abdera abdera, InputStream in, String charset) {
    try {
      return parse(abdera, new InputStreamReader(in, charset));
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static Div parse(Abdera abdera, Reader in) {
    String result = null;
    Div div = abdera.getFactory().newDiv();
    try {
      nu.validator.htmlparser.sax.HtmlParser htmlParser = new nu.validator.htmlparser.sax.HtmlParser();
      htmlParser.setBogusXmlnsPolicy(XmlViolationPolicy.ALTER_INFOSET);     
      htmlParser.setMappingLangToXmlLang(true);
      htmlParser.setReportingDoctype(false);          
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Writer w = new OutputStreamWriter(out, "UTF-8");
      HtmlSerializer ser = new HtmlSerializer(w);      
      htmlParser.setContentHandler(ser);
      htmlParser.setLexicalHandler(ser);
      //htmlParser.setErrorHandler(new SystemErrErrorHandler());
      htmlParser.parseFragment(new InputSource(in), "div");
      w.flush();
      result = new String(out.toByteArray(),"UTF-8");
      div.setValue(result);
      return div;
    } catch (Exception e) {
      // this is a temporary hack. some html really 
      // can't be parsed successfully. in that case,
      // we produce something that will likely render
      // rather ugly. but there's not much else we 
      // can do
      if (result != null) div.setText(result);
      return div;
    }
  }
  
}
