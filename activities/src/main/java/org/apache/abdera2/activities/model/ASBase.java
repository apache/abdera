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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.Generator.Copyable;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.mediatype.MimeTypeParseException;

public class ASBase 
  implements Iterable<String>, Cloneable, Serializable, Copyable {

  private static final long serialVersionUID = -5432906925445653268L;
  private final Map<String,Object> exts =
    new HashMap<String,Object>();
  
  public ASBase() {}
  
  public void setLang(Lang lang) {
    setProperty("lang", lang);
  }
  
  public Lang getLang() {
    return getProperty("lang");
  }
  
  @SuppressWarnings("unchecked")
  public <T>T getProperty(String name) {
    return (T)exts.get(name);
  }
  
  public void setProperty(String name, Object value) {
    if (value != null)
      exts.put(name, value);
    else if (exts.containsKey(name))
      exts.remove(name);
  }

  public Iterator<String> iterator() {
    return exts.keySet().iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((exts == null) ? 0 : exts.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ASBase other = (ASBase) obj;
    if (exts == null) {
      if (other.exts != null)
        return false;
    } else if (!exts.equals(other.exts))
      return false;
    return true;
  }

  public <T extends ASBase>T as(Class<T> type) {
    try {
      // if we already implement the type, just return 
      // casted as that type... otherwise, create a 
      // new instance and copy all the properties over
      if (type.isAssignableFrom(this.getClass()))
        return type.cast(this);
      ASBase t = type.newInstance();
      t.exts.putAll(exts);
      t.contentType = contentType;
      t.lastModified = lastModified;
      t.entityTag = entityTag;
      t.language = language;
      t.slug = slug;
      t.base = base;      
      return type.cast(t);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
 
  public String toString() {
    return IO.get().write(this);
  }
  
  public void writeTo(IO io, Writer out) {
    io.write(this,out);
  }
  
  public void writeTo(IO io, OutputStream out, String charset) {
    io.write(this,out,charset);
  }
  
  public void writeTo(IO io, OutputStream out) {
    io.write(this,out,"UTF-8");
  }
  
  public void writeTo(Writer out) {
    IO.get().write(this,out);
  }
  
  public void writeTo(Writer out, TypeAdapter<?>... adapters) {
    IO.get(adapters).write(this,out);
  }
  
  public void writeTo(OutputStream out, String charset) {
    try {
      OutputStreamWriter writer = 
        new OutputStreamWriter(out,charset);
      writeTo(writer);
      writer.flush();
    } catch (Throwable t) {}
  }

  public void writeTo(OutputStream out, String charset, TypeAdapter<?>... adapters) {
    try {
      OutputStreamWriter writer =
        new OutputStreamWriter(out,charset);
      writeTo(writer,adapters);
      writer.flush();
    } catch (Throwable t) {}
  }
  
  public void writeTo(OutputStream out) {
    writeTo(out,"UTF-8");
  }

  public void writeTo(OutputStream out, TypeAdapter<?>... adapters) {
    writeTo(out,"UTF-8",adapters);
  }
  
  /////// DOCUMENT PROPERTIES ///////
  
  private MimeType contentType;
  private Date lastModified;
  private EntityTag entityTag;
  private Lang language;
  private String slug;
  private IRI base;
  
  public MimeType getContentType() {
    return contentType;
  }
  
  public void setContentType(String mimeType) {
    try {
      this.contentType = new MimeType(mimeType);
    } catch (javax.activation.MimeTypeParseException t) {
      throw new MimeTypeParseException(t);
    }
  }
  
  public void setContentType(MimeType mimeType) {
    this.contentType = mimeType;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
  public EntityTag getEntityTag() {
    return entityTag;
  }
  
  public void setEntityTag(EntityTag entityTag) {
    this.entityTag = entityTag;
  }
  
  public void setEntityTag(String entityTag) {
    this.entityTag = new EntityTag(entityTag);
  }
  
  public Lang getLanguage() {
    return language;
  }
  
  public void setLanguage(Lang language) {
    this.language = language;
  }
  
  public void setLanguage(String language) {
    this.language = new Lang(language);
  }
  
  public String getSlug() {
    return slug;
  }
  
  public void setSlug(String slug) {
    this.slug = slug;
  }
  
  public IRI getBaseUri() {
    return base;
  }
  
  public void setBaseUri(IRI iri) {
    this.base = iri;
  }
  
  public void setBaseUri(String iri) {
    this.base = new IRI(iri);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <T extends ASObject>Generator<T> newGenerator() {
    return (Generator<T>)new Generator(getClass(),this);
  }

  public Object copy() {
    try {
      Class<?> _class = this.getClass();
      ASBase copy = (ASBase) _class.newInstance();
      for (String name : this) {
        // do a potentially deep copy
        Object val = getProperty(name);
        copy.setProperty(
          name,
          val instanceof Copyable ? 
            ((Copyable)val).copy() : 
            val);
      }
      copy.contentType = contentType;
      copy.lastModified = lastModified;
      copy.entityTag = entityTag;
      copy.language = language;
      copy.slug = slug;
      copy.base = base;
      return copy;
    } catch (Throwable t) {
      throw new RuntimeException();
    }
  }
  
  public boolean has(String name) {
    return exts.containsKey(name);
  }
}


