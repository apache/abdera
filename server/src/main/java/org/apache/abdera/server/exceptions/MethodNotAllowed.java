package org.apache.abdera.server.exceptions;

import org.apache.abdera.server.AbderaServerException;

public class MethodNotAllowed 
  extends AbderaServerException {

  private static final long serialVersionUID = -633052744794889086L;

  public MethodNotAllowed() {
    super(405, null);
  }

  public MethodNotAllowed(String text) {
    super(405, text);
  }
  
}
