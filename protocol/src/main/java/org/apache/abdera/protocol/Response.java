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

import java.util.Date;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.util.EntityTag;

public interface Response
  extends Message {

  public static enum ResponseType {
    SUCCESS, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN;
    
    public static ResponseType select(int status) {
      if (status >= 200 && status < 300) return SUCCESS;
      if (status >= 300 && status < 400) return REDIRECTION;
      if (status >= 400 && status < 500) return CLIENT_ERROR;
      if (status >= 500 && status < 600) return SERVER_ERROR;
      return UNKNOWN;
    }
    
  }
  
  EntityTag getEntityTag();
    
  ResponseType getType();
  
  int getStatus();
  
  String getStatusText();
  
  Date getLastModified();
  
  long getContentLength();
  
  String getAllow();
  
  IRI getLocation();
  
  boolean isPrivate();
  
  boolean isPublic();
  
  boolean isMustRevalidate();
  
  boolean isProxyRevalidate();
  
  long getSMaxAge();
  
  long getAge();
  
  Date getExpires();
  
  String[] getNoCacheHeaders();
  
  String[] getPrivateHeaders();
  
}
