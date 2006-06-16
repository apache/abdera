package org.apache.abdera.model;

/**
 * Provides an interface for the Atom Threading Extension total
 * element.
 */
public interface Total extends ExtensionElement {

  int getValue();
  
  void setValue(int value);
  
}
