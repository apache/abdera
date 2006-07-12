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
package org.apache.abdera.parser.stax;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 * Will attempt to autodetect the character encoding from the stream
 * This will preserve the BOM if it exists
 */
public class SniffingInputStream 
  extends FilterInputStream {

  private String encoding = null;
  private boolean bomset = false;
  
  public SniffingInputStream(InputStream in) {
    super(new BufferedInputStream(in));
    try {
      encoding = detectEncoding();
    } catch (IOException e) {}
  }

  public boolean isBomSet() {
    return bomset;
  }
  
  public String getEncoding() {
    return encoding;
  }
  
  private String detectEncoding() throws IOException {
    BufferedInputStream pin = (BufferedInputStream) this.in;
    byte[] bom = new byte[4];
    pin.mark(pin.available());
    pin.read(bom);
    pin.reset();  
    String charset = null;
    if (bom[0] == 0x00 && bom[1] == 0x00 && bom[2] == 0xFFFFFFFE && bom[3] == 0xFFFFFFFF) {
      bomset = true;
      return "utf-32be";
    } else if (bom[0] == 0xFFFFFFFF && bom[1] == 0xFFFFFFFE && bom[2] == 0x00 && bom[3] == 0x00) {
      bomset = true;
      return "utf-32le";
    } else if ((bom[0] == 0xFFFFFFFE && bom[1] == 0xFFFFFFFF && bom[2] == 0x00 && bom[3] == 0x00) ||
               (bom[0] == 0x00 && bom[1] == 0x00 && bom[2] == 0xFFFFFFFF && bom[3] == 0xFFFFFFFE)) {
      bomset = true;
      return null;
    } else if (bom[0] == 0xFFFFFFFE && bom[1] == 0xFFFFFFFF) {
      bomset = true;
      return "utf-16be";
    } else if (bom[0] == 0xFFFFFFFF && bom[1] == 0xFFFFFFFE) {
      bomset = true;
      return "utf-16le";
    } else if (bom[0] == 0xFFFFFFEF && bom[1] == 0xFFFFFFBB && bom[2] == 0xFFFFFFBF)  {
      bomset = true;
      return "utf-8";
    } else if (bom[0] == 0x00 && bom[1] == 0x00 && bom[2] == 0x00 && bom[3] == 0x3C) {
      charset = "utf-32be";
    } else if (bom[0] == 0x3C && bom[1] == 0x00 && bom[2] == 0x00 && bom[3] == 0x00) {
      charset = "utf-32le";
    } else if (bom[0] == 0x00 && bom[1] == 0x3C && bom[2] == 0x00 && bom[3] == 0x3F) {
      charset = "utf-16be";
    } else if (bom[0] == 0x3C && bom[1] == 0x00 && bom[2] == 0x3F && bom[3] == 0x00) {
      charset = "utf-16le";
    } else if (bom[0] == 0x4C && bom[1] == 0x6F && bom[2] == 0xA7 && bom[3] == 0x94) {
      charset = "edbdic";
    } 
    bomset = false;
    try {
      XMLStreamReader xmlreader = 
        XMLInputFactory.newInstance().createXMLStreamReader(pin);
      String cs = xmlreader.getCharacterEncodingScheme();
      if (cs != null) charset = cs;
    } catch (Exception e) {
    } finally {
      try {
        pin.reset();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return charset;
  }
  
}
