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
package org.apache.abdera2.extra;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.abdera2.model.Base;

/**
 * Provides a simple (and likely somewhat inefficient) implementation of javax.xml.transform.Source that allows Abdera
 * objects to be used with the javax.xml.transform API's
 */
public final class AbderaSource 
  extends StreamSource 
  implements Source {

    private final byte[] buffer;

    public AbderaSource(Base base) {
        this.buffer = read(base);
    }
    
    private byte[] read(Base base) {
      byte[] data = null;
      try {
          ByteArrayOutputStream out = 
            new ByteArrayOutputStream();
          base.writeTo(out);
          data = out.toByteArray();
      } catch (IOException e) {
      }
      return data;
    }

    @Override
    public InputStream getInputStream() {
        // JIRA: https://issues.apache.org/jira/browse/ABDERA-235
        return new ByteArrayInputStream(buffer);
    }

    @Override
    public Reader getReader() {
        return new InputStreamReader(getInputStream());
    }

    @Override
    public void setInputStream(InputStream in) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReader(Reader reader) {
        throw new UnsupportedOperationException();
    }

}
