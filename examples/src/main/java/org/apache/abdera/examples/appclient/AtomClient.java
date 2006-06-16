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
package org.apache.abdera.examples.appclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;


/**
 * This implements a simple Atom Publishing Client based on the Apache
 * Commons HTTP Client.
 */
public class AtomClient {

  private HttpClient client = null;
  private boolean bufferStream = false;
  
  public AtomClient() {
    this(Version.APP_NAME + " " + Version.VERSION); 
  }
  
  public AtomClient(String userAgent) {
    init(userAgent);
  }
  
  public HttpClient getClient() {
    return client;
  }
  
  private void init(String userAgent) {
    Protocol.registerProtocol(
      "https", 
      new Protocol(
        "https", 
        (ProtocolSocketFactory)new SimpleSSLProtocolSocketFactory(), 
        443));
    client = new HttpClient();
    client.getParams().setParameter(
      HttpClientParams.USER_AGENT, 
      userAgent);
    client.getParams().setParameter(
      HttpClientParams.COOKIE_POLICY,
      CookiePolicy.IGNORE_COOKIES);
    client.getParams().setParameter(
      HttpClientParams.HTTP_CONTENT_CHARSET, 
      "UTF-8");
  }

  public boolean getUseBuffer() {
    return bufferStream;
  }
  
  public void setUseBuffer(boolean useBuffer) {
    this.bufferStream = useBuffer;
  }
  
  public <T extends Element>Document<T> get(
    String uri) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return get(new URI(uri), null);
  }
  
  public <T extends Element>Document<T> get(
    URI uri) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             URISyntaxException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return get(uri, null);
  }
  
  public <T extends Element>Document<T> get(
    String uri, 
    RequestOptions options) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return get(new URI(uri), options);
  }
  
  public <T extends Element>Document<T> get(
    URI uri, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             URISyntaxException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    GetMethod method = new GetMethod(uri.toASCIIString());                       // account for possible use of IRI's
    try {
      int status = _invoke(method, options);
      if (ClientException.isClientException(status)) 
        throw new ClientException(status, method.getStatusText());
      if (ServerException.isServerException(status)) 
        throw new ServerException(status, method.getStatusText());
      if (status == 304) throw new NotModifiedException();
System.out.println(method.getResponseBodyAsString());
      return _parse(method, new URI(method.getURI().toString()));
    } finally {
      if (bufferStream)
        method.releaseConnection();                                              // this will cause any open streams to be closed
    }
  }

  public Document<Entry> post(
    String uri, 
    Entry entry) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return post(uri, entry, null);
  }
  
  public Document<Entry> post(
    URI uri, 
    Entry entry)
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
      return post(uri, entry, null);
  }
  
  public Document<Entry> post(
    String uri, 
    Entry entry, 
    RequestOptions options) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return post(new URI(uri), entry, options);
  }
  
  public Document<Entry> post(
    URI uri, 
    Entry entry, 
    RequestOptions options)
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
      Document<Entry> doc = entry.getDocument();
      String ctype = null;
      try {
        MimeType mimeType = new MimeType("application/atom+xml");
        if (doc.getCharset() != null) 
          mimeType.setParameter("charset", doc.getCharset());
        else mimeType.setParameter("charset", "utf-8");
        ctype = mimeType.toString();
      } catch (Exception e) {}
      return _post(uri, new EntryRequestEntity(doc), ctype, options);
  }

  public Document<Entry> post(
    String uri, 
    InputStream in, 
    MimeType mimeType) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return post(uri, in, mimeType, null);
  }
  
  public Document<Entry> post(
    URI uri, 
    InputStream in, 
    MimeType mimeType)
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
      return post(uri, in, mimeType, null);
  }
 
  public Document<Entry> post(
    String uri, 
    InputStream in, 
    MimeType mimeType, 
    RequestOptions options) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    return post(new URI(uri), in, mimeType, options);
  }
  
  public Document<Entry> post(
    URI uri, 
    InputStream in, 
    MimeType mimeType, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             URISyntaxException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
      return _post(
        uri, 
        new InputStreamRequestEntity(in), 
        (mimeType != null) ? mimeType.toString() : null, 
        options);
  }
  
  public Document<Entry> _post(
    URI uri, 
    RequestEntity requestEntity, 
    String contentType, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             URISyntaxException, 
             MimeTypeParseException, 
             DateParseException, 
             NotModifiedException {
    PostMethod method = new PostMethod(uri.toASCIIString());                     // account for possible use of IRI's
    method.setRequestEntity(requestEntity);
    method.setRequestHeader("Content-Type", contentType);
    try {
      int status = _invoke(method, options);
      if (ClientException.isClientException(status)) 
        throw new ClientException(status, method.getStatusText());
      if (ServerException.isServerException(status)) 
        throw new ServerException(status, method.getStatusText());
      if (status == 304) throw new NotModifiedException();
      MimeType atomtype = new MimeType("application/atom+xml");
      String ctype = _getResponseHeader(method, "Content-Type", null);
      String location = _getResponseHeader(method, "Location", null);
      URI locationuri = (location != null) ? uri.resolve(location) : null;
      if (ctype != null && atomtype.match(ctype)) {
        try {
          return _parse(method, locationuri );
        } catch (Exception e) {
          if (location != null)
            return get(locationuri);
        }
      } else {
        if (location != null)
          return get(locationuri);
      }
    } finally {
      if (bufferStream)
        method.releaseConnection();                                              // this will cause any open streams to be closed
    }
    return null;
  }
  
  public void put(
    String uri, 
    Entry entry) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
    put(uri, entry, null);
  }
  
  public void put(
    URI uri, 
    Entry entry) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
      put(uri, entry, null);
  }
  
  public void put(
    String uri, 
    Entry entry, 
    RequestOptions options) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
    put(new URI(uri), entry, options);
  }
  
  public void put(
    URI uri, 
    Entry entry, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
      Document<Entry> doc = entry.getDocument();
      String ctype = null;
      try {
        MimeType mimeType = new MimeType("application/atom+xml");
        if (doc.getCharset() != null) 
          mimeType.setParameter("charset", doc.getCharset());
        else mimeType.setParameter("charset", "utf-8");
        ctype = mimeType.toString();
      } catch (Exception e) {}
      _put(uri, new EntryRequestEntity(doc), ctype, options);
  }

  public void put(
    String uri, 
    InputStream in, 
    MimeType mimeType) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
    put(uri, in, mimeType, null);
  }
  
  public void put(
    URI uri, 
    InputStream in, 
    MimeType mimeType) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
      put(uri, in, mimeType, null);
  }
 
  public void put(
    String uri, 
    InputStream in, 
    MimeType mimeType, 
    RequestOptions options) 
      throws URISyntaxException, 
             HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
    put(new URI(uri), in, mimeType, options);
  }
  
  public void put(
    URI uri, 
    InputStream in, 
    MimeType mimeType, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException {
      _put(
        uri, 
        new InputStreamRequestEntity(in), 
        (mimeType != null) ? mimeType.toString() : null, 
        options);
  }
  
  public void _put(
    URI uri, 
    RequestEntity requestEntity, 
    String contentType, 
    RequestOptions options) 
      throws HttpException, 
             IOException, 
             ClientException, 
             ServerException, 
             NotModifiedException{
    PutMethod method = new PutMethod(uri.toASCIIString());                     // account for possible use of IRI's
    method.setRequestEntity(requestEntity);
    method.setRequestHeader("Content-Type", contentType);
    try {
      int status = _invoke(method, options);
      if (ClientException.isClientException(status)) {
        System.out.println(method.getResponseBodyAsString());
        throw new ClientException(status, method.getStatusText());
      }
      if (ServerException.isServerException(status)) 
        throw new ServerException(status, method.getStatusText());
      if (status == 304) throw new NotModifiedException();
    } finally {
      if (bufferStream)
        method.releaseConnection();                                              // this will cause any open streams to be closed
    }
  }
  
  public void delete(
      String uri) 
        throws URISyntaxException, 
               HttpException, 
               IOException, 
               ClientException, 
               ServerException, 
               NotModifiedException  {
      delete(new URI(uri), null);
    }
    
    public void delete(
      URI uri) 
        throws HttpException, 
               IOException, 
               ClientException, 
               ServerException, 
               NotModifiedException {
      delete(uri, null);
    }
    
    public void delete(
      String uri, 
      RequestOptions options) 
        throws HttpException, 
               IOException, 
               ClientException, 
               ServerException, 
               URISyntaxException, 
               NotModifiedException {
      delete(new URI(uri), options);
    }
    
    public void delete(
      URI uri, 
      RequestOptions options) 
        throws HttpException, 
               IOException, 
               ClientException, 
               ServerException, 
               NotModifiedException  {
      DeleteMethod method = new DeleteMethod(uri.toASCIIString());                // account for possible use of IRI's
      try {
        int status = _invoke(method, options);
        if (ClientException.isClientException(status)) 
          throw new ClientException(status, method.getStatusText());
        if (ServerException.isServerException(status)) 
          throw new ServerException(status, method.getStatusText());
        if (status == 304) throw new NotModifiedException();
      } finally {
        if (bufferStream)
          method.releaseConnection();                                              // this will cause any open streams to be closed
      }
    }
  
  private <T extends Element>Document<T> _parse(
    HttpMethod method, URI uri) 
      throws URISyntaxException, 
             IOException, 
             MimeTypeParseException, 
             DateParseException {
    String clocation = _getResponseHeader(method, "Content-Location", null);
    if (clocation != null)
      uri = new URI(clocation);                                                  // get the content-location
    InputStream in = _getInputStream(method);
    Parser parser = Parser.INSTANCE;
    ParserOptions parserOptions = parser.getDefaultParserOptions();
    MimeType mimeType = _getContentType(method);
    if (mimeType != null) {
      parserOptions.setCharset(
        _getContentType(method).getParameter(
          "charset"));
    }
    Document<T> doc = parser.parse(in, uri, parserOptions);
    doc.setContentType(mimeType);
    doc.setLastModified(_getLastModified(method));
    return doc;
  }
  
  private int _invoke(
    HttpMethod method, 
    RequestOptions options) 
      throws HttpException, 
             IOException {
    if (options != null) {
      if (!options.getAllowCache())
        method.setRequestHeader(
          "Cache-Control", 
          "no-cache");
      if (options.getIfMatch() != null)
        method.setRequestHeader(
          "If-Match", 
          _getStringFromArray(
            options.getIfMatch(), true));
      if (options.getIfNoneMatch() != null)
        method.setRequestHeader(
          "If-None-Match", 
          _getStringFromArray(
            options.getIfNoneMatch(), true));
      if (options.getIfUnmodifiedSince() != null) 
        method.setRequestHeader(
          "If-Unmodified-Since", 
          DateUtil.formatDate(
            options.getIfUnmodifiedSince()));
      if (options.getIfModifiedSince() != null) 
        method.setRequestHeader(
          "If-Modified-Since", 
          DateUtil.formatDate(
            options.getIfModifiedSince()));
      if (options.getUseDeltaEncoding()) 
        method.setRequestHeader("A-IM", "feed");
    }
    int status = client.executeMethod(method);
    if (options != null && options.getCaptureResponseHeaders()) {
      Header[] headers = method.getResponseHeaders();
      for (Header header : headers) {
        options.setResponseHeader(
          header.getName(), 
          header.getValue());
      }
    }    
    return status;
  }
  
  private String _getStringFromArray(String[] strings, boolean quote) {
    String s = "";
    for (String string : strings) {
      if (s.length() > 0) s += ", ";
      if (quote && string.charAt(0) != '"') string = "\"" + string;
      if (quote && string.charAt(string.length()-1) != '"') string += "\"";
      s += string;
    }
    return s;
  }
  
  private String _getResponseHeader(HttpMethod method, String header, String _default) {
    Header val = method.getResponseHeader(header);
    return (val != null) ? val.getValue() : _default; 
  }
  
  private InputStream _getInputStream(HttpMethod method) throws IOException {
    InputStream in = method.getResponseBodyAsStream();
    if (in != null) {
      String cencoding = _getResponseHeader(method, "Content-Encoding", null);   // handle content-encoding
      if ("gzip".equalsIgnoreCase(cencoding))                                    //   GZip
        in = new GZIPInputStream(in);
      else if ("compress".equalsIgnoreCase(cencoding))                           //   Compress
        in = new InflaterInputStream(in);
      else if ("zip".equalsIgnoreCase(cencoding))                                //   Zip
        in = new ZipInputStream(in);
      if (bufferStream) {                                                        // buffer the stream?
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.available());
        int n = -1;
        while ((n = in.read()) > -1) { baos.write(n); }
        in = new ByteArrayInputStream(baos.toByteArray());
      }
    }
    return in;
  }
  
  private MimeType _getContentType(
    HttpMethod method) 
      throws MimeTypeParseException {
    String contentType = _getResponseHeader(method, "Content-Type", null);
    return (contentType != null) ? new MimeType(contentType) : null;
  }
  
  private Date _getLastModified(
    HttpMethod method) 
      throws DateParseException {
    String date = _getResponseHeader(method, "Last-Modified", null);
    return (date != null) ? DateUtil.parseDate(date) : new Date();
  }
}
