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
package org.apache.abdera.ext.media;

import javax.xml.namespace.QName;

public interface MediaConstants {

    public enum Type {
        PLAIN, HTML
    }

    public enum Algo {
        SHA1, MD5
    }

    public enum Relationship {
        ALLOW, DENY
    }

    public enum RestrictionType {
        COUNTRY, URI
    }

    public enum Medium {
        IMAGE, AUDIO, VIDEO, DOCUMENT, EXECUTABLE
    }

    public enum Expression {
        FULL, SAMPLE, NONSTOP
    }

    public static final String MEDIA_NS = "http://search.yahoo.com/mrss/";
    public static final String MEDIA_PREFIX = "media";
    public static final String LN_GROUP = "group";
    public static final String LN_CONTENT = "content";
    public static final String LN_ADULT = "adult";
    public static final String LN_RATING = "rating";
    public static final String LN_TITLE = "title";
    public static final String LN_DESCRIPTION = "description";
    public static final String LN_KEYWORDS = "keywords";
    public static final String LN_THUMBNAIL = "thumbnail";
    public static final String LN_CATEGORY = "category";
    public static final String LN_HASH = "hash";
    public static final String LN_PLAYER = "player";
    public static final String LN_CREDIT = "credit";
    public static final String LN_COPYRIGHT = "copyright";
    public static final String LN_TEXT = "text";
    public static final String LN_RESTRICTION = "restriction";

    public static final QName GROUP = new QName(MEDIA_NS, LN_GROUP, MEDIA_PREFIX);
    public static final QName CONTENT = new QName(MEDIA_NS, LN_CONTENT, MEDIA_PREFIX);
    public static final QName ADULT = new QName(MEDIA_NS, LN_ADULT, MEDIA_PREFIX);
    public static final QName RATING = new QName(MEDIA_NS, LN_RATING, MEDIA_PREFIX);
    public static final QName TITLE = new QName(MEDIA_NS, LN_TITLE, MEDIA_PREFIX);
    public static final QName DESCRIPTION = new QName(MEDIA_NS, LN_DESCRIPTION, MEDIA_PREFIX);
    public static final QName KEYWORDS = new QName(MEDIA_NS, LN_KEYWORDS, MEDIA_PREFIX);
    public static final QName THUMBNAIL = new QName(MEDIA_NS, LN_THUMBNAIL, MEDIA_PREFIX);
    public static final QName CATEGORY = new QName(MEDIA_NS, LN_CATEGORY, MEDIA_PREFIX);
    public static final QName HASH = new QName(MEDIA_NS, LN_HASH, MEDIA_PREFIX);
    public static final QName PLAYER = new QName(MEDIA_NS, LN_PLAYER, MEDIA_PREFIX);
    public static final QName CREDIT = new QName(MEDIA_NS, LN_CREDIT, MEDIA_PREFIX);
    public static final QName COPYRIGHT = new QName(MEDIA_NS, LN_COPYRIGHT, MEDIA_PREFIX);
    public static final QName TEXT = new QName(MEDIA_NS, LN_TEXT, MEDIA_PREFIX);
    public static final QName RESTRICTION = new QName(MEDIA_NS, LN_RESTRICTION, MEDIA_PREFIX);

}
