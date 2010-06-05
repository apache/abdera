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
import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.SimpleTarget;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRequestContext extends AbstractRequest implements RequestContext {

    private final static Log log = LogFactory.getLog(AbstractRequestContext.class);

    protected final Provider provider;
    protected Subject subject;
    protected Principal principal;
    protected Target target;
    protected final String method;
    protected final IRI requestUri;
    protected final IRI baseUri;
    protected Document<?> document;

    protected AbstractRequestContext(Provider provider, String method, IRI requestUri, IRI baseUri) {
        this.provider = provider;
        this.method = method;
        this.baseUri = baseUri;
        this.requestUri = requestUri;

    }

    protected Target initTarget() {
        try {
            Target target = provider.resolveTarget(this);
            return target != null ? target : new SimpleTarget(TargetType.TYPE_NOT_FOUND, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Abdera getAbdera() {
        return provider.getAbdera();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Element> Document<T> getDocument() throws ParseException, IOException {
        log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
        if (document == null)
            document = getDocument(getAbdera().getParser());
        return (Document<T>)document;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Element> Document<T> getDocument(Parser parser) throws ParseException, IOException {
        log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
        if (parser == null)
            parser = getAbdera().getParser();
        if (parser == null)
            throw new IllegalArgumentException("No Parser implementation was provided");
        if (document == null)
            document = getDocument(parser, parser.getDefaultParserOptions());
        return (Document<T>)document;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Element> Document<T> getDocument(ParserOptions options) throws ParseException,
        IOException {
        log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
        if (document == null)
            document = getDocument(getAbdera().getParser(), options);
        return (Document<T>)document;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Element> Document<T> getDocument(Parser parser, ParserOptions options)
        throws ParseException, IOException {
        log.debug(Localizer.get("PARSING.REQUEST.DOCUMENT"));
        if (parser == null)
            parser = getAbdera().getParser();
        if (parser == null)
            throw new IllegalArgumentException("No Parser implementation was provided");
        if (document == null) {
            document = parser.parse(getInputStream(), getResolvedUri().toString(), options);
        }
        return (Document<T>)document;
    }

    public IRI getBaseUri() {
        return baseUri;
    }

    public IRI getResolvedUri() {
        return baseUri.resolve(getUri());
    }

    public String getMethod() {
        return method;
    }

    public IRI getUri() {
        return requestUri;
    }

    public Subject getSubject() {
        return subject;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public Target getTarget() {
        return target;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getTargetPath() {
        String uri = getUri().toString();
        String cpath = getContextPath();
        return cpath == null ? uri : uri.substring(cpath.length());
    }

    public RequestContext setAttribute(String name, Object value) {
        return setAttribute(Scope.REQUEST, name, value);
    }

    public String urlFor(Object key, Object param) {
        return provider.urlFor(this, key, param);
    }

    public String absoluteUrlFor(Object key, Object param) {
        return getResolvedUri().resolve(urlFor(key, param)).toString();
    }

    public boolean isAtom() {
        try {
            return MimeTypeHelper.isAtom(getContentType().toString());
        } catch (Exception e) {
            return false;
        }
    }
}
