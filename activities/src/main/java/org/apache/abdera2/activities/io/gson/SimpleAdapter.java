package org.apache.abdera2.activities.io.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public abstract class SimpleAdapter<T>
  implements GsonTypeAdapter<T> {

  private final Class<T> _class;
  
  public SimpleAdapter() {
    this._class = _getAdaptedClass(this.getClass());
  }
  
  @SuppressWarnings("unchecked")
  private static <T>Class<T> _getAdaptedClass(Class<?> _class) {
    if (_class.isAnnotationPresent(AdaptedType.class)) {
      AdaptedType at = _class.getAnnotation(AdaptedType.class);
      return (Class<T>) at.value();
    } else throw new IllegalArgumentException();
  }
  
  public SimpleAdapter(Class<T> _class) {
    this._class = _class;
  }
  
  public Class<T> getAdaptedClass() {
    return _class;
  }

  public JsonElement serialize(
    T t, 
    Type type, 
    JsonSerializationContext context) {
      return context.serialize(serialize(t));
  }

  protected String serialize(T t) {
    return t != null ? t.toString() : null;
  }
  
  protected abstract T deserialize(String v);
  
  public T deserialize(
    JsonElement json, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    return deserialize(json.getAsJsonPrimitive().getAsString());
  }
}