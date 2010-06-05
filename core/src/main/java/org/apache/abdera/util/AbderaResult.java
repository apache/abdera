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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/**
 * Provides a simple (and likely somewhat inefficient) implementation of javax.xml.transform.Result that allows Abdera
 * objects to be used with the javax.xml.transform API's Only use this once per transform!!!
 */
public final class AbderaResult extends StreamResult implements Result {

    private final Abdera abdera;
    private PipedOutputStream pipeout;
    private PipedInputStream pipein;
    private Document<?> doc;

    public AbderaResult() {
        this(new Abdera());
    }

    public AbderaResult(Abdera abdera) {
        this.abdera = abdera;
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> Document<T> getDocument() {
        if (doc == null) {
            if (pipein == null)
                return null;
            doc = abdera.getParser().parse(pipein);
        }
        return (Document<T>)doc;
    }

    @Override
    public OutputStream getOutputStream() {
        if (pipein == null && pipeout == null) {
            try {
                pipeout = new PipedOutputStream();
                pipein = new PipedInputStream(pipeout);
            } catch (IOException e) {
            }
        }
        return pipeout;
    }

    @Override
    public Writer getWriter() {
        return new OutputStreamWriter(getOutputStream());
    }

    @Override
    public void setOutputStream(OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWriter(Writer out) {
        throw new UnsupportedOperationException();
    }

}
