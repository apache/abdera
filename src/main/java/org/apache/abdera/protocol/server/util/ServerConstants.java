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
package org.apache.abdera.protocol.server.util;

public interface ServerConstants {


  public static final String REQUEST_HANDLER_MANAGER = 
    "org.apache.abdera.protocol.server.servlet.RequestHandlerManager";
  
  public static final String PROVIDER_MANAGER =
    "org.apache.abdera.protocol.server.provider.ProviderManager";
  
  public static final String SUBJECT_RESOLVER = 
    "org.apache.abdera.protocol.server.auth.SubjectResolver";
  
  public static final String TARGET_RESOLVER = 
    "org.apache.abdera.protocol.server.provider.TargetResolver";
  
  public static final String SERVICE_CONTEXT =
    "org.apache.abdera.protocol.server.ServiceContext";
}
