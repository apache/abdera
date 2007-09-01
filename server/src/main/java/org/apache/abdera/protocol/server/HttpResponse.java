package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponse {

  void setStatus(int status);

  void setHeader(String name, String value);

  void setContentType(String contentType);

  void setDateHeader(String key, long time);

  OutputStream getOutputStream() throws IOException;

  void addCookie(String name, String value);

  void addCookie(
    String name, 
    String value, 
    String domain,
    String path,
    int maxage,
    String comment);
  
  void setContentLength(int length);
  
  void sendError(int status) throws IOException;

  void sendError(int status, String message) throws IOException;

  void sendRedirect(String to) throws IOException;
  
  void reset();
}
