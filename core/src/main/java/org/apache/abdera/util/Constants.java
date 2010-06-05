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

import javax.xml.namespace.QName;

public interface Constants {

    public static final String CONFIG_PARSER = "org.apache.abdera.parser.Parser";
    public static final String CONFIG_FACTORY = "org.apache.abdera.factory.Factory";
    public static final String CONFIG_XPATH = "org.apache.abdera.xpath.XPath";
    public static final String CONFIG_PARSERFACTORY = "org.apache.abdera.parser.ParserFactory";
    public static final String CONFIG_WRITERFACTORY = "org.apache.abdera.writer.WriterFactory";
    public static final String CONFIG_WRITER = "org.apache.abdera.writer.Writer";
    public static final String CONFIG_STREAMWRITER = "org.apache.abdera.writer.StreamWriter";
    public static final String DEFAULT_PARSER = "org.apache.abdera.parser.stax.FOMParser";
    public static final String DEFAULT_FACTORY = "org.apache.abdera.parser.stax.FOMFactory";
    public static final String DEFAULT_XPATH = "org.apache.abdera.parser.stax.FOMXPath";
    public static final String DEFAULT_PARSERFACTORY = "org.apache.abdera.parser.stax.FOMParserFactory";
    public static final String DEFAULT_WRITERFACTORY = "org.apache.abdera.parser.stax.FOMWriterFactory";
    public static final String DEFAULT_WRITER = "org.apache.abdera.parser.stax.FOMWriter";
    public static final String DEFAULT_STREAMWRITER = "org.apache.abdera.parser.stax.StaxStreamWriter";
    public static final String NAMED_WRITER = "org.apache.abdera.writer.NamedWriter";
    public static final String NAMED_PARSER = "org.apache.abdera.parser.NamedParser";
    public static final String STREAM_WRITER = "org.apache.abdera.writer.StreamWriter";
    public static final String PREFIX = "";
    public static final String APP_PREFIX = "";
    public static final String CONTROL_PREFIX = "";

    public static final String ATOM_MEDIA_TYPE = "application/atom+xml";
    public static final String FEED_MEDIA_TYPE = ATOM_MEDIA_TYPE + ";type=feed";
    public static final String ENTRY_MEDIA_TYPE = ATOM_MEDIA_TYPE + ";type=entry";
    public static final String APP_MEDIA_TYPE = "application/atomsvc+xml";
    public static final String XML_MEDIA_TYPE = "application/xml";
    public static final String CAT_MEDIA_TYPE = "application/atomcat+xml";
    public static final String MULTIPART_RELATED_TYPE = "Multipart/Related";

    public static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    public static final String APP_NS = "http://www.w3.org/2007/app";

    /** @deprecated Use Constants.APP_NS instead **/
    public static final String APP_NS_PRE_RFC = "http://purl.org/atom/app#";

    /** @deprecated **/
    public static final String CONTROL_NS = "http://example.net/appns/";
    public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
    public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";

    public static final String LN_ACCEPT = "accept";
    public static final String LN_CLASS = "class";
    public static final String LN_DIV = "div";
    public static final String LN_FEED = "feed";
    public static final String LN_ENTRY = "entry";
    public static final String LN_SERVICE = "service";
    public static final String LN_AUTHOR = "author";
    public static final String LN_CATEGORY = "category";
    public static final String LN_CONTENT = "content";
    public static final String LN_CONTRIBUTOR = "contributor";
    public static final String LN_GENERATOR = "generator";
    public static final String LN_ICON = "icon";
    public static final String LN_ID = "id";
    public static final String LN_LOGO = "logo";
    public static final String LN_LINK = "link";
    public static final String LN_PUBLISHED = "published";
    public static final String LN_RIGHTS = "rights";
    public static final String LN_SOURCE = "source";
    public static final String LN_SUBTITLE = "subtitle";
    public static final String LN_SUMMARY = "summary";
    public static final String LN_TITLE = "title";
    public static final String LN_UPDATED = "updated";
    public static final String LN_EDITED = "edited";
    public static final String LN_TERM = "term";
    public static final String LN_SCHEME = "scheme";
    public static final String LN_LABEL = "label";
    public static final String LN_HREF = "href";
    public static final String LN_LANG = "lang";
    public static final String LN_BASE = "base";
    public static final String LN_SPACE = "space";
    public static final String LN_URI = "uri";
    public static final String LN_VERSION = "version";
    public static final String LN_REL = "rel";
    public static final String LN_TYPE = "type";
    public static final String LN_HREFLANG = "hreflang";
    public static final String LN_LENGTH = "length";
    public static final String LN_NAME = "name";
    public static final String LN_EMAIL = "email";
    public static final String LN_WORKSPACE = "workspace";
    public static final String LN_SRC = "src";
    public static final String LN_COLLECTION = "collection";
    public static final String LN_CONTROL = "control";
    public static final String LN_DRAFT = "draft";
    public static final String LN_CATEGORIES = "categories";
    public static final String LN_FIXED = "fixed";
    public static final String LN_ALTERNATE = "alternate";
    public static final String LN_ALTERNATE_MULTIPART_RELATED = "multipart-related";

    public static final QName DIV = new QName(XHTML_NS, LN_DIV, "");

    public static final QName CONTROL = new QName(APP_NS, LN_CONTROL, "app");
    public static final QName DRAFT = new QName(APP_NS, LN_DRAFT, "app");
    public static final QName CATEGORIES = new QName(APP_NS, LN_CATEGORIES, APP_PREFIX);
    public static final QName SERVICE = new QName(APP_NS, LN_SERVICE, APP_PREFIX);
    public static final QName EDITED = new QName(APP_NS, LN_EDITED, "app");
    public static final QName ACCEPT = new QName(APP_NS, LN_ACCEPT, APP_PREFIX);
    public static final QName WORKSPACE = new QName(APP_NS, LN_WORKSPACE, APP_PREFIX);
    public static final QName COLLECTION = new QName(APP_NS, LN_COLLECTION, APP_PREFIX);

    /** @deprecated **/
    public static final QName PRE_RFC_CONTROL = new QName(APP_NS_PRE_RFC, LN_CONTROL, "app");
    /** @deprecated **/
    public static final QName PRE_RFC_DRAFT = new QName(APP_NS_PRE_RFC, LN_DRAFT, "app");
    /** @deprecated **/
    public static final QName PRE_RFC_CATEGORIES = new QName(APP_NS_PRE_RFC, LN_CATEGORIES, APP_PREFIX);
    /** @deprecated **/
    public static final QName PRE_RFC_SERVICE = new QName(APP_NS_PRE_RFC, LN_SERVICE, APP_PREFIX);
    /** @deprecated **/
    public static final QName PRE_RFC_EDITED = new QName(APP_NS_PRE_RFC, LN_EDITED, "app");
    /** @deprecated **/
    public static final QName PRE_RFC_ACCEPT = new QName(APP_NS_PRE_RFC, LN_ACCEPT, APP_PREFIX);
    /** @deprecated **/
    public static final QName PRE_RFC_WORKSPACE = new QName(APP_NS_PRE_RFC, LN_WORKSPACE, APP_PREFIX);
    /** @deprecated **/
    public static final QName PRE_RFC_COLLECTION = new QName(APP_NS_PRE_RFC, LN_COLLECTION, APP_PREFIX);

    public static final QName FEED = new QName(ATOM_NS, LN_FEED, PREFIX);
    public static final QName AUTHOR = new QName(ATOM_NS, LN_AUTHOR, PREFIX);
    public static final QName CATEGORY = new QName(ATOM_NS, LN_CATEGORY, PREFIX);
    public static final QName CONTENT = new QName(ATOM_NS, LN_CONTENT, PREFIX);
    public static final QName CONTRIBUTOR = new QName(ATOM_NS, LN_CONTRIBUTOR, PREFIX);
    public static final QName GENERATOR = new QName(ATOM_NS, LN_GENERATOR, PREFIX);
    public static final QName ICON = new QName(ATOM_NS, LN_ICON, PREFIX);
    public static final QName ID = new QName(ATOM_NS, LN_ID, PREFIX);
    public static final QName LOGO = new QName(ATOM_NS, LN_LOGO, PREFIX);
    public static final QName LINK = new QName(ATOM_NS, LN_LINK, PREFIX);
    public static final QName PUBLISHED = new QName(ATOM_NS, LN_PUBLISHED, PREFIX);
    public static final QName RIGHTS = new QName(ATOM_NS, LN_RIGHTS, PREFIX);
    public static final QName SOURCE = new QName(ATOM_NS, LN_SOURCE, PREFIX);
    public static final QName SUBTITLE = new QName(ATOM_NS, LN_SUBTITLE, PREFIX);
    public static final QName SUMMARY = new QName(ATOM_NS, LN_SUMMARY, PREFIX);
    public static final QName TITLE = new QName(ATOM_NS, LN_TITLE, PREFIX);
    public static final QName PREFIXED_TITLE = new QName(ATOM_NS, LN_TITLE, "atom");
    public static final QName UPDATED = new QName(ATOM_NS, LN_UPDATED, PREFIX);
    public static final QName ENTRY = new QName(ATOM_NS, LN_ENTRY, PREFIX);
    public static final QName TERM = new QName(LN_TERM);
    public static final QName SCHEME = new QName(LN_SCHEME);
    public static final QName FIXED = new QName(LN_FIXED);
    public static final QName LABEL = new QName(LN_LABEL);
    public static final QName ATITLE = new QName(LN_TITLE);
    public static final QName HREF = new QName(LN_HREF);
    public static final QName LANG = new QName(XML_NS, LN_LANG, "xml");
    public static final QName BASE = new QName(XML_NS, LN_BASE, "xml");
    public static final QName SPACE = new QName(XML_NS, LN_SPACE, "xml");
    public static final QName AURI = new QName(LN_URI);
    public static final QName VERSION = new QName(LN_VERSION);
    public static final QName REL = new QName(LN_REL);
    public static final QName TYPE = new QName(LN_TYPE);
    public static final QName HREFLANG = new QName(LN_HREFLANG);
    public static final QName LENGTH = new QName(LN_LENGTH);
    public static final QName NAME = new QName(ATOM_NS, LN_NAME, PREFIX);
    public static final QName EMAIL = new QName(ATOM_NS, LN_EMAIL, PREFIX);
    public static final QName URI = new QName(ATOM_NS, LN_URI, PREFIX);
    public static final QName SRC = new QName(LN_SRC);
    public static final QName AID = new QName(LN_ID);
    public static final QName CLASS = new QName(LN_CLASS);
    public static final QName ALTERNATE = new QName(LN_ALTERNATE);

    public static final String TEXT = "text";
    public static final String HTML = "html";
    public static final String XHTML = "xhtml";
    public static final String XML = "xml";
    public static final String YES = "yes";
    public static final String NO = "no";

}
