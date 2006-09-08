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
package org.apache.abdera.protocol.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.model.Base;
import org.apache.abdera.util.MimeTypeHelper;

public class BaseResponseContext<T extends Base>
  extends AbstractResponseContext {

  private final T base;
  private final boolean chunked;
  
  public BaseResponseContext(T base) {
    this(base, true);
  }
  
  public BaseResponseContext(T base, boolean chunked) {
    this.base = base;
    setStatus(200);
    setStatusText("OK");
    this.chunked = chunked;
  }
  
  public T getBase() {
    return base;
  }
  
  public boolean hasEntity() {
    return (base != null);
  }

  public void writeTo(OutputStream out) throws IOException {
    if (hasEntity()) base.writeTo(out);
  }

  @Override
  public MimeType getContentType() 
    throws MimeTypeParseException {
      MimeType t = super.getContentType();
      if (t == null) {
        String type = MimeTypeHelper.getMimeType(base);
        if (type != null) t = new MimeType(type);
      }
      return t;
  }

  @Override
  public long getContentLength() {
    long len = super.getContentLength();
    if (hasEntity() && len == -1 && !chunked) {
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        base.writeTo(out);
        len = out.size();
        super.setContentLength(len);
      } catch (Exception e) {}
    }
    return len;
  }
}
