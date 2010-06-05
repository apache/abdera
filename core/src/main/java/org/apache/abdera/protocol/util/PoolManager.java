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
package org.apache.abdera.protocol.util;

import java.util.Stack;

import org.apache.abdera.protocol.ItemManager;

/**
 * Implements a simple pool manager. By default, an upper limit to the pool is set at 25 entries. New items can always
 * be created, but if more than 25 entries are released back to the pool, the extras are discarded. Items added to the
 * stack should never maintain any kind of state as it is entirely possible that different threads will be grabbing
 * items from the pool
 */
public abstract class PoolManager<T> implements ItemManager<T> {

    private static final int DEFAULT_SIZE = 25;
    private final Stack<T> pool;

    protected PoolManager() {
        this(DEFAULT_SIZE);
    }

    protected PoolManager(int max) {
        this.pool = initStack(max);
    }

    private Stack<T> initStack(final int max) {
        return new Stack<T>() {
            private static final long serialVersionUID = -6647024253014661104L;

            @Override
            public T push(T item) {
                T obj = super.push(item);
                if (this.size() > max)
                    this.removeElementAt(0);
                return obj;
            }
        };
    }

    protected synchronized T getInstance() {
        return (!pool.empty()) ? pool.pop() : internalNewInstance();
    }

    public synchronized void release(T t) {
        if (t == null || pool.contains(t))
            return;
        pool.push(t);
    }

    protected abstract T internalNewInstance();

}
