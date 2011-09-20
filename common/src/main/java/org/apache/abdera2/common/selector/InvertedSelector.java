package org.apache.abdera2.common.selector;

/**
 * Selector that inverts the results of the wrapped selector
 */
public class InvertedSelector 
  implements Selector {

  private final Selector selector;
  
  public InvertedSelector(Selector selector) {
    this.selector = selector;
  }
  
  public boolean select(Object item) {
    return !selector.select(item);
  }

}
