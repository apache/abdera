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
package org.apache.abdera2.protocol.client;

import java.util.Date;
import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.protocol.EntityProvider;
import org.apache.abdera2.protocol.error.Error;
import org.apache.abdera2.protocol.error.AbderaProtocolException;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.http.entity.mime.content.ContentBody;

/**
 * A client session. Session's MUST NOT be used by more
 * than one Thread of execution as a time as multiple threads would stomp 
 * all over the shared session context. It is critical to completely
 * consume each ClientResponse before executing an additional request on 
 * the same session.
 */
public class AbderaSession extends Session {

    protected AbderaSession(AbderaClient client) {
        super(client);
    }
    
    protected AbderaClient getAbderaClient() {
      return (AbderaClient) client;
    }
    
    protected Abdera getAbdera() {
      return getAbderaClient().getAbdera();
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends ClientResponse>T wrap(ClientResponse resp) {
      return (T)(resp instanceof AbderaClientResponseImpl ?
        resp : new AbderaClientResponseImpl(resp));
    }
    
    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, EntityProvider provider, RequestOptions options) {
        return wrap(post(uri, new EntityProviderEntity(getAbdera(), provider), options));
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param base An Abdera FOM Document or Element object providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, Base base, RequestOptions options) {
        if (base instanceof Document) {
            Document<?> d = (Document<?>)base;
            if (options.getSlug() == null && d.getSlug() != null)
                options.setSlug(d.getSlug());
        }
        return wrap(execute("POST", uri, new AbderaEntity(base), options));
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object. If the contentType is not provided this method tries to get it from the type attribute
     * of the entry content.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     */
    public <T extends ClientResponse>T post(String uri, Entry entry, ContentBody media) {
        return wrap(post(uri, entry, media, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object. If the contentType is not provided this method tries to get it from the type attribute
     * of the entry content.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, Entry entry, ContentBody media, RequestOptions options) {
        return wrap(post(uri, entry, media, null, options));
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     * @param contentType the content type of the media object
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, Entry entry, ContentBody media, String contentType, RequestOptions options) {
        return wrap(execute("POST", uri, new MultipartRelatedEntity(entry, media, contentType), options));
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, EntityProvider provider, RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        if (options.isConditionalPut()) {
            EntityTag etag = provider.getEntityTag();
            if (etag != null)
                options.setIfMatch(etag);
            else {
                Date lm = provider.getLastModified();
                if (lm != null)
                    options.setIfUnmodifiedSince(lm);
            }
        }
        return wrap(put(uri, new EntityProviderEntity(getAbdera(), provider), options));
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, Base base, RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        if (base instanceof Document) {
            Document<?> d = (Document<?>)base;
            if (options.getSlug() == null && d.getSlug() != null)
                options.setSlug(d.getSlug());

            if (options.isConditionalPut()) {
                if (d.getEntityTag() != null)
                    options.setIfMatch(d.getEntityTag());
                else if (d.getLastModified() != null)
                    options.setIfUnmodifiedSince(d.getLastModified());
            }
        }
        return wrap(execute("PUT", uri, new AbderaEntity(base), options));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload the request
     */
    public <T extends ClientResponse>T post(String uri, EntityProvider provider) {
        return wrap(post(uri, provider, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     */
    public <T extends ClientResponse>T post(String uri, Base base) {
        return wrap(post(uri, base, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, EntityProvider provider) {
        return wrap(put(uri, provider, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, Base base) {
        return wrap(put(uri, base, getDefaultRequestOptions()));
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param base A FOM Document and Element providing the payload for the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        String method, 
        String uri, 
        Base base, 
        RequestOptions options) {
        return wrap(execute(
            method, 
            uri, 
            new AbderaEntity(base), 
            options));
    }
    
    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param base A FOM Document and Element providing the payload for the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        Method method, 
        String uri, 
        Base base, 
        RequestOptions options) {
        return wrap(execute(
            method.name(), 
            uri, 
            new AbderaEntity(base), 
            options));
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        String method, String uri, 
        EntityProvider provider, 
        RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        return wrap(execute(
            method, 
            uri, 
            new EntityProviderEntity(getAbdera(), provider), 
            options));
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The Request Options
     */
    public <T extends ClientResponse>T execute(
        Method method, String uri, 
        EntityProvider provider, 
        RequestOptions options) {
          return wrap(execute(method.name(),uri,provider,options));
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends ClientResponse>T checkRequestException(ClientResponse response, RequestOptions options) {
        if (response == null)
            return (T)response;
        AbderaClientResponse acr = (AbderaClientResponse) response;
        ResponseType type = response.getType();
        if ((type.equals(ResponseType.CLIENT_ERROR) && options.is4xxRequestException()) || (type
            .equals(ResponseType.SERVER_ERROR) && options.is5xxRequestException())) {
            try {
                Document<Element> doc = acr.getDocument();
                org.apache.abdera2.protocol.error.Error error = null;
                if (doc != null) {
                    Element root = doc.getRoot();
                    if (root instanceof Error)
                        error = (Error)root;
                }
                if (error == null)
                    error =
                        org.apache.abdera2.protocol.error.Error.create(
                          getAbdera(), 
                          response.getStatus(), 
                          response.getStatusText());
                error.throwException();
            } catch (AbderaProtocolException pe) {
                throw pe;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (T)response;
    }
    
}
