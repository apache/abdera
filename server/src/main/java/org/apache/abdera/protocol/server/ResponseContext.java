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
import java.io.OutputStream;
import java.util.Date;

import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.Writer;

public interface ResponseContext 
  extends Response {

  boolean hasEntity();
  
  void writeTo(OutputStream out) throws IOException;
  
  void writeTo(java.io.Writer javaWriter) throws IOException;
  
  void writeTo(OutputStream out, Writer writer) throws IOException;
  
  void writeTo(java.io.Writer javaWriter, Writer abderaWriter) throws IOException;
  
  ResponseContext setWriter(Writer writer);
  
  ResponseContext removeHeader(String name);
  
  ResponseContext setEncodedHeader(String name, String charset, String value);
  
  ResponseContext setEncodedHeader(String name, String charset, String... vals);
  
  ResponseContext setEscapedHeader(String name, Profile profile, String value);
  
  ResponseContext setHeader(String name, Object value);
  
  ResponseContext setHeader(String name, Object... vals);
  
  ResponseContext addEncodedHeader(String name, String charset, String value);

  ResponseContext addEncodedHeaders(String name, String charset, String... vals);

  ResponseContext addHeader(String name, Object value);

  ResponseContext addHeaders(String name, Object... vals);

  ResponseContext setAge(long age);

  ResponseContext setContentLanguage(String language);

  ResponseContext setContentLength(long length);

  ResponseContext setContentLocation(String uri);

  ResponseContext setSlug(String slug);

  ResponseContext setContentType(String type);

  ResponseContext setContentType(String type, String charset);

  ResponseContext setEntityTag(String etag);

  ResponseContext setEntityTag(EntityTag etag);

  ResponseContext setExpires(Date date);

  ResponseContext setLastModified(Date date);

  ResponseContext setLocation(String uri);

  ResponseContext setStatus(int status);

  ResponseContext setStatusText(String text);

  ResponseContext setAllow(String method);

  ResponseContext setAllow(String... methods);

}
