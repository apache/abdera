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

import org.apache.abdera.util.AbstractExtensionFactory;

public class RssExtensionFactory extends AbstractExtensionFactory implements RssConstants {

    public RssExtensionFactory() {
        super("", ENC_NS, DC_NS, RDF_NS);
        addMimeType(QNAME_RSS, RSS_MEDIATYPE);
        addMimeType(QNAME_RDF, RDF_MEDIATYPE);
        addImpl(QNAME_RSS, RssFeed.class);
        addImpl(QNAME_RDF, RssFeed.class);
        addImpl(QNAME_CHANNEL, RssChannel.class);
        addImpl(QNAME_RDF_CHANNEL, RssChannel.class);
        addImpl(QNAME_ITEM, RssItem.class);
        addImpl(QNAME_RDF_ITEM, RssItem.class);
        addImpl(QNAME_LINK, RssLink.class);
        addImpl(QNAME_RDF_LINK, RssLink.class);
        addImpl(QNAME_TITLE, RssText.class);
        addImpl(QNAME_RDF_TITLE, RssText.class);
        addImpl(QNAME_DC_TITLE, RssText.class);
        addImpl(QNAME_DESCRIPTION, RssText.class);
        addImpl(QNAME_RDF_DESCRIPTION, RssText.class);
        addImpl(QNAME_DC_DESCRIPTION, RssText.class);
        addImpl(QNAME_COPYRIGHT, RssText.class);
        addImpl(QNAME_DC_RIGHTS, RssText.class);
        addImpl(QNAME_MANAGINGEDITOR, RssPerson.class);
        addImpl(QNAME_MANAGINGEDITOR2, RssPerson.class);
        addImpl(QNAME_DC_CREATOR, RssPerson.class);
        addImpl(QNAME_DC_CONTRIBUTOR, RssPerson.class);
        addImpl(QNAME_WEBMASTER, RssPerson.class);
        addImpl(QNAME_WEBMASTER2, RssPerson.class);
        addImpl(QNAME_PUBDATE, RssDateTime.class);
        addImpl(QNAME_PUBDATE2, RssDateTime.class);
        addImpl(QNAME_LASTBUILDDATE, RssDateTime.class);
        addImpl(QNAME_LASTBUILDDATE2, RssDateTime.class);
        addImpl(QNAME_DC_DATE, RssDateTime.class);
        addImpl(QNAME_CATEGORY, RssCategory.class);
        addImpl(QNAME_DC_SUBJECT, RssCategory.class);
        addImpl(QNAME_GENERATOR, RssGenerator.class);
        addImpl(QNAME_DOCS, RssLink.class);
        addImpl(QNAME_CLOUD, RssCloud.class);
        addImpl(QNAME_TTL, RssText.class);
        addImpl(QNAME_IMAGE, RssImage.class);
        addImpl(QNAME_RDF_IMAGE, RssImage.class);
        addImpl(QNAME_TEXTINPUT, RssTextInput.class);
        addImpl(QNAME_TEXTINPUT2, RssTextInput.class);
        addImpl(QNAME_SKIPHOURS, RssSkipHours.class);
        addImpl(QNAME_SKIPHOURS2, RssSkipHours.class);
        addImpl(QNAME_SKIPDAYS, RssSkipDays.class);
        addImpl(QNAME_SKIPDAYS2, RssSkipDays.class);
        addImpl(QNAME_URL, RssUriElement.class);
        addImpl(QNAME_RDF_URL, RssUriElement.class);
        addImpl(QNAME_AUTHOR, RssPerson.class);
        addImpl(QNAME_ENCLOSURE, RssEnclosure.class);
        addImpl(QNAME_GUID, RssGuid.class);
        addImpl(QNAME_DC_IDENTIFIER, RssGuid.class);
        addImpl(QNAME_COMMENTS, RssLink.class);
        addImpl(QNAME_SOURCE, RssSource.class);
        addImpl(QNAME_DC_SOURCE, RssSource.class);
        addImpl(QNAME_CONTENT_ENCODED, RssContent.class);
    }

}
