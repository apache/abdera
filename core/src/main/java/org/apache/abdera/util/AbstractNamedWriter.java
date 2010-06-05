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

import org.apache.abdera.writer.NamedWriter;

/**
 * Base abstract implementation of NamedWriter
 */
public abstract class AbstractNamedWriter extends AbstractWriter implements NamedWriter {

    protected final String name;
    protected final String[] formats;

    protected AbstractNamedWriter(String name, String... formats) {
        this.name = name;
        this.formats = formats;
    }

    public String getName() {
        return name;
    }

    public String[] getOutputFormats() {
        return formats;
    }

    public boolean outputsFormat(String mediatype) {
        for (String format : formats) {
            if (MimeTypeHelper.isMatch(format, mediatype))
                return true;
        }
        return false;
    }
}
