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

  public void setContentType(String contentType) {
    response.setContentType(contentType);
  }

  public void setDateHeader(String key, long time) {
    response.setDateHeader(key, time);
  }

  public void setHeader(String name, String value) {
    response.setHeader(name, value);
  }

  public void setStatus(int status) {
    response.setStatus(status);
  }

  public void addCookie(String name, String value) {
    addCookie(name,value,null,null,-1,null);
  }
  
  public void addCookie(
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
  }
  
  public void setCharacterEncoding(String charset) {
    response.setCharacterEncoding(charset);
  }
  
  public void setContentLength(int length) {
    response.setContentLength(length);
  }

  public void sendError(int status) throws IOException {
    response.sendError(status);
  }
  
  public void sendError(int status, String message) throws IOException {
    response.sendError(status, message);
  }
  
  public void sendRedirect(String to) throws IOException {
    response.sendRedirect(to);
  }
  
  public void reset() {
    response.reset();
  }
  
  public HttpServletResponse getActual() {
    return response;
  }
}
