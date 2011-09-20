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
package org.apache.abdera2.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * RewindableInputStream is a specialization of the PushbackInputStream that maintains an internal buffer of read bytes
 * that a user can rewind (unread) back into the stream without having to do their own buffer management. The rewind
 * buffer grows dynamically
 */
public class RewindableInputStream extends DynamicPushbackInputStream {

    private static final int INITIAL_CAPACITY = 32;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private byte[] buffer;
    private int position;
    private final int scale;
    private final float lf;

    public RewindableInputStream(InputStream in) {
        this(in, INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public RewindableInputStream(InputStream in, int capacity) {
      this(in, capacity, DEFAULT_LOAD_FACTOR);
    }
    
    public RewindableInputStream(InputStream in, int capacity, float lf) {
        super(in);
        grow(capacity);
        this.scale = (int)(capacity * lf);
        this.lf = Math.max(0.5f,Math.min(1.0f, lf));
    }

    public int position() {
        return position;
    }

    private void grow(int capacity) {
        if (buffer == null) {
            buffer = new byte[capacity];
            return;
        } else {
            byte[] buf = new byte[buffer.length + capacity];
            System.arraycopy(buffer, 0, buf, 0, buffer.length);
            buffer = buf;
        }
    }

    private void shrink(int len) {
        if (buffer == null)
            return;
        byte[] buf = new byte[buffer.length - len];
        System.arraycopy(buffer, 0, buf, 0, buf.length);
        position = buffer.length - len;
        buffer = buf;
    }

    public void rewind() throws IOException {
        if (buffer.length == 0)
            return;
        unread(buffer, 0, position);
        shrink(buffer.length);
        if (shouldGrow(position,0))
          grow(scale);
    }

    public void rewind(int offset, int len) throws IOException {
        if (buffer.length == 0)
            return;
        if (offset > buffer.length)
            throw new ArrayIndexOutOfBoundsException(offset);
        unread(buffer, offset, len);
        shrink(len);
        if (shouldGrow(position,0))
          grow(scale);
    }

    public void rewind(int len) throws IOException {
        if (buffer.length == 0)
            return;
        rewind(buffer.length - len, len);
    }

    public int read() throws IOException {
        int i = super.read();
        if (i != -1) {
            if (shouldGrow(position,0))
                grow(scale);
            buffer[position++] = (byte)i;
        }
        return i;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int r = super.read(b, off, len);
        if (r != -1) {
            if (shouldGrow(position,r))
                grow(Math.max(position + r, scale));
            System.arraycopy(b, off, buffer, position, r);
            position = position + r;
        }
        return r;
    }
    
    private boolean shouldGrow(int position, int r) {
      int t = (int)(buffer.length * lf);
      return position + r >= t;
    }

    public long skip(long n) throws IOException {
        return super.skip(n);
    }

}
