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

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.UrlEncoding;

/**
 * Root impl for Message interface impls. This is provided solely as a way of keeping the interface and impl's
 * consistent across the Request and Response objects.
 */
public abstract class AbstractMessage implements Message {

    protected int flags = 0;
    protected long max_age = -1;

    public CacheControl getCacheControl() {
        String cc = getHeader("Cache-Control");
        return cc != null ? new CacheControl(cc) : null;
    }

    public String getContentLanguage() {
        return getHeader("Content-Language");
    }

    public IRI getContentLocation() {
        String value = getHeader("Content-Location");
        return (value != null) ? new IRI(value) : null;
    }

    public MimeType getContentType() {
        try {
            String value = getHeader("Content-Type");
            return (value != null) ? new MimeType(value) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    public String getDecodedHeader(String header) {
        return UrlEncoding.decode(Codec.decode(getHeader(header)));
    }

    public Iterable<String> getDecodedHeaders(String header) {
        Iterable<Object> headers = getHeaders(header);
        List<String> items = new ArrayList<String>();
        for (Object h : headers)
          items.add(UrlEncoding.decode(Codec.decode(h.toString())));
        return items;
    }

    public String getSlug() {
        return getDecodedHeader("Slug");
    }

    public Iterable<WebLink> getWebLinks() {
      List<WebLink> links = new ArrayList<WebLink>();
      Iterable<Object> headers = this.getHeaders("Link");
      for (Object obj : headers) {
        Iterable<WebLink> list = WebLink.parse(obj.toString());
        for (WebLink link : list)
          links.add(link);
      }
      return links;
    }
    
    public Iterable<Preference> getPrefer() {
      List<Preference> links = new ArrayList<Preference>();
      Iterable<Object> headers = this.getHeaders("Prefer");
      for (Object obj : headers) {
        Iterable<Preference> list = Preference.parse(obj.toString());
        for (Preference link : list)
          links.add(link);
      }
      return links;
    }
    
    public Iterable<Preference> getPreferApplied() {
      List<Preference> links = new ArrayList<Preference>();
      Iterable<Object> headers = this.getHeaders("Preference-Applied");
      for (Object obj : headers) {
        Iterable<Preference> list = Preference.parse(obj.toString());
        for (Preference link : list)
          links.add(link);
      }
      return links;
    }
}
