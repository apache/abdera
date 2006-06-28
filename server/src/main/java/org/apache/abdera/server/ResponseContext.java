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
package org.apache.abdera.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.activation.MimeType;

public interface ResponseContext {

  int getStatus();
  
  public String getStatusText();
  
  public Date getLastModified();
  
  public String getEntityTag();
  
  public String getContentLanguage();
  
  public URI getContentLocation();
   
  public MimeType getContentType();
  
  public URI getLocation();
  
  public CachePolicy getCachePolicy();
  
  public void setHeader(String name, String value);
  
  public void addHeader(String name, String value);
  
  public void setHeader(String name, int value);
  
  public void addHeader(String name, int value);
  
  public Map<String, String> getHeaders();
  
  boolean hasEntity();
  
  void writeTo(OutputStream out) throws IOException;
  
}