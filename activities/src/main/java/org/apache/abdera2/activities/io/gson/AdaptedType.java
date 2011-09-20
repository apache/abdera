package org.apache.abdera2.activities.io.gson;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Intended to be used on TypeAdapter instances to 
 * identify the adapted type
 */
@Retention(RUNTIME)
@Target( {TYPE})
@Inherited
public @interface AdaptedType {
  Class<?> value();
}
