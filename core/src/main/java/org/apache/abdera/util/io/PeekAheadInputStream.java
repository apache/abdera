package org.apache.abdera.util.io;
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

/**
 * A version of RewindableInputStream that provides methods for peeking ahead 
 * in the stream (equivalent to read() followed by an appropriate unread() 
 */
public class PeekAheadInputStream 
  extends RewindableInputStream {
  
  public PeekAheadInputStream(InputStream in) {
    super(in);
  }
  
  public PeekAheadInputStream(InputStream in, int initialSize) {
    super(in,initialSize);
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

}
