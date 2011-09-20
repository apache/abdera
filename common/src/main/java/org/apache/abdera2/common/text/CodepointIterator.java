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
package org.apache.abdera2.common.text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;


import com.ibm.icu.text.UCharacterIterator;

public abstract class CodepointIterator 
  implements Iterator<Integer> {
  
  /**
   * Get a CodepointIterator for the specified char array
   */
  public static CodepointIterator getInstance(char[] array) {
      return new CharArrayCodepointIterator(array);
  }

  /**
   * Get a CodepointIterator for the specified CharSequence
   */
  public static CodepointIterator getInstance(CharSequence seq) {
      return new CharSequenceCodepointIterator(seq);
  }

  /**
   * Get a CodepointIterator for the specified byte array, using the default charset
   */
  public static CodepointIterator getInstance(byte[] array) {
      return new ByteArrayCodepointIterator(array);
  }

  /**
   * Get a CodepointIterator for the specified byte array, using the specified charset
   */
  public static CodepointIterator getInstance(byte[] array, String charset) {
      return new ByteArrayCodepointIterator(array, charset);
  }

  /**
   * Get a CodepointIterator for the specified CharBuffer
   */
  public static CodepointIterator getInstance(CharBuffer buffer) {
      return new CharBufferCodepointIterator(buffer);
  }

  /**
   * Get a CodepointIterator for the specified ReadableByteChannel
   */
  public static CodepointIterator getInstance(ReadableByteChannel channel) {
      return new ReadableByteChannelCodepointIterator(channel);
  }

  /**
   * Get a CodepointIterator for the specified ReadableByteChannel
   */
  public static CodepointIterator getInstance(ReadableByteChannel channel, String charset) {
      return new ReadableByteChannelCodepointIterator(channel, charset);
  }

  /**
   * Get a CodepointIterator for the specified InputStream
   */
  public static CodepointIterator getInstance(InputStream in) {
      return new ReadableByteChannelCodepointIterator(Channels.newChannel(in));
  }

  /**
   * Get a CodepointIterator for the specified InputStream using the specified charset
   */
  public static CodepointIterator getInstance(InputStream in, String charset) {
      return new ReadableByteChannelCodepointIterator(Channels.newChannel(in), charset);
  }

  /**
   * Get a CodepointIterator for the specified Reader
   */
  public static CodepointIterator getInstance(Reader in) {
      return new ReaderCodepointIterator(in);
  }

  /**
   * Get the next codepoint
   */
  protected abstract int get();


  /**
   * True if there are codepoints remaining
   */
  public abstract boolean hasNext();

  /**
   * Return the next codepoint
   */
  public Integer next() throws InvalidCharacterException {
      return get();
  }

  public void remove() {
      throw new UnsupportedOperationException();
  }


  static class ByteArrayCodepointIterator extends CharArrayCodepointIterator {
      public ByteArrayCodepointIterator(byte[] bytes) {
          this(bytes, Charset.defaultCharset());
      }

      public ByteArrayCodepointIterator(byte[] bytes, String charset) {
          this(bytes, Charset.forName(charset));
      }

      public ByteArrayCodepointIterator(byte[] bytes, Charset charset) {
          super(charset.decode(ByteBuffer.wrap(bytes)).array());
      }
  }

  static class CharArrayCodepointIterator extends CodepointIterator {      
      protected final UCharacterIterator i;
      protected int current;
      public CharArrayCodepointIterator(char[] buffer) {
          this.i = UCharacterIterator.getInstance(buffer);
          current = i.next();
      }
      public CharArrayCodepointIterator(char[] buffer, int n, int e) {
          this.i = UCharacterIterator.getInstance(buffer, n, e);
          current = i.next();
      }
      protected int get() {
        if (current != UCharacterIterator.DONE) {
          int v = current;
          current = i.nextCodePoint();
          return v;
        } else throw new NoSuchElementException();
      }
      public boolean hasNext() {
        return current != UCharacterIterator.DONE;
      }

  }

  static class CharBufferCodepointIterator extends CharArrayCodepointIterator {
      public CharBufferCodepointIterator(CharBuffer cb) {
          super(cb.array());
      }
  }

  static class CharSequenceCodepointIterator extends CharArrayCodepointIterator {
      public CharSequenceCodepointIterator(CharSequence buffer) {
          this(buffer, 0, buffer.length());
      }
      public CharSequenceCodepointIterator(CharSequence buffer, int n, int e) {
          super(toCharArray(buffer,n,e));
      }
      private static char[] toCharArray(CharSequence seq, int n, int e) {
        char[] ret = new char[seq.length()];
        for (int i = n; i < e; i++)
          ret[i] = seq.charAt(i);
        return ret;
      }
  }

  static class ReadableByteChannelCodepointIterator extends CharArrayCodepointIterator {
      public ReadableByteChannelCodepointIterator(ReadableByteChannel channel) {
          this(channel, Charset.defaultCharset());
      }
      public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, String charset) {
          this(channel, Charset.forName(charset));
      }
      public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, Charset charset) {
          super(toCharArray(channel, charset));
      }
      private static char[] toCharArray(ReadableByteChannel channel, Charset charset) {
        try {
          ByteBuffer buf = ByteBuffer.allocate(1024);
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          WritableByteChannel outc = Channels.newChannel(out);
          while (channel.read(buf) > 0) {
              buf.flip();
              outc.write(buf);
          }
          CharBuffer cb = charset.decode(ByteBuffer.wrap(out.toByteArray()));
          return cb.array();
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
  }

  static class ReaderCodepointIterator extends CharArrayCodepointIterator {
      public ReaderCodepointIterator(Reader reader) {
          super(toCharArray(reader));
      }
      private static char[] toCharArray(Reader reader) {
        try {
          StringBuilder sb = new StringBuilder();
          char[] buf = new char[1024];
          int n = -1;
          while ((n = reader.read(buf)) > -1) {
              sb.append(buf, 0, n);
          }
          char[] ret = new char[sb.length()];
          sb.getChars(0, sb.length(), ret, 0);
          return ret;
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
  }

}
