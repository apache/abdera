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
package org.apache.abdera.protocol.error;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.writer.StreamWriter;

/**
 * Abdera protocol error element. The Abdera error document provides a simple structure for reporting errors back to
 * Abdera clients.
 */
public class Error extends ExtensibleElementWrapper {

    public static final String NS = "http://abdera.apache.org";
    public static final QName ERROR = new QName(NS, "error");
    public static final QName CODE = new QName(NS, "code");
    public static final QName MESSAGE = new QName(NS, "message");

    public Error(Element internal) {
        super(internal);
    }

    public Error(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * The code should typically match the HTTP status code; however, certain application scenarios may require the use
     * of a different code
     */
    public int getCode() {
        String code = getSimpleExtension(CODE);
        return code != null ? Integer.parseInt(code) : -1;
    }

    /**
     * The code should typically match the HTTP status code; however, certain application scenarios may require the use
     * of a different code
     */
    public Error setCode(int code) {
        if (code > -1) {
            Element element = getExtension(CODE);
            if (element != null) {
                element.setText(Integer.toString(code));
            } else {
                addSimpleExtension(CODE, Integer.toString(code));
            }
        } else {
            Element element = getExtension(CODE);
            if (element != null)
                element.discard();
        }
        return this;
    }

    /**
     * Human-readable, language-sensitive description of the error
     */
    public String getMessage() {
        return getSimpleExtension(MESSAGE);
    }

    /**
     * Human-readable, language-sensitive description of the error
     */
    public Error setMessage(String message) {
        if (message != null) {
            Element element = getExtension(MESSAGE);
            if (element != null) {
                element.setText(message);
            } else {
                addSimpleExtension(MESSAGE, message);
            }
        } else {
            Element element = getExtension(MESSAGE);
            if (element != null)
                element.discard();
        }
        return this;
    }

    /**
     * Will throw a ProtocolException that wraps this element. This is useful on the client side to surface error
     * responses
     */
    public void throwException() {
        throw new ProtocolException(this);
    }

    /**
     * Create a new Error object
     */
    public static Error create(Abdera abdera, int code, String message) {
        return create(abdera, code, message, null);
    }

    public static Error create(Abdera abdera, int code, String message, Throwable t) {
        Document<Error> doc = abdera.getFactory().newDocument();
        Error error = abdera.getFactory().newElement(ERROR, doc);
        error.setCode(code).setMessage(message);
        return error;
    }

    public static void create(StreamWriter sw, int code, String message, Throwable t) {
        sw.startDocument().startElement(ERROR).startElement(CODE).writeElementText(code).endElement()
            .startElement(MESSAGE).writeElementText(message).endElement().endElement().endDocument();
    }
}
