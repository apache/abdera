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
package org.apache.abdera.i18n.text.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.abdera.i18n.text.Filter;

/**
 * A reader implementation that filters out unwanted characters By default, unwanted characters are simply removed from
 * the stream. Alternatively, a replacement character can be provided so long as it is acceptable to the specified
 * filter
 */
public class FilteredCharReader extends FilterReader {

    /**
     * The XMLVersion determines which set of restrictions to apply depending on the XML version being parsed
     */
    private final Filter filter;
    private final char replacement;

    public FilteredCharReader(InputStream in, Filter filter) {
        this(new InputStreamReader(in), filter);
    }

    public FilteredCharReader(InputStream in, String charset, Filter filter) throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), filter);
    }

    public FilteredCharReader(InputStream in, Filter filter, char replacement) {
        this(new InputStreamReader(in), filter, replacement);
    }

    public FilteredCharReader(InputStream in, String charset, Filter filter, char replacement)
        throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), filter, replacement);
    }

    public FilteredCharReader(Reader in) {
        this(in, new NonOpFilter(), (char)0);
    }

    public FilteredCharReader(Reader in, Filter filter) {
        this(in, filter, (char)0);
    }

    public FilteredCharReader(Reader in, char replacement) {
        this(in, new NonOpFilter(), replacement);
    }

    public FilteredCharReader(Reader in, Filter filter, char replacement) {
        super(in);
        this.filter = filter;
        this.replacement = replacement;
        if (replacement != 0 && ((!Character.isValidCodePoint(replacement)) || !filter.accept(replacement)))
            throw new IllegalArgumentException();
    }

    @Override
    public int read() throws IOException {
        int c = -1;
        if (replacement == 0) {
            while (((c = super.read()) != -1 && !filter.accept(c))) {
            }
        } else {
            c = super.read();
            if (c != -1 && !filter.accept(c))
                c = replacement;
        }
        return c;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int n = off;
        for (; n < Math.min(len, cbuf.length - off); n++) {
            int r = read();
            if (r != -1)
                cbuf[n] = (char)r;
            else
                break;
        }
        n -= off;
        return n <= 0 ? -1 : n;
    }

    public static Filter getUnacceptableFilter(int... unacceptable) {
        return new CharArrayFilter(unacceptable);
    }

    private static class NonOpFilter implements Filter {
        public boolean accept(int c) {
            return true;
        }
    }

    private static class CharArrayFilter implements Filter {
        private final int[] chars;

        public CharArrayFilter(int[] chars) {
            this.chars = chars;
            Arrays.sort(this.chars);
        }

        public boolean accept(int c) {
            return Arrays.binarySearch(chars, c) < 0;
        }
    }
}
