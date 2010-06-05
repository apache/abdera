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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.codec.net.QCodec;

public class Rfc2047Helper {

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static enum Codec {
        B, Q
    };

    public static String encode(String value) {
        return encode(value, DEFAULT_CHARSET, Codec.B);
    }

    public static String encode(String value, String charset) {
        return encode(value, charset, Codec.B);
    }

    /**
     * Used to encode a string as specified by RFC 2047
     * 
     * @param value The string to encode
     * @param charset The character set to use for the encoding
     */
    public static String encode(String value, String charset, Codec codec) {
        if (value == null)
            return null;
        try {
            switch (codec) {
                case Q:
                    return (new QCodec(charset)).encode(value);
                case B:
                default:
                    return (new BCodec(charset)).encode(value);
            }
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * Used to decode a string as specified by RFC 2047
     * 
     * @param value The encoded string
     */
    public static String decode(String value) {
        if (value == null)
            return null;
        try {
            // try BCodec first
            return (new BCodec()).decode(value);
        } catch (DecoderException de) {
            // try QCodec next
            try {
                return (new QCodec()).decode(value);
            } catch (Exception ex) {
                return value;
            }
        } catch (Exception e) {
            return value;
        }
    }

}
