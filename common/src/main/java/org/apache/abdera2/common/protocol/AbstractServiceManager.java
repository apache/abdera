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
package org.apache.abdera2.common.protocol;

import java.util.Map;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.protocol.servlet.async.DefaultProcessor;
import org.apache.abdera2.common.protocol.servlet.async.DefaultTaskExecutor;
import org.apache.abdera2.common.protocol.servlet.async.ProcessorQueue;
import org.apache.abdera2.common.protocol.servlet.async.TaskExecutor;
import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.abdera2.common.pusher.SimpleChannelManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ServiceManager is used by the AbderaServlet to bootstrap the server instance. There should be little to no reason
 * why an end user would need to use this class directly.
 */
public abstract class AbstractServiceManager<P extends Provider> 
  implements ServiceManager<P> {

    private final static Log log = LogFactory.getLog(AbstractServiceManager.class);

    protected AbstractServiceManager() {}

    /* (non-Javadoc)
     * @see org.apache.abdera2.protocol.server.IServiceManager#newProcessorQueue(java.util.Map)
     */
    public ProcessorQueue newProcessorQueue(Map<String, String> properties) {
      String instance = properties.get(PROCESSORQUEUE);
      if (instance == null)
        instance = DefaultProcessor.class.getName();
      log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "ProcessorQueue"));
      ProcessorQueue queue = (ProcessorQueue)Discover.locate(ProcessorQueue.class, instance);
      return queue;
    }
    
    /* (non-Javadoc)
     * @see org.apache.abdera2.protocol.server.IServiceManager#newTaskExecutor(java.util.Map)
     */
    public TaskExecutor newTaskExecutor(Map<String, String> properties) {
      String instance = properties.get(TASKEXECUTOR);
      if (instance == null)
        instance = DefaultTaskExecutor.class.getName();
      log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "TaskExecutor"));
      TaskExecutor exec = (TaskExecutor)Discover.locate(TaskExecutor.class, instance);
      exec.init(properties);
      return exec;
    }
    
    /* (non-Javadoc)
     * @see org.apache.abdera2.protocol.server.IServiceManager#newChannelManager(java.util.Map)
     */
    public ChannelManager newChannelManager(Map<String, String> properties) {
      String instance = properties.get(CHANNELMANAGER);
      if (instance == null)
        instance = SimpleChannelManager.class.getName();
      log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE", "ChannelManager"));
      ChannelManager cm = (ChannelManager)Discover.locate(ChannelManager.class, instance);
      cm.init(properties);
      return cm;
    }
}
