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
package org.apache.abdera.protocol.server.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.abdera.i18n.io.RewindableInputStream;

/**
 * Utility class that serves as the basis for a variety of Filter implementations
 */
public abstract class AbstractFilter 
  implements Filter {

  private FilterConfig config;
  
  public void destroy() {
  }

  public void init(FilterConfig config) throws ServletException {
    this.config = config;
  }

  protected FilterConfig getConfig() {
    return config;
  }
   
  /**
   * A HttpServletResponseWrapper implementation that applies GZip or Deflate
   * compression to response output.
   */
  public static class CompressingResponseWrapper 
    extends HttpServletResponseWrapper {

    ServletOutputStream out = null;
    PrintWriter output = null;
    
    public CompressingResponseWrapper(
      HttpServletResponse response, 
      String method) 
        throws IOException {
      super(response);
      out = new CompressingServletOutputStream(
        method, response.getOutputStream());
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
      if (output == null)
        output = new PrintWriter(
          new OutputStreamWriter(
            getOutputStream(), 
            this.getCharacterEncoding()));
      return output;
    }
  
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      return out;
    }
    
    public void finish() throws IOException {
      out.flush();
    }
    
    public static boolean canHandle(String enc) {
      return enc.equalsIgnoreCase("gzip") ||
             enc.equalsIgnoreCase("compress");
    }
    
  }
  
  /**
   * A ServletOutputStream implementation that handles the GZip and Deflate
   * compression for the CompressingResponseWrapper
   */
  public static class CompressingServletOutputStream 
    extends ServletOutputStream {

    private DeflaterOutputStream dout;
    
    public CompressingServletOutputStream(String method, ServletOutputStream out) {
      try {
        if ("gzip".equalsIgnoreCase(method)) 
          dout = new GZIPOutputStream(out);
        if ("compress".equalsIgnoreCase(method))
          dout = new DeflaterOutputStream(out);
      } catch (IOException e) {}
    }
    
    public CompressingServletOutputStream(DeflaterOutputStream dout) {
      this.dout = dout;
    }
    
    @Override
    public void write(int b) throws IOException {
      dout.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      dout.write(b,off,len);
    }

    @Override
    public void write(byte[] b) throws IOException {
      dout.write(b);
    }

  }
  
  /**
   * A HttpServletResponseWrapper implementation that buffers the response 
   * content in memory so that a filter can perform operations on the full
   * response content (e.g. digitally sign it, encrypt it, etc)
   */
  public static class BufferingResponseWrapper 
    extends HttpServletResponseWrapper {
  
    PrintWriter output = null;
    ServletOutputStream outStream = null;
  
    public BufferingResponseWrapper(HttpServletResponse response) {
      super(response);
    }
  
    @Override
    public PrintWriter getWriter() throws IOException {
      if (output == null)
        output = new PrintWriter(
          new OutputStreamWriter(
            getOutputStream(), 
            this.getCharacterEncoding()));
      return output;
    }
  
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (outStream == null)
        outStream = new BufferingServletOutputStream();
      return outStream;
    }
  
    public Reader getReader() throws IOException {
      return new InputStreamReader(
        getInputStream(), 
        this.getCharacterEncoding());
    }
  
    public InputStream getInputStream() throws IOException {
      BufferingServletOutputStream out = 
        (BufferingServletOutputStream)getOutputStream();
      return new ByteArrayInputStream(
        out.getBuffer().toByteArray());
    }
  }

  /**
   * Implementation of ServletOutputStream that handles the in-memory 
   * buffering of the response content
   */
  public static class BufferingServletOutputStream 
    extends ServletOutputStream {

    ByteArrayOutputStream out = null;
  
    public BufferingServletOutputStream() {
      this.out = new ByteArrayOutputStream();
    }
    
    public ByteArrayOutputStream getBuffer() {
      return out;
    }
  
    public void write(int b) throws IOException {
      out.write(b);
    }
  
    public void write(byte[] b) throws IOException {
      out.write(b);
    }
  
    public void write(byte[] b, int off, int len) throws IOException {
      out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
      out.close();
      super.close();
    }

    @Override
    public void flush() throws IOException {
      out.flush();
      super.flush();
    }
    
  }

  /**
   * Implementation of HttpServletRequestWrapper that allows a Filter 
   * to perform operations on the full content of a request while still
   * allowing downstream operations to be performed on the content.
   * (e.g. decrypting requests, verifying digital signatures, etc) 
   */
  public static class BufferedRequestWrapper 
    extends HttpServletRequestWrapper {

    private BufferedServletInputStream bin;
    private RewindableInputStream rin;
    private BufferedReader rdr;

    public BufferedRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    public void setInputStream(InputStream in) {
      bin = new BufferedServletInputStream(in);
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
      if (rdr != null) throw new IllegalStateException();
      if (bin == null) {
        rin = new RewindableInputStream(super.getInputStream());
        bin = new BufferedServletInputStream(rin);
      }
      return bin;
    }

    @Override
    public BufferedReader getReader() throws IOException {
      if (rdr == null) {
        String charset = this.getCharacterEncoding();
        rdr = (charset == null) ?
          new BufferedReader(new InputStreamReader(getInputStream())) :
          new BufferedReader(new InputStreamReader(getInputStream(),charset));
      }
      return rdr;
    }
    
    public void reset() throws IOException {
      if (bin != null) rin.rewind();
      rdr = null;
    }
    }
    
    public static class BufferedServletInputStream 
    extends ServletInputStream {
    
    private InputStream in;
    
    public BufferedServletInputStream(InputStream in) {
      this.in = in;
      try {
        in.mark(in.available());
      } catch (Exception e) {}
    }
    
    @Override
    public int read() throws IOException {
      return in.read();
    }

  }

}
