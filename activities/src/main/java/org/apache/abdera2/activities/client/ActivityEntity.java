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
package org.apache.abdera2.activities.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;

/**
 * Required for the Apache Commons HTTP AbderaClient.
 */
public class ActivityEntity
  extends BasicHttpEntity 
  implements HttpEntity {

    private final ASBase base;
    private final Header ct;
    private final long len;
    private byte[] buf = null;

    /**
     * Create the entity using the specified Activity element. 
     * The content length will not be calculated automatically so the 
     * Content-Length header in the part will not be specified. The 
     * Content-Type will be automatically detected based on the type of
     * element passed in.
     */
    public ActivityEntity(ASBase base) {
      this(base,false,null);
    }
    
    /**
     * Create the entity using the specified Activity document or element.
     * The content length will not be calculated. The Content-Type 
     * specified will be used. If contentType is null, the Content-Type 
     * will be automatically detected based on the type of element passed in.
     */
    public ActivityEntity(ASBase base, String contentType) {
      this(base,false,contentType);
    }
    
    /**
     * Create the entity using the specified Activity document or element.
     * The content length will be automatically calculated if the 
     * calclen argument is true. The Content-Type will be automatically
     * detected based on the type of element passed in.
     */
    public ActivityEntity(ASBase base, boolean calclen) {
      this(base,calclen,null);
    }
    
    /**
     * Create the entity using the specified Activity document or element.
     * The content length will be automatically calculated if the 
     * calclen argument is true. The Content-Type specified will be
     * used. If contentType is null, the Content-Type will be automatically
     * detected based on the type of element passed in.
     */
    public ActivityEntity(ASBase base, boolean calclen, String contentType) {
        this.base = base;
        this.ct = new BasicHeader(
          "Content-Type", 
          contentType!=null?contentType:"application/json");
        if (calclen) {
          ByteArrayOutputStream out = 
            new ByteArrayOutputStream();
          base.writeTo(out);
          buf = out.toByteArray();
          len = buf.length;
        } else len = -1;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeTo(OutputStream out) throws IOException {
      base.writeTo(out);
    }
    
    public InputStream getContent() {
      try {
        if (buf == null) {
          ByteArrayOutputStream out = 
            new ByteArrayOutputStream();
          base.writeTo(out);
          buf = out.toByteArray();
        }
        return new ByteArrayInputStream(buf);
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    public long getContentLength() {
        return len;
    }

    public boolean isChunked() {
      return len == -1;
    }
    
    public Header getContentType() {
        return ct;
    }

}
