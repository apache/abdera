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
package org.apache.abdera.ext.rss;

import javax.xml.namespace.QName;

public interface RssConstants {

    public static final String RSS_MEDIATYPE = "application/rss+xml";
    public static final String RDF_MEDIATYPE = "application/rdf+xml";

    public static final String ENC_NS = "http://purl.org/rss/1.0/modules/content/";
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RSS1_NS = "http://purl.org/rss/1.0/";
    public static final String DC_NS = "http://purl.org/dc/elements/1.1/";

    public static final QName QNAME_RDF = new QName(RDF_NS, "RDF");
    public static final QName QNAME_RDF_CHANNEL = new QName(RSS1_NS, "channel");
    public static final QName QNAME_RDF_TITLE = new QName(RSS1_NS, "title");
    public static final QName QNAME_RDF_LINK = new QName(RSS1_NS, "link");
    public static final QName QNAME_RDF_DESCRIPTION = new QName(RSS1_NS, "description");
    public static final QName QNAME_RDF_ITEM = new QName(RSS1_NS, "item");
    public static final QName QNAME_RDF_IMAGE = new QName(RSS1_NS, "image");
    public static final QName QNAME_RDF_SEQ = new QName(RDF_NS, "Seq");
    public static final QName QNAME_RDF_LI = new QName(RDF_NS, "li");
    public static final QName QNAME_RDF_RESOURCE = new QName(RDF_NS, "resource");
    public static final QName QNAME_RDF_ABOUT = new QName(RDF_NS, "about");
    public static final QName QNAME_RDF_URL = new QName(RSS1_NS, "url");
    public static final QName QNAME_RDF_ITEMS = new QName(RSS1_NS, "items");

    public static final QName QNAME_RSS = new QName("rss");
    public static final QName QNAME_CHANNEL = new QName("channel");
    public static final QName QNAME_ITEM = new QName("item");
    public static final QName QNAME_LINK = new QName("link");
    public static final QName QNAME_TITLE = new QName("title");
    public static final QName QNAME_DESCRIPTION = new QName("description");
    public static final QName QNAME_LANGUAGE = new QName("language");
    public static final QName QNAME_COPYRIGHT = new QName("copyright");
    public static final QName QNAME_MANAGINGEDITOR = new QName("managingEditor");
    public static final QName QNAME_MANAGINGEDITOR2 = new QName("managingeditor");
    public static final QName QNAME_WEBMASTER = new QName("webMaster");
    public static final QName QNAME_WEBMASTER2 = new QName("webmaster");
    public static final QName QNAME_PUBDATE = new QName("pubDate");
    public static final QName QNAME_PUBDATE2 = new QName("pubdate");
    public static final QName QNAME_LASTBUILDDATE = new QName("lastBuildDate");
    public static final QName QNAME_LASTBUILDDATE2 = new QName("lastbuilddate");
    public static final QName QNAME_CATEGORY = new QName("category");
    public static final QName QNAME_GENERATOR = new QName("generator");
    public static final QName QNAME_DOCS = new QName("docs");
    public static final QName QNAME_CLOUD = new QName("cloud");
    public static final QName QNAME_TTL = new QName("ttl");
    public static final QName QNAME_IMAGE = new QName("image");
    public static final QName QNAME_RATING = new QName("rating");
    public static final QName QNAME_TEXTINPUT = new QName("textInput");
    public static final QName QNAME_TEXTINPUT2 = new QName("textinput");
    public static final QName QNAME_SKIPHOURS = new QName("skipHours");
    public static final QName QNAME_SKIPHOURS2 = new QName("skiphours");
    public static final QName QNAME_SKIPDAYS = new QName("skipDays");
    public static final QName QNAME_SKIPDAYS2 = new QName("skipdays");
    public static final QName QNAME_AUTHOR = new QName("author");
    public static final QName QNAME_ENCLOSURE = new QName("enclosure");
    public static final QName QNAME_GUID = new QName("guid");
    public static final QName QNAME_COMMENTS = new QName("comments");
    public static final QName QNAME_SOURCE = new QName("source");

    public static final QName QNAME_URL = new QName("url");
    public static final QName QNAME_WIDTH = new QName("width");
    public static final QName QNAME_HEIGHT = new QName("height");
    public static final QName QNAME_DAY = new QName("day");
    public static final QName QNAME_HOUR = new QName("hour");
    public static final QName QNAME_NAME = new QName("name");

    public static final QName QNAME_CONTENT_ENCODED = new QName(ENC_NS, "encoded");

    public static final QName QNAME_DC_TITLE = new QName(DC_NS, "title");
    public static final QName QNAME_DC_CREATOR = new QName(DC_NS, "creator");
    public static final QName QNAME_DC_SUBJECT = new QName(DC_NS, "subject");
    public static final QName QNAME_DC_DESCRIPTION = new QName(DC_NS, "description");
    public static final QName QNAME_DC_PUBLISHER = new QName(DC_NS, "publisher");
    public static final QName QNAME_DC_CONTRIBUTOR = new QName(DC_NS, "contributor");
    public static final QName QNAME_DC_DATE = new QName(DC_NS, "date");
    public static final QName QNAME_DC_TYPE = new QName(DC_NS, "type");
    public static final QName QNAME_DC_FORMAT = new QName(DC_NS, "format");
    public static final QName QNAME_DC_IDENTIFIER = new QName(DC_NS, "identifier");
    public static final QName QNAME_DC_SOURCE = new QName(DC_NS, "source");
    public static final QName QNAME_DC_LANGUAGE = new QName(DC_NS, "language");
    public static final QName QNAME_DC_RELATION = new QName(DC_NS, "relation");
    public static final QName QNAME_DC_COVERAGE = new QName(DC_NS, "covrerage");
    public static final QName QNAME_DC_RIGHTS = new QName(DC_NS, "rights");

}
