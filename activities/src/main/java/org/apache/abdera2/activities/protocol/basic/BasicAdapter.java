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
package org.apache.abdera2.activities.protocol.basic;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.logging.Logger;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.protocol.AbstractActivitiesProvider;
import org.apache.abdera2.activities.protocol.ActivitiesRequestContext;
import org.apache.abdera2.activities.protocol.ActivitiesResponseContext;
import org.apache.abdera2.activities.protocol.ErrorObject;
import org.apache.abdera2.activities.protocol.managed.FeedConfiguration;
import org.apache.abdera2.activities.protocol.managed.ManagedCollectionAdapter;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.ProviderHelper;
import org.apache.abdera2.common.protocol.Target;
import org.apache.abdera2.common.protocol.TargetType;
import org.apache.abdera2.common.protocol.RequestContext.Scope;
import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.abdera2.common.pusher.Pusher;

/**
 * The BasicAdapter provides a simplistic interface for working with Atompub collections with a restricted set of
 * options/features. The idea of the basic adapter is to make it easy to provide a minimally capable Atompub server
 */
@SuppressWarnings("unchecked")
public abstract class BasicAdapter extends ManagedCollectionAdapter {

    public static Logger logger = Logger.getLogger(BasicAdapter.class.getName());

    protected BasicAdapter(FeedConfiguration config) {
        super(config);
    }

    public String getProperty(String key) throws Exception {
        Object val = config.getProperty(key);
        if (val == null) {
            logger.warning("Cannot find property " + key + "in Adapter properties file for feed " + config.getFeedId());
            throw new RuntimeException();
        }
        if (val instanceof String)
            return (String)val;
        throw new RuntimeException();
    }

    protected Collection<ASObject> createCollection() {
      Collection<ASObject> col = new Collection<ASObject>();
      col.setProperty("id", config.getFeedUri());
      col.setProperty("title", config.getFeedTitle());
      col.setProperty("updated", new Date());
      PersonObject person = new PersonObject();
      person.setDisplayName(config.getFeedAuthor());
      col.setProperty("author", person);
      col.setItems(new LinkedHashSet<ASObject>());
      return col;
    }
    
    protected void addEditLinkToObject(ASObject object) throws Exception {
      if (AbstractActivitiesProvider.getEditUriFromEntry(object) == null) {
        object.setProperty("editLink", object.getId());
      }
    }

    protected void setObjectIdIfNull(ASObject object) throws Exception {
        if (object.getId() != null)
            return;
        String uuidUri = UUID.randomUUID().toString();
        String[] segments = uuidUri.split(":");
        String entryId = segments[segments.length - 1];
        object.setId(createEntryIdUri(entryId));
    }

    protected String createEntryIdUri(String entryId) throws Exception {
        return config.getFeedUri() + "/" + entryId;
    }

    private void push(RequestContext context, String channel, ASObject object) {
      if (context.getAttribute(Scope.CONTAINER, "AbderaChannelManager") != null) {
        ChannelManager cm = (ChannelManager) context.getAttribute(Scope.CONTAINER, "AbderaChannelManager");
        if (cm != null) {
          Pusher<ASObject> pusher = cm.getPusher(channel);
          if (pusher != null) {
            pusher.push(object);
          }
        }
      }
    }
    
    private <S extends ResponseContext>S createOrUpdateObject(RequestContext context, boolean createFlag) {
        try {
            ActivitiesRequestContext request = (ActivitiesRequestContext) context;
            MimeType mimeType = request.getContentType();
            String contentType = mimeType == null ? null : mimeType.toString();
            if (contentType != null && !MimeTypeHelper.isJson(contentType))
                return (S)ProviderHelper.notsupported(request);
            
            ASBase base = (ASBase)request.getEntity();
            Target target = request.getTarget();

            if (base instanceof Collection && createFlag && target.getType() == TargetType.TYPE_COLLECTION) {
              // only allow multiposts on collections.. these always create, never update
              Collection<ASObject> coll = (Collection<ASObject>) base;
              Collection<ASObject> retl = new Collection<ASObject>();
              int c = 0;
              for (ASObject inputEntry : coll.getItems()) {
                ASObject newEntry = createItem(inputEntry,c++);
                push(context,target.getParameter(BasicProvider.PARAM_FEED),newEntry);
                if (newEntry != null) {
                  retl.addItem(newEntry);
                } else {
                  ErrorObject err = new ErrorObject();
                  err.setCode(-100);
                  err.setDisplayName("Error adding object");
                  retl.addItem(err);
                }
              }
              ActivitiesResponseContext<Collection<ASObject>> rc = 
                new ActivitiesResponseContext<Collection<ASObject>>(retl);
              rc.setStatus(createFlag?201:200);
              return (S)rc;
            } else if (base instanceof ASObject){
              String entryId = !createFlag ? target.getParameter(BasicProvider.PARAM_ENTRY) : null;
              ASObject inputEntry = (ASObject) base;
              ASObject newEntry = createFlag ? createItem(inputEntry) : updateItem(entryId, inputEntry);
              push(context,target.getParameter(BasicProvider.PARAM_FEED),newEntry);
              if (newEntry != null) {
                  String loc = newEntry.getProperty("editLink");
                  ActivitiesResponseContext<ASObject> rc = 
                    new ActivitiesResponseContext<ASObject>(newEntry);
                  rc.setStatus(createFlag?201:200);
                  rc.setLocation(loc);
                  return (S)rc;
              } else {
                  return (S)ProviderHelper.notfound(request);
              }
            } else {
              return (S)ProviderHelper.notallowed(request);
            }
            
        } catch (Exception e) {
            return (S)ProviderHelper.servererror(context, e.getMessage(), e);
        }
    }

    public <S extends ResponseContext>S postItem(RequestContext request) {
        return createOrUpdateObject(request, true);
    }

    public <S extends ResponseContext>S deleteItem(RequestContext request) {
        Target target = request.getTarget();
        String entryId = target.getParameter(BasicProvider.PARAM_ENTRY);
        try {
            return (S)(deleteItem(entryId) ? ProviderHelper.nocontent() : ProviderHelper.notfound(request));
        } catch (Exception e) {
            return (S)ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public <S extends ResponseContext>S putItem(RequestContext request) {
        return createOrUpdateObject(request, false);
    }

    public <S extends ResponseContext>S getItem(RequestContext request) {
        Target target = request.getTarget();
        String entryId = target.getParameter(BasicProvider.PARAM_ENTRY);
        try {
            ASObject object = getItem(entryId);
            
            if (object != null) {
              ActivitiesResponseContext<ASObject> rc = 
                new ActivitiesResponseContext<ASObject>(object);
              rc.setStatus(200);
              return (S)rc;
            } else return (S)ProviderHelper.notfound(request);       
        } catch (Exception e) {
            return (S)ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public <S extends ResponseContext>S getItemList(RequestContext request) {
        try {
            Collection<ASObject> collection = 
              getCollection();

            if (collection != null) { 
              ActivitiesResponseContext<Collection<ASObject>> rc = 
                new ActivitiesResponseContext<Collection<ASObject>>(collection);
              rc.setStatus(200);
              return (S)rc;
            } else return (S)ProviderHelper.notfound(request);
        } catch (Exception e) {
            return (S)ProviderHelper.servererror(request, e.getMessage(), e);
        }
    }

    public <S extends ResponseContext>S extensionRequest(RequestContext request) {
        return (S)ProviderHelper.notallowed(request, ProviderHelper.getDefaultMethods(request));
    }

    public <S extends ResponseContext>S getCategories(RequestContext request) {
        return (S)ProviderHelper.notfound(request);
    }

    public abstract Collection<ASObject> getCollection() throws Exception;

    public abstract ASObject getItem(Object objectId) throws Exception;

    public abstract ASObject createItem(ASObject object) throws Exception;

    public abstract ASObject createItem(ASObject object, int c) throws Exception;
    
    public abstract ASObject updateItem(Object objectId, ASObject object) throws Exception;

    public abstract boolean deleteItem(Object objectId) throws Exception;

}
