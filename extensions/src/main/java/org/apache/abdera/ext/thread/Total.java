package org.apache.abdera.ext.thread;

import org.apache.abdera.model.Element;

/**
 * Provides an interface for the Atom Threading Extension total
 * element.
 */
public interface Total extends Element {

  int getValue();
  
  void setValue(int value);
  
}
