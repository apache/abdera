/**
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.abdera.spring;

import java.util.Collection;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class ProviderDefinitionParserTest extends AbstractDependencyInjectionSpringContextTests {

    @Test
    public void testParser() throws Exception {
        DefaultProvider p = (DefaultProvider)applicationContext.getBean(Provider.class.getName());

        Resolver<Target> tresolver = p.getTargetResolver();
        assertTrue(tresolver instanceof RegexTargetResolver);

        WorkspaceManager wm = p.getWorkspaceManager();

        Collection<WorkspaceInfo> workspaces = wm.getWorkspaces(null);
        assertEquals(1, workspaces.size());

        WorkspaceInfo w = workspaces.iterator().next();
        assertNotNull(w);
        assertEquals("Foo Workspace", w.getTitle(null));

        Collection<CollectionInfo> collections = w.getCollections(null);
        assertEquals(2, collections.size());

        assertEquals(2, p.getFilters(null).length); // Parameter isn't used
    }

    @Override
    protected String getConfigPath() {
        return "/org/apache/abdera/spring/beans.xml";
    }

}
