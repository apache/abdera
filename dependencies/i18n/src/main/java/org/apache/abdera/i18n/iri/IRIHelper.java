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
package org.apache.abdera.i18n.iri;

import java.util.regex.Pattern;

/**
 * Utility methods for working with URI's / IRI's
 */
public class IRIHelper {

    private static final Pattern javascript =
        Pattern.compile("\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern mailto =
        Pattern.compile("\\s*m\\s*a\\s*i\\s*l\\s*t\\s*o\\s*:.*", Pattern.CASE_INSENSITIVE);

    public static boolean isJavascriptUri(IRI uri) {
        if (uri == null)
            return false;
        return javascript.matcher(uri.toString()).matches();
    }

    public static boolean isMailtoUri(IRI uri) {
        if (uri == null)
            return false;
        return mailto.matcher(uri.toString()).matches();
    }

}
