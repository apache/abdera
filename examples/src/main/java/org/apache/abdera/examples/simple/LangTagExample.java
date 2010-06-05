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
package org.apache.abdera.examples.simple;

import java.util.Locale;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.rfc4646.Range;
import org.apache.abdera.i18n.rfc4646.Subtag;

/**
 * Example using the Lang tag implementation
 */
@SuppressWarnings("unused")
public class LangTagExample {

    public static void main(String... args) throws Exception {

        // English, written in Latin script, as spoken in California
        Lang lang = new Lang("en-Latn-US-calif");

        // Iterate over the tags
        for (Subtag tag : lang)
            System.out.println(tag.getType() + "\t" + tag.getName());

        // Access individual tags
        String language = lang.getLanguage().getName();
        String script = lang.getScript().getName();
        String region = lang.getRegion().getName();
        String variant = lang.getVariant().getName();

        // Perform extended range matching
        Range range = new Range("en-US-*", true);
        System.out.println(range.matches(lang, true));

        // Locale integration
        Locale locale = lang.getLocale();
        System.out.println(locale);
    }

}
