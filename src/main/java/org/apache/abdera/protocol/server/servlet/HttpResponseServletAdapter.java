package org.apache.abdera.protocol.server.servlet;

import java.io.IOException;
import java.io.OutputStream;

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

}
