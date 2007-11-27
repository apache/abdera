package org.apache.abdera.i18n.io;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.apache.abdera.i18n.io.CharArrayCodepointIterator;

/**
 * Iterate over Unicode codepoints in a CharSequence (e.g. String, StringBuffer, etc)
 */
public class ReadableByteChannelCodepointIterator 
  extends CharArrayCodepointIterator {
  
  public ReadableByteChannelCodepointIterator(
    ReadableByteChannel channel) {
      this(channel,Charset.defaultCharset());
  }
  
  public ReadableByteChannelCodepointIterator(
    ReadableByteChannel channel, 
    String charset) {
      this(channel,Charset.forName(charset));
  }
  
  public ReadableByteChannelCodepointIterator(
    ReadableByteChannel channel, 
    Charset charset) {
      try {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel outc = Channels.newChannel(out);
        while(channel.read(buf) > 0) {
          buf.flip();
          outc.write(buf);
        }
        CharBuffer cb = charset.decode(ByteBuffer.wrap(out.toByteArray()));
        buffer = cb.array();
        position = cb.position();
        limit = cb.limit();
      } catch (Exception e) {}
  }
    
}
