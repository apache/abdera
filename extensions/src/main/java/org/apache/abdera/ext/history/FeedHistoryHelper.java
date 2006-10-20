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
package org.apache.abdera.ext.history;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.iri.IRISyntaxException;

/**
 * Initial support for Mark Nottingham's Feed History draft 
 * (http://ietfreport.isoc.org/all-ids/draft-nottingham-atompub-feed-history-07.txt)
 */
public class FeedHistoryHelper {

  public static final String FHNS = "http://purl.org/syndication/history/1.0";
  public static final QName COMPLETE = new QName(FHNS, "complete");
  public static final QName ARCHIVE = new QName(FHNS, "archive");
  
  public static boolean isComplete(Feed feed) {
    return (feed.getExtension(COMPLETE) != null);
  }
  
  public static void setComplete(Feed feed, boolean complete) {
    if (complete) {
      if (!isComplete(feed)) feed.addExtension(COMPLETE);
    } else {
      if (isComplete(feed)) {
        Element ext = feed.getExtension(COMPLETE);
        ext.discard(); 
      }
    }
  }
  
  public static void setArchive(Feed feed, boolean archive) {
    if (archive) {
      if (!isArchive(feed)) feed.addExtension(ARCHIVE);
    } else {
      if (isArchive(feed)) {
        Element ext = feed.getExtension(ARCHIVE);
        ext.discard();
      }
    }
  }

  public static boolean isArchive(Feed feed) {
    return feed.getExtension(ARCHIVE) != null;
  }
  
  public static boolean isPaged(Feed feed) {
    return feed.getLink("next") != null ||
           feed.getLink("previous") != null ||
           feed.getLink("first") != null ||
           feed.getLink("last") != null;
  }
  
  public static Link setNext(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("next");
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "next");
    }
    return link;
  }
  
  public static Link setPrevious(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("previous");
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "previous");
    }
    return link;
  }
  
  public static Link setFirst(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("first");
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "first");
    }
    return link;
  }
  
  public static Link setLast(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("last");
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "last");
    }
    return link;
  }
  
  public static Link setNextArchive(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("next-archive");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "next-archive");
    }
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "next-archive");
    }
    return link;
  }
  
  public static Link setPreviousArchive(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("prev-archive");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "prev-archive");
    }
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "prev-archive");
    }
    return link;
  }
  
  public static Link setCurrent(Feed feed, String iri) throws IRISyntaxException {
    Link link = feed.getLink("current");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "current");
    }
    if (link != null) {
      link.setHref(iri);
    } else {
      link = feed.addLink(iri, "current");
    }
    return link;
  }
  
  public static IRI getNext(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("next");
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getPrevious(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("previous");
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getFirst(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("first");
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getLast(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("last");
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getPreviousArchive(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("prev-archive");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "prev-archive");
    }
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getNextArchive(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("next-archive");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "next-archive");
    }
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public static IRI getCurrent(Feed feed) throws IRISyntaxException {
    Link link = feed.getLink("current");
    if (link == null) { // try the full IANA URI version
      link = feed.getLink(Link.IANA_BASE + "current");
    }
    return (link != null) ? link.getResolvedHref() : null;
  }
}
