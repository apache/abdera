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
package org.apache.abdera2.common.protocol;

import java.util.Date;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.ResponseType;

public abstract class AbstractResponse extends AbstractMessage implements Response {

    public long getAge() {
        String value = getHeader("Age");
        try {
            return (value != null) ? Long.parseLong(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getAllow() {
        return getHeader("Allow");
    }

    public long getContentLength() {
        String value = getHeader("Content-Length");
        try {
            return (value != null) ? Long.parseLong(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public EntityTag getEntityTag() {
        String etag = getHeader("ETag");
        return (etag != null) ? EntityTag.parse(getHeader("ETag")) : null;
    }

    public Date getExpires() {
        return getDateHeader("Expires");
    }

    public Date getLastModified() {
        return getDateHeader("Last-Modified");
    }

    public IRI getLocation() {
        String l = getHeader("Location");
        return l != null ? new IRI(l) : null;
    }

    public ResponseType getType() {
        return ResponseType.select(getStatus());
    }

    public Iterable<Authentication> getAuthentication() {
      String auth =  getHeader("WWW-Authenticate");
      return auth != null ? Authentication.parse(auth) : null;
  }
}
