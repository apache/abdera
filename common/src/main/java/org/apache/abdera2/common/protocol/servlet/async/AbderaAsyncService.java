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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.abdera2.common.protocol.Provider;
import org.apache.abdera2.common.protocol.ServiceManager;
import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebListener
public class AbderaAsyncService 
  implements ServletContextListener, Runnable {

    private static final int DEFAULT_WORKER_THREADS = 10;
    public static final String PROPERTY_WORKER_THREADS = "AbderaWorkerThreadCount";
    public static final String PROPERTY_ATOMPUB_SERVICE = "AbderaAtompubService";
    public static final String PROPERTY_CHANNEL_SERVICE = "AbderaChannelService";
  
    public static final String RUNNER = "AbderaRunner";
    public static final String SERVICEMANAGER = "AbderaServiceManager";
    public static final String PROVIDER = "AbderaProvider";
    public static final String QUEUE = "AbderaProcessorQueue";
    public static final String CM = "AbderaChannelManager";
  
    private final static Log log = LogFactory.getLog(AbderaAsyncService.class);
    
    private ServletContext context;
    private TaskExecutor exec;
    private ProcessorQueue queue;
    private ChannelManager cm;
    private boolean deployAtompubService;
    private boolean deployChannelService;
  
    public AbderaAsyncService() {
      log.debug("Abdera Async Service Created");
    }
    
    protected Map<String, String> getProperties(ServletContext context) {
      Map<String, String> properties = new HashMap<String, String>();
      Enumeration<String> e = context.getInitParameterNames();
      while (e.hasMoreElements()) {
          String key = e.nextElement();
          String val = context.getInitParameter(key);
          properties.put(key, val);
      }
      return properties;
    }
    
    private int worker_threads(Map<String,String> properties) {
      int c = DEFAULT_WORKER_THREADS;
      if (properties.containsKey(PROPERTY_WORKER_THREADS)) {
        String val = properties.get(PROPERTY_WORKER_THREADS);
        c = Math.max(1,Integer.parseInt(val));
      }
      return c;
    }
    
    private boolean getBooleanProperty(Map<String, String> properties, String name, boolean def) {
      boolean answer = def;
      if (properties.containsKey(name)) {
        String val = properties.get(name);
        answer = "TRUE".equalsIgnoreCase(val) || 
                 "1".equals(val) ||
                 "YES".equalsIgnoreCase(val);
      }
      return answer;
    }
    
    public boolean isDeployAtompubService(Map<String,String> properties) {
      return getBooleanProperty(properties,PROPERTY_ATOMPUB_SERVICE,false);
    }
    
    public boolean isDeployChannelService(Map<String,String> properties) {
      return getBooleanProperty(properties,PROPERTY_CHANNEL_SERVICE,false);
    }
    
    @SuppressWarnings("unchecked")
    protected ServiceManager<Provider> createServiceManager(ServletContext context) {
      String prop = context.getInitParameter(ServiceManager.class.getName());
      return prop != null ? 
        ServiceManager.Factory.getInstance(prop) :  
        ServiceManager.Factory.getInstance();
    }

    
    public void contextInitialized(ServletContextEvent event) {   
      this.context = event.getServletContext();
      Map<String,String> properties = getProperties(context);
      this.deployAtompubService = isDeployAtompubService(properties);
      this.deployChannelService = isDeployChannelService(properties);
      ServiceManager<Provider> manager = 
        createServiceManager(context);
      
      if (manager == null) {
          log.error("Service Manager is null. Application can not function correctly");
          throw new IllegalStateException("Service Manager is null");
      }
      
      if (deployAtompubService) {
        log.debug("Initializing Abdera Atompub Service...");
        queue = manager.newProcessorQueue(properties);
        exec = manager.newTaskExecutor(properties);
        Provider provider = manager.newProvider(properties);
        Processor processor = queue != null ? queue.getProcessor() : null;
        
        log.debug(String.format("Queue:           %s",queue));
        log.debug(String.format("Processor:       %s",processor));
        log.debug(String.format("Executor:        %s",exec));
        log.debug(String.format("Service Manager: %s",manager));
        log.debug(String.format("Provider:        %s",provider));
        
        if (processor != null)
          context.setAttribute(Processor.NAME, processor);
        else {
          log.error("Queue Processor is null. Application can not function correctly");
          throw new IllegalStateException("Queue Processor is null");
        }
        if (exec != null)
          context.setAttribute(RUNNER, exec);
        else {
          log.error("Task Executor is null. Application can not function correctly");
          throw new IllegalStateException("Task Executor is null");
        }
        if (provider != null)
          context.setAttribute(PROVIDER, provider);
        else {
          log.error("Provider is null. Application can not function correctly");
          throw new IllegalStateException("Provider is null");
        }
        if (queue != null)
          context.setAttribute(QUEUE, queue);
        else {
          log.error("Queue is null. Application can not function correctly");
          throw new IllegalStateException("Queue is null");
        }
        context.setAttribute(SERVICEMANAGER, manager);
  
        int ct = worker_threads(properties);
        log.debug(String.format("Launching watcher threads [%d]",ct));
        
        for (int c = 0; c < ct; c++)
          exec.startWorker(this);
        
        log.debug("Abdera Atompub Service is ready...");
      }
      
      if (deployChannelService) {
        log.debug("Initializing Abdera Channel Service");
        cm = manager.newChannelManager(properties);
        log.debug(String.format("Channel Manager: %s", cm));
        if (cm != null) {
          context.setAttribute(CM, cm);
          log.debug("Abdera Channel Service is ready...");
        } else log.debug("Abdera Channel Service could not be started");
      }
    }
    
    public void contextDestroyed(ServletContextEvent event) {
      ServletContext context = event.getServletContext();
      if (deployAtompubService) {
        log.debug("Shutting down the Abdera Service...");
        if (exec != null)
          exec.shutdown();
        // if there are remaining outstanding requests after 
        // shutdown we need to deal with them
        if (queue != null)
          queue.cancelRemaining();
        
        context.removeAttribute(Processor.NAME);
        context.removeAttribute(RUNNER);
        context.removeAttribute(SERVICEMANAGER);
        context.removeAttribute(PROVIDER);
        context.removeAttribute(QUEUE);
      }
      if (deployChannelService) {
        if (cm != null)
          cm.shutdown();
        context.removeAttribute(CM);
      }
    }

    public void run() {
      TaskExecutor exec = 
        (TaskExecutor) context.getAttribute(RUNNER);
      ProcessorQueue processor =
        (ProcessorQueue) context.getAttribute(QUEUE);
      while(exec.isRunning()) {
        if (processor.hasNext()) {
          final AbderaTask task = processor.next();
          log.debug(String.format("Processing New AbderaTask (%s)...",task.getId())); 
          exec.execute(new Runnable() {
            public void run() {
              try {
                task.invoke();
              } catch (Throwable t) {
                log.error(String.format("Error invoking AbderaTask (%s)",task.getId()),t);
              }
              log.debug(String.format("AbderaTask (%s) is complete",task.getId()));
            }
          });
        }
      }
    }
	
}
