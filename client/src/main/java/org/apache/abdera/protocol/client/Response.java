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

import javax.activation.MimeType;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;

public interface Response {

  public static enum ResponseType {
    SUCCESS, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN
  }

  ResponseType getResponseClass();
  
  String getMethod();
  
  int getStatus();
  
  String getUri();
  
  String getStatusText();
  
  Date getLastModified();
  
  String getEntityTag();
  
  MimeType getContentType();
  
  Date getServerDate();
  
  String getLocation();
  
  String getContentLocation();
  
  String getHeader(String header);
  
  Date getDateHeader(String header);
  
  String[] getHeaders(String header);
  
  String[] getHeaderNames();
  
  void release();
  
  InputStream getInputStream() throws IOException;
  
  void setInputStream(InputStream in);
  
  <T extends Element>Document<T> getDocument() throws ParseException;
  
  <T extends Element>Document<T> getDocument(ParserOptions options) throws ParseException;

  <T extends Element>Document<T> getDocument(Parser parser) throws ParseException;
  
  <T extends Element>Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException;
  
  boolean isPrivate();
  
  boolean isPublic();
  
  boolean isNoCache();
  
  boolean isNoStore();
  
  boolean isNoTransform();
  
  boolean isMustRevalidate();
  
  long getMaxAge();
  
  long getAge();
  
  Date getExpires();
  
  String[] getNoCacheHeaders();
  
  String[] getPrivateHeaders();
  
}
