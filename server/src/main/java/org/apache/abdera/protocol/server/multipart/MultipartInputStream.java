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
package org.apache.abdera.protocol.server.multipart;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * InputStream that reads a given inputStream and skips the boundary tokens.
 */
public class MultipartInputStream extends FilterInputStream {

    private InputStream input;
    private byte[] boundary;
    private byte[] storedBuffer;
    private int storedBufferPosition;
    private boolean fakeEof;
    private boolean realEof;
    private boolean bufferEnd;
    private int[] lastTable = new int[256];

    public MultipartInputStream(InputStream input, byte[] boundary) {
        super(input);
        this.input = input;
        this.boundary = boundary;
        computeLastTable();
    }

    public void skipBoundary() throws IOException {
        byte[] buffer = new byte[256];
        while (read(buffer) != -1) {
        }
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        if (read(b) == -1) {
            return -1;
        }
        return b[0] & 0xff;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length == 0) {
            return 0;
        }

        int bytesReaded = -1;
        if (!checkEndOfFile()) {
            int bufferLength =
                Math.max(boundary.length * 3/* number of tokens into the stream */, length + boundary.length);

            byte[] newBuffer = new byte[bufferLength];

            int position = cleanStoredBuffer(newBuffer, 0, bufferLength);
            if (bufferLength >= position) {
                position += readBuffer(newBuffer, position, bufferLength - position);
            }

            if (realEof && position == 0) {
                return -1;
            }

            bytesReaded = getBytesReaded(buffer, newBuffer, offset, position, length);
        }
        return bytesReaded != 0 ? bytesReaded : -1;
    }

    private int readBuffer(byte[] buffer, int offset, int length) throws IOException {
        int count = 0;
        int read = 0;

        do {
            read = input.read(buffer, offset + count, length - count);
            if (read > 0) {
                count += read;
            }
        } while (read > 0 && count < length);

        if (read < 0) {
            realEof = true;
        }

        return count;
    }

    private boolean checkEndOfFile() {
        if (fakeEof) {
            fakeEof = false;
            return true;
        }

        if (realEof && storedBuffer == null) {
            return true;
        }
        if (realEof && !bufferEnd) {
            bufferEnd = true;
        }

        return false;
    }

    private int getBytesReaded(byte[] buffer, byte[] newBuffer, int offSet, int position, int length) {
        int boundaryPosition = locateBoundary(newBuffer, boundary.length - 1, position);
        int bytesReaded;

        if (length < boundaryPosition || boundaryPosition == -1) {// boundary not found
            bytesReaded = Math.min(length, position);
            createStoredBuffer(newBuffer, bytesReaded, position);
        } else {
            bytesReaded = boundaryPosition;
            createStoredBuffer(newBuffer, bytesReaded + boundary.length, position);

            if (bytesReaded == 0) {
                return -1;
            }

            fakeEof = true;
        }

        System.arraycopy(newBuffer, 0, buffer, offSet, bytesReaded);
        return bytesReaded;
    }

    private void createStoredBuffer(byte[] buffer, int start, int end) {
        int length = end - start;

        if (length > 0) {
            if (bufferEnd && storedBuffer != null) {
                storedBufferPosition -= length;
            } else {
                int bufferLength = (storedBuffer == null ? 0 : storedBuffer.length - storedBufferPosition);
                byte[] newBuffer = new byte[length + bufferLength];
                System.arraycopy(buffer, start, newBuffer, 0, length);

                if (storedBuffer != null) {
                    System.arraycopy(storedBuffer, storedBufferPosition, newBuffer, length, bufferLength);
                }

                storedBuffer = newBuffer;
                storedBufferPosition = 0;
            }
        }
    }

    private int cleanStoredBuffer(byte[] buffer, int offset, int length) {
        int i = 0;

        if (storedBuffer != null) {
            for (i = 0; i < length && storedBufferPosition < storedBuffer.length; i++) {
                buffer[offset + i] = storedBuffer[storedBufferPosition++];
            }

            if (storedBufferPosition >= storedBuffer.length) {
                storedBuffer = null;
                storedBufferPosition = 0;
            }
        }

        return i;
    }

    /* computation of the last table */
    private void computeLastTable() {
        Arrays.fill(lastTable, boundary.length);
        for (int i = 0; i < boundary.length - 1; i++) {
            lastTable[boundary[i] & 0xff] = boundary.length - i - 1;
        }
    }

    /* simplified boyer-moore algorithm */
    private int locateBoundary(byte[] bytes, int start, int end) {
        int position = -1;
        if (end > boundary.length) {
            int j = 0;
            int k = 0;
            for (int i = start; i < end; i += lastTable[bytes[i] & 0xff]) {
                for (k = i, j = boundary.length - 1; j >= 0 && boundary[j] == bytes[k]; j--) {
                    k--;
                }
                if (j == -1) {
                    position = k + 1;
                }
            }
        }
        return position;
    }

}
