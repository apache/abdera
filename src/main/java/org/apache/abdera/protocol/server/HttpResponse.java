package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponse {

  void setStatus(int status);

  void setHeader(String name, String value);

  void setContentType(String contentType);

  void setDateHeader(String key, long time);

  OutputStream getOutputStream() throws IOException;

}
