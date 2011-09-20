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

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.ResponseType;

/**
 * Base interface for an Atompub protocol response message
 */
public interface Response extends Message {

    /**
     * Get the Entity Tag returned by the server
     */
    EntityTag getEntityTag();

    /**
     * Get the response type classification
     */
    ResponseType getType();

    /**
     * Get the specific response status code
     */
    int getStatus();

    /**
     * Get the response status text
     */
    String getStatusText();

    /**
     * Get the value of the Last-Modified response header
     */
    Date getLastModified();

    /**
     * Get the value of the Content-Length response header
     */
    long getContentLength();

    /**
     * Get the value of the Allow response header
     */
    String getAllow();

    /**
     * Get the value of the Location response header
     */
    IRI getLocation();

    /**
     * Get the age of this response as specified by the server
     */
    long getAge();

    /**
     * Get the date/time this response expires
     */
    Date getExpires();

}
