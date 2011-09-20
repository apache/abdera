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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.abdera2.model.Element.Helper;
import org.apache.abdera2.model.Entry;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicHeader;

/**
 * Implementation of the HttpEntity class for use with Multipart Atom Posts.
 * The first part of the multipart related package is an Atom Entry document
 * whose content element specifies a src="cid:..." attribute referencing the
 * second part of the multipart package. This is used primarily as a means
 * of simplifying posts to Atompub Media Collections.
 */
public class MultipartRelatedEntity 
  extends BasicHttpEntity 
  implements HttpEntity {

    private final HttpMultipart multipart;
    private final Header contentType;
    
    private String boundary;
    
    public MultipartRelatedEntity(
      Entry entry, 
      ContentBody other) {
        this(entry, other, null, null);
    }

    public MultipartRelatedEntity(
      Entry entry, 
      ContentBody other, 
      String contentType) {
        this(entry, other, contentType, null);
    }

    public MultipartRelatedEntity(
      Entry entry, 
      ContentBody other, 
      String contentType, 
      String boundary) {
        if (entry == null ||
            other == null)
          throw new IllegalArgumentException();
        this.boundary = boundary != null ? boundary : String.valueOf(System.currentTimeMillis());
        this.contentType = 
          new BasicHeader(
              "Content-Type",String.format("Multipart/Related; boundary=\"%s\";type=\"%s\"",this.boundary,Helper.getMimeType(entry)));
        String cs = entry.getDocument().getCharset();
        Charset charset = cs != null ? Charset.forName(cs) : Charset.defaultCharset();
        multipart = new HttpMultipart("related", charset, this.boundary, HttpMultipartMode.STRICT);
        multipart.addBodyPart(new FormBodyPart("entry",new AbderaBody(entry)));
        String contentId = entry.getContentSrc().toString();
        if (!contentId.matches("cid\\:.+")) {
            throw new IllegalArgumentException("entry content source is not a correct content-ID");
        }
        FormBodyPart other_part = new FormBodyPart("other",other);
        other_part.addField("Content-ID", String.format("<%s>",contentId.substring(4)));
        other_part.addField("Content-Type", other.getMimeType());
        multipart.addBodyPart(other_part);
    }
    
    public void consumeContent()
      throws IOException, UnsupportedOperationException{
      if (isStreaming()) {
        throw new UnsupportedOperationException(
          "Streaming entity does not implement #consumeContent()");
      }
    }
    
    public InputStream getContent() {
      throw new UnsupportedOperationException();
    }
    
    public void writeTo(OutputStream out) throws IOException {
      multipart.writeTo(out);
    }

    public long getContentLength() {
        return -1;
    }
    
    public Header getContentType() {
       return contentType; 
    }

    public boolean isRepeatable() {
      for (FormBodyPart part: this.multipart.getBodyParts()) {
        ContentBody body = part.getBody();
        if (body.getContentLength() < 0) {
          return false;
        }
      }
      return true;
    }
    
    public boolean isChunked() {
        return !isRepeatable();
    } 
    
    public boolean isStreaming() {
        return !isRepeatable();
    }
}
