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
package org.apache.abdera.model;

import java.net.URI;
import java.net.URISyntaxException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * <p>Represents an collection element in an Atom Publishing Protocol 
 * introspection document.</p>
 * 
 * <p>Per APP Draft-08:</p>
 * 
 * <pre>
 *  The "app:collection" describes an Atom Protocol collection.  One
 *  child element is defined here for app:collection: "app:member-type".
 *
 *  appCollection =
 *     element app:collection {
 *        appCommonAttributes,
 *        attribute title { text },
 *        attribute href { text },
 *        ( appAccept
 *          &amp; extensionElement* )
 *     }
 * </pre>
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Collection 
  extends ExtensibleElement, ExtensionElement {

  /**
   * APP Draft-08: The app:collection element MUST contain a "title" 
   * attribute, whose value gives a human-readable name for the collection.  
   * This attribute is Language-Sensitive.
   */
  String getTitle();

  /**
   * APP Draft-08: The app:collection element MUST contain a "title" 
   * attribute, whose value gives a human-readable name for the collection.  
   * This attribute is Language-Sensitive.
   */
  void setTitle(String title);

  /**
   * APP Draft-08: The app:collection element MUST contain a "href" 
   * attribute, whose value gives the IRI of the collection.
   */
  URI getHref() throws URISyntaxException;

  URI getResolvedHref() throws URISyntaxException;
  
  /**
   * APP Draft-08: The app:collection element MUST contain a "href" 
   * attribute, whose value gives the IRI of the collection.
   */
  void setHref(URI href);
  
  /**
   * APP Draft-08: The app:collection element MUST contain a "href" 
   * attribute, whose value gives the IRI of the collection.
   * @throws URISyntaxException 
   */
  void setHref(String href) throws URISyntaxException;

  /**
   * PaceMediaEntries5 (proposed change to APP Draft-08)
   */
  String[] getAccept() ;

  /**
   * PaceMediaEntries5 (proposed change to APP Draft-08)
   */
  void setAccept(String[] mediaRanges);
  
  /**
   * Returns true if the collection accepts the specified media type
   * @throws MimeTypeParseException 
   * @throws javax.activation.MimeTypeParseException 
   */
  boolean accepts(String mediaType);
  
  /**
   * Returns true if the collection accepts the specified media type
   */
  boolean accepts(MimeType mediaType);
}
