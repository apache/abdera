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
package org.apache.abdera.security.util.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.Filter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.security.AbderaSecurity;

public abstract class SecurityFilter 
  implements Filter {

  protected final Abdera abdera;
  protected final AbderaSecurity security;
  
  protected SecurityFilter() {
    this.abdera = new Abdera();
    this.security = new AbderaSecurity(abdera);
  }
  
  public void destroy() {
  }

  protected Document<Element> getDocument(BufferingResponseWrapper wrapper) {
    Reader rdr = wrapper.getReader();
    InputStream in = wrapper.getInputStream();
    Parser parser = abdera.getParser();
    try {
      if (rdr != null) {
        return parser.parse(rdr);
      }
      if (in != null) {
        return parser.parse(in);
      }
    } catch (Exception e) {}
    return null;
  }
 
  public static class BufferingResponseWrapper 
    extends HttpServletResponseWrapper {
    
    CharArrayWriter output = null;
    ByteArrayOutputStream outStream = null;
    
    BufferingResponseWrapper(HttpServletResponse response) {
      super(response);
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
      if (outStream != null) throw new IllegalStateException();
      if (output == null) output = new CharArrayWriter();
      return new PrintWriter(output);
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (output != null) throw new IllegalStateException();
      if (outStream == null) outStream = new ByteArrayOutputStream();
      return new BufferingServletOutputStream(outStream);
    }
    
    public Reader getReader() {
      if (output == null) return null;
      return new CharArrayReader(output.toCharArray());
    }
    
    public InputStream getInputStream() {
      if (outStream == null) return null;
      return new ByteArrayInputStream(outStream.toByteArray());
    }
  }
  
  public static class BufferingServletOutputStream 
    extends ServletOutputStream {
  
    ByteArrayOutputStream out = null;
    
    BufferingServletOutputStream(ByteArrayOutputStream out) {
      this.out = out;
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

}
