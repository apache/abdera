package org.apache.abdera2.activities.model;

/**
 * The Generator is used to create instances of specific
 * types of Activity Objects. They are typically best 
 * used when generating multiple objects from a single
 * base template, for instance, when producing multiple
 * activity objects that share a base common set of 
 * properties (e.g. same actor, same provider, same verb, etc)
 */
public class Generator<T extends ASBase> {

  private final ASBase template;
  private final Class<T> _class;
  
  private T item;
  
  public Generator(Class<T> _class) {
    this(_class,null);
  }
  
  public Generator(Class<T> _class, ASBase template) {
    this._class = _class;
    this.template = template;
  }
  
  public Generator<T> startNew() {
    if (item != null) 
      throw new IllegalStateException();
    try {
      item = _class.newInstance();
      for (String name : template) {
        Object obj = template.getProperty(name);
        item.setProperty(
          name, 
          obj instanceof Copyable ? 
            ((Copyable)obj).copy() : 
            obj);
      }
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    return this;
  }
  
  public Generator<T> set(String name, Object value) {
    if (item == null)
      throw new IllegalStateException();
    item.setProperty(name,value);
    return this;
  }
  
  public T complete() {
    T t = item;
    item = null;
    return t;
  }
  
  public static interface Copyable {
    Object copy();
  }
  
  public static Generator<Activity> activityGenerator() {
    return new Generator<Activity>(Activity.class);
  }
  
  public static Generator<MediaLink> mediaLinkGenerator() {
    return new Generator<MediaLink>(MediaLink.class);
  }
  
  public static Generator<ASObject> objectGenerator() {
    return new Generator<ASObject>(ASObject.class);
  }
}
