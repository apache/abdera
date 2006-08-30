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
package org.apache.abdera.protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.protocol.util.ProtocolConstants;

public interface Response extends ProtocolConstants {

  public static enum ResponseType {
    SUCCESS, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN
  }
  
  public ResponseType getType();
  
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
  
  public URI getUriHeader(String name) throws URISyntaxException;
  
  public String getHeader(String name);
  
  public List<Object> getHeaders(String name);
  
  public Map<String, List<Object>> getHeaders();
  
  public String[] getHeaderNames();
  
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
