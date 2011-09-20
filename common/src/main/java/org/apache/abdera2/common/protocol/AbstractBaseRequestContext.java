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
package org.apache.abdera2.common.protocol;

import java.security.Principal;
import java.util.Arrays;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.abdera2.common.iri.IRI;

@SuppressWarnings("unchecked")
public abstract class AbstractBaseRequestContext 
  extends AbstractRequest 
  implements RequestContext {

    protected final Provider provider;
    protected Subject subject;
    protected Principal principal;
    protected Target target;
    protected final String method;
    protected final IRI requestUri;
    protected final IRI baseUri;
    
    protected AbstractBaseRequestContext(
        Provider provider, 
        String method, 
        IRI requestUri, 
        IRI baseUri) {
        this.provider = provider;
        this.method = method;
        this.baseUri = baseUri;
        this.requestUri = requestUri;
    }
    
    protected Target initTarget(Provider provider) {
      try {
          return provider.resolveTarget(this);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }
    
    protected Target initTarget() {
        try {
            Target target = initTarget(provider);
            return target != null ? 
              target : 
              new SimpleTarget(TargetType.TYPE_NOT_FOUND, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public <T extends RequestContext>T setAttribute(String name, Object value) {
        return setAttribute(Scope.REQUEST, name, value);
    }

    public String urlFor(Object key, Object param) {
        return provider.urlFor(this, key, param);
    }

    public String absoluteUrlFor(Object key, Object param) {
        return getResolvedUri().resolve(urlFor(key, param)).toString();
    }

    public Iterator<Property> iterator() {
      return Arrays.asList(Property.values()).iterator();
    }
}
