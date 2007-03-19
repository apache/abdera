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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Element;

public final class MediaExtensionFactory implements ExtensionFactory {

  @SuppressWarnings({ "unchecked", "deprecation" })
  public <T extends Element> T getElementWrapper(Element internal) {
    QName qname = internal.getQName();
    if (qname.equals(MediaConstants.ADULT)) return (T) new MediaAdult(internal);
    if (qname.equals(MediaConstants.CATEGORY)) return (T) new MediaCategory(internal);
    if (qname.equals(MediaConstants.CONTENT)) return (T) new MediaContent(internal);
    if (qname.equals(MediaConstants.COPYRIGHT)) return (T) new MediaCopyright(internal);
    if (qname.equals(MediaConstants.CREDIT)) return (T) new MediaCredit(internal);
    if (qname.equals(MediaConstants.DESCRIPTION)) return (T) new MediaDescription(internal);
    if (qname.equals(MediaConstants.GROUP)) return (T) new MediaGroup(internal);
    if (qname.equals(MediaConstants.HASH)) return (T) new MediaHash(internal);
    if (qname.equals(MediaConstants.KEYWORDS)) return (T) new MediaKeywords(internal);
    if (qname.equals(MediaConstants.PLAYER)) return (T) new MediaPlayer(internal);
    if (qname.equals(MediaConstants.RATING)) return (T) new MediaRating(internal);
    if (qname.equals(MediaConstants.RESTRICTION)) return (T) new MediaRestriction(internal);
    if (qname.equals(MediaConstants.TEXT)) return (T) new MediaText(internal);
    if (qname.equals(MediaConstants.THUMBNAIL)) return (T) new MediaThumbnail(internal);
    if (qname.equals(MediaConstants.TITLE)) return (T) new MediaTitle(internal);
    return (T)internal;
  }

  public List<String> getNamespaces() {
    return java.util.Arrays.asList(new String[] {MediaConstants.MEDIA_NS});
  }

  public boolean handlesNamespace(String namespace) {
    return namespace.equals(MediaConstants.MEDIA_NS);
  }

}

