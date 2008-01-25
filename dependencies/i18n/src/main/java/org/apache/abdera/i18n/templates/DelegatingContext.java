package org.apache.abdera.i18n.templates;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public abstract class DelegatingContext 
  extends CachingContext {

  protected final Context subcontext;
  
  protected DelegatingContext(Context subcontext) {
    this.subcontext = subcontext;
  }
  
  protected <T> T resolveActual(String var) {
    return (T)this.subcontext.resolve(var);
  }

  public Iterator<String> iterator() {
    return this.subcontext.iterator();
  }

}
