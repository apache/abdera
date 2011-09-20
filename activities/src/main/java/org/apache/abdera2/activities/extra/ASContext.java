package org.apache.abdera2.activities.extra;

import java.util.Iterator;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.common.templates.AbstractContext;

/**
 * URI Templates Context implementation based on an Activity Streams
 * object. Makes it easier to construct new URLs based on the properties
 * of an Activity Streams object
 */
public final class ASContext 
  extends AbstractContext {
  private static final long serialVersionUID = 4445623432125049535L;
  private final ASBase base;
  
  public ASContext(ASBase base) {
    this.base = base;
  }
  
  public <T> T resolve(String var) {
    return base.getProperty(var);
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean contains(String var) {
    return base.has(var);
  }

  public Iterator<String> iterator() {
    return base.iterator();
  }
}