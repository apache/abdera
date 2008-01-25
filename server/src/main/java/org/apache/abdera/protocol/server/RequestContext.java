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
package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.Request;

public interface RequestContext 
  extends Request {

  public enum Scope { REQUEST, SESSION };
  public enum Property { 
    SESSIONID, SESSIONCREATED, SESSIONACCESSED, SESSIONTIMEOUT,
    CHARACTERENCODING, LOCALES, PROTOCOL, REMOTEADDRESS, REMOTEHOST,
    REMOTEUSER, SCHEME, PRINCIPAL, AUTHTYPE, CONTENTLENGTH, 
    CONTENTTYPE, CONTEXTPATH, LOCALADDR, LOCALNAME,
    SERVERNAME, SERVERPORT};
  
  Abdera getAbdera();
    
  Provider getProvider();
    
  Target getTarget();
    
  Subject getSubject();
  
  Principal getPrincipal();
  
  Locale getPreferredLocale();
  
  Locale[] getPreferredLocales();
  
  String getMethod();
  
  IRI getUri();
  
  IRI getResolvedUri();
  
  IRI getBaseUri();
  
  Object getProperty(Property property);
  
  String getParameter(String name);
  
  String[] getParameterNames();
  
  List<String> getParameters(String name);
  
  Object getAttribute(Scope scope,String name);
  
  String[] getAttributeNames(Scope scope);
  
  RequestContext setAttribute(String name, Object value);
  
  RequestContext setAttribute(Scope scope, String name, Object value);
  
  InputStream getInputStream() throws IOException;
  
  Reader getReader() throws IOException;
  
  <T extends Element>Document<T> getDocument() throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(Parser parser) throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(ParserOptions options) throws ParseException, IOException;
  
  boolean isUserInRole(String role);
  
  String getContextPath();
  
  /**
   * Returns the subset of the request URI that is to be used to resolve the Target
   * (everything after the context path)
   */
  String getTargetPath();
  
  /**
   * Returns the subset of the request URI that is the base of the target path
   * (e.g. HttpServletRequest.getServletPath())
   * @return
   */
  String getTargetBasePath();
  
  String resolveIri(Object key, Object param);
  
  String resolveAbsoluteIri(Object key, Object param);
}
