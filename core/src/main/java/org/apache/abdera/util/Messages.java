/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {

  private static Messages instance = null;
  
  public static synchronized Messages getInstance() {
    if (instance == null) instance = new Messages();
    return instance;
  }
  
  public static synchronized void setInstance(Messages messages) {
    Messages.instance = messages;
  }
  
  public static String get(String key) {
    return getInstance().getValue(key);
  }
  
  public static String get(String key, String defaultValue) {
    return getInstance().getValue(key,defaultValue);
  }
  
  public static String format(String key, Object... args) {
    return getInstance().formatValue(key,args);
  }
  
  private final String BUNDLE = "abderamessages";
  
  private final Locale locale;
  private final ResourceBundle bundle;
  
  public Messages() {
    this(Locale.getDefault(), ServiceUtil.getClassLoader());
  }
  
  public Messages(Locale locale, ClassLoader loader) {
    this.locale = locale;
    this.bundle = initResourceBundle(locale, loader);
  }
  
  private ResourceBundle initResourceBundle(Locale locale, ClassLoader loader) {
    try {
      return ResourceBundle.getBundle(BUNDLE, locale, loader);
    } catch (Exception e) {
      return null;
    }
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  private String getValue(String key) {
    try {
      return bundle.getString(key);
    } catch (Exception e) {
      return null;
    }
  }
  
  private String getValue(String key, String defaultValue) {
    String value = get(key);
    return value != null ? value : defaultValue;
  }
  
  private String formatValue(String key, Object... args) {
    String value = get(key);
    return value != null ? MessageFormat.format(value, args) : null;
  }
  
}
