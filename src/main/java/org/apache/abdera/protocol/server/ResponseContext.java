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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public interface ResponseContext {

  public int getStatus();
  
  public String getStatusText();
  
  public Date getLastModified();
  
  public String getEntityTag();
  
  public String getContentLanguage();
  
  public URI getContentLocation() throws URISyntaxException;
  
  public long getContentLength();
  
  public MimeType getContentType() throws MimeTypeParseException;
  
  public String getAllow();
  
  public URI getLocation() throws URISyntaxException;
  
  public Date getDateHeader(String name);
  
  public String getHeader(String name);
  
  public List<Object> getHeaders(String name);
  
  public Map<String, List<Object>> getHeaders();
  
  public boolean hasEntity();
  
  public void writeTo(OutputStream out) throws IOException;
  
  boolean isPrivate();
  
  boolean isPublic();
  
  boolean isNoCache();
  
  boolean isNoStore();
  
  boolean isNoTransform();
  
  boolean isMustRevalidate();
  
  boolean isProxyRevalidate();
  
  long getMaxAge();
  
  long getSMaxAge();
  
  long getAge();
  
  Date getExpires();
  
  String[] getNoCacheHeaders();
  
  String[] getPrivateHeaders();
  
  String getCacheControl();
  
}