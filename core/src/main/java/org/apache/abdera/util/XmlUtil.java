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

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Filter;

public class XmlUtil {

    public enum XMLVersion {
        XML10, XML11;
        private final Filter filter;

        XMLVersion() {
            this.filter = new XmlFilter(this);
        }

        public Filter filter() {
            return filter;
        }
    };

    private static class XmlFilter implements Filter {
        private final XMLVersion version;

        XmlFilter(XMLVersion version) {
            this.version = version;
        }

        public boolean accept(int c) {
            return !restricted(version, c);
        }
    }

    // inversion set
    private static int[] RESTRICTED_SET_v1 = {0, 9, 11, 13, 14, 32, 55296, 57344, 65534, 65536};

    // inversion set
    private static int[] RESTRICTED_SET_v11 = {11, 13, 14, 32, 127, 160, 55296, 57344, 65534, 65536};

    public static boolean restricted(XMLVersion version, char c) {
        return restricted(version, (int)c);
    }

    public static boolean restricted(XMLVersion version, int c) {
        return CharUtils.invset_contains(version == XMLVersion.XML10 ? RESTRICTED_SET_v1 : RESTRICTED_SET_v11, c);
    }

    public static XMLVersion getVersion(String version) {
        return version == null ? XMLVersion.XML10 : version.equals("1.0") ? XMLVersion.XML10 : version.equals("1.1")
            ? XMLVersion.XML11 : XMLVersion.XML10;
    }
}
