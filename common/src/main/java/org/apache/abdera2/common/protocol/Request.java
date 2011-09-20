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

import java.util.Date;

import org.apache.abdera2.common.http.EntityTag;

/**
 * A protocol request. This is used as a base for both server and client requests
 */
public interface Request extends Message {

    /**
     * Get the value of the Accept header
     */
    String getAccept();

    /**
     * Get the value of the Accept-Charset header
     */
    String getAcceptCharset();

    /**
     * Get the value of the Accept-Encoding header
     */
    String getAcceptEncoding();

    /**
     * Get the value of the Accept-Language header
     */
    String getAcceptLanguage();

    /**
     * Get a listing of Etags from the If-Match header
     */
    Iterable<EntityTag> getIfMatch();

    /**
     * Get the value of the If-Modified-Since header
     */
    Date getIfModifiedSince();

    /**
     * Get a listing of ETags from the If-None-Match header
     */
    Iterable<EntityTag> getIfNoneMatch();

    /**
     * Get the value of the If-Unmodified-Since header
     */
    Date getIfUnmodifiedSince();

}
