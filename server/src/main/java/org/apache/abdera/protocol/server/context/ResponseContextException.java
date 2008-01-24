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
package org.apache.abdera.protocol.server.context;


public class ResponseContextException extends Exception {

  private static final long serialVersionUID = -3031651143835987024L;

  private AbstractResponseContext responseContext;

  public ResponseContextException(AbstractResponseContext responseContext, Throwable t) {
    super(t);
    this.responseContext = responseContext;
  }

  public ResponseContextException(AbstractResponseContext responseContext) {
    super();
    this.responseContext = responseContext;
  }

  public ResponseContextException(int responseCode) {
    this(new EmptyResponseContext(responseCode));
  }

  public ResponseContextException(int responseCode, Throwable t) {
    this(new EmptyResponseContext(responseCode), t);
  }

  public ResponseContextException(String msg, int responseCode) {
    this.responseContext = new EmptyResponseContext(responseCode);
    this.responseContext.setStatusText(msg);
  }

  public AbstractResponseContext getResponseContext() {
    return responseContext;
  }

  @Override
  public String getMessage() {
    return responseContext.getStatusText();
  }
  
}
