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
package org.apache.abdera.protocol.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.i18n.text.Rfc2047Helper;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

/**
 * The RequestOptions class allows a variety of options affecting the execution of the request to be modified.
 */
public class RequestOptions extends AbstractRequest implements Request {

    private boolean noLocalCache = false;
    private boolean revalidateAuth = false;
    private boolean useChunked = false;
    private boolean usePostOverride = false;
    private boolean requestException4xx = false;
    private boolean requestException5xx = false;
    private boolean useExpectContinue = true;
    private boolean useConditional = true;
    private boolean followRedirects = true;

    private final Map<String, String[]> headers = new HashMap<String, String[]>();

    public RequestOptions() {
    }

    /**
     * Create the RequestOptions object with the specified If-Modified-Since header value
     * 
     * @param ifModifiedSince
     */
    public RequestOptions(Date ifModifiedSince) {
        this();
        setIfModifiedSince(ifModifiedSince);
    }

    /**
     * Create the RequestOptions object with the specified If-None-Match header value
     * 
     * @param IfNoneMatch
     */
    public RequestOptions(String ifNoneMatch) {
        this();
        setIfNoneMatch(ifNoneMatch);
    }

    /**
     * Create the RequestOptions object with the specified If-None-Match header value
     * 
     * @param IfNoneMatch
     */
    public RequestOptions(String... ifNoneMatch) {
        this();
        setIfNoneMatch(ifNoneMatch);
    }

    /**
     * Create the RequestOptions object with the specified If-Modified-Since and If-None-Match header values
     * 
     * @param ifModifiedSince
     * @param IfNoneMatch
     */
    public RequestOptions(Date ifModifiedSince, String ifNoneMatch) {
        this();
        setIfModifiedSince(ifModifiedSince);
        setIfNoneMatch(ifNoneMatch);
    }

    /**
     * Create the RequestOptions object with the specified If-Modified-Since and If-None-Match header values
     * 
     * @param ifModifiedSince
     * @param IfNoneMatch
     */
    public RequestOptions(Date ifModifiedSince, String... ifNoneMatch) {
        this();
        setIfModifiedSince(ifModifiedSince);
        setIfNoneMatch(ifNoneMatch);
    }

    /**
     * Create the RequestOptions object
     * 
     * @param no_cache True if the request will indicate that cached responses should not be returned
     */
    public RequestOptions(boolean no_cache) {
        this();
        setNoCache(no_cache);
    }

    private Map<String, String[]> getHeaders() {
        return headers;
    }

    private String combine(String... values) {
        StringBuilder v = new StringBuilder();
        for (String val : values) {
            if (v.length() > 0)
                v.append(", ");
            v.append(val);
        }
        return v.toString();
    }

    /**
     * The difference between this and getNoCache is that this only disables the local cache without affecting the
     * Cache-Control header.
     */
    public boolean getUseLocalCache() {
        return !noLocalCache;
    }

    /**
     * True if the local client cache should be used
     */
    public RequestOptions setUseLocalCache(boolean use_cache) {
        this.noLocalCache = !use_cache;
        return this;
    }

    /**
     * Set the value of the HTTP Content-Type header
     */
    public RequestOptions setContentType(String value) {
        return setHeader("Content-Type", value);
    }

    public RequestOptions setContentLocation(String iri) {
        return setHeader("Content-Location", iri);
    }

    /**
     * Set the value of the HTTP Content-Type header
     */
    public RequestOptions setContentType(MimeType value) {
        return setHeader("Content-Type", value.toString());
    }

    /**
     * Set the value of the HTTP Authorization header
     */
    public RequestOptions setAuthorization(String auth) {
        return setHeader("Authorization", auth);
    }

    /**
     * Set the value of a header using proper encoding of non-ascii characters
     */
    public RequestOptions setEncodedHeader(String header, String charset, String value) {
        return setHeader(header, Rfc2047Helper.encode(value, charset));
    }

    /**
     * Set the values of a header using proper encoding of non-ascii characters
     */
    public RequestOptions setEncodedHeader(String header, String charset, String... values) {
        if (values != null && values.length > 0) {
            for (int n = 0; n < values.length; n++) {
                values[n] = Rfc2047Helper.encode(values[n], charset);
            }
            getHeaders().put(header, new String[] {combine(values)});
        } else {
            removeHeaders(header);
        }
        return this;
    }

    /**
     * Set the value of the specified HTTP header
     */
    public RequestOptions setHeader(String header, String value) {
        return value != null ? setHeader(header, new String[] {value}) : removeHeaders(header);
    }

    /**
     * Set the value of the specified HTTP header
     */
    public RequestOptions setHeader(String header, String... values) {
        if (values != null && values.length > 0) {
            getHeaders().put(header, new String[] {combine(values)});
        } else {
            removeHeaders(header);
        }
        return this;
    }

    /**
     * Set the date value of the specified HTTP header
     */
    public RequestOptions setDateHeader(String header, Date value) {
        return value != null ? setHeader(header, DateUtil.formatDate(value)) : removeHeaders(header);
    }

    /**
     * Similar to setEncodedHeader, but allows for multiple instances of the specified header
     */
    public RequestOptions addEncodedHeader(String header, String charset, String value) {
        return addHeader(header, Rfc2047Helper.encode(value, charset));
    }

    /**
     * Similar to setEncodedHeader, but allows for multiple instances of the specified header
     */
    public RequestOptions addEncodedHeader(String header, String charset, String... values) {
        if (values == null || values.length == 0)
            return this;
        for (int n = 0; n < values.length; n++) {
            values[n] = Rfc2047Helper.encode(values[n], charset);
        }
        List<String> list = Arrays.asList(getHeaders().get(header));
        String value = combine(values);
        if (list != null) {
            if (!list.contains(value))
                list.add(value);
        } else {
            setHeader(header, new String[] {value});
        }
        return this;
    }

    /**
     * Similar to setHeader but allows for multiple instances of the specified header
     */
    public RequestOptions addHeader(String header, String value) {
        if (value != null)
            addHeader(header, new String[] {value});
        return this;
    }

    /**
     * Similar to setHeader but allows for multiple instances of the specified header
     */
    public RequestOptions addHeader(String header, String... values) {
        if (values == null || values.length == 0)
            return this;
        String[] headers = getHeaders().get(header);
        List<String> list = headers != null ? Arrays.asList(headers) : new ArrayList<String>();
        String value = combine(values);
        if (list != null) {
            if (!list.contains(value))
                list.add(value);
        } else {
            setHeader(header, new String[] {value});
        }
        return this;
    }

    /**
     * Similar to setDateHeader but allows for multiple instances of the specified header
     */
    public RequestOptions addDateHeader(String header, Date value) {
        if (value == null)
            return this;
        return addHeader(header, DateUtil.formatDate(value));
    }

    /**
     * Returns the text value of the specified header
     */
    public String getHeader(String header) {
        String[] list = getHeaders().get(header);
        return (list != null && list.length > 0) ? list[0] : null;
    }

    /**
     * Return a listing of text values for the specified header
     */
    public String[] getHeaders(String header) {
        return getHeaders().get(header);
    }

    /**
     * Returns the date value of the specified header
     */
    public Date getDateHeader(String header) {
        String val = getHeader(header);
        try {
            return (val != null) ? DateUtil.parseDate(val) : null;
        } catch (DateParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a listing of header names
     */
    public String[] getHeaderNames() {
        Set<String> names = getHeaders().keySet();
        return names.toArray(new String[names.size()]);
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public RequestOptions setIfMatch(String entity_tag) {
        return setIfMatch(new EntityTag(entity_tag));
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public RequestOptions setIfMatch(EntityTag entity_tag) {
        return setHeader("If-Match", entity_tag.toString());
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public RequestOptions setIfMatch(EntityTag... entity_tags) {
        return setHeader("If-Match", EntityTag.toString(entity_tags));
    }

    /**
     * Sets the value of the HTTP If-Match header
     */
    public RequestOptions setIfMatch(String... entity_tags) {
        return setHeader("If-Match", EntityTag.toString(entity_tags));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public RequestOptions setIfNoneMatch(String entity_tag) {
        return setIfNoneMatch(new EntityTag(entity_tag));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public RequestOptions setIfNoneMatch(EntityTag entity_tag) {
        return setHeader("If-None-Match", entity_tag.toString());
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public RequestOptions setIfNoneMatch(EntityTag... entity_tags) {
        return setHeader("If-None-Match", EntityTag.toString(entity_tags));
    }

    /**
     * Sets the value of the HTTP If-None-Match header
     */
    public RequestOptions setIfNoneMatch(String... entity_tags) {
        return setHeader("If-None-Match", EntityTag.toString(entity_tags));
    }

    /**
     * Sets the value of the HTTP If-Modified-Since header
     */
    public RequestOptions setIfModifiedSince(Date date) {
        return setDateHeader("If-Modified-Since", date);
    }

    /**
     * Sets the value of the HTTP If-Unmodified-Since header
     */
    public RequestOptions setIfUnmodifiedSince(Date date) {
        return setDateHeader("If-Unmodified-Since", date);
    }

    /**
     * Sets the value of the HTTP Accept header
     */
    public RequestOptions setAccept(String accept) {
        return setAccept(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept header
     */
    public RequestOptions setAccept(String... accept) {
        return setHeader("Accept", combine(accept));
    }

    public RequestOptions setAcceptLanguage(Locale locale) {
        return setAcceptLanguage(Lang.fromLocale(locale));
    }

    public RequestOptions setAcceptLanguage(Locale... locales) {
        String[] langs = new String[locales.length];
        for (int n = 0; n < locales.length; n++)
            langs[n] = Lang.fromLocale(locales[n]);
        setAcceptLanguage(langs);
        return this;
    }

    /**
     * Sets the value of the HTTP Accept-Language header
     */
    public RequestOptions setAcceptLanguage(String accept) {
        return setAcceptLanguage(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Language header
     */
    public RequestOptions setAcceptLanguage(String... accept) {
        return setHeader("Accept-Language", combine(accept));
    }

    /**
     * Sets the value of the HTTP Accept-Charset header
     */
    public RequestOptions setAcceptCharset(String accept) {
        return setAcceptCharset(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Charset header
     */
    public RequestOptions setAcceptCharset(String... accept) {
        return setHeader("Accept-Charset", combine(accept));
    }

    /**
     * Sets the value of the HTTP Accept-Encoding header
     */
    public RequestOptions setAcceptEncoding(String accept) {
        return setAcceptEncoding(new String[] {accept});
    }

    /**
     * Sets the value of the HTTP Accept-Encoding header
     */
    public RequestOptions setAcceptEncoding(String... accept) {
        return setHeader("Accept-Encoding", combine(accept));
    }

    /**
     * Sets the value of the Atom Publishing Protocol Slug header
     */
    public RequestOptions setSlug(String slug) {
        if (slug.indexOf((char)10) > -1 || slug.indexOf((char)13) > -1)
            throw new IllegalArgumentException(Localizer.get("SLUG.BAD.CHARACTERS"));
        return setHeader("Slug", UrlEncoding.encode(slug, Profile.ASCIISANSCRLF.filter()));
    }

    /**
     * Sets the value of the HTTP Cache-Control header
     */
    public RequestOptions setCacheControl(String cc) {
        CacheControlUtil.parseCacheControl(cc, this);
        return this;
    }

    /**
     * Remove the specified HTTP header
     */
    public RequestOptions removeHeaders(String name) {
        getHeaders().remove(name);
        return this;
    }

    /**
     * Return the value of the Cache-Control header
     */
    public String getCacheControl() {
        return CacheControlUtil.buildCacheControl(this);
    }

    /**
     * Configure the AbderaClient Side cache to revalidate when using Authorization
     */
    public boolean getRevalidateWithAuth() {
        return revalidateAuth;
    }

    /**
     * Configure the AbderaClient Side cache to revalidate when using Authorization
     */
    public RequestOptions setRevalidateWithAuth(boolean revalidateAuth) {
        this.revalidateAuth = revalidateAuth;
        return this;
    }

    /**
     * Should the request use chunked encoding?
     */
    public boolean isUseChunked() {
        return useChunked;
    }

    /**
     * Set whether the request should use chunked encoding.
     */
    public RequestOptions setUseChunked(boolean useChunked) {
        this.useChunked = useChunked;
        return this;
    }

    /**
     * Set whether the request should use the X-HTTP-Method-Override option
     */
    public RequestOptions setUsePostOverride(boolean useOverride) {
        this.usePostOverride = useOverride;
        return this;
    }

    /**
     * Return whether the request should use the X-HTTP-Method-Override option
     */
    public boolean isUsePostOverride() {
        return this.usePostOverride;
    }

    /**
     * Set whether or not to throw a RequestExeption on 4xx responses
     */
    public RequestOptions set4xxRequestException(boolean v) {
        this.requestException4xx = v;
        return this;
    }

    /**
     * Return true if a RequestException should be thrown on 4xx responses
     */
    public boolean is4xxRequestException() {
        return this.requestException4xx;
    }

    /**
     * Set whether or not to throw a RequestExeption on 5xx responses
     */
    public RequestOptions set5xxRequestException(boolean v) {
        this.requestException5xx = v;
        return this;
    }

    /**
     * Return true if a RequestException should be thrown on 5xx responses
     */
    public boolean is5xxRequestException() {
        return this.requestException5xx;
    }

    /**
     * Set whether or not to use the HTTP Expect-Continue mechanism (enabled by default)
     */
    public RequestOptions setUseExpectContinue(boolean useExpect) {
        this.useExpectContinue = useExpect;
        return this;
    }

    /**
     * Return true if Expect-Continue should be used
     */
    public boolean isUseExpectContinue() {
        return this.useExpectContinue;
    }

    /**
     * True if HTTP Conditional Requests should be used automatically. This only has an effect when putting a Document
     * that has an ETag or Last-Modified date present
     */
    public boolean isConditionalPut() {
        return this.useConditional;
    }

    /**
     * True if HTTP Conditinal Request should be used automatically. This only has an effect when putting a Document
     * that has an ETag or Last-Modified date present
     */
    public RequestOptions setConditionalPut(boolean conditional) {
        this.useConditional = conditional;
        return this;
    }

    /**
     * True if the client should follow redirects automatically
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * True if the client should follow redirects automatically
     */
    public RequestOptions setFollowRedirects(boolean followredirects) {
        this.followRedirects = followredirects;
        return this;
    }
}
