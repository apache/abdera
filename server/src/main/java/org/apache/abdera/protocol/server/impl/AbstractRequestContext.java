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
package org.apache.abdera.protocol.server.impl;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRequestContext 
  extends AbstractRequest
  implements RequestContext {

  private final static Log log = LogFactory.getLog(AbstractRequestContext.class);
  
  protected final ServiceContext context;
  protected Subject subject;
  protected Principal principal;
  protected Target target;
  protected final String method;
  protected final IRI requestUri;
  protected final IRI baseUri;
  protected Document document;
  
  protected AbstractRequestContext(
    ServiceContext context,
    String method, 
    IRI requestUri,
    IRI baseUri) {
      this.context = context;
      this.method = method;
      this.baseUri = baseUri;
      this.requestUri = requestUri;
  }
  
  public Abdera getAbdera() {
    return context.getAbdera();
  }
    
  @SuppressWarnings("unchecked")
  public synchronized <T extends Element>Document<T> getDocument()
    throws ParseException, 
           IOException {
    log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
    if (document == null) {
      Abdera abdera = context.getAbdera();
      Parser parser = abdera.getParser();
      document = getDocument(parser);
    } 
    return document;
  }
  
  @SuppressWarnings("unchecked")
  public synchronized <T extends Element>Document<T> getDocument(
    Parser parser)
      throws ParseException, 
             IOException {
    log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
    if (document == null) {
      ParserOptions options = parser.getDefaultParserOptions();
      document = getDocument(parser, options);
    }
    return document;
  }
  
  @SuppressWarnings("unchecked")
  public synchronized <T extends Element>Document<T> getDocument(
    ParserOptions options)
     throws ParseException, 
            IOException  {
    log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
    if (document == null) {
      Abdera abdera = context.getAbdera();
      Parser parser = abdera.getParser();
      document = getDocument(parser, options);
    }
    return document;
  }
  
  @SuppressWarnings("unchecked")
  public synchronized <T extends Element>Document<T> getDocument(
    Parser parser, 
    ParserOptions options) 
      throws ParseException, 
             IOException {
    log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
    if (document == null) {
      return parser.parse(
        getInputStream(), 
        null, options);
    } 
    return document;
  }
  
  public IRI getBaseUri() {
    return baseUri;
  }

  public IRI getResolvedUri() {
    return baseUri.resolve(getUri());
  }
  
  public String getMethod() {
    return method;
  }

  public IRI getUri() {
    return requestUri;
  }
  
  public Subject getSubject() {
    return subject;
  }
  
  public Principal getPrincipal() {
    return principal;
  }

  public Target getTarget() {
    return target;
  }
  
  public ServiceContext getServiceContext() {
    return context;
  }

  public String getTargetPath() {
    String uri = getUri().toString();
    String cpath = getContextPath();
    return cpath == null ? uri : uri.substring(cpath.length());
  }
  
}
