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
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.protocol.util.ProtocolConstants;

public interface Request extends ProtocolConstants {

  String getHeader(String name);
  
  List<String> getHeaders(String name);
  
  String[] getHeaderNames();
  
  String getAccept();
  
  String getAcceptCharset();
  
  String getAcceptEncoding();
  
  String getAcceptLanguage();
  
  String getAuthorization();
  
  String getCacheControl();
  
  MimeType getContentType() throws MimeTypeParseException;
  
  Date getDateHeader(String name);
  
  String getIfMatch();
  
  Date getIfModifiedSince();
  
  String getIfNoneMatch();
  
  Date getIfUnmodifiedSince();
  
  long getMaxAge();
  
  long getMaxStale();
  
  long getMinFresh();
  
  boolean isNoCache();
  
  boolean isNoStore();
  
  boolean isNoTransform();
  
  boolean isOnlyIfCached();
  
}
