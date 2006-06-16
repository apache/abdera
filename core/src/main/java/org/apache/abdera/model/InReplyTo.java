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
 * Provides an interface for the Atom Threading Extension in-reply-to
 * element.  The in-reply-to element allows an entry to be marked as 
 * a response to another resource.
 */
public interface InReplyTo 
  extends ExtensionElement {
  
  /**
   * Returns the persistent and universally unique identifier of the 
   * resource the entry is a response to.
   */
  URI getRef() throws URISyntaxException;
  
  /**
   * Sets the persistent and universally unique identifier of the 
   * resource that this entry is a response to
   */
  void setRef(URI ref);

  /**
   * Sets the persistent and universally unique identifier of the 
   * resource that this entry is a response to
   */
  void setRef(String ref) throws URISyntaxException;
  
  /**
   * Returns the resolved value of the href attribute
   */
  URI getResolvedHref() throws URISyntaxException;
  
  /**
   * Returns a dereferenceable URI indicating where a representation of the 
   * resource being responded to may be retrieved
   */
  URI getHref() throws URISyntaxException;
  
  /**
   * Sets a dereferenceable URI indicating where a representation of the 
   * resource being responded to may be retrieved
   */
  void setHref(URI ref);
  
  /**
   * Sets a dereferenceable URI indicating where a representation of the 
   * resource being responded to may be retrieved
   */
  void setHref(String ref) throws URISyntaxException;
  
  /**
   * Returns the media type of the resource referenced by the href attribute
   */
  MimeType getMimeType() throws MimeTypeParseException;

  /**
   * Sets the media type of the resource referenced by the href attribute
   */
  void setMimeType(MimeType mimeType);
  
  /**
   * Sets the media type of the resource referenced by the href attribute
   */
  void setMimeType(String mimeType) throws MimeTypeParseException;
  
  /**
   * Returns a dereferenceable URI of an Atom Feed or Entry Document resolved
   * against the in-scope Base URI
   */
  URI getResolvedSource() throws URISyntaxException;
  
  /**
   * Returns a dereferenceable URI of an Atom Feed or Entry Document
   */
  URI getSource() throws URISyntaxException;
  
  /**
   * Sets a dereferenceable URI of an Atom Feed or Entry Document
   */
  void setSource(URI source);
  
  /**
   * Sets a dereferenceable URI of an Atom Feed or Entry Document
   */
  void setSource(String source) throws URISyntaxException;
  
}
