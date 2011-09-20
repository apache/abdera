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
package org.apache.abdera2.protocol.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.protocol.EntityProvider;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;

/**
 * Implementation of HttpEntity based on the EntityProvider interface
 */
public class EntityProviderEntity 
    extends BasicHttpEntity 
    implements HttpEntity {

    private final Abdera abdera;
    private final EntityProvider provider;
    private byte[] buf = null;
    private InputStream content = null;
    private boolean auto_indent = false;
    private String encoding = "UTF-8";
    private final Header ct;

    public EntityProviderEntity(
      EntityProvider provider) {
      this(Abdera.getInstance(),provider);
    }
    
    public EntityProviderEntity(
        Abdera abdera, 
        EntityProvider provider) {
          this.abdera = abdera;
          this.provider = provider;
          this.ct = new BasicHeader(
            "Content-Type", provider.getContentType());
    }

    private void write(OutputStream out) {
        provider.writeTo(
            abdera.create(StreamWriter.class)
              .setOutputStream(out, encoding)
                .setAutoIndent(auto_indent));
    }

    public boolean isChunked() {
      return true;
    }
    
    public long getContentLength() {
      return -1;
    }

    public Header getContentType() {
        return ct;
    }

    public boolean isRepeatable() {
        return provider.isRepeatable();
    }

    @Override
    public InputStream getContent() {
      InputStream in = null;
      if (isRepeatable()) {
        if (buf == null) {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          write(out);
          buf = out.toByteArray();
        }
        in = new ByteArrayInputStream(buf);
      } else {
        if (content == null) {
          if (buf == null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(out);
            buf = out.toByteArray();
          }
          content = new ByteArrayInputStream(buf);
        }
        in = content;
      }
      return in;
    }
    
    @Override
    public void writeTo(OutputStream out) throws IOException {
      write(out);
    }

    public boolean isAutoIndent() {
        return auto_indent;
    }

    public void setAutoIndent(boolean auto_indent) {
        this.auto_indent = auto_indent;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
