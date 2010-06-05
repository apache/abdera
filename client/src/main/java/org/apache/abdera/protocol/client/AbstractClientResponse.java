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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.util.AbstractResponse;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

/**
 * Abstract base class for a ClientResponse
 */
public abstract class AbstractClientResponse extends AbstractResponse implements ClientResponse {

    protected final Abdera abdera;
    protected final Parser parser;
    protected final Date now = new Date();

    protected InputStream in = null;
    protected Date response_date = null;

    protected AbstractClientResponse(Abdera abdera) {
        this.abdera = abdera;
        this.parser = abdera.getParser();
    }

    protected Date initResponseDate() {
        Date date = getDateHeader("Date");
        return (date != null) ? date : now;
    }

    protected synchronized Parser getParser() {
        return parser;
    }

    /**
     * Get the response payload as a parsed Abdera FOM Document
     */
    public <T extends Element> Document<T> getDocument() throws ParseException {
        return getDocument(getParser());
    }

    /**
     * Get the response payload as a parsed Abdera FOM Document using the specified parser options
     * 
     * @param options The parser options
     */
    public <T extends Element> Document<T> getDocument(ParserOptions options) throws ParseException {
        return getDocument(getParser(), options);
    }

    /**
     * Get the response payload as a parsed Abdera FOM Document using the specified parser
     * 
     * @param parser The parser
     */
    public <T extends Element> Document<T> getDocument(Parser parser) throws ParseException {
        return getDocument(parser, parser.getDefaultParserOptions());
    }

    /**
     * Get the response payload as a parsed Abdera FOM Document using the specified parser and parser options
     * 
     * @param parser The parser
     * @param options The parser options
     */
    public <T extends Element> Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException {
        try {
            if (options == null)
                options = parser.getDefaultParserOptions();
            String charset = getCharacterEncoding();
            if (charset != null)
                options.setCharset(charset);
            IRI cl = getContentLocation();
            if (cl != null && !cl.isAbsolute()) {
                IRI r = new IRI(getUri());
                cl = r.resolve(cl);
            }
            String base = (cl != null) ? cl.toASCIIString() : getUri();
            Document<T> doc = parser.parse(getReader(), base, options);
            EntityTag etag = getEntityTag();
            if (etag != null)
                doc.setEntityTag(etag);
            Date lm = getLastModified();
            if (lm != null)
                doc.setLastModified(lm);
            MimeType mt = getContentType();
            if (mt != null)
                doc.setContentType(mt.toString());
            String language = getContentLanguage();
            if (language != null)
                doc.setLanguage(language);
            String slug = getSlug();
            if (slug != null)
                doc.setSlug(slug);
            return doc;
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * Get the response payload as an input stream
     */
    public InputStream getInputStream() throws IOException {
        return in;
    }

    /**
     * Set the response input stream (used internally by Abdera)
     */
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * Get the response payload as a reader (assumed UTF-8 charset)
     */
    public Reader getReader() throws IOException {
        String charset = getCharacterEncoding();
        return getReader(charset != null ? charset : "UTF-8");
    }

    /**
     * Get the response payload as a reader using the specified charset
     * 
     * @param charset The character set encoding
     */
    public Reader getReader(String charset) throws IOException {
        if (charset == null)
            charset = "UTF-8";
        return new InputStreamReader(getInputStream(), charset);
    }

    /**
     * Return the date returned by the server in the response
     */
    public Date getServerDate() {
        if (response_date == null)
            response_date = initResponseDate();
        return response_date;
    }

    protected void parse_cc() {
        String cc = getHeader("Cache-Control");
        if (cc != null)
            CacheControlUtil.parseCacheControl(cc, this);
    }

    /**
     * Get the character set encoding specified by the server in the Content-Type header
     */
    public String getCharacterEncoding() {
        String charset = null;
        try {
            MimeType mt = getContentType();
            if (mt != null)
                charset = mt.getParameter("charset");
        } catch (Exception e) {
        }
        return charset;
    }

    /**
     * Return the named HTTP header as a java.util.Date
     */
    public Date getDateHeader(String header) {
        try {
            String value = getHeader(header);
            if (value != null)
                return DateUtil.parseDate(value);
            else
                return null;
        } catch (DateParseException e) {
            return null; // treat other invalid date formats, especially including the value "0", as in the past
        }
    }
}
