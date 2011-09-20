package org.apache.abdera2.common.templates;

import org.apache.abdera2.common.anno.Param;

public class AnnotationContext extends MapContext {

  private static final long serialVersionUID = 3092158634973274492L;
  
  private void process(org.apache.abdera2.common.anno.Context context) {
    for (Param param : context.value())
      put(param.name().toLowerCase(),param.value());
  }
  
  public AnnotationContext(org.apache.abdera2.common.anno.Context context) {
    process(context);
  }
  
  public AnnotationContext(Object object) {
    org.apache.abdera2.common.anno.Context context = getContext(object);
    if (object == null)
      throw new IllegalArgumentException();
    else process(context);
  }
  
  public static org.apache.abdera2.common.anno.Context getContext(Object object) {
    if (object == null) 
      throw new IllegalArgumentException();
    Class<?> _class = 
      object instanceof Class<?> ? 
          (Class<?>)object : object.getClass();
    if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.Context.class)) {
      return
        _class.getAnnotation(org.apache.abdera2.common.anno.Context.class);
    }
    return null;
  }
  
}
