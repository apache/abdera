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

public interface HttpResponse {

  HttpResponse setStatus(int status);

  HttpResponse setHeader(String name, String value);

  HttpResponse setContentType(String contentType);

  HttpResponse setDateHeader(String key, long time);

  OutputStream getOutputStream() throws IOException;

  HttpResponse addCookie(String name, String value);

  HttpResponse addCookie(
    String name, 
    String value, 
    String domain,
    String path,
    int maxage,
    String comment);
  
  HttpResponse setContentLength(int length);
  
  HttpResponse sendError(int status) throws IOException;

  HttpResponse sendError(int status, String message) throws IOException;

  HttpResponse sendRedirect(String to) throws IOException;
  
  HttpResponse reset();
}
