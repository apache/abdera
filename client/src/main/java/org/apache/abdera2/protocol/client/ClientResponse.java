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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Date;

import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.protocol.Response;

public interface ClientResponse extends Response {

    Session getSession();
  
    /**
     * Return the request method
     */
    Method getMethod();

    /**
     * Return the request URI. The request was redirected, this will return the new URI
     */
    String getUri();

    /**
     * Release the resources associated with this response
     */
    void release();

    /**
     * Returns the inputstream used to read data from this response
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns a reader used to read data from this response. Will use the character set declared in the Content-Type to
     * create the reader
     */
    Reader getReader() throws IOException;

    /**
     * Returns a reader used to read data from this response. Will use the character set specified to create the reader
     */
    Reader getReader(String charset) throws IOException;

    /**
     * Return the server-specified date returned in the response
     */
    Date getServerDate();

    /**
     * Return the character set encoding specified in the ContentType header, if any
     */
    String getCharacterEncoding();

    void writeTo(OutputStream out) throws IOException;
}
