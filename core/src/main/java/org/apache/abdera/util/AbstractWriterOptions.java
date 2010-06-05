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

import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;
import org.apache.abdera.writer.WriterOptions;

public abstract class AbstractWriterOptions implements WriterOptions {

    protected String charset = "UTF-8";
    protected CompressionCodec[] codecs = null;
    protected boolean autoclose = false;

    public Object clone() throws CloneNotSupportedException {
        AbstractWriterOptions copy = (AbstractWriterOptions)super.clone();
        return copy;
    }

    public CompressionCodec[] getCompressionCodecs() {
        return codecs;
    }

    public WriterOptions setCompressionCodecs(CompressionCodec... codecs) {
        this.codecs = codecs;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public WriterOptions setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public boolean getAutoClose() {
        return autoclose;
    }

    public WriterOptions setAutoClose(boolean autoclose) {
        this.autoclose = autoclose;
        return this;
    }

}
