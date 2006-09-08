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

import java.io.IOException;

import javax.activation.MimeTypeParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;

public abstract class AbstractProvider 
  implements Provider {

  private final AbderaServer abderaServer;
  
  protected AbstractProvider(AbderaServer abderaServer) {
    this.abderaServer = abderaServer;
  }
  
  protected AbderaServer getAbderaServer() {
    return abderaServer;
  }
  
  protected Abdera getAbdera() {
    return getAbderaServer().getAbdera();
  }
  
  protected Factory getFactory() {
    return getAbdera().getFactory();
  }
  
  protected Parser getParser() {
    return getAbdera().getParser();
  }


  @SuppressWarnings("unchecked")
  protected Document<Entry> getEntryFromRequestContext(
    RequestContext context) 
      throws IOException, 
             MimeTypeParseException {
    String ctype = (context.getContentType() != null) ?
      context.getContentType().toString() : null;
    if (ctype != null && ctype.length() != 0 &&
        (!MimeTypeHelper.isMatch(ctype, Constants.ATOM_MEDIA_TYPE) &&
         !MimeTypeHelper.isMatch(ctype, Constants.XML_MEDIA_TYPE))) {
      return null;
    }
    // might still be an atom entry, let's try it
    try {
      Parser parser = getParser();
      Document doc = parser.parse(context.getInputStream());
      Element root = doc.getRoot();
      if (root != null && root.getQName().equals(Constants.ENTRY)) {
        return doc;
      }
    } catch (Exception e) {}
    return null;
  }
  
  protected boolean isValidEntry(Entry entry) {
    try {
      if (entry.getId() == null || 
          entry.getId().toString().length() == 0) return false;
      if (entry.getTitle() == null) return false;
      if (entry.getAuthor() == null) return false;
      if (entry.getUpdated() == null) return false;
      if (entry.getContent() == null) {
        if (entry.getAlternateLink() == null) return false;
        if (entry.getSummary() == null) return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
  
}
