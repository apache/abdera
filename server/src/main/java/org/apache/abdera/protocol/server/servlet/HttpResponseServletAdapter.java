package org.apache.abdera.protocol.server.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.protocol.server.HttpResponse;

public class HttpResponseServletAdapter implements HttpResponse {

  private HttpServletResponse response;

  public HttpResponseServletAdapter(HttpServletResponse response) {
    this.response = response;
  }

  public OutputStream getOutputStream() throws IOException {
    return response.getOutputStream();
  }

  public HttpResponse setContentType(String contentType) {
    response.setContentType(contentType);
    return this;
  }

  public HttpResponse setDateHeader(String key, long time) {
    response.setDateHeader(key, time);
    return this;
  }

  public HttpResponse setHeader(String name, String value) {
    response.setHeader(name, value);
    return this;
  }

  public HttpResponse setStatus(int status) {
    response.setStatus(status);
    return this;
  }

  public HttpResponse addCookie(String name, String value) {
    return addCookie(name,value,null,null,-1,null);
  }
  
  public HttpResponse addCookie(
    String name, 
    String value, 
    String domain,
    String path,
    int maxage,
    String comment) {
      Cookie cookie = new Cookie(name,value);
      if (domain != null) cookie.setDomain(domain);
      if (path != null) cookie.setPath(path);
      if (maxage >= 0) cookie.setMaxAge(maxage);
      if (comment != null) cookie.setComment(comment);
      response.addCookie(cookie);
      return this;
  }
  
  public HttpResponse setCharacterEncoding(String charset) {
    response.setCharacterEncoding(charset);
    return this;
  }
  
  public HttpResponse setContentLength(int length) {
    response.setContentLength(length);
    return this;
  }

  public HttpResponse sendError(int status) throws IOException {
    response.sendError(status);
    return this;
  }
  
  public HttpResponse sendError(int status, String message) throws IOException {
    response.sendError(status, message);
    return this;
  }
  
  public HttpResponse sendRedirect(String to) throws IOException {
    response.sendRedirect(to);
    return this;
  }
  
  public HttpResponse reset() {
    response.reset();
    return this;
  }
  
  public HttpServletResponse getActual() {
    return response;
  }
}
