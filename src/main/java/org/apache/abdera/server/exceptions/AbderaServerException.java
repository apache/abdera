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
package org.apache.abdera.server.exceptions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;

import org.apache.abdera.server.ResponseContext;
import org.apache.abdera.server.cache.CachePolicy;

@SuppressWarnings("serial")
public class AbderaServerException 
  extends Exception 
  implements ResponseContext {

  private int status = 0;
  private String statusText = null;
  private Date lastModified = null;
  private String etag = null;
  private String language = null;
  private URI contentLocation = null;
  private long contentLength = -1;
  private MimeType contentType = null;
  private URI location = null;
  private Map<String,List<String>> headers = new HashMap<String, List<String>>();
  
  public AbderaServerException(int status, String text) {
    this.status = status;
    this.statusText = text;
  }
  
  public int getStatus() {
    return status;
  }

  public String getStatusText() {
    return statusText;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getEntityTag() {
    return etag;
  }

  public String getContentLanguage() {
    return language;
  }

  public URI getContentLocation() {
    return contentLocation;
  }

  public MimeType getContentType() {
    return contentType;
  }
  
  public long getContentLength() {
    return contentLength;
  }

  public URI getLocation() {
    return location;
  }

  public void writeTo(OutputStream out) throws IOException {
    // TODO
  }

  public CachePolicy getCachePolicy() {
    return null;
  }

  public boolean hasEntity() {
    return false;
  }

  public void addHeader(String name, int value) {
    addHeader(name, Integer.toString(value));       
  }

  public void addHeader(String name, String value) {
    if(!this.headers.containsKey(name)) {
      setHeader(name, value);
    } else {
      List<String> values = this.headers.get(name);
      values.add(value);
    }
  }

  public Map<String, List<String>> getHeaders() {
    return this.headers;
  }

  public void setHeader(String name, int value) {
    setHeader(name, Integer.toString(value));    
  }

  public void setHeader(String name, String value) {
    this.headers.put(name, Arrays.asList(new String[] { value }));    
  }
}
