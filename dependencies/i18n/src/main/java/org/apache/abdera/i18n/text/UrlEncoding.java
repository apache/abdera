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
package org.apache.abdera.i18n.text;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Performs URL Percent Encoding
 */
public final class UrlEncoding {

    private static final String DEFAULT_ENCODING = "UTF-8";
    public final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private UrlEncoding() {
    }

    private static void encode(Appendable sb, byte... bytes) {
        encode(sb, 0, bytes.length, bytes);
    }

    private static void encode(Appendable sb, int offset, int length, byte... bytes) {
        try {
            for (int n = offset, i = 0; n < bytes.length && i < length; n++, i++) {
                byte c = bytes[n];
                sb.append("%");
                sb.append(HEX[(c >> 4) & 0x0f]);
                sb.append(HEX[(c >> 0) & 0x0f]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(char... chars) {
        return encode(chars, 0, chars.length, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(char[] chars, Filter Filter) {
        return encode(chars, 0, chars.length, DEFAULT_ENCODING, new Filter[] {Filter});
    }

    public static String encode(char[] chars, Filter... filters) {
        return encode(chars, 0, chars.length, DEFAULT_ENCODING, filters);
    }

    public static String encode(char[] chars, String enc) {
        return encode(chars, 0, chars.length, enc, new Filter[0]);
    }

    public static String encode(char[] chars, String enc, Filter Filter) {
        return encode(chars, 0, chars.length, enc, new Filter[] {Filter});
    }

    public static String encode(char[] chars, String enc, Filter... filters) {
        return encode(chars, 0, chars.length, enc, filters);
    }

    public static String encode(char[] chars, int offset, int length) {
        return encode(chars, offset, length, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(char[] chars, int offset, int length, String enc) {
        return encode(chars, offset, length, enc, new Filter[0]);
    }

    public static String encode(char[] chars, int offset, int length, Filter Filter) {
        return encode(chars, offset, length, DEFAULT_ENCODING, new Filter[] {Filter});
    }

    public static String encode(char[] chars, int offset, int length, Filter... filters) {
        return encode(chars, offset, length, DEFAULT_ENCODING, filters);
    }

    public static String encode(char[] chars, int offset, int length, String enc, Filter Filter) {
        return encode(chars, offset, length, enc, new Filter[] {Filter});
    }

    public static String encode(char[] chars, int offset, int length, String enc, Filter... filters) {
        try {
            return encode((CharSequence)CharBuffer.wrap(chars, offset, length), enc, filters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(InputStream in) throws IOException {
        StringBuilder buf = new StringBuilder();
        byte[] chunk = new byte[1024];
        int r = -1;
        while ((r = in.read(chunk)) > -1)
            encode(buf, 0, r, chunk);
        return buf.toString();
    }

    public static String encode(InputStream in, String charset) throws IOException {
        return encode(in, charset, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(InputStream in, String charset, Filter Filter) throws IOException {
        return encode(in, charset, DEFAULT_ENCODING, new Filter[] {Filter});
    }

    public static String encode(InputStream in, String charset, String enc) throws IOException {
        return encode(in, charset, enc, new Filter[0]);
    }

    public static String encode(InputStream in, String charset, String enc, Filter Filter) throws IOException {
        return encode(in, charset, enc, new Filter[] {Filter});
    }

    public static String encode(InputStream in, String charset, String enc, Filter... filters) throws IOException {
        return encode(new InputStreamReader(in, charset), enc, filters);
    }

    public static String encode(InputStream in, String charset, Filter... filters) throws IOException {
        return encode(new InputStreamReader(in, charset), DEFAULT_ENCODING, filters);
    }

    public static String encode(Reader reader) throws IOException {
        return encode(reader, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(Readable readable) throws IOException {
        return encode(readable, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(Reader reader, String enc) throws IOException {
        return encode(reader, enc, new Filter[0]);
    }

    public static String encode(Readable readable, String enc) throws IOException {
        return encode(readable, enc, new Filter[0]);
    }

    public static String encode(Reader reader, String enc, Filter Filter) throws IOException {
        return encode(reader, enc, new Filter[] {Filter});
    }

    public static String encode(Reader reader, Filter Filter) throws IOException {
        return encode(reader, DEFAULT_ENCODING, new Filter[] {Filter});
    }

    public static String encode(Reader reader, Filter... filters) throws IOException {
        return encode(reader, DEFAULT_ENCODING, filters);
    }

    public static String encode(Readable readable, String enc, Filter Filter) throws IOException {
        return encode(readable, enc, new Filter[] {Filter});
    }

    public static String encode(Readable readable, Filter Filter) throws IOException {
        return encode(readable, DEFAULT_ENCODING, new Filter[] {Filter});
    }

    public static String encode(Readable readable, Filter... filters) throws IOException {
        return encode(readable, DEFAULT_ENCODING, filters);
    }

    private static void processChars(StringBuilder sb, CharBuffer chars, String enc, Filter... filters)
        throws IOException {
        for (int n = 0; n < chars.length(); n++) {
            char c = chars.charAt(n);
            if (!CharUtils.isHighSurrogate(c) && check(c, filters)) {
                encode(sb, String.valueOf(c).getBytes(enc));
            } else if (CharUtils.isHighSurrogate(c)) {
                if (check(c, filters)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append(c);
                    buf.append(chars.charAt(++n));
                    byte[] b = buf.toString().getBytes(enc);
                    encode(sb, b);
                } else {
                    sb.append(c);
                    sb.append(chars.charAt(++n));
                }
            } else {
                sb.append(c);
            }
        }
    }

    public static String encode(Readable readable, String enc, Filter... filters) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer chars = CharBuffer.allocate(1024);
        while (readable.read(chars) > -1) {
            chars.flip();
            processChars(sb, chars, enc, filters);
        }
        return sb.toString();
    }

    public static String encode(Reader reader, String enc, Filter... filters) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] chunk = new char[1024];
        int r = -1;
        while ((r = reader.read(chunk)) > -1)
            processChars(sb, CharBuffer.wrap(chunk, 0, r), enc, filters);
        return sb.toString();
    }

    public static String encode(byte... bytes) {
        StringBuilder buf = new StringBuilder();
        encode(buf, bytes);
        return buf.toString();
    }

    public static String encode(byte[] bytes, int off, int len) {
        StringBuilder buf = new StringBuilder();
        encode(buf, off, len, bytes);
        return buf.toString();
    }

    public static String encode(CharSequence s) {
        return encode(s, Filter.NONOPFILTER);
    }

    public static String encode(CharSequence s, Filter Filter) {
        return encode(s, new Filter[] {Filter});
    }

    public static String encode(CharSequence s, Filter... filters) {
        try {
            if (s == null)
                return null;
            return encode(s, "utf-8", filters);
        } catch (UnsupportedEncodingException e) {
            return null; // shouldn't happen
        }
    }

    public static String encode(CharSequence s, int offset, int length) {
        return encode(s, offset, length, Filter.NONOPFILTER);
    }

    public static String encode(CharSequence s, int offset, int length, Filter Filter) {
        return encode(s, offset, length, new Filter[] {Filter});
    }

    public static String encode(CharSequence s, int offset, int length, Filter... filters) {
        try {
            if (s == null)
                return null;
            return encode(s, offset, length, "utf-8", filters);
        } catch (UnsupportedEncodingException e) {
            return null; // shouldn't happen
        }
    }

    private static boolean check(int codepoint, Filter... filters) {
        for (Filter Filter : filters) {
            if (Filter.accept(codepoint))
                return true;
        }
        return false;
    }

    public static String encode(CharSequence s, int offset, int length, String enc, Filter... filters)
        throws UnsupportedEncodingException {
        int end = Math.min(s.length(), offset + length);
        CharSequence seq = s.subSequence(offset, end);
        return encode(seq, enc, filters);
    }

    public static String encode(CharSequence s, String enc, Filter... filters) throws UnsupportedEncodingException {
        if (s == null)
            return null;
        StringBuilder sb = new StringBuilder();

        for (int n = 0; n < s.length(); n++) {
            char c = s.charAt(n);
            if (!CharUtils.isHighSurrogate(c) && check(c, filters)) {
                encode(sb, String.valueOf(c).getBytes(enc));
            } else if (CharUtils.isHighSurrogate(c)) {
                if (check(c, filters)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append(c);
                    buf.append(s.charAt(++n));
                    byte[] b = buf.toString().getBytes(enc);
                    encode(sb, b);
                } else {
                    sb.append(c);
                    sb.append(s.charAt(++n));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String decode(String e, String enc) throws UnsupportedEncodingException {
        DecodingReader r = new DecodingReader(e.getBytes(enc), enc);
        char[] buf = new char[e.length()];
        try {
            int l = r.read(buf);
            e = new String(buf, 0, l);
        } catch (Exception ex) {
        }
        return e;
    }

    public static String decode(String e) {
        try {
            return decode(e, "utf-8");
        } catch (Exception ex) {
            return e;
        }
    }

    public static class EncodingOutputStream extends FilterOutputStream {

        public EncodingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String enc = encode(b, off, len);
            out.write(enc.getBytes(DEFAULT_ENCODING));
        }

        @Override
        public void write(byte[] b) throws IOException {
            String enc = encode(b);
            out.write(enc.getBytes(DEFAULT_ENCODING));
        }

        @Override
        public void write(int b) throws IOException {
            String enc = encode((byte)b);
            out.write(enc.getBytes(DEFAULT_ENCODING));
        }
    }

    public static class EncodingWriter extends FilterWriter {
        private final Filter[] filters;

        public EncodingWriter(OutputStream out) {
            this(new OutputStreamWriter(out));
        }

        public EncodingWriter(OutputStream out, Filter Filter) {
            this(new OutputStreamWriter(out), Filter);
        }

        public EncodingWriter(OutputStream out, Filter... filters) {
            this(new OutputStreamWriter(out), filters);
        }

        public EncodingWriter(Writer out) {
            this(out, new Filter[0]);
        }

        public EncodingWriter(Writer out, Filter Filter) {
            this(out, new Filter[] {Filter});
        }

        public EncodingWriter(Writer out, Filter... filters) {
            super(out);
            this.filters = filters;
        }

        @Override
        public void write(char[] b, int off, int len) throws IOException {
            String enc = encode(b, off, len, filters);
            out.write(enc.toCharArray());
        }

        @Override
        public void write(char[] b) throws IOException {
            String enc = encode(b, filters);
            out.write(enc.toCharArray());
        }

        @Override
        public void write(int b) throws IOException {
            String enc = encode(new char[] {(char)b}, filters);
            out.write(enc.toCharArray());
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            String enc = encode(str, off, len, filters);
            out.write(enc.toCharArray());
        }
    }

    public static class DecodingInputStream extends FilterInputStream {
        public DecodingInputStream(InputStream in) {
            super(in);
        }

        public DecodingInputStream(byte[] in) {
            super(new ByteArrayInputStream(in));
        }

        public int read() throws IOException {
            int c = super.read();
            if (c == '%') {
                int c1 = super.read();
                int c2 = super.read();
                return decode((char)c1, (char)c2);
            } else {
                return c;
            }
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            int n = off;
            int i = -1;
            while ((i = read()) != -1 && n < off + len) {
                b[n++] = (byte)i;
            }
            return n - off;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public long skip(long n) throws IOException {
            long i = 0;
            for (; i < n; i++)
                read();
            return i;
        }

    }

    public static class DecodingReader extends FilterReader {
        public DecodingReader(byte[] buf) throws UnsupportedEncodingException {
            this(new ByteArrayInputStream(buf));
        }

        public DecodingReader(byte[] buf, String enc) throws UnsupportedEncodingException {
            this(new ByteArrayInputStream(buf), enc);
        }

        public DecodingReader(InputStream in) throws UnsupportedEncodingException {
            this(in, DEFAULT_ENCODING);
        }

        public DecodingReader(InputStream in, String enc) throws UnsupportedEncodingException {
            this(new InputStreamReader(in, enc));
        }

        public DecodingReader(Reader in) {
            super(in);
        }

        public int read() throws IOException {
            int c = super.read();
            if (c == '%') {
                int c1 = super.read();
                int c2 = super.read();
                return decode((char)c1, (char)c2);
            } else {
                return c;
            }
        }

        @Override
        public synchronized int read(char[] b, int off, int len) throws IOException {
            int n = off;
            int i = -1;
            while ((i = read()) != -1 && n < off + len) {
                b[n++] = (char)i;
            }
            return n - off;
        }

        @Override
        public int read(char[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public long skip(long n) throws IOException {
            long i = 0;
            for (; i < n; i++)
                read();
            return i;
        }
    }

    private static byte decode(char c, int shift) {
        return (byte)((((c >= '0' && c <= '9') ? c - '0' : (c >= 'A' && c <= 'F') ? c - 'A' + 10
            : (c >= 'a' && c <= 'f') ? c - 'a' + 10 : -1) & 0xf) << shift);
    }

    private static byte decode(char c1, char c2) {
        return (byte)(decode(c1, 4) | decode(c2, 0));
    }

}
