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
package org.apache.abdera.protocol.server.context;

import java.io.IOException;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.writer.StreamWriter;

/**
 * StreamWriterResponseContext implementation based on the EntityProvider interface
 */
public class EntityProviderResponseContext extends StreamWriterResponseContext {

    private final EntityProvider provider;

    public EntityProviderResponseContext(EntityProvider provider, Abdera abdera, String encoding, String sw) {
        super(abdera, encoding, sw);
        this.provider = provider;
        init();
    }

    public EntityProviderResponseContext(EntityProvider provider, Abdera abdera, String encoding) {
        super(abdera, encoding);
        this.provider = provider;
        init();
    }

    public EntityProviderResponseContext(EntityProvider provider, Abdera abdera) {
        super(abdera);
        this.provider = provider;
        init();
    }

    private void init() {
        setContentType(provider.getContentType());
        setEntityTag(provider.getEntityTag());
        setLastModified(provider.getLastModified());
    }

    @Override
    protected void writeTo(StreamWriter sw) throws IOException {
        provider.writeTo(sw);
    }

}
