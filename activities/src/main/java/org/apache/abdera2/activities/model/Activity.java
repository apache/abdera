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
package org.apache.abdera2.activities.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;

/**
 * An Activity. Represents some action that has been taken. At it's core,
 * every Activity consists of an Actor (who performed the action), a Verb
 * (what action was performed) and an Object (what was acted upon), and a 
 * Target (to which the action is directed). For instance, if I said
 * "John posted a photo to his album", the Actor is "John", the Verb is 
 * "post", the Object is "a photo" and the Target is "his album". 
 */
@SuppressWarnings("unchecked")
@Name("activity")
public class Activity extends ASObject {

  private static final long serialVersionUID = -3284781784555866672L;
  public static final String ACTOR = "actor";
  public static final String CONTENT = "content";
  public static final String GENERATOR = "generator";
  public static final String ICON = "icon";
  public static final String ID = "id";
  public static final String OBJECT = "object";
  public static final String PUBLISHED = "published";
  public static final String PROVIDER = "provider";
  public static final String TARGET = "target";
  public static final String TITLE = "title";
  public static final String UPDATED = "updated";
  public static final String URL = "url";
  public static final String VERB = "verb";
  
  public enum Audience { 
    TO, BTO, CC, BCC; 
    String label() {
      return name().toLowerCase();
    }
  };
  
  public Activity() {}
  
  public Activity(
    ASObject actor, 
    Verb verb) {
      setActor(actor);
      setVerb(verb);
  }
  
  public Activity(
    ASObject actor, 
    Verb verb, 
    ASObject object) {
      setActor(actor);
      setVerb(verb);
      setObject(object);
  }
  
  public Activity(
    ASObject actor, 
    Verb verb, 
    ASObject object, 
    ASObject target) {
      setActor(actor);
      setVerb(verb);
      setObject(object);
      setTarget(target);
  }
  
  public ASObject getActor() {
    return getProperty(ACTOR);
  }
  
  public <E extends ASObject>E getActor(boolean create) {
    ASObject obj = getActor();
    if (obj == null && create) {
      obj = new PersonObject();
      setActor(obj);
    }
    return (E)obj;
  }
  
  public void setActor(ASObject actor) {
    setProperty(ACTOR, actor);
  }
  
  public <E extends ASObject>E setActor(String displayName) {
    ASObject obj = getActor(true);
    obj.setDisplayName(displayName);
    return (E)obj;
  }
  
  public String getContent() {
    return getProperty(CONTENT);
  }
  
  public void setContent(String content) {
    setProperty(CONTENT, content);
  }
  
  public <E extends ASObject>E getGenerator() {
    return (E)getProperty(GENERATOR);
  }
  
  public <E extends ASObject>E getGenerator(boolean create) {
    ASObject obj = getGenerator();
    if (obj == null && create) {
      obj = new ServiceObject();
      setGenerator(obj);
    }
    return (E)obj;
  }
  
  public void setGenerator(ASObject generator) {
    setProperty(GENERATOR, generator); 
  }
  
  public <E extends ASObject>E setGenerator(String displayName) {
    ASObject obj = getGenerator(true);
    obj.setDisplayName(displayName);
    return (E)obj;
  }
  
  public MediaLink getIcon() {
    return getProperty(ICON);
  }
  
  public void setIcon(MediaLink icon) {
    setProperty(ICON, icon);  
  }
  
  public String getId() {
    return getProperty(ID);
  }
  
  public void setId(String id) {
    setProperty(ID, id);
  }
  
  public <E extends ASObject>E getObject() {
    return (E)getProperty(OBJECT);
  }
  
  public void setObject(ASObject object) {
    setProperty(OBJECT, object);
  }
  
  public Date getPublished() {
    return getProperty(PUBLISHED);
  }
  
  public void setPublished(Date published) {
    setProperty(PUBLISHED, published);
    
  }
  
  public <E extends ASObject>E getProvider() {
    return (E)getProperty(PROVIDER);
  }
  
  public <E extends ASObject>E getProvider(boolean create) {
    ASObject obj = getProvider();
    if (obj == null && create) {
      obj = new ServiceObject();
      setProvider(obj);
    }
    return (E)obj;
  }
  
  public void setProvider(ASObject provider) {
    setProperty(PROVIDER, provider);
  }
  
  public <E extends ASObject>E setProvider(String displayName) {
    ASObject obj = getProvider(true);
    obj.setDisplayName(displayName);
    return (E)obj;
  }
  
  public <E extends ASObject>E getTarget() {
    return (E)getProperty(TARGET);
  }
  
  public void setTarget(ASObject target) {
    setProperty(TARGET, target);
    
  }
  
  public String getTitle() {
    return getProperty(TITLE);
  }
  
  public void setTitle(String title) {
    setProperty(TITLE, title);
    
  }
  
  public Date getUpdated() {
    return getProperty(UPDATED);
  }
  
  public void setUpdated(Date updated) {
    setProperty(UPDATED, updated);
    
  }
  
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  public void setUrl(IRI url) {
    setProperty(URL, url);
  }
  
  public Verb getVerb() {
    return getProperty(VERB);
  }
  
  public void setVerb(Verb verb) {
    setProperty(VERB, verb);
  }

  @Override
  public <E extends ASObject>E getAuthor() {
    return (E)getActor();
  }
  
  public <E extends ASObject>E getAuthor(boolean create) {
    return (E)getActor(create);
  }

  @Override
  public void setAuthor(ASObject author) {
    setActor(author); 
  }
  
  public <E extends ASObject>E setAuthor(String displayName) {
    return (E)setActor(displayName);
  }

  @Override
  public String getDisplayName() {
    return getTitle();
  }

  @Override
  public void setDisplayName(String displayName) {
    setTitle(displayName);
  }

  @Override
  public MediaLink getImage() {
    return getIcon();
  }

  @Override
  public void setImage(MediaLink image) {
    setIcon(image);
  }
  
  public Iterable<ASObject> getAudience(Audience audience) {
    return getProperty(audience.label());
  }
  
  public void setAudience(Audience audience, Set<ASObject> set) {
    setProperty(audience.label(), set);
  }
  
  public void addAudience(Audience audience, ASObject... objs) {
    Set<ASObject> list = getProperty(audience.label());
    if (list == null) {
      list = new HashSet<ASObject>();
      setProperty(audience.label(),list);
    }
    for (ASObject obj : objs)
      list.add(obj);
  }

}
