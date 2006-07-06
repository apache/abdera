package org.apache.abdera.server.exceptions;

/**
 * @author Elias Torres (<a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com</a>)
 *
 */
public class NotFoundException extends AbderaServerException {
  
  /**
   * 
   */
  private static final long serialVersionUID = -3161208634818367903L;

  public NotFoundException() {
    super(404, null);
  }

  public NotFoundException(String text) {
    super(404, text);
  }
}
