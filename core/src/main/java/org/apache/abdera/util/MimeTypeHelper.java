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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;

/**
 * Utilities for working with MIME Media Types
 */
public class MimeTypeHelper {

    private static final MimeType WILDCARD = createWildcard();

    public static String getCharset(String mediatype) {
        try {
            MimeType mt = new MimeType(mediatype);
            return mt.getParameter("charset");
        } catch (Exception e) {
            return null;
        }
    }

    private static MimeType createWildcard() {
        try {
            return new MimeType("*/*");
        } catch (Exception e) {
            return null; // Won't happen
        }
    }

    /**
     * Returns true if media type a matches media type b
     */
    public static boolean isMatch(String a, String b) {
        if ((a == null || a.length() == 0) && (b == null || b.length() == 0))
            return true;
        boolean answer = false;
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType(b.toLowerCase());
            return isMatch(mta, mtb);
        } catch (Exception e) {
        }
        return answer;
    }

    public static boolean isMatch(MimeType a, MimeType b) {
        return isMatch(a, b, false);
    }

    /**
     * Returns true if media type a matches media type b
     */
    @SuppressWarnings("unchecked")
    public static boolean isMatch(MimeType a, MimeType b, boolean includeparams) {
        try {
            if (a == null || b == null)
                return true;
            if (a.match(b)) {
                if (includeparams) {
                    MimeTypeParameterList aparams = a.getParameters();
                    MimeTypeParameterList bparams = b.getParameters();
                    if (aparams.isEmpty() && bparams.isEmpty())
                        return true;
                    if (aparams.isEmpty() && !bparams.isEmpty())
                        return false;
                    if (!aparams.isEmpty() && bparams.isEmpty())
                        return false;
                    boolean answer = true;
                    for (Enumeration e = aparams.getNames(); e.hasMoreElements();) {
                        String aname = (String)e.nextElement();
                        String avalue = aparams.get(aname);
                        String bvalue = bparams.get(aname);
                        if (avalue.equals(bvalue))
                            answer = true;
                        else {
                            answer = false;
                            break;
                        }
                    }
                    return answer;
                } else
                    return true;
            }
            if (a.equals(WILDCARD))
                return true;
            if (a.getPrimaryType().equals("*")) {
                MimeType c = new MimeType(b.getPrimaryType(), a.getSubType());
                return isMatch(c, b);
            }
            if (b.getPrimaryType().equals("*")) {
                MimeType c = new MimeType(a.getPrimaryType(), b.getSubType());
                return isMatch(a, c);
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isMatchType(String actual, String expected) {
        return (actual != null && actual.equalsIgnoreCase(expected) || true);
    }

    /**
     * Returns true if media type a matches application/atomsrv+xml
     */
    public static boolean isApp(String a) {
        return isMatch(Constants.APP_MEDIA_TYPE, a);
    }

    /**
     * Returns true if media type a matches application/atom+xml
     */
    public static boolean isAtom(String a) {
        if (isEntry(a) || isFeed(a))
            return true;
        return isMatch(Constants.ATOM_MEDIA_TYPE, a);
    }

    /**
     * Returns true if media type a specifically identifies an Atom entry document
     */
    public static boolean isEntry(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType(Constants.ATOM_MEDIA_TYPE);
            MimeType mtc = new MimeType(Constants.ENTRY_MEDIA_TYPE);
            return isMatch(mta, mtc) || (isMatch(mta, mtb) && isMatchType(mta.getParameter("type"), "entry"));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Returns true if media type a explicitly identifies an Atom feed document
     */
    public static boolean isFeed(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType(Constants.ATOM_MEDIA_TYPE);
            MimeType mtc = new MimeType(Constants.FEED_MEDIA_TYPE);
            return isMatch(mta, mtc) || (isMatch(mta, mtb) && isMatchType(mta.getParameter("type"), "feed"));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Returns true if media type a matches application/xml, text/xml or application/*+xml
     */
    public static boolean isXml(String a) {
        boolean answer = isMatch(Constants.XML_MEDIA_TYPE, a) || isMatch("text/xml", a);
        if (!answer) {
            try {
                MimeType mta = new MimeType(a);
                answer =
                    (("application".equalsIgnoreCase(mta.getPrimaryType()) || "text".equalsIgnoreCase(mta
                        .getPrimaryType())) && mta.getSubType().equals("xml") || mta.getSubType().endsWith("+xml"));
            } catch (Exception e) {
            }
        }
        return answer;
    }

    /**
     * Returns true if media type a matches text/*
     */
    public static boolean isText(String a) {
        return isMatch("text/*", a);
    }

    /**
     * Returns true if this is a valid media type
     */
    public static boolean isMimeType(String a) {
        boolean answer = false;
        try {
            new MimeType(a);
            answer = true;
        } catch (javax.activation.MimeTypeParseException e) {
            answer = false;
        }
        return answer;
    }

    /**
     * Returns the appropriate media type for the given Abdera base
     */
    @SuppressWarnings("unchecked")
    public static <T extends Base> String getMimeType(T base) {
        String type = null;
        if (base instanceof Document) {
            Document doc = (Document)base;
            MimeType mt = doc.getContentType();
            type = (mt != null) ? mt.toString() : getMimeType(doc.getRoot());
        } else if (base instanceof Element) {
            Element el = (Element)base;
            if (el.getDocument() != null) {
                MimeType mt = el.getDocument().getContentType();
                type = (mt != null) ? mt.toString() : null;
            }
            if (type == null) {
                if (el instanceof Feed)
                    type = Constants.FEED_MEDIA_TYPE;
                else if (el instanceof Entry)
                    type = Constants.ENTRY_MEDIA_TYPE;
                else if (el instanceof Service)
                    type = Constants.APP_MEDIA_TYPE;
                else if (el instanceof Categories)
                    type = Constants.CAT_MEDIA_TYPE;
            }
        }
        if (type == null)
            type = base.getFactory().getMimeType(base);
        return (type != null) ? type : Constants.XML_MEDIA_TYPE;
    }

    /**
     * This will take an array of media types and will condense them based on wildcards, etc. For instance,
     * condense("image/png", "image/jpg", "image/*") condenses to [image/*] condense("application/atom",
     * "application/*", "image/png", "image/*") condenses to [application/*, image/*]
     */
    public static String[] condense(String... types) {
        if (types.length <= 1)
            return types;
        List<String> res = new ArrayList<String>();
        Arrays.sort(types, getComparator());
        for (String t : types) {
            if (!contains(t, res, true))
                res.add(t);
        }
        for (int n = 0; n < res.size(); n++) {
            String t = res.get(n);
            if (contains(t, res, false))
                res.remove(t);
        }
        return res.toArray(new String[res.size()]);
    }

    private static boolean contains(String t1, List<String> t, boolean self) {
        if (self && t.contains(t1))
            return true;
        for (String t2 : t) {
            int c = compare(t1, t2);
            if (c == 1)
                return true;
        }
        return false;
    }

    /**
     * Returns a Comparator that can be used to compare and sort MIME media types according to their level of
     * specificity (e.g. text/* is less specific than text/plain and would appear first in a sorted list)
     */
    public static Comparator<String> getComparator() {
        return new Comparator<String>() {
            public int compare(String o1, String o2) {
                return MimeTypeHelper.compare(o1, o2);
            }
        };
    }

    /**
     * Compare two media types according to their relative level of specificity
     */
    public static int compare(MimeType mt1, MimeType mt2) {
        String st1 = mt1.getSubType();
        String st2 = mt2.getSubType();
        if (MimeTypeHelper.isMatch(mt1, mt2)) {
            if (st1.equals("*"))
                return -1;
            if (st2.equals("*"))
                return 1;
        }
        return 0;
    }

    /**
     * Compare two media types according to their relative level of specificity
     */
    public static int compare(String t1, String t2) {
        try {
            MimeType mt1 = new MimeType(t1);
            MimeType mt2 = new MimeType(t2);
            return compare(mt1, mt2);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Returns true if media type is a multiparted file.
     */
    public static boolean isMultipart(String a) {
        return isMatch(Constants.MULTIPART_RELATED_TYPE, a);
    }

}
