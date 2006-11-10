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
  
  /**
   * The current set of default ParserOptions.
   *
   * This field is protected by synchronizing on "this".
   */
  protected ParserOptions options = null;
  
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
    IRI base) {
      Document<T> document = builder.getFomDocument();
      try {
        document.setBaseUri(base.toString());
      } catch (Exception e) {}
      return document;
  }
  
  private void setCharset(ParserOptions options, String charset, Document doc) {
    if (charset != null) doc.setCharset(charset);
    if (options.getCharset() != null) {
      ((OMDocument)doc).setCharsetEncoding(options.getCharset());
    }    
  }
  
  public <T extends Element>Document<T> parse(
    InputStream in, 
    String base, 
    ParserOptions options)
      throws ParseException {
    Document<T> document = null;
    if (in == null)
      throw new IllegalArgumentException("InputStream must not be null");
    try {
      String charset = (options != null) ? options.getCharset() : null;
      boolean detect = (options != null) ? options.getAutodetectCharset() : true;
      if (charset == null && detect) {
        FOMSniffingInputStream sin = 
          (in instanceof FOMSniffingInputStream) ? 
            (FOMSniffingInputStream)in : 
            new FOMSniffingInputStream(in);
        charset = sin.getEncoding();
        in = sin;
      }
      XMLStreamReader xmlreader = StAXUtils.createXMLStreamReader(in);
      if (options != null && charset != null) options.setCharset(charset);
      FOMFactory factory = getFomFactory(options);
      FOMBuilder builder = new FOMBuilder(factory, xmlreader, options);
      document = getDocument(builder, (base != null) ? new IRI(base) : null);
      setCharset(options, xmlreader.getCharacterEncodingScheme(), document);
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
    return document;
  }

  public <T extends Element> Document<T> parse(
    Reader in, 
    String base, 
    ParserOptions options) 
      throws ParseException {
    Document<T> document = null;
    if (in == null)
      throw new IllegalArgumentException("Reader must not be null");
    try {
      FOMFactory factory = getFomFactory(options);
      XMLStreamReader xmlreader = StAXUtils.createXMLStreamReader(in);
      FOMBuilder builder = new FOMBuilder(factory, xmlreader, options);
      document = getDocument(builder, base != null ? new IRI(base) : null);
      setCharset(options, xmlreader.getCharacterEncodingScheme(), document);
    } catch (Exception e) {
      if (!(e instanceof ParseException))
        e = new ParseException(e);
      throw (ParseException)e;
    }
    return document;
  }
  
  @Override
  public synchronized ParserOptions getDefaultParserOptions() {
    if (options == null)
      options = new FOMParserOptions(getFactory());

    // Make a copy of the options, so that changes to it don't result in
    // changes to the Parser's defaults.  Also, this allows us to remain
    // thread safe without having to make ParseOptions implementations
    // synchronized.

    try {
      return (ParserOptions) options.clone();
    } catch (CloneNotSupportedException cnse) {
      // This shouldn't actually happen
      throw new RuntimeException(cnse);
    }
  }

  public synchronized void setDefaultParserOptions(ParserOptions options) {
    // Ok, we need to make a defensive copy of the options, since otherwise
    // the caller still has access to the object, which means our access to
    // it isn't certain to be thread safe.

    try {
      this.options = (ParserOptions) options.clone();
    } catch (CloneNotSupportedException cnse) {
      // This shouldn't actually happen
      throw new RuntimeException(cnse);
    }
  }

}
