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

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public final class ConfigProperties 
  implements Constants {
  
  ConfigProperties() {}
  
  private static ResourceBundle BUNDLE = null;
  private static String parser = null;
  private static String factory = null;
  private static String xpath = null;
  
  private static ResourceBundle getBundle() {
    if (BUNDLE == null) {
      try {
        BUNDLE = ResourceBundle.getBundle("abdera");
      } catch (Exception e) {}
    } 
    return BUNDLE;
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
    if (xpath == null)
      xpath = getConfigurationOption(CONFIG_XPATH);
    return (xpath != null) ? xpath : DEFAULT_XPATH;
  }
  
  public static String getDefaultParser() {
    if (parser == null)
      parser = getConfigurationOption(CONFIG_PARSER);
    return (parser != null) ? parser : DEFAULT_PARSER;
  }
  
  public static String getDefaultFactory() {
    if (factory == null)
      factory = getConfigurationOption(CONFIG_FACTORY);
    return (factory != null) ? factory : DEFAULT_FACTORY;
  }
  
}
