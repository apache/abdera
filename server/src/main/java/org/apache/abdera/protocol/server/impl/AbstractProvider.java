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

import java.util.Date;


import org.apache.abdera.model.Categories;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.util.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractProvider 
  extends ProviderSupport
  implements Provider {

  private final static Log log = LogFactory.getLog(AbstractProvider.class);
  
  protected int defaultpagesize = 10;
  
  protected AbstractProvider() {}
  
  protected AbstractProvider(int defaultpagesize) {
    this.defaultpagesize = defaultpagesize;
  }
  
  public ResponseContext request(RequestContext request) {
    TargetType type = request.getTarget().getType();
    String method = request.getMethod();
    log.debug(Messages.format("TARGET.TYPE",type));
    log.debug(Messages.format("TARGET.ID",request.getTarget().getIdentity()));
    log.debug(Messages.format("METHOD",method));
    if (method.equals("GET")) {
      if (type == TargetType.TYPE_SERVICE) {
        return getService(request);
      }
      if (type == TargetType.TYPE_COLLECTION) {
        return getFeed(request);
      }
      if (type == TargetType.TYPE_ENTRY) {
        return getEntry(request);
      }
      if (type == TargetType.TYPE_MEDIA) {
        return getMedia(request);
      }
      if (type == TargetType.TYPE_CATEGORIES) {
        return getCategories(request);
      }
    }
    else if (method.equals("HEAD")) {
      if (type == TargetType.TYPE_SERVICE) {
        return getService(request);
      }
      if (type == TargetType.TYPE_COLLECTION) {
        return getFeed(request);
      }
      if (type == TargetType.TYPE_ENTRY) {
        return getEntry(request);
      }
      if (type == TargetType.TYPE_MEDIA) {
        return getMedia(request);
      }
      if (type == TargetType.TYPE_CATEGORIES) {
        return getCategories(request);
      }
    }
    else if (method.equals("POST")) {
      if (type == TargetType.TYPE_COLLECTION) {
        return createEntry(request);
      }
      if (type == TargetType.TYPE_ENTRY) {
        return entryPost(request);
      }
      if (type == TargetType.TYPE_MEDIA) {
        return mediaPost(request);
      }
    }
    else if (method.equals("PUT")) {
      if (type == TargetType.TYPE_ENTRY) {
        return updateEntry(request);
      }
      if (type == TargetType.TYPE_MEDIA) {
        return updateMedia(request);
      }
    }
    else if (method.equals("DELETE")) {
      if (type == TargetType.TYPE_ENTRY) {
        return deleteEntry(request);
      }
      if (type == TargetType.TYPE_MEDIA) {
        return deleteMedia(request);
      }
    } 
    else if (method.equals("OPTIONS")) {
      AbstractResponseContext rc = new EmptyResponseContext(200);
      rc.addHeader("Allow", combine(getAllowedMethods(type)));
      return rc;
    }
    return notallowed(
      request.getAbdera(), 
      request, 
      Messages.get("NOT.ALLOWED"), 
      getAllowedMethods(
        request.getTarget().getType()));
  }
  
  public String[] getAllowedMethods(TargetType type) {
    if (type == null)                       return new String[0];
    if (type == TargetType.TYPE_COLLECTION) return new String[] { "GET", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_CATEGORIES) return new String[] { "GET", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_ENTRY)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_MEDIA)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_SERVICE)    return new String[] { "GET", "HEAD", "OPTIONS" };
    return new String[] { "GET", "HEAD", "OPTIONS" };
  }
  
  protected String combine(String... vals) {
    StringBuffer buf = new StringBuffer();
    for(String val : vals) {
      if (buf.length() > 0) buf.append(", ");
      buf.append(val);
    }
    return buf.toString();
  }
  
  public ResponseContext entryPost(
    RequestContext request) {
      return notallowed(
        request.getAbdera(), 
        request, 
        Messages.get("NOT.ALLOWED"), 
        getAllowedMethods(
          request.getTarget().getType()));
  }
    
  public ResponseContext mediaPost(
    RequestContext request) {
      return notallowed(
        request.getAbdera(), 
        request, 
        Messages.get("NOT.ALLOWED"), 
        getAllowedMethods(
          request.getTarget().getType()));
  } 

  public ResponseContext getCategories(
    RequestContext request) {
      Categories cats = request.getAbdera().newCategories();
      return returnBase(cats.getDocument(), 200, new Date());
  }
  
  public ResponseContext deleteMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
    
  public ResponseContext getMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
  
  public ResponseContext updateMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
}
