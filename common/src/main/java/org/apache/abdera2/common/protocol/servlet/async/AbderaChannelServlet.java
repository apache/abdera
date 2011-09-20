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
import javax.servlet.AsyncEvent;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.abdera2.common.pusher.Listener;
import org.apache.abdera2.common.pusher.Receiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet(asyncSupported=true)
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbderaChannelServlet extends HttpServlet {

  private final static Log log = LogFactory.getLog(AbderaChannelServlet.class);
  
  private static final long serialVersionUID = 3751815744618869423L;

  protected abstract String getChannel(AsyncContext context);
  
  protected abstract AsyncListener<?> createListener(AsyncContext context);
  
  protected abstract long getTimeout(ServletConfig config, ServletContext context);
  
  protected void doGet(
      final HttpServletRequest request, 
      final HttpServletResponse response) 
        throws ServletException, IOException {
    final ServletContext sc = getServletContext();
    final ChannelManager cm = (ChannelManager) sc.getAttribute(AbderaAsyncService.CM);
    if (cm == null || !cm.isShutdown()) {
      final AsyncContext context = request.startAsync(request, response);
      context.setTimeout(getTimeout(getServletConfig(),sc));
      context.start(
        new Runnable() {
          public void run() {
            String channel = getChannel(context);
            log.debug(String.format("Selected Channel Name: %s",channel));
            if (channel != null) {
              final Receiver receiver = cm.getReceiver(channel);
              log.debug(String.format("Selected Receiver: %s",receiver));
              if (receiver != null) {
                final Listener listener = createListener(context);
                context.addListener(
                  new javax.servlet.AsyncListener() {
                    public void onComplete(AsyncEvent event) throws IOException {
                      System.out.println("on complete");
                      try {
                        receiver.stopListening(listener);
                      } catch (Throwable t) {}
                    }
                    public void onError(AsyncEvent event) throws IOException {
                      System.out.println("on error");
                      event.getThrowable().printStackTrace();
                      try {
                        receiver.stopListening(listener);
                      } catch (Throwable t) {}
                    }
                    public void onStartAsync(AsyncEvent event)
                        throws IOException {
                    }
                    public void onTimeout(AsyncEvent event) throws IOException {
                      System.out.println("on timeout");
                      try {
                        receiver.stopListening(listener);
                      } catch (Throwable t) {}
                    }
                  }
                );
                log.debug(String.format("Listener: %s",listener));
                if (listener != null) {
                  request.setAttribute("AbderaChannel", channel);
                  request.setAttribute("AbderaReceiver", receiver);
                  request.setAttribute("AbderaListener", listener);
                  receiver.startListening(listener);      
                }
              } 
            }
          }
        }
      );
    } else {
      response.sendError(
        HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
        "Abdera Service in unavailable");
    }
    
  }
  
  public abstract static class AsyncListener<T> implements Listener<T> {

    private final AsyncContext context;
    private boolean done = false;
    
    protected AsyncListener(AsyncContext context) {
      this.context = context;
    }
    
    protected HttpServletRequest getRequest() {
      return (HttpServletRequest) context.getRequest();
    }
    
    protected HttpServletResponse getResponse() {
      return (HttpServletResponse) context.getResponse();
    }
    
    protected boolean isDone() {
      return done;
    }
    
    public void afterItems() {
      if (!done) {
      try {
        finish();
        getResponse().flushBuffer();
      } catch (Throwable t) {
        // whoops, must have lost the connection before the request completed.
      }
      context.complete();
      done = true;
      }
    }
    
    protected void finish() {
      // by default do nothing
    }
    
    public void beforeItems() {
      // by default do nothing
    }
  }
}
