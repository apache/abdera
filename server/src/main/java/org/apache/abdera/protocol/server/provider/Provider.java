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
package org.apache.abdera.protocol.server.provider;

import org.apache.abdera.protocol.ResponseInfo;

public interface Provider {

  ResponseInfo getInfo(RequestContext request);
  
  ResponseContext createEntry(RequestContext request);
  
  ResponseContext deleteEntry(RequestContext request);
  
  ResponseContext deleteMedia(RequestContext request);
  
  ResponseContext updateEntry(RequestContext request);
  
  ResponseContext updateMedia(RequestContext request);
  
  ResponseContext getService(RequestContext request, boolean full);
  
  ResponseContext getFeed(RequestContext request, boolean full);
  
  ResponseContext getEntry(RequestContext request, boolean full, boolean editable);
  
  ResponseContext getMedia(RequestContext request, boolean full, boolean editable);
  
  ResponseContext entryPost(RequestContext request);
  
  ResponseContext mediaPost(RequestContext request);
  
}
