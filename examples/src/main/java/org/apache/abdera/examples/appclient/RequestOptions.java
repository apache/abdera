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
package org.apache.abdera.examples.appclient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestOptions {

  private boolean useDelta = false;
  private Date ifModifiedSince = null;
  private Date ifUnmodifiedSince = null;
  private String[] ifMatch = null;
  private String[] ifNoneMatch = null;
  private boolean allowCache = true;
  private Map<String,String> requestHeaders = null;
  private Map<String,String> responseHeaders = null;
  private boolean captureResponseHeaders = false;
  
  public boolean getUseDeltaEncoding() {
    return useDelta;
  }
  
  public void setUseDeltaEncoding(boolean useDelta) {
    this.useDelta = useDelta;
  }
  
  public Date getIfModifiedSince() {
    return this.ifModifiedSince;
  }
  
  public void setIfModifiedSince(Date ifModifiedSince) {
    this.ifModifiedSince = ifModifiedSince;
  }
  
  public Date getIfUnmodifiedSince() {
    return this.ifUnmodifiedSince;
  }
  
  public void setIfUnmodifiedSince(Date ifUnmodifiedSince) {
    this.ifUnmodifiedSince = ifUnmodifiedSince;
  }
  
  public String[] getIfMatch() {
    return this.ifMatch;
  }
  
  public void setIfMatch(String[] ifMatch) {
    this.ifMatch = ifMatch;
  }

  public String[] getIfNoneMatch() {
    return this.ifNoneMatch;
  }
  
  public void setIfNoneMatch(String[] ifNoneMatch) {
    this.ifNoneMatch = ifNoneMatch;
  }
  
  public boolean getAllowCache() {
    return this.allowCache;
  }
  
  public void setAllowCache(boolean allowCache) {
    this.allowCache = allowCache;
  }
  
  public String getRequestHeader(String name) {
    if (requestHeaders == null) requestHeaders = new HashMap<String,String>();
    return requestHeaders.get(name);
  }
  
  public void setRequestHeader(String name, String value) {
    if (requestHeaders == null) requestHeaders = new HashMap<String,String>();
    requestHeaders.put(name, value);
  }
  
  public String getResponseHeader(String name) {
    if (responseHeaders == null) responseHeaders = new HashMap<String,String>();
    return responseHeaders.get(name);
  }
  
  public void setResponseHeader(String name, String value) {
    if (responseHeaders == null) responseHeaders = new HashMap<String,String>();
    responseHeaders.put(name, value);
  }  
  
  public boolean getCaptureResponseHeaders() {
    return this.captureResponseHeaders;
  }
  
  public void setCaptureResponseHeaders(boolean capture) {
    this.captureResponseHeaders = capture;
  }
}
