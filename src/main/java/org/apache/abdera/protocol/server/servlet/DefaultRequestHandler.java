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
package org.apache.abdera.protocol.server.servlet;

import org.apache.abdera.protocol.server.provider.AbstractResponseContext;
import org.apache.abdera.protocol.server.provider.EmptyResponseContext;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.ResponseContext;
import org.apache.abdera.protocol.server.provider.TargetType;

public class DefaultRequestHandler 
  extends AbstractRequestHandler
  implements RequestHandler {

  protected ResponseContext process(
    Provider provider, 
    RequestContext request) {
      
      TargetType type = request.getTarget().getType();    
      String method = request.getMethod();
        
      if (method.equals("GET")) {
        if (type == TargetType.TYPE_SERVICE) {
          return provider.getService(request);
        }
        if (type == TargetType.TYPE_COLLECTION) {
          return provider.getFeed(request);
        }
        if (type == TargetType.TYPE_ENTRY) {
          return provider.getEntry(request);
        }
        if (type == TargetType.TYPE_MEDIA) {
          return provider.getMedia(request);
        }
        if (type == TargetType.TYPE_CATEGORIES) {
          return provider.getCategories(request);
        }
      }
      else if (method.equals("HEAD")) {
        if (type == TargetType.TYPE_SERVICE) {
          return provider.getService(request);
        }
        if (type == TargetType.TYPE_COLLECTION) {
          return provider.getFeed(request);
        }
        if (type == TargetType.TYPE_ENTRY) {
          return provider.getEntry(request);
        }
        if (type == TargetType.TYPE_MEDIA) {
          return provider.getMedia(request);
        }
        if (type == TargetType.TYPE_CATEGORIES) {
          return provider.getCategories(request);
        }
      }
      else if (method.equals("POST")) {
        if (type == TargetType.TYPE_COLLECTION) {
          return provider.createEntry(request);
        }
        if (type == TargetType.TYPE_ENTRY) {
          return provider.entryPost(request);
        }
        if (type == TargetType.TYPE_MEDIA) {
          return provider.mediaPost(request);
        }
      }
      else if (method.equals("PUT")) {
        if (type == TargetType.TYPE_ENTRY) {
          return provider.updateEntry(request);
        }
        if (type == TargetType.TYPE_MEDIA) {
          return provider.updateMedia(request);
        }
      }
      else if (method.equals("DELETE")) {
        if (type == TargetType.TYPE_ENTRY) {
          return provider.deleteEntry(request);
        }
        if (type == TargetType.TYPE_MEDIA) {
          return provider.deleteMedia(request);
        }
      } 
      else if (method.equals("OPTIONS")) {
        AbstractResponseContext rc = new EmptyResponseContext(200);
        rc.addHeader("Allow", combine(getAllowedMethods(type)));
        return rc;
      }
      return null;
  }
  
  protected String[] getAllowedMethods(TargetType type) {
    if (type == null)                       return new String[0];
    if (type == TargetType.TYPE_COLLECTION) return new String[] { "GET", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_CATEGORIES) return new String[] { "GET", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_ENTRY)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_MEDIA)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_SERVICE)    return new String[] { "GET", "HEAD", "OPTIONS" };
    return new String[] { "GET", "HEAD", "OPTIONS" };
  }
  
}
