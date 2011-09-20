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
package org.apache.abdera2.common.pusher;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ChannelManager implementation based on an internal HashMap of Pusher/Receiver pairs
 */
@SuppressWarnings("unchecked")
public abstract class MapChannelManager 
  implements ChannelManager {

  public static abstract class Channel {
    private final Pusher<?> pusher;
    private final Receiver<?> receiver;
    public Channel(Pusher<?> pusher, Receiver<?> receiver) {
      this.pusher = pusher;
      this.receiver = receiver;
    }
    abstract protected void shutdown();
  }
  
  private final Map<String,Channel> map = 
    new ConcurrentHashMap<String,Channel>();
  
  private boolean shuttingDown = false;
  
  public Iterator<String> iterator() {
    return map.keySet().iterator();
  }

  protected Channel get(String channel) {
    return get(channel,false);
  }
  
  private synchronized Channel get(String channel, boolean skip) {
    if (!skip && shuttingDown)
      throw new IllegalStateException(
        "Channel Manager is shutting down");
    Channel c = map.get(channel);
    if (c == null) {
      c = createChannel();
      map.put(channel,c);
    }
    return c;
  }
  
  public <T> Pusher<T> getPusher(String channel) {
    return (Pusher<T>)get(channel).pusher;
  }

  public <T> Receiver<T> getReceiver(String channel) {
    return (Receiver<T>)get(channel).receiver;
  }

  protected abstract Channel createChannel();
  
  protected void clearChannels() {
    map.clear();
  }
  
  public void shutdownChannel(String channel) {
    if (map.containsKey(channel)) {
      Channel c = map.get(channel);
      c.shutdown();
      map.remove(channel);
    }
  }
  
  public void shutdown() {
    shuttingDown = true;
    for (String name : this) {
      Channel c = get(name,true);
      if (c != null) 
        c.shutdown();
    }
    clearChannels();
  }
  
  public boolean isShutdown() {
    return shuttingDown;
  }
  
  public void init(Map<String,String> properties) {}
}
