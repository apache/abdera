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
package org.apache.abdera2.common.protocol.servlet.async;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.protocol.Provider;
import org.apache.abdera2.common.protocol.servlet.ServletRequestContext;

@WebServlet(asyncSupported=true)
public class AsyncAbderaServlet 
  extends HttpServlet {

    private static final long serialVersionUID = 2086707888078611321L;
    @Override
    protected void service(
        final HttpServletRequest request, 
        final HttpServletResponse response) 
          throws ServletException, IOException {
      ServletContext sc = getServletContext();
      Processor proc = (Processor) sc.getAttribute(Processor.NAME);
      if (proc == null || !proc.isShutdown()) {
        final AsyncContext context = request.startAsync(request, response);
        Provider provider = (Provider) sc.getAttribute(AbderaAsyncService.PROVIDER);
        ServletRequestContext reqcontext = new ServletRequestContext(provider, request, sc);        
        proc.submit(context,provider,reqcontext);
      } else {
        response.sendError(
          HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
          "Abdera Service in unavailable");
      }
    }
}
