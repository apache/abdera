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

import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.RequestContextWrapper;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;

public abstract class AbstractEncryptedRequestFilter implements Filter {

    // The methods that allow encrypted bodies
    protected final List<String> methods = new ArrayList<String>();

    protected AbstractEncryptedRequestFilter() {
        this("POST", "PUT");
    }

    protected AbstractEncryptedRequestFilter(String... methods) {
        for (String method : methods)
            this.methods.add(method);
        initProvider();
    }

    protected void initProvider() {
    }

    protected void addProvider(Provider provider) {
        if (Security.getProvider(provider.getName()) == null)
            Security.addProvider(provider);
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        bootstrap(request);
        String method = request.getMethod();
        if (methods.contains(method.toUpperCase())) {
            return chain.next(new DecryptingRequestContextWrapper(request));
        } else
            return chain.next(request);
    }

    protected abstract void bootstrap(RequestContext request);

    protected abstract Object initArg(RequestContext request);

    protected abstract EncryptionOptions initEncryptionOptions(RequestContext request, Encryption encryption, Object arg);

    private class DecryptingRequestContextWrapper extends RequestContextWrapper {

        public DecryptingRequestContextWrapper(RequestContext request) {
            super(request);
        }

        @SuppressWarnings("unchecked")
        public <T extends Element> Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException,
            IOException {
            Document<Element> doc = super.getDocument();
            try {
                if (doc != null) {
                    AbderaSecurity security = new AbderaSecurity(getAbdera());
                    Encryption enc = security.getEncryption();
                    if (enc.isEncrypted(doc)) {
                        Object arg = initArg(request);
                        EncryptionOptions encoptions = initEncryptionOptions(request, enc, arg);
                        doc = enc.decrypt(doc, encoptions);
                    }
                }
            } catch (Exception e) {
            }
            return (Document<T>)doc;
        }
    }
}
