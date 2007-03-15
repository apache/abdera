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
package org.apache.abdera.g14n.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.abdera.g14n.io.ByteArrayCodepointIterator;
import org.apache.abdera.g14n.io.CodepointIterator;

/**
 * Implements a buffer that provides a slightly more efficient way of writing,
 * and then reading a stream of bytes.  
 * 
 * To use:
 * 
 *   ReadWriteByteChannel rw = new ReadWriteByteChannel();
 *   OutputStream out = rw.getOutputStream();
 *   out.write(bytes);
 *   out.close();
 * 
 *   InputStream in = rw.getInputStream();
 *   int i = -1;
 *   while ((i = in.read()) != -1) {...}
 * 
 * By default, closing the OutputStream will automatically cause it to
 * flip over to Read mode, locking the buffer from further writes and 
 * setting the read position to 0.
 * 
 * Once the Buffer has been fully read, it must be reset, which sets it 
 * back into write mode and moves the position pointer back to 0;
 * 
 */
public class ReadWriteByteChannel 
  implements ReadableByteChannel,
             WritableByteChannel,
             Cloneable, 
             Serializable {

  private static final long serialVersionUID = 5984202999779004084L;
  private static final int INITIAL_CAPACITY = 32;
  protected int position = 0;
  protected int scale = INITIAL_CAPACITY;
  protected boolean closed = false;
  protected byte[] buffer = null;
  protected boolean flipped = false;
  protected boolean flipOnClose = true;
  
  public ReadWriteByteChannel() {
    this(INITIAL_CAPACITY);
  }
  
  public ReadWriteByteChannel(int capacity) {
    this(capacity, true);
  }
  
  public ReadWriteByteChannel(int capacity, boolean flipOnClose) {
    grow(capacity);
    this.scale = capacity;
    this.flipOnClose = flipOnClose;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public InputStream getInputStream() {
    if (!flipped) notflipped();
    return Channels.newInputStream(this);
  }
  
  public OutputStream getOutputStream() {
    if (flipped) alreadyflipped();
    return Channels.newOutputStream(this);
  }
  
  public Writer getWriter(String charset) {
    if (flipped) alreadyflipped();
    return Channels.newWriter(this, charset);
  }
  
  public Reader getReader(String charset) {
    if (!flipped) notflipped();
    return Channels.newReader(this, charset);
  }
  
  public byte[] getBuffer() {
    if (!flipped) notflipped(); 
    return buffer;
  }
  
  public CodepointIterator getIterator() {
    if (!flipped) notflipped();
    return new ByteArrayCodepointIterator(buffer);
  }
  
  public CodepointIterator getIterator(String charset) {
    if (!flipped) notflipped();
    return new ByteArrayCodepointIterator(buffer,charset);
  }
  
  private void grow(int capacity)  {
    if (buffer == null) {
      buffer = new byte[capacity];
      return;
    } else {
      byte[] buf =  new byte[buffer.length + capacity];
      System.arraycopy(buffer, 0, buf, 0, buffer.length);
      buffer = buf;
    }
  }

  private void compact() {
    if (buffer != null) {
      byte[] buf = new byte[position];
      System.arraycopy(buffer,0,buf,0,position);
      buffer = buf;
    }
  }
  
  public synchronized int read(ByteBuffer dst) throws IOException {
    if (!flipped) notflipped();
    if (dst.hasRemaining() && position < buffer.length) {
      int r = Math.min(dst.remaining(), buffer.length - position);
      dst.put(buffer, position, r);
      dst.flip();
      position += r;
      return r;
    }
    return -1;
  }

  public synchronized void flip() {
    if (flipped) alreadyflipped();
    compact();
    position = 0;
    flipped = true;
  }
  
  public synchronized void rewind() {
    position = 0;
  }
  
  public synchronized void reset() {
    position = 0;
    compact();   // clear the buffer
    grow(scale); // grow the buffer
    flipped = false;
  }
  
  public void close() throws IOException {
    if (flipOnClose && !flipped) flip();
  }
  
  public boolean isOpen() {
    return true;
  }

  public synchronized int write(ByteBuffer src) throws IOException {
    if (flipped) alreadyflipped();
    if (src.hasRemaining()) {
      int r = Math.min(src.remaining(), buffer.length - position);
      src.get(buffer, position, r);
      position += r;
      return r;
    }
    return -1;
  }

  private void alreadyflipped() {
    throw new IllegalStateException("The buffer has already been flipped");
  }
  
  private void notflipped() {
    throw new IllegalStateException("The buffer has not yet been flipped");
  }
  
  public void transferTo(WritableByteChannel channel) throws IOException {
    if (!flipped) notflipped();
    ByteBuffer buf = ByteBuffer.wrap(buffer);
    while(buf.hasRemaining()) channel.write(buf);
    buf.clear();
  }
}
