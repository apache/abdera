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
package org.apache.abdera.protocol.client;

/**
 * RequestExceptions will be thrown optionally depending on the settings in
 * RequestOptions for 4xx and 5xx responses.  This allows client implementations
 * to use proper java error handling when error responses are encountered 
 */
public final class RequestException 
  extends ClientException {

  private static final long serialVersionUID = -4644092407795348739L;
  private final ClientResponse response;
  
  protected RequestException(ClientResponse response) {
    super(response.getStatus() + "::" + response.getStatusText());
    this.response = response;
  }
  
  public ClientResponse getClientResponse() {
    return response;
  }
  
  public int getStatus() {
    return response.getStatus();
  }
  
}
