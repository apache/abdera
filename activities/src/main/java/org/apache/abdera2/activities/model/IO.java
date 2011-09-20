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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.anno.DefaultImplementation;

/**
 * Primary interface for serializing/deserializing Activity objects. 
 * Instances of IO are threadsafe. 
 */
@DefaultImplementation("org.apache.abdera2.activities.io.gson.GsonIO")
public abstract class IO {

  protected boolean autoclose = false;
  protected String defcharset = "UTF-8";
  
  /**
   * True if streams and writers should be automatically closed when
   * the IO instance is done with them. Applies to both reads and writes
   */
  public void setAutoClose(boolean autoclose) {
    this.autoclose = true;
  }
  
  public boolean getAutoClose() {
    return autoclose;
  }
  
  /**
   * Adds a mapping of a property name to a specific value class. The 
   * serializer/deserializer will use this to select the appropriate 
   * type adapter for the property.
   */
  public abstract void addPropertyMapping(String name, Class<?> _class);
  
  /**
   * Registers an appropriate objectType mapping for the object. This is
   * used to automatically select an appropriate class for individual 
   * "objectType" values.
   */
  public abstract void addObjectMapping(Class<? extends ASObject>... _class);
  
  public void setDefaultCharset(String charset) {
    this.defcharset = charset!=null?charset:"UTF-8";
  }
  
  public String getDefaultCharset() {
    return defcharset;
  }
  
  public String write(
    ASBase base) {
    StringBuilder buf = 
      new StringBuilder();
    write(base, buf);
    return buf.toString();
  }
  
  public abstract void write(
    ASBase base, 
    Appendable writer);
  
  public void write(ASBase base, OutputStream out) {
    write(base,out,null);
  }
  
  public void write(ASBase base, OutputStream out, String charset) {
    try {
      OutputStreamWriter writer = 
        new OutputStreamWriter(
          out,charset!=null?charset:defcharset);
      write(base,writer);
      writer.flush();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  public <T extends ASBase>T read(InputStream in, String charset) {
    try {
      return read(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public Activity readActivity(InputStream in, String charset) {
    try {
      return readActivity(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public <T extends ASObject>Collection<T> readCollection(InputStream in, String charset) {
    try {
      return readCollection(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public <T extends ASObject> T readObject(InputStream in, String charset) {
    try {
      return readObject(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public MediaLink readMediaLink(InputStream in, String charset) {
    try {
      return readMediaLink(new InputStreamReader(in,charset));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  public abstract <T extends ASBase>T read(Reader reader);
  public abstract <T extends ASBase>T read(String json);
  public abstract Activity readActivity(Reader reader);
  public abstract Activity readActivity(String json);
  public abstract <T extends ASObject>Collection<T> readCollection(Reader reader);
  public abstract <T extends ASObject>Collection<T> readCollection(String json);
  public abstract <T extends ASObject>T readObject(Reader reader);
  public abstract <T extends ASObject>T readObject(String json);
  public abstract MediaLink readMediaLink(Reader reader);
  public abstract MediaLink readMediaLink(String json);
  
  public static IO get(TypeAdapter<?>... adapters) {    
    String defaultImpl = 
      AnnoUtil.getDefaultImplementation(IO.class);
    return Discover.locate(
        IO.class, 
        defaultImpl, 
        (Object)adapters);  
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
  
  public abstract void writeCollection(
    Writer out,
    ASBase header,
    Iterable<ASObject> objects);
  
  public CollectionWriter getCollectionWriter(OutputStream out, String charset) {
    try {
      return getCollectionWriter(new OutputStreamWriter(out,charset));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public abstract CollectionWriter getCollectionWriter(Writer out);
}
