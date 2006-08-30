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
package org.apache.abdera.server.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.URLDataSource;

import org.apache.abdera.model.Base;
import org.apache.abdera.server.ResponseContext;
import org.apache.abdera.server.impl.AbstractResponseContext;
import org.apache.abdera.util.AbderaDataSource;
import org.apache.axiom.attachments.ByteArrayDataSource;

public class AbderaServerException 
  extends Exception 
  implements ResponseContext {

  private static final long serialVersionUID = -4477406225965489951L;
  protected ExceptionResponseContext context = null;
  protected DataSource ds = null;
  
  public static enum Code {
    MULTIPLECHOICES(300),
    MOVEDPERMANENTLY(301),
    FOUND(302),
    SEEOTHER(303),
    NOTMODIFIED(304),
    USEPROXY(305),
    TEMPORARYREDIRECT(307),
    BADREQUEST(400),
    UNAUTHORIZED(401),
    PAYMENTREQUIRED(402),
    FORBIDDEN(403),
    NOTFOUND(404),
    METHODNOTALLOWED(405),
    NOTACCEPTABLE(406),
    REQUESTTIMEOUT(408),
    CONFLICT(409),
    GONE(410),
    LENGTHREQUIRED(411),
    PRECONDITIONFAILED(412),
    REQUESTENTITYTOOLARGE(413),
    UNSUPPORTEDMEDIATYPE(415),
    EXPECTATIONFAILED(417),
    INTERNALSERVERERROR(500),
    NOTIMPLEMENTED(501),
    SERVICEUNAVAILABLE(503);
    
    private int code = 0;
    private Code(int code) {
      this.code = code;
    }
    
    public int getCode() {
      return code;
    }
    
    public static Code getByCode(int status) {
      for (Code code : Code.values()) {
        if (code.code == status) return code;
      }
      return null;
    }
  }
  
  public AbderaServerException(Code code) {
    this(code.getCode(),null);
  }
  
  public AbderaServerException(Code code, String text, String body) {
    this(code, text);
    setDataSource(body);
  }
  
  public AbderaServerException(Code code, String text, URL body) {
    this(code, text);
    setDataSource(body);
  }
  
  public <T extends Base>AbderaServerException(Code code, String text, T body) {
    this(code, text);
    setDataSource(body);
  }
  
  public AbderaServerException(Code code, String text, DataSource body) {
    this(code,text);
    setDataSource(body);
  }
  
  public AbderaServerException(Code code, String text) {
    this(code.getCode(), text);
  }
  
  public AbderaServerException(int status, String text) {
    super();
    context = new ExceptionResponseContext(this);
    context.setStatus(status);
    context.setStatusText(text);
  }
  
  public AbderaServerException(Throwable t) {
    super(t);
    context = new ExceptionResponseContext(this);
    context.setStatus(500);
    context.setStatusText(getMessage());
  }
  
  public Code getCode() {
    return Code.getByCode(getStatus());
  }
  
  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }
  
  public void setDataSource(String text) {
    this.ds = (text != null) ? 
      new ByteArrayDataSource(text.getBytes(), "text/plain") : 
        null;
  }
  
  public <T extends Base>void setDataSource(T base) {
    this.ds = (base != null) ?
      new AbderaDataSource(base) : null;
  }

  public void setDataSource(URL url) {
    this.ds = (url != null) ?
      new URLDataSource(url) : null;
  }
  
  public void setDataSource(File file) {
    this.ds = (file != null) ?
      new FileDataSource(file) : null;
  }
  
  public DataSource getDataSource() {
    return ds;
  }
  
  public boolean hasEntity() {
    if (ds != null) return true;
    return context.hasEntity();
  }

  public void writeTo(OutputStream out) throws IOException {
    if (ds != null) {
      InputStream in = ds.getInputStream();
      int n = -1;
      while((n = in.read()) != -1) { out.write(n); }
      out.flush();
    } else {
      context.writeTo(out);
    }
  }
  
  public long getAge() {
    return context.getAge();
  }

  public String getCacheControl() {
    return context.getCacheControl();
  }

  public String getContentLanguage() {
    return context.getContentLanguage();
  }

  public long getContentLength() {
    return context.getContentLength();
  }

  public URI getContentLocation() throws URISyntaxException {
    return context.getContentLocation();
  }

  public MimeType getContentType() throws MimeTypeParseException {
    return context.getContentType();
  }

  public Date getDateHeader(String name) {
    return context.getDateHeader(name);
  }

  public String getEntityTag() {
    return context.getEntityTag();
  }

  public Date getExpires() {
    return context.getExpires();
  }

  public String getHeader(String name) {
    return context.getHeader(name);
  }

  public List<Object> getHeaders(String name) {
    return context.getHeaders(name);
  }

  public Map<String, List<Object>> getHeaders() {
    return context.getHeaders();
  }

  public Date getLastModified() {
    return context.getLastModified();
  }

  public URI getLocation() throws URISyntaxException {
    return context.getLocation();
  }

  public long getMaxAge() {
    return context.getMaxAge();
  }

  public String[] getNoCacheHeaders() {
    return context.getNoCacheHeaders();
  }

  public String[] getPrivateHeaders() {
    return context.getPrivateHeaders();
  }

  public long getSMaxAge() {
    return context.getSMaxAge();
  }

  public int getStatus() {
    return context.getStatus();
  }

  public String getStatusText() {
    return context.getStatusText();
  }

  public boolean isMustRevalidate() {
    return context.isMustRevalidate();
  }

  public boolean isNoCache() {
    return context.isNoCache();
  }

  public boolean isNoStore() {
    return context.isNoStore();
  }

  public boolean isNoTransform() {
    return context.isNoTransform();
  }

  public boolean isPrivate() {
    return context.isPrivate();
  }

  public boolean isProxyRevalidate() {
    return context.isProxyRevalidate();
  }

  public boolean isPublic() {
    return context.isPublic();
  }

  public String getAllow() {
    return context.getAllow();
  }
  
  public void setAllow(String method) {
    this.context.setAllow(method);
  }
  
  public void setAllow(String... methods) {
    this.context.setAllow(methods);
  }
  
  protected static class ExceptionResponseContext 
    extends AbstractResponseContext {

    private Throwable t = null;
    private long len = -1;
    
    ExceptionResponseContext(Throwable t) {
      this.t = t;
    }
    
    
    
    public boolean hasEntity() {
      return true;
    }
  
    public void writeTo(OutputStream out) throws IOException {
      PrintStream ps = 
        (out instanceof PrintStream) ? 
          (PrintStream)out : 
          new PrintStream(out);
      t.printStackTrace(ps);
    }
  
    @Override
    public long getContentLength() {
      if (len == -1) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        t.printStackTrace(ps);
        ps.flush();
        len = out.size();
      } 
      return len;
    }
  
    @Override
    public void setContentLength(long length) {
      len = length;
    }
    
  }

}
