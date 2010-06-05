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
package org.apache.abdera.protocol.server.adapters.jcr;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.AbstractItemManager;

public class SessionPoolManager extends AbstractItemManager<Session> {
    private Repository repository;
    private Credentials credentials;

    public SessionPoolManager(int maxSize, Repository repository, Credentials credentials) {
        super(maxSize);
        this.repository = repository;
        this.credentials = credentials;
    }

    @Override
    protected Session internalNewInstance() {
        try {
            return repository.login(credentials);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Session get(Request request) {
        return getInstance();
    }
}
