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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.AsyncContext;

import org.apache.abdera2.common.protocol.Provider;
import org.apache.abdera2.common.protocol.RequestContext;

public class DefaultProcessor 
  implements Processor, ProcessorQueue {

  private final Queue<AbderaTask> queue = 
    new ConcurrentLinkedQueue<AbderaTask>();
  boolean rejectNew = false;
  
  public void submit(AsyncContext context, Provider provider, RequestContext requestContext) {
    if (!rejectNew) {
      queue.offer(new AbderaTask(context,provider,requestContext));
    } else {
      AbderaTask.cancel(context, "NEW");
    }
  }

  public AbderaTask next() {
    return queue.poll();
  }
  
  public boolean hasNext() {
    return !queue.isEmpty();
  }

  public Processor getProcessor() {
    return this;
  }
  
  public boolean isShutdown() {
    return rejectNew;
  }

  public void cancelRemaining() {
    rejectNew = true;
    while(hasNext())
      next().cancel();
  }

}
