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
package org.apache.abdera.test.client;

import org.mortbay.jetty.servlet.ServletHandler;

import junit.framework.TestCase;

public abstract class JettyTest extends TestCase {
  
  protected int numtests = 0;
  protected int testsrun = 0;
  
  protected JettyTest(int numtests) {
    this.numtests = numtests;
  }
  
  protected static ServletHandler getServletHandler(String... servletMappings) {
    ServletHandler handler = new ServletHandler();
    for (int n = 0; n < servletMappings.length; n = n + 2) {
      String name = servletMappings[n];
      String root = servletMappings[n+1];
      handler.addServletWithMapping(name, root);
    }
    try {
      JettyUtil.addHandler(handler);
    } catch (Exception e) {}
    return handler;
  }
    
  protected abstract ServletHandler getServletHandler();
  
  protected String getBase() {
    return "http://localhost:" + JettyUtil.getPort();
  }
  
  public void tearDown() throws Exception {
    if (++testsrun == numtests)
      JettyUtil.removeHandler(getServletHandler());
  }
}
