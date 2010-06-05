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
package org.apache.abdera.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.abdera.model.Base;

/**
 * Provides a simple (and likely somewhat inefficient) implementation of javax.xml.transform.Source that allows Abdera
 * objects to be used with the javax.xml.transform API's
 */
public final class AbderaSource extends StreamSource implements Source {

    private final Base base;

    public AbderaSource(Base base) {
        this.base = base;
    }

    @Override
    public InputStream getInputStream() {
        try {
            PipedOutputStream pipeout = new PipedOutputStream();
            PipedInputStream pipein = new PipedInputStream(pipeout);
            base.writeTo(pipeout);
            pipeout.flush();
            pipeout.close();
            return pipein;
        } catch (IOException e) {
            return null;
        }
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
