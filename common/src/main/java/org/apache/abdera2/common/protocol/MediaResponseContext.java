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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

import org.apache.abdera2.common.http.EntityTag;

/**
 * ResponseContext implementation for arbitrary media resources
 */
public class MediaResponseContext extends SimpleResponseContext {

    private InputStream in;
    // JIRA: https://issues.apache.org/jira/browse/ABDERA-248
    private boolean autoclose = true;

    public MediaResponseContext(InputStream in, EntityTag etag, int status, boolean autoclose) {
      this.in = in;
      this.status = status;
      this.autoclose = false;
      setEntityTag(etag);
    }
    
    public MediaResponseContext(InputStream in, EntityTag etag, int status) {
        this(in,etag,status,true);
    }

    public MediaResponseContext(InputStream in, int status, boolean autoclose) {
      this.in = in;
      this.status = status;
      this.autoclose = autoclose;
    }
    
    public MediaResponseContext(InputStream in, int status) {
        this(in,status,true);
    }

    public MediaResponseContext(InputStream in, Date lastmodified, int status, boolean autoclose) {
      this.in = in;
      this.status = status;
      this.autoclose = autoclose;
      setLastModified(lastmodified);
    }
    
    public MediaResponseContext(InputStream in, Date lastmodified, int status) {
        this(in,lastmodified,status,true);
    }

    public MediaResponseContext(byte[] bytes, int status) {
        this(new ByteArrayInputStream(bytes), status);
    }

    public MediaResponseContext(byte[] bytes, Date lastmodified, int status) {
        this(new ByteArrayInputStream(bytes), lastmodified, status);
    }

    public MediaResponseContext(byte[] bytes, EntityTag etag, int status) {
        this(new ByteArrayInputStream(bytes), etag, status);
    }

    public MediaResponseContext(ReadableByteChannel channel, int status, boolean autoclose) {
      this(Channels.newInputStream(channel), status, autoclose);
    }
    
    public MediaResponseContext(ReadableByteChannel channel, int status) {
        this(channel,status,true);
    }

    public MediaResponseContext(ReadableByteChannel channel, Date lastmodified, int status, boolean autoclose) {
      this(Channels.newInputStream(channel), lastmodified,status,autoclose);
    }
    
    public MediaResponseContext(ReadableByteChannel channel, Date lastmodified, int status) {
        this(channel, lastmodified, status, true);
    }

    public MediaResponseContext(ReadableByteChannel channel, EntityTag etag, int status, boolean autoclose) {
      this(Channels.newInputStream(channel), etag,status,autoclose);
    }
    
    public MediaResponseContext(ReadableByteChannel channel, EntityTag etag, int status) {
        this(channel, etag, status, true);
    }

    public boolean hasEntity() {
        return in != null;
    }

    public void writeTo(OutputStream out) throws IOException {
        if (hasEntity()) {
            if (in != null) {
                byte[] buf = new byte[500];
                int r = -1;
                while ((r = in.read(buf)) != -1)
                    out.write(buf, 0, r);
                if (autoclose)
                  in.close();
            }
        }
    }

    protected void writeEntity(Writer out) throws IOException {
        if (in != null) {
            InputStreamReader rdr = new InputStreamReader(in);
            char[] buf = new char[500];
            int r = -1;
            while ((r = rdr.read(buf)) != -1)
                out.write(buf, 0, r);
            if (autoclose)
              rdr.close();
        }
    }

}
