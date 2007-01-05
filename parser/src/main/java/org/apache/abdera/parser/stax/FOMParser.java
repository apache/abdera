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

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.util.FOMSniffingInputStream;
import org.apache.abdera.util.AbstractParser;
import org.apache.abdera.util.iri.IRI;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.util.StAXUtils;

public class FOMParser 
  extends AbstractParser 
  implements Parser {

  public FOMParser() {
    super();
  }
  
  public FOMParser(Abdera abdera) {
    super(abdera);
  }
  
  private FOMFactory getFomFactory(ParserOptions options) {
    FOMFactory factory = 
      (options != null && options.getFactory() != null) ? 
        (FOMFactory)options.getFactory() : null;
    if (factory == null) {
      Factory f = getFactory();
      factory = (f instanceof FOMFactory) ? 
        (FOMFactory)f : new FOMFactory();
    }
    return factory;
  }
  
  private <T extends Element>Document<T> getDocument(
    FOMBuilder builder, 
    IRI base, 
    ParserOptions options) 
      throws ParseException {
    Document<T> document = builder.getFomDocument();
    try {
      if (base != null)
        document.setBaseUri(base.toString());
      if (options != null && options.getCharset() != null)
        ((OMDocument)document).setCharsetEncoding(options.getCharset());
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
    return document;
  }
    
  public <T extends Element>Document<T> parse(
    InputStream in, 
    String base, 
    ParserOptions options)
      throws ParseException {
    if (in == null)
      throw new IllegalArgumentException("InputStream must not be null");
    try {
      if (options == null) options = getDefaultParserOptions();
      String charset = options.getCharset();
      if (charset == null && options.getAutodetectCharset()) {
        FOMSniffingInputStream sin = 
          (in instanceof FOMSniffingInputStream) ? 
            (FOMSniffingInputStream)in : 
            new FOMSniffingInputStream(in);
        charset = sin.getEncoding();
        if (charset != null) options.setCharset(charset);
        in = sin;
      }
      XMLStreamReader xmlreader = (charset == null) ? 
        StAXUtils.createXMLStreamReader(in) : 
        StAXUtils.createXMLStreamReader(in, charset); 
      return parse(xmlreader, base, options);
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
  }

  public <T extends Element> Document<T> parse(
    Reader in, 
    String base, 
    ParserOptions options) 
      throws ParseException {
    if (in == null)
      throw new IllegalArgumentException("Reader must not be null");
    try {
      if (options == null) options = getDefaultParserOptions();
      return parse(StAXUtils.createXMLStreamReader(in), base, options);
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
  }
  
  private <T extends Element> Document<T> parse(
    XMLStreamReader reader, 
    String base,
    ParserOptions options)
      throws ParseException {
    try {
      FOMBuilder builder = 
        new FOMBuilder(
          getFomFactory(options), 
          reader, 
          options);
      return getDocument(builder, base != null ? new IRI(base) : null, options);
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
  }

  @Override
  protected ParserOptions initDefaultParserOptions() {
    return new FOMParserOptions(getFactory());
  }

}
