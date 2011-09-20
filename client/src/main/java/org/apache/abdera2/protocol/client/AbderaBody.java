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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element.Helper;
import org.apache.http.entity.mime.content.AbstractContentBody;

/**
 * Used with the MultipartRelatedEntity to include an Atom Entry as a 
 * multipart message part. This is primarily used internally by the 
 * Abdera client and there will be very little reason, if any, for 
 * applications to use this class directly. 
 */
public class AbderaBody 
  extends AbstractContentBody {

  private final Base base;
  private final long len;
  
  /**
   * Create the body using the specified Abdera document or element. 
   * The content length will not be calculated automatically so the 
   * Content-Length header in the part will not be specified. The 
   * Content-Type will be automatically detected based on the type of
   * element passed in.
   */
  public AbderaBody(Base base) {
    this(base,false,null);
  }
  
  /**
   * Create the body using the specified Abdera document or element.
   * The content length will not be calculated. The Content-Type 
   * specified will be used. If contentType is null, the Content-Type 
   * will be automatically detected based on the type of element passed in.
   */
  public AbderaBody(Base base, String contentType) {
    this(base,false,contentType);
  }

  /**
   * Create the body using the specified Abdera document or element.
   * The content length will be automatically calculated if the 
   * calclen argument is true. The Content-Type will be automatically
   * detected based on the type of element passed in.
   */
  public AbderaBody(Base base, boolean calclen) {
    this(base,calclen,null);
  }
  
  /**
   * Create the body using the specified Abdera document or element.
   * The content length will be automatically calculated if the 
   * calclen argument is true. The Content-Type specified will be
   * used. If contentType is null, the Content-Type will be automatically
   * detected based on the type of element passed in.
   */
  public AbderaBody(Base base,boolean calclen,String contentType) {
    super(contentType!=null?contentType:Helper.getMimeType(base));
    this.base = base;
    if (calclen) {
      long l = -1;
      try {
        ByteArrayOutputStream out = 
          new ByteArrayOutputStream();
        base.writeTo(out);
        l = out.toByteArray().length;
      } catch (Throwable t) {}
      len = l;
    } else {
      len = -1;
    }
  }

  public String getFilename() {
    return null;
  }

  public void writeTo(OutputStream out) throws IOException {
    base.writeTo(out);
  }

  public String getCharset() {
    if (base instanceof Document)
      return ((Document<?>)base).getCharset();
    return null;
  }

  public long getContentLength() {
    return len;
  }

  public String getTransferEncoding() {
    return "8bit";
  }

}
