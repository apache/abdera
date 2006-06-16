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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * Required for the Apache Commons HTTP Client.
 */
public class EntryRequestEntity 
  implements RequestEntity {

  Document<Entry> entryDoc = null;
  
  public EntryRequestEntity(Document<Entry> entryDocument) {
    this.entryDoc = entryDocument;
  }
  
  public boolean isRepeatable() {
    return true;
  }

  public void writeRequest(OutputStream out) throws IOException {
    entryDoc.writeTo(out);
  }

  public long getContentLength() {
    int n = 0;
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      entryDoc.writeTo(out);
      n = out.size();
    } catch (Exception e) {}
    return n;
  }

  public String getContentType() {
    return "application/atom+xml; charset=\"utf-8\"";
  }
  
}