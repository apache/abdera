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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import org.apache.abdera2.common.date.Duration;
import org.apache.abdera2.common.date.Interval;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.activities.model.TypeAdapter;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.activities.model.objects.Address;
import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.PlaceObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings("unchecked")
public class GsonIO extends IO {

  private final Gson gson;
  private final BaseAdapter asbs;
  
  public GsonIO() {
    this.asbs = new BaseAdapter();
    this.gson = gson(false,(BaseAdapter)asbs);
  }
  
  public GsonIO(TypeAdapter<?>... adapters) {
    this.asbs = new BaseAdapter();
    this.gson = gson(false,asbs, adapters);
  }
  
  public GsonIO(Boolean prettyprint) {
    this.asbs = new BaseAdapter();
    this.gson = gson(prettyprint,(BaseAdapter)asbs);
  }
  
  public GsonIO(Boolean prettyprint, TypeAdapter<?>... adapters) {
    this.asbs = new BaseAdapter();
    this.gson = gson(prettyprint,asbs, adapters);
  }
  
  private static Gson gson(Boolean pretty, BaseAdapter asbs, TypeAdapter<?>... adapters) {
    GsonBuilder gb = new GsonBuilder();    
    gb.registerTypeHierarchyAdapter(Verb.class, new VerbAdapter());
    gb.registerTypeHierarchyAdapter(Lang.class, new LangAdapter());
    gb.registerTypeHierarchyAdapter(ASBase.class,  asbs);  
    gb.registerTypeAdapter(ASBase.class, asbs);
    gb.registerTypeAdapter(Date.class, new DateAdapter());
    gb.registerTypeAdapter(Duration.class, new DurationAdapter());
    gb.registerTypeAdapter(Interval.class, new IntervalAdapter());
    gb.registerTypeAdapter(Activity.class,  asbs);
    gb.registerTypeAdapter(PlaceObject.class, asbs);
    gb.registerTypeAdapter(Mood.class, asbs);
    gb.registerTypeAdapter(Address.class, asbs);
    gb.registerTypeAdapter(IRI.class, new IriAdapter());
    gb.registerTypeAdapter(IsoPosition.class, new PositionAdapter());
    gb.registerTypeAdapter(EntityTag.class, new EntityTagAdapter());
    gb.registerTypeAdapter(Template.class, new TemplateAdapter());
    for(TypeAdapter<?> adapter : adapters) {
      if (adapter instanceof GsonTypeAdapter) {
        gb.registerTypeAdapter(
          adapter.getAdaptedClass(), adapter);
      }
    }
    gb.enableComplexMapKeySerialization();
    if (pretty)
      gb.setPrettyPrinting();
    return gb.create();
  }
  
  public String write(ASBase base) {
    return gson.toJson(base);
  }
  
  public void write(ASBase base, Appendable writer) {
    gson.toJson(base, writer);
  }

  public <T extends ASBase>T read(Reader reader) {
    return (T)gson.fromJson(reader, ASBase.class);
  }
  
  public <T extends ASBase>T read(String json) {
    return (T)gson.fromJson(json, ASBase.class);
  }
  
  public Activity readActivity(Reader reader) {
    return gson.fromJson(reader, Activity.class);
  }
  
  public Activity readActivity(String json) {
    return gson.fromJson(json, Activity.class);
  }
  
  public <T extends ASObject>Collection<T> readCollection(Reader reader) {
    return gson.fromJson(reader, Collection.class);
  }
  
  public <T extends ASObject>Collection<T> readCollection(String json) {
    return gson.fromJson(json, Collection.class);
  }
  
  public <T extends ASObject>T readObject(Reader reader) {
    return (T)gson.fromJson(reader, ASObject.class);
  }
  
  public <T extends ASObject>T readObject(String json) {
    return (T)gson.fromJson(json, ASObject.class);
  }
  
  public MediaLink readMediaLink(Reader reader) {
    return gson.fromJson(reader, MediaLink.class);
  }
  
  public MediaLink readMediaLink(String json) {
    return gson.fromJson(json, MediaLink.class);
  }

  @Override
  public void addPropertyMapping(String name, Class<?> _class) {
    asbs.addPropertyMap(name, _class);
  }

  @Override
  public void addObjectMapping(Class<? extends ASObject>... _class) {
    asbs.addObjectMap(_class);
  }
  
  public void writeCollection(
    OutputStream out, 
    String charset,
    ASBase header,
    Iterable<ASObject> objects) {
    try {
      OutputStreamWriter outw = 
        new OutputStreamWriter(out,charset);
      writeCollection(outw,header,objects);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void writeCollection(
    Writer out,
    ASBase header,
    Iterable<ASObject> objects) {
    try {
      JsonWriter writer = new JsonWriter(out);
      writer.beginObject();
      if (header != null) {
        for (String name : header) {
          Object val = header.getProperty(name);
          writer.name(name);
          if (val != null) {
            gson.toJson(val,val.getClass(),writer);
          } else writer.nullValue();
        }
      }
      writer.name("items");
      writer.beginArray();
      for (ASObject obj : objects) {
        gson.toJson(obj,ASBase.class,writer);
      }
      writer.endArray();
      writer.endObject();
      writer.flush();
      if (autoclose)
        writer.close();
    } catch (IOException t) {
      throw new RuntimeException(t);
    }
  }
  
  public CollectionWriter getCollectionWriter(Writer out) {
    return new GsonCollectionWriter(gson,out, autoclose);
  }
  
  private static class GsonCollectionWriter
    implements CollectionWriter {
    private final JsonWriter writer;
    private final Gson gson;
    private final boolean autoclose;
    private boolean _items = false;
    private boolean _header = false;
    GsonCollectionWriter(Gson gson, Writer out, boolean autoclose) {
      this.gson = gson;
      this.writer = new JsonWriter(out);
      this.autoclose = autoclose;
      try {
        writer.beginObject();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    public void writeHeader(ASBase base) {
      if (_items || _header)
        throw new IllegalStateException();
      try {
        if (base != null) {
          for (String name : base) {
            Object val = base.getProperty(name);
            writer.name(name);
            if (val != null) {
              gson.toJson(val,val.getClass(),writer);
            } else writer.nullValue();
          }
        }
        _header = true;
        writer.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    public void writeObject(ASObject object) {
      try {
        if (!_items) {
          writer.name("items");
          writer.beginArray();
          _items = true;
        }
        gson.toJson(object,ASBase.class,writer);
        writer.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    public void complete() {
      try {
        if (_items) writer.endArray();
        writer.endObject();
        writer.flush();
        if (autoclose) 
          writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
