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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.activation.MimeType;

/**
 * <p>The top level artifact of the Feed Object Model.  The Parser component
 * processes data from an InputStream and returns a Document instance.  The 
 * type of Document returned depends on the XML format being parsed.  The 
 * Feed Object Model supports four basic types of documents: FeedDocument,
 * EntryDocument, ServiceDocument (Atom Publishing Protocol Introspection
 * Documents) and XmlDocument (any arbitrary XML).</p>
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Document<T extends Element>  
  extends Base, Serializable {  
  
  /**
   * Returns the root element of the document (equivalent to DOM's getDocumentElement)
   */
  T getRoot();
  
  /**
   * Sets the root element of the document
   */
  void setRoot(T root);
  
  /**
   * Returns the Base URI of the document.  All relative URI's contained in the
   * document will be resolved according to this base.
   */
  URI getBaseUri();
  
  /**
   * Sets the Base URI of the document.  All relative URI's contained in the 
   * document will be resolved according to this base.
   */
  void setBaseUri(URI base);

  /**
   * Sets the Base URI of the document.  All relative URI's contained in the 
   * document will be resolved according to this base.
   * @throws URISyntaxException 
   */
  void setBaseUri(String base) throws URISyntaxException;
  
  /**
   * Returns the content type of this document
   */
  MimeType getContentType();
  
  /**
   * Sets the content type for this document
   */
  void setContentType(MimeType contentType);
  
  /**
   * Returns the last modified date for this document
   */
  Date getLastModified();
  
  /**
   * Sets the last modified date for this document
   */
  void setLastModified(Date lastModified);
  
  /**
   * Gets the charset used for this document
   */
  String getCharset();
  
  /**
   * Sets the charset used for this document
   */
  void setCharset(String charset);
}
