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
import java.util.Date;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.common.protocol.AbstractCollectionAdapter;
import org.apache.abdera2.common.protocol.CollectionAdapter;
import org.apache.abdera2.common.protocol.CollectionInfo;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.ResponseContextException;

@SuppressWarnings("unchecked")
public abstract class AbstractActivitiesCollectionAdapter
  extends AbstractCollectionAdapter
  implements CollectionAdapter, 
             CollectionInfo {

  public String[] getAccepts(RequestContext request) {
    return new String[] {"application/json"};
  }
  
  protected <S extends ResponseContext>S buildCreateEntryResponse(String link, ASBase base) {
    ActivitiesResponseContext<ASBase> rc = 
      new ActivitiesResponseContext<ASBase>(base);
    rc.setLocation(link);
    rc.setContentLocation(rc.getLocation().toString());
    rc.setEntityTag(AbstractActivitiesProvider.calculateEntityTag(base));
    rc.setStatus(201);
    return (S)rc;
  }

  protected <S extends ResponseContext>S buildGetEntryResponse(RequestContext request, ASObject base)
      throws ResponseContextException {
      base.setSource(createSourceObject(request));
      ActivitiesResponseContext<ASObject> rc = 
        new ActivitiesResponseContext<ASObject>(base);
      rc.setEntityTag(AbstractActivitiesProvider.calculateEntityTag(base));
      return (S)rc;
  }

  protected <S extends ResponseContext>S buildGetFeedResponse(Collection<ASObject> collection) {
      ActivitiesResponseContext<Collection<ASObject>> rc = 
        new ActivitiesResponseContext<Collection<ASObject>>(collection);
      rc.setEntityTag(AbstractActivitiesProvider.calculateEntityTag(collection));
      return (S)rc;
  }

  protected ServiceObject createSourceObject(RequestContext request) throws ResponseContextException {
    ServiceObject object = new ServiceObject();
    object.setDisplayName(getTitle(request));
    object.setId(getId(request));
    PersonObject personObject = new PersonObject();
    personObject.setDisplayName(getAuthor(request));
    object.setProperty("author", personObject);
    return object;
  }
  
  /**
   * Create the base feed for the requested collection.
   */
  protected Collection<ASObject> createCollectionBase(RequestContext request) throws ResponseContextException {
      Collection<ASObject> collection = 
        new Collection<ASObject>();
      collection.setProperty("id", getId(request));
      collection.setProperty("title", getTitle(request));
      collection.setProperty("updated", new Date());  
      PersonObject personObject = new PersonObject();
      personObject.setDisplayName(getAuthor(request));
      collection.setProperty("author", personObject);
      return collection;
  }

  protected ASObject getEntryFromRequest(RequestContext request) throws ResponseContextException {
      ASObject object;
      try {
          ActivitiesRequestContext context = (ActivitiesRequestContext) request;
          object = context.getEntity();
      } catch (IOException e) {
          throw new ResponseContextException(500, e);
      }
      return object;
  }

}
