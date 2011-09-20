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
package org.apache.abdera2.activities.io.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.MimeType;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.activities.model.*;
import org.apache.abdera2.activities.model.objects.AccountObject;
import org.apache.abdera2.activities.model.objects.Address;
import org.apache.abdera2.activities.model.objects.ArticleObject;
import org.apache.abdera2.activities.model.objects.AudioObject;
import org.apache.abdera2.activities.model.objects.BadgeObject;
import org.apache.abdera2.activities.model.objects.BookmarkObject;
import org.apache.abdera2.activities.model.objects.CommentObject;
import org.apache.abdera2.activities.model.objects.EventObject;
import org.apache.abdera2.activities.model.objects.FileObject;
import org.apache.abdera2.activities.model.objects.GroupObject;
import org.apache.abdera2.activities.model.objects.ImageObject;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.NameObject;
import org.apache.abdera2.activities.model.objects.NoteObject;
import org.apache.abdera2.activities.model.objects.OrganizationObject;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.PlaceObject;
import org.apache.abdera2.activities.model.objects.ProductObject;
import org.apache.abdera2.activities.model.objects.QuestionObject;
import org.apache.abdera2.activities.model.objects.ReviewObject;
import org.apache.abdera2.activities.model.objects.ServiceObject;
import org.apache.abdera2.activities.model.objects.VideoObject;
import org.apache.abdera2.activities.protocol.ErrorObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * (De)serialization of ASBase object
 */
public class BaseAdapter 
  implements GsonTypeAdapter<ASBase> {

  private final Map<String,Class<?>> map = 
    new ConcurrentHashMap<String,Class<?>>();
  private final Map<String,Class<? extends ASObject>> objsmap =
    new ConcurrentHashMap<String,Class<? extends ASObject>>();
  
  public BaseAdapter() {
    initPropMap();
  }
  
  @SuppressWarnings("unchecked")
  private void initPropMap() {
    map.put("verb",Verb.class);
    map.put("url",IRI.class);
    map.put("fileurl", IRI.class);
    map.put("gadget", IRI.class);
    map.put("updated", Date.class);
    map.put("published", Date.class);
    map.put("lang", Lang.class);
    map.put("icon", MediaLink.class);
    map.put("image", MediaLink.class);
    map.put("totalitems", Integer.class);
    map.put("duration", Integer.class);
    map.put("height", Integer.class);
    map.put("location", PlaceObject.class);
    map.put("mood", Mood.class);
    map.put("address", Address.class);
    map.put("stream", MediaLink.class);
    map.put("fullimage", MediaLink.class);
    map.put("endtime", Date.class);
    map.put("starttime", Date.class);
    map.put("mimetype", MimeType.class);
    map.put("rating", Double.class);
    map.put("position", IsoPosition.class);
    map.put("etag", EntityTag.class);
    
    // From the replies spec
    map.put("attending", Collection.class);
    map.put("followers", Collection.class);
    map.put("following", Collection.class);
    map.put("friends", Collection.class);
    map.put("friend-requests", Collection.class);
    map.put("likes", Collection.class);
    map.put("notAttending", Collection.class);
    map.put("maybeAttending", Collection.class);
    map.put("members", Collection.class);
    map.put("replies", Collection.class);
    map.put("reviews", Collection.class);
    map.put("saves", Collection.class);
    map.put("shares", Collection.class);
    
    processType(
      objsmap,map,
      Address.class,
      Activity.class,
      ArticleObject.class,
      AudioObject.class,
      BadgeObject.class,
      BookmarkObject.class,
      Collection.class,
      CommentObject.class,
      EventObject.class,
      FileObject.class,
      GroupObject.class,
      ImageObject.class,
      NoteObject.class,
      PersonObject.class,
      PlaceObject.class,
      ProductObject.class,
      QuestionObject.class,
      ReviewObject.class,
      ServiceObject.class,
      VideoObject.class,
      ErrorObject.class,
      NameObject.class,
      AccountObject.class,
      OrganizationObject.class);
  }
  
  private static void processType(
    Map<String,Class<? extends ASObject>> map, 
    Map<String,Class<?>> propsmap,
    Class<? extends ASObject>... _classes) {
    for (Class<? extends ASObject> _class : _classes) {
      String name = AnnoUtil.getName(_class);
      map.put(name, _class);
      if (_class.isAnnotationPresent(Properties.class)) {
        Properties props = _class.getAnnotation(Properties.class);
        for (Property prop : props.value()) {
          String _propname = prop.name();
          Class<?> _propclass = prop.to();
          propsmap.put(_propname, _propclass);
        }
      }
    }
  }
  
  public void addObjectMap(Class<? extends ASObject>... _class) {
    processType(objsmap,map,_class);
  }
  
  public void addPropertyMap(String name, Class<?> _class) {
    map.put(name,_class);
  }
  
  public JsonElement serialize(
    ASBase asbase, 
    Type type,
    JsonSerializationContext context) {

    JsonObject el = new JsonObject();
    
    for (String key : asbase) {
      Object val = asbase.getProperty(key);
      if (val != null) {
        JsonElement value = null;
        if (val instanceof Verb)
          value = context.serialize(val, Verb.class);
        else 
          value = context.serialize(val, val.getClass());
        el.add(key, value);
      }
    }
    
    return el;
  }

  public ASBase deserialize(
    JsonElement el, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    JsonObject obj = (JsonObject)el;
    ASBase base = null;
    if (type == Collection.class) {
      base = new Collection<ASObject>();
    } else if (type == Activity.class) {
      base = new Activity();
    } else if (type == MediaLink.class) {
      base = new MediaLink();
    } else if (type == PlaceObject.class) {
      base = new PlaceObject();
    } else if (type == Mood.class) {
      base = new Mood();
    } else if (type == Address.class) {
      base = new Address();
    } else {
      JsonPrimitive ot = obj.getAsJsonPrimitive("objectType");
      if (ot != null) {
        String ots = ot.getAsString();
        Class<? extends ASObject> _class = objsmap.get(ots);
        if (_class != null) {
          base = Discover.locate(_class, _class.getName());
          try {
            base = _class.newInstance();
          } catch (Throwable t) {}
          
        } else base = new ASObject(ots);
      } else {
        if (obj.has("verb") && (obj.has("actor") || obj.has("object") || obj.has("target"))) {
          base = new Activity();
        } else if (obj.has("items") && obj.has("totalItems")) {
          base = new Collection<ASObject>();
        } else {
          base = new ASObject(); // anonymous object
        }
      }
    }
    for (Entry<String,JsonElement> entry : obj.entrySet()) {
      String name = entry.getKey();
      Class<?> _class = map.get(name.toLowerCase(Locale.US));
      JsonElement val = entry.getValue();
      if (val.isJsonPrimitive()) {
        if (_class != null) {
          base.setProperty(name, context.deserialize(val,_class));
        } else {
          JsonPrimitive prim = val.getAsJsonPrimitive();
          if (prim.isBoolean()) 
            base.setProperty(name, prim.getAsBoolean());
          else if (prim.isNumber())
            base.setProperty(name, prim.getAsNumber());
          else {
            base.setProperty(name, prim.getAsString());
          }
        }
      } else if (val.isJsonArray()) {
        List<Object> list = new ArrayList<Object>();
        JsonArray arr = val.getAsJsonArray();
        processArray(arr, _class, context, list);
        base.setProperty(name, list);
      } else if (val.isJsonObject()) {
        if (map.containsKey(name)) {
          base.setProperty(name, context.deserialize(val, map.get(name)));
        } else
          base.setProperty(name, context.deserialize(val, ASObject.class));
      }
    }
    return base;
  }

  private void processArray(JsonArray arr, Class<?> _class, JsonDeserializationContext context, List<Object> list) {
    for (JsonElement mem : arr) {
      if (mem.isJsonPrimitive()) {
        if (_class != null) {
          list.add(context.deserialize(mem, _class));
        } else {
          JsonPrimitive prim2 = (JsonPrimitive) mem;
          if (prim2.isBoolean())
            list.add(prim2.getAsBoolean());
          else if (prim2.isNumber())
            list.add(prim2.getAsNumber());
          else
            list.add(prim2.getAsString());
        }
      } else if (mem.isJsonObject()) {
        list.add(context.deserialize(mem, _class!=null?_class:ASObject.class));
      } else if (mem.isJsonArray()) {
        JsonArray array = mem.getAsJsonArray();
        List<Object> objs = new ArrayList<Object>();
        processArray(array,_class,context,objs);
        list.add(objs);
      }
    }
  }
  
  public Class<ASBase> getAdaptedClass() {
    return ASBase.class;
  }

}
