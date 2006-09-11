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
package org.apache.abdera.examples.appserver;

import java.util.Date;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException.Code;
import org.apache.abdera.protocol.server.util.AbstractProvider;

public class SimpleProvider 
  extends AbstractProvider
  implements Provider {

  private static Document<Service> service_doc = null;
  private static Document<Feed> feed_doc = null;
  
  public SimpleProvider(AbderaServer abderaServer, SimpleProviderManager factory) {
    super(abderaServer, factory);
  }
  
  private synchronized Document<Service> getService() {
    if (service_doc == null) {
      try {
        Factory factory = getFactory();
        service_doc = factory.newDocument();
        Service service = factory.newService(service_doc);
        Workspace workspace = service.addWorkspace("Simple");
        Collection entries = workspace.addCollection("foo", "atom/foo");
        entries.setAccept("entry");
      } catch (Exception e) {}
    }
    return service_doc;
  }
  
  private synchronized Document<Feed> getFeed() {
    if (feed_doc == null) {
      try {
        Factory factory = getFactory();
        feed_doc = factory.newDocument();
        Feed feed = factory.newFeed(feed_doc);
        feed.setId(FOMHelper.generateUuid());
        feed.setTitle("Foo");
        feed.setUpdated(new Date());
        feed.addLink("http://example.org");
        feed.addLink("", "self");
        feed.addAuthor("Simple Service");
      } catch (Exception e) {}
    }
    return feed_doc;
  }

  public void checkExists(
    RequestContext context) 
      throws AbderaServerException {
  }

  public void checkModified(
    RequestContext context)
      throws AbderaServerException {
  }
  
  public Entry addEntry(
    RequestContext context) 
      throws AbderaServerException {
    try {
      Document<Entry> entry_doc = 
        getEntryFromRequestContext(context);
      Entry entry = null;
      if (entry_doc != null) {
        entry = (Entry) entry_doc.getRoot().clone();
        if (!isValidEntry(entry))
          throw new AbderaServerException(
            AbderaServerException.Code.BADREQUEST, "Invalid Entry", "");
        entry.setUpdated(new Date());
        entry.setModified(entry.getUpdated());
        entry.getIdElement().setValue(FOMHelper.generateUuid());
        entry.addLink("foo/" + entry.getId().toString(), "edit");
        getFeed().getRoot().insertEntry((Entry) entry.clone());
      }
      return entry;
    } catch (Exception e) {
      throw new AbderaServerException(Code.INTERNALSERVERERROR);
    }
  }

  public void deleteEntry(
    RequestContext context) 
      throws AbderaServerException {
    Entry entry = getEntryFromFeed(context);
    if (entry != null)
      entry.discard();
    else
      throw new AbderaServerException(Code.NOTFOUND);
  }

  public Entry getEntry(
    RequestContext context) 
      throws AbderaServerException {
    Entry entry = getEntryFromFeed(context);
    if (entry != null)
      return (Entry)entry.clone();
    else
      throw new AbderaServerException(Code.NOTFOUND);
  }

  public Entry updateEntry(
    RequestContext context) 
      throws AbderaServerException {
    Entry entry = getEntryFromFeed(context);
    if (entry == null) throw new AbderaServerException(Code.NOTFOUND);
    try {
      Document<Entry> entry_doc = getEntryFromRequestContext(context);
      if (entry_doc == null) 
        throw new AbderaServerException(
          AbderaServerException.Code.UNSUPPORTEDMEDIATYPE, 
          "Unsupported Media Type", "");
      Document<Feed> feed_doc = getFeed();
      Entry e = (Entry) entry_doc.getRoot().clone();
      // check to see if it's a valid atom entry
      if (!isValidEntry(entry))
        throw new AbderaServerException(
          AbderaServerException.Code.BADREQUEST, "Invalid Entry", "");
      // check to see if the atom:id matches
      if (!e.getId().equals(entry.getId()))
        throw new AbderaServerException(
          AbderaServerException.Code.CONFLICT, "Conflict. Cannot change atom:id", "");
      // override atom:updated 
      e.setUpdated(new Date());
      // set the app:modified date
      e.setModified(e.getUpdated());
      // make sure the edit link stays the same
      Link editLink = e.getEditLink();
      if (editLink == null) 
        e.addLink("foo/" + e.getId().toString(), "edit");
      else editLink.setHref("foo/" + e.getId().toString());
      // discard the original
      entry.discard();
      // insert the new
      e = (Entry) e.clone();
      feed_doc.getRoot().insertEntry(e);
      return e;
    } catch (Exception e) {
      throw new AbderaServerException(e);
    }
  }
  
  public Feed getFeed(
    RequestContext context) 
      throws AbderaServerException {
    return getFeed().getRoot();
  }

  public Source getFeedForEntry(
    Entry entry) 
      throws AbderaServerException {
    return getFeed().getRoot().getAsSource();
  }

  public Service getService(
    RequestContext context)
      throws AbderaServerException {
    return getService().getRoot();
  }

  private Entry getEntryFromFeed(RequestContext context) {
    try {
      Target target = context.getTarget();
      Document<Feed> doc_feed = getFeed();
      String id = target.getValue(2);
      return doc_feed.getRoot().getEntry(id);
    } catch (Exception e) {
      return null;
    }
  }
  
}
