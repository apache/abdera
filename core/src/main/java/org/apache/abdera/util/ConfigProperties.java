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

import java.util.ResourceBundle;

public final class ConfigProperties 
  implements Constants {
  
  ConfigProperties() {}
  
  private static ThreadLocal bundleStore = new ThreadLocal();
  
  @SuppressWarnings("unchecked")
  private static void storeBundle(ResourceBundle bundle) {
    bundleStore.set(bundle);
  }
  
  private static ResourceBundle retrieveBundle() {
    return (ResourceBundle) bundleStore.get();
  }
  
  private static ResourceBundle getBundle() {
    ResourceBundle bundle = retrieveBundle();
    if (bundle == null) {
      try {
        bundle = ResourceBundle.getBundle(
          "abdera", 
          java.util.Locale.getDefault(), 
          ServiceUtil.getClassLoader());
        storeBundle(bundle);
      } catch (Exception e) {}
    } 
    return bundle;
  }

  public static String getConfigurationOption(String id) {
    String option = System.getProperty(id);
    try {
      if (getBundle() != null && option == null)
        option = getBundle().getString(id);
    } catch (Exception e) {}
    return option;
  }
  
  public static String getDefaultXPath() {
    String xpath = getConfigurationOption(CONFIG_XPATH);
    return (xpath != null) ? xpath : DEFAULT_XPATH;
  }
  
  public static String getDefaultParser() {
    String parser = getConfigurationOption(CONFIG_PARSER);
    return (parser != null) ? parser : DEFAULT_PARSER;
  }
  
  public static String getDefaultFactory() {
    String factory = getConfigurationOption(CONFIG_FACTORY);
    return (factory != null) ? factory : DEFAULT_FACTORY;
  }
  
}
