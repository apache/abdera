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
package org.apache.abdera2.activities.protocol;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.TypeAdapter;
import org.apache.abdera2.common.protocol.BaseRequestContextWrapper;
import org.apache.abdera2.common.protocol.RequestContext;

public class ActivitiesRequestContext extends BaseRequestContextWrapper {

  private ASBase entity = null;
  
  public ActivitiesRequestContext(RequestContext request) {
    super(request);
  }

  public IO getIO(TypeAdapter<?>... adapters) {
    
    ActivitiesProvider provider = getProvider();
    Set<TypeAdapter<?>> as = 
      new HashSet<TypeAdapter<?>>(provider.getTypeAdapters());
    for (TypeAdapter<?> ta : adapters)
      as.add(ta);
    return IO.get(as.toArray(new TypeAdapter[as.size()]));
  }
  
  @SuppressWarnings("unchecked")
  public <T extends ASBase>T getEntity() throws IOException {
    try {
    if (entity == null) {
      Reader reader = getReader();
      if (reader != null)
        entity = getIO().read(reader);
      else // try input stream, but this should've worked
        entity = getIO().read(getInputStream(), "UTF-8");
    }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    setDocProperties(entity);
    return (T)entity;
  }
  
  private void setDocProperties(ASBase base) {
    String etag = getHeader("ETag");
    if (etag != null)
        base.setEntityTag(etag);
    Date lm = getDateHeader("Last-Modified");
    if (lm != null)
        base.setLastModified(lm);
    MimeType mt = getContentType();
    if (mt != null)
        base.setContentType(mt.toString());
    String language = getContentLanguage();
    if (language != null)
        base.setLanguage(language);
    String slug = getSlug();
    if (slug != null)
        base.setSlug(slug);
  }
}
