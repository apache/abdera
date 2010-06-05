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

public class Sanitizer {

    public static final String SANITIZE_PATTERN = "[^A-Za-z0-9\\%!$&\\\\'()*+,;=_]+";

    public static String sanitize(String slug) {
        return sanitize(slug, null, false, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler) {
        return sanitize(slug, filler, false, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler, boolean lower) {
        return sanitize(slug, filler, lower, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler, String pattern) {
        return sanitize(slug, filler, false, null, pattern);
    }

    public static String sanitize(String slug, String filler, boolean lower, String pattern) {
        return sanitize(slug, filler, lower, null, pattern);
    }

    public static String sanitize(String slug, String filler, boolean lower, Normalizer.Form form) {
        return sanitize(slug, filler, lower, form, SANITIZE_PATTERN);
    }

    /**
     * Used to sanitize a string. Optionally performs Unicode Form KD normalization on a string to break extended
     * characters down, then replaces non alphanumeric characters with a specified filler replacement.
     * 
     * @param slug The source string
     * @param filler The replacement string
     * @param lower True if the result should be lowercase
     * @param form Unicode Normalization form to use (or null)
     */
    public static String sanitize(String slug, String filler, boolean lower, Normalizer.Form form, String pattern) {
        if (slug == null)
            return null;
        if (lower)
            slug = slug.toLowerCase();
        if (form != null) {
            try {
                slug = Normalizer.normalize(slug, form);
            } catch (Exception e) {
            }
        }
        slug = slug.replaceAll("\\s+", "_");
        if (filler != null) {
            slug = slug.replaceAll(pattern, filler);
        } else {
            slug = UrlEncoding.encode(slug, PathNoDelimFilter);
        }
        return slug;
    }

    private static final Filter PathNoDelimFilter = new Filter() {
        public boolean accept(int c) {
            return !(CharUtils.isAlphaDigit(c) || c == '-'
                || c == '.'
                || c == '_'
                || c == '~'
                || c == '&'
                || c == '='
                || c == '+'
                || c == '$'
                || c == ','
                || c == ';' || c == '%');
        }
    };
}
