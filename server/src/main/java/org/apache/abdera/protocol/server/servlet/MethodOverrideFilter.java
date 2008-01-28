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
package org.apache.abdera.protocol.server.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;


/**
 * HTTP Servlet Filter that implements support for Google's X-HTTP-Method-Override
 * header to perform "POST Tunneling" of various HTTP operations 
 */
public class MethodOverrideFilter 
  extends AbstractFilter
  implements Filter {

  public static final String METHODS = "org.apache.abdera.protocol.server.servlet.Overrides";
  
  private String[] METHODS_TO_OVERRIDE;
  
  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain chain) 
      throws IOException, 
             ServletException {
    chain.doFilter(
      new MethodOverrideRequestWrapper(
        (HttpServletRequest) request), 
        response);
    
    HttpServletResponse hresponse = (HttpServletResponse) response;
    hresponse.setHeader("Cache-Control", "no-cache");
  }

  @Override
  public void init(FilterConfig config) throws ServletException {
    String param = config.getInitParameter(METHODS);
    if (param != null) {
      String[] methods = param.split("\\s*,\\s*");
      Arrays.sort(methods);
      this.METHODS_TO_OVERRIDE = methods;
    }
  }

  private class MethodOverrideRequestWrapper 
    extends HttpServletRequestWrapper {

    private final String method;
    
    public MethodOverrideRequestWrapper(HttpServletRequest request) {
      super(request);
      String method = super.getMethod();
      String xheader = getHeader("X-HTTP-Method-Override");
      if (xheader == null) xheader = getHeader("X-Method-Override");
      if (xheader != null) xheader = xheader.toUpperCase().trim();
      if (method.equals("POST") && 
        xheader != null && 
        Arrays.binarySearch(METHODS_TO_OVERRIDE,xheader) > -1) {
          method = xheader;
      }
      this.method = method;
    }

    @Override
    public String getMethod() {
      return method;
    }
    
  }
}
