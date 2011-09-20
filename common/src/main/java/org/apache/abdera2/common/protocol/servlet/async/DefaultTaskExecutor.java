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

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultTaskExecutor 
  implements TaskExecutor {

  private ThreadPoolExecutor exec;

  public static final String TERMINATION_TIMEOUT = "AbderaDefaultTaskExecutorTerminationTimout";
  public static final long DEFAULT_TERMINATION_TIMEOUT = 10;
  
  private long terminationTimeout = DEFAULT_TERMINATION_TIMEOUT;
  
  public DefaultTaskExecutor() {}
  
  public void execute(Runnable task) {
    exec.execute(task);
  }

  public void init(Map<String, String> properties) {
    exec = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    if (properties.containsKey(TERMINATION_TIMEOUT)) {
      String val = properties.get(TERMINATION_TIMEOUT);
      terminationTimeout = Math.max(1,Long.parseLong(val));
    }
  }

  public void startWorker(Runnable worker) {
    exec.execute(worker);
  }

  public void shutdown() {
    exec.shutdown();
    try {
      exec.awaitTermination(
        terminationTimeout, TimeUnit.SECONDS);
    } catch (Throwable t) {}
  }

  public boolean isRunning() {
    return !exec.isShutdown() && 
           !exec.isTerminated() && 
           !exec.isTerminating(); 
  }

}
