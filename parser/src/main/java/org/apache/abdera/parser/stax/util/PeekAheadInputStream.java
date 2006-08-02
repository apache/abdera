package org.apache.abdera.parser.stax.util;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * A version of PushbackInputStream that provides methods for peeking ahead 
 * in the stream (equivalent to read() followed by an appropriate unread() 
 * and a dynamically (de)allocated pushback buffer.  Whenever the buffer is 
 * consumed completely, it is deallocated.  Whenever bytes are unread, the 
 * buffer is automatically sized up to fit the number of bytes being unread 
 */
public class PeekAheadInputStream 
  extends PushbackInputStream {

  private int origsize = 1;
  
  public PeekAheadInputStream(InputStream in) {
    super(in);
  }
  
  public PeekAheadInputStream(InputStream in, int initialSize) {
    super(in,initialSize);
    this.origsize = initialSize;
  }

  public synchronized int clear() {
    int m = buf.length;
    buf = new byte[origsize];
    pos = origsize;
    return m;
  }
  
  public synchronized int shrink() {
    byte[] old = buf;
    if (pos == 0) return 0; // nothing to do
    int n = old.length - pos;
    int m, p,s,l;
    if (n < origsize) {
      buf = new byte[origsize];
      p = pos;
      s = origsize - n;
      l = old.length-p;
      m = old.length - origsize;
      pos = s;
    } else {
      buf = new byte[n];
      p = pos;
      s = 0;
      l = n;
      m = old.length - l;
      pos = 0;
    }
    System.arraycopy(old, p, buf, s, l);
    return m;
  }
  
  private void resize(int len) {
    byte[] old = buf;
    buf = new byte[old.length + len];
    System.arraycopy(old, 0, buf, len, old.length);
  }

  @Override
  public synchronized void unread(byte[] b, int off, int len) throws IOException {
    if (len > pos && pos + len > buf.length) {
      resize(len-pos);
      pos += len-pos;
    }
    super.unread(b, off, len);
  }

  @Override
  public synchronized void unread(int b) throws IOException {
    if (pos == 0) {
      resize(1);
      pos++;
    }
    super.unread(b);
  }
  
  public synchronized int peek() throws IOException {
    int m = read();
    unread(m);
    return m;
  }
  
  public synchronized int peek(byte[] buf) throws IOException {
    return peek(buf, 0, buf.length);
  }
  
  public synchronized int peek(byte[] buf, int off, int len) throws IOException {
    int r = read(buf, off, len);
    unread(buf,off,len);
    return r;
  }

  @Override
  public synchronized int read() throws IOException {
    int m = super.read();
    if (pos >= buf.length && buf.length > origsize) shrink();
    return m;
  }

  @Override
  public synchronized int read(byte[] b, int off, int len) throws IOException {
    this.available(); // workaround for a problem in PushbackInputStream, without this, the amount of bytes read from some streams will be incorrect
    int r = super.read(b, off, len);
    if (pos >= buf.length && buf.length > origsize) shrink();
    return r;
  }

  @Override
  public synchronized long skip(long n) throws IOException {
    long r = super.skip(n);
    if (pos >= buf.length && buf.length > origsize) shrink();
    return r;
  }
}
