package org.apache.abdera.ext.thread;

import org.apache.abdera.model.ExtensionElement;

/**
 * Provides an interface for the Atom Threading Extension total
 * element.
 */
public interface Total extends ExtensionElement {

  int getValue();
  
  void setValue(int value);
  
}
