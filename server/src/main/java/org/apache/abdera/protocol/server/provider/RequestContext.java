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
package org.apache.abdera.protocol.server.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.i18n.iri.IRI;

public interface RequestContext 
  extends Request {

  public enum Scope { REQUEST, SESSION };
  public enum Property { 
    SESSIONID, SESSIONCREATED, SESSIONACCESSED, SESSIONTIMEOUT,
    CHARACTERENCODING, LOCALES, PROTOCOL, REMOTEADDRESS, REMOTEHOST,
    REMOTEUSER, SCHEME, PRINCIPAL};
  
  ServiceContext getServiceContext();
    
  Target getTarget();
    
  Subject getSubject();
  
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
  
  void setAttribute(Scope scope, String name, Object value);
  
  InputStream getInputStream() throws IOException;
  
  Reader getReader() throws IOException;
  
  <T extends Element>Document<T> getDocument() throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(Parser parser) throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException, IOException;
  
  <T extends Element>Document<T> getDocument(ParserOptions options) throws ParseException, IOException;
  
  boolean isUserInRole(String role);
}
