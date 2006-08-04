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
package org.apache.abdera.protocol.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The CachingInputStream will automatically write 
 * read bytes out to an OutputStream.  
 */
public class CachingInputStream 
  extends FilterInputStream {

  private OutputStream out;
  private boolean consumeOnClose = true;
  
  public CachingInputStream(
    InputStream in, 
    OutputStream out,
    boolean consumeOnClose) {
      super(in);
      this.out = out;
      this.consumeOnClose = consumeOnClose;
  }

  protected int write(int n) throws IOException {
    if (out != null) out.write(n);
    return n;
  }
  
  protected void write(byte[] b, int off, int len) throws IOException {
    if (out != null) out.write(b, off, len);
  }
  
  protected void close_out() throws IOException {
    if (out != null) out.close();
  }
  
  public void flush() throws IOException {
    if (out != null) out.flush();
  }
  
  @Override
  public int read() throws IOException {
    int n = write(super.read());
    if (n == -1) close();
    return n;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int n = super.read(b, off, len);
    if (n != -1) write(b,0,n);
    if (n == -1) close();
    return n;
  }

  @Override
  public void close() throws IOException {
    if (consumeOnClose) {
      byte[] buf = new byte[1024];
      int n = -1;
      while ((n = read(buf)) != -1) {
        write(buf,0,n);
      }
    }
    flush();
    close_out();
    super.close();
  }
}
