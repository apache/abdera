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
package org.apache.abdera.protocol.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.rfc4646.Range;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

public final class LocalizationHelper {

    private LocalizationHelper() {
    }

    public static Link[] selectAlternate(Source source) {
        return selectAlternate(source, Locale.getDefault());
    }

    public static Link[] selectAlternate(Entry entry) {
        return selectAlternate(entry, Locale.getDefault());
    }

    public static Link[] selectAlternate(Source source, Locale locale) {
        return selectAlternate(source, new Range(Lang.fromLocale(locale), true));
    }

    public static Link[] selectAlternate(Entry entry, Locale locale) {
        return selectAlternate(entry, new Range(Lang.fromLocale(locale), true));
    }

    public static Link[] selectAlternate(Entry entry, Locale... locales) {
        Range[] ranges = new Range[locales.length];
        for (int n = 0; n < locales.length; n++)
            ranges[n] = new Range(Lang.fromLocale(locales[n]), true);
        return selectAlternate(entry, ranges);
    }

    public static Link[] selectAlternate(Entry entry, Range range) {
        return selectAlternate(entry, new Range[] {range});
    }

    public static Link[] selectAlternate(Entry entry, Range... ranges) {
        return selectAlternate(entry.getLinks("alternate"), ranges);
    }

    public static Link[] selectAlternate(Entry entry, String range) {
        return selectAlternate(entry, new String[] {range});
    }

    public static Link[] selectAlternate(Entry entry, String... ranges) {
        Range[] r = new Range[ranges.length];
        for (int n = 0; n < ranges.length; n++)
            r[n] = new Range(ranges[n], true);
        return selectAlternate(entry, r);
    }

    public static Link[] selectAlternate(Source source, Locale... locales) {
        Range[] ranges = new Range[locales.length];
        for (int n = 0; n < locales.length; n++)
            ranges[n] = new Range(Lang.fromLocale(locales[n]), true);
        return selectAlternate(source, ranges);
    }

    public static Link[] selectAlternate(Source source, Range range) {
        return selectAlternate(source, new Range[] {range});
    }

    public static Link[] selectAlternate(Source source, Range... ranges) {
        return selectAlternate(source.getLinks("alternate"), ranges);
    }

    public static Link[] selectAlternate(Source source, String range) {
        return selectAlternate(source, new String[] {range});
    }

    public static Link[] selectAlternate(Source source, String... ranges) {
        Range[] r = new Range[ranges.length];
        for (int n = 0; n < ranges.length; n++)
            r[n] = new Range(ranges[n], true);
        return selectAlternate(source, r);
    }

    public static Link[] selectAlternate(List<Link> links, String range) {
        return selectAlternate(links, new Range(range, true));
    }

    public static Link[] selectAlternate(List<Link> links, Range range) {
        return selectAlternate(links, new Range[] {range});
    }

    public static Link[] selectAlternate(List<Link> links, Range... ranges) {
        List<Link> matching = new ArrayList<Link>();
        for (Range range : ranges) {
            for (Link link : links) {
                String hreflang = link.getHrefLang();
                if (hreflang != null) {
                    Lang lang = new Lang(hreflang);
                    Range basic = range.toBasicRange();
                    Lang blang = !basic.toString().equals("*") ? new Lang(basic.toString()) : null;
                    if (range.matches(lang) || (blang != null && lang.isParentOf(blang)))
                        matching.add(link);
                }
            }
            Collections.sort(matching, new Comparator<Link>() {
                public int compare(Link o1, Link o2) {
                    Lang l1 = new Lang(o1.getHrefLang());
                    Lang l2 = new Lang(o2.getHrefLang());
                    return l1.compareTo(l2);
                }
            });
        }
        return matching.toArray(new Link[matching.size()]);
    }

}
