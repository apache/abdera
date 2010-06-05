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
package org.apache.abdera.security.util.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextWrapper;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.writer.Writer;

@SuppressWarnings("unchecked")
public abstract class AbstractEncryptedResponseFilter implements Filter {

    public AbstractEncryptedResponseFilter() {
        initProvider();
    }

    protected void initProvider() {
    }

    protected void addProvider(Provider provider) {
        if (Security.getProvider(provider.getName()) == null)
            Security.addProvider(provider);
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        Object arg = initArg(request);
        if (doEncryption(request, arg)) {
            return new EncryptingResponseContext(request.getAbdera(), request, chain.next(request), arg);
        } else {
            return chain.next(request);
        }
    }

    protected abstract boolean doEncryption(RequestContext request, Object arg);

    protected abstract EncryptionOptions initEncryptionOptions(RequestContext request,
                                                               ResponseContext response,
                                                               Encryption enc,
                                                               Object arg);

    protected abstract Object initArg(RequestContext request);

    private class EncryptingResponseContext extends ResponseContextWrapper {

        private final RequestContext request;
        private final AbderaSecurity security;
        private final Abdera abdera;
        private final Object arg;

        public EncryptingResponseContext(Abdera abdera, RequestContext request, ResponseContext response, Object arg) {
            super(response);
            this.abdera = abdera;
            this.request = request;
            this.security = new AbderaSecurity(abdera);
            this.arg = arg;
        }

        public void writeTo(OutputStream out, Writer writer) throws IOException {
            try {
                encrypt(out, null);
            } catch (Exception se) {
                throw new RuntimeException(se);
            }
        }

        public void writeTo(OutputStream out) throws IOException {
            try {
                encrypt(out, null);
            } catch (Exception se) {
                throw new RuntimeException(se);
            }
        }

        private void encrypt(OutputStream aout, Writer writer) throws Exception {
            Document<Element> doc = null;
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (writer == null)
                    super.writeTo(out);
                else
                    super.writeTo(out, writer);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                doc = abdera.getParser().parse(in);
            } catch (Exception e) {
            }
            if (doc != null) {
                Encryption enc = security.getEncryption();
                EncryptionOptions options = initEncryptionOptions(request, response, enc, arg);
                doc = enc.encrypt(doc, options);
            }
            if (doc != null)
                doc.writeTo(aout);
            else
                throw new RuntimeException("There was an error encrypting the response");
        }
    }
}
