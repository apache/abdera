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
package org.apache.abdera.contrib.rss;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Element;

public class RssExtensionFactory 
  implements ExtensionFactory, RssConstants {

  @SuppressWarnings("unchecked")
  public <T extends Element> T getElementWrapper(Element internal) {
    
    QName qname = internal.getQName();
    if (qname.equals(QNAME_RSS)) {
      if (internal.getAttributeValue("version") != null) {
        return (T)(new RssFeed(internal));
      }
    } if (qname.equals(QNAME_RDF)) {
      return (T)(new RssFeed(internal));
    } else if (qname.equals(QNAME_CHANNEL) || qname.equals(QNAME_RDF_CHANNEL)) {
      return (T)(new RssChannel(internal)); 
    } else if (qname.equals(QNAME_ITEM) || qname.equals(QNAME_RDF_ITEM)) {
      return (T)(new RssItem(internal));
    } else if (qname.equals(QNAME_LINK) || qname.equals(QNAME_RDF_LINK)) {
      return (T)(new RssLink(internal));
    } else if (qname.equals(QNAME_TITLE) || qname.equals(QNAME_RDF_TITLE)) {
      return (T)(new RssText(internal));
    } else if (qname.equals(QNAME_DESCRIPTION) || qname.equals(QNAME_RDF_DESCRIPTION)) {
      return (T)(new RssText(internal));
    } else if (qname.equals(QNAME_COPYRIGHT)) {
      return (T)(new RssText(internal));
    } else if (qname.equals(QNAME_MANAGINGEDITOR) || qname.equals(QNAME_MANAGINGEDITOR2)) {
      return (T)(new RssPerson(internal));
    } else if (qname.equals(QNAME_WEBMASTER) || qname.equals(QNAME_WEBMASTER2)) {
      return (T)(new RssPerson(internal));
    } else if (qname.equals(QNAME_PUBDATE) || qname.equals(QNAME_PUBDATE2) ||
               qname.equals(QNAME_LASTBUILDDATE) || qname.equals(QNAME_LASTBUILDDATE2)) {
      return (T)(new RssDateTime(internal));
    } else if (qname.equals(QNAME_CATEGORY)) {
      return (T)(new RssCategory(internal));
    } else if (qname.equals(QNAME_GENERATOR)) {
      return (T)(new RssGenerator(internal));
    } else if (qname.equals(QNAME_DOCS)) {
      return (T)(new RssLink(internal));
    } else if (qname.equals(QNAME_CLOUD)) {
      return (T)(new RssCloud(internal));
    } else if (qname.equals(QNAME_TTL)) {
      return (T)(new RssText(internal));
    } else if (qname.equals(QNAME_IMAGE) || qname.equals(QNAME_RDF_IMAGE)) {
      return (T)(new RssImage(internal));
    } else if (qname.equals(QNAME_RATING)) {
      return (T) internal;
    } else if (qname.equals(QNAME_TEXTINPUT) || qname.equals(QNAME_TEXTINPUT2)) {
      return (T)(new RssTextInput(internal));
    } else if (qname.equals(QNAME_SKIPHOURS) || qname.equals(QNAME_SKIPHOURS2)) {
      return (T)(new RssSkipHours(internal));
    } else if (qname.equals(QNAME_SKIPDAYS) || qname.equals(QNAME_SKIPDAYS2)) {
      return (T)(new RssSkipDays(internal));
    } else if (qname.equals(QNAME_URL) || qname.equals(QNAME_RDF_URL)) {
      return (T)(new RssUriElement(internal));
    } else if (qname.equals(QNAME_AUTHOR)) {
      return (T)(new RssPerson(internal));
    } else if (qname.equals(QNAME_ENCLOSURE)) {
      return (T)(new RssEnclosure(internal));
    } else if (qname.equals(QNAME_GUID)) {
      return (T)(new RssGuid(internal));
    } else if (qname.equals(QNAME_COMMENTS)) {
      return (T)(new RssLink(internal));
    } else if (qname.equals(QNAME_SOURCE)) {
      return (T)(new RssSource(internal));
    } else if (qname.equals(QNAME_CONTENT_ENCODED)) {
      return (T)(new RssContent(internal));
    }
    
    return (T) internal;
  }

  public List<String> getNamespaces() {
    List<String> namespaces = new ArrayList<String>();
    namespaces.add("");
    namespaces.add(ENC_NS);
    return namespaces;
  }

  public boolean handlesNamespace(String namespace) {
    return (namespace.equals("") || namespace.equals(ENC_NS));
  }

}
