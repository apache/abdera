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
package org.apache.abdera.protocol;

import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.util.ProtocolConstants;

/**
 * A protocol message. This is used as the basis for both request and response objects in order to provide a consistent
 * interface.
 */
public interface Message extends ProtocolConstants {

    /**
     * Get the value of the specified header
     */
    String getHeader(String name);

    /**
     * Get the decoded value of a RFC 2047 header
     */
    String getDecodedHeader(String name);

    /**
     * Return multiple values for the specified header
     */
    Object[] getHeaders(String name);

    /**
     * Return multiple decoded values for the specified header
     */
    String[] getDecodedHeaders(String name);

    /**
     * Return a listing of header names
     */
    String[] getHeaderNames();

    /**
     * Return the value of the Cache-Control header
     */
    String getCacheControl();

    /**
     * Return the value of the Slug header
     */
    String getSlug();

    /**
     * Return the value of the Content-Type header
     */
    MimeType getContentType();

    /**
     * Return the value of the Content-Location header
     */
    IRI getContentLocation();

    /**
     * Return the value of the Content-Language header
     */
    String getContentLanguage();

    /**
     * Return the value of a Date header
     */
    Date getDateHeader(String name);

    /**
     * Return the maximum-age as specified by the Cache-Control header
     */
    long getMaxAge();

    /**
     * Return true if the Cache-Control header contains no-cache
     */
    boolean isNoCache();

    /**
     * Return true if the Cache-Control header contains no-store
     */
    boolean isNoStore();

    /**
     * Return true if the Cache-Control header contains no-transform
     */
    boolean isNoTransform();

}
