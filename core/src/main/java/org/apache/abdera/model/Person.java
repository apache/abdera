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

/**
 * <p>An Atom Person Construct</p>
 * 
 * <p>Per RFC4287:</p>
 * 
 * <pre>
 *   A Person construct is an element that describes a person,
 *   corporation, or similar entity (hereafter, 'person').
 *
 *   atomPersonConstruct =
 *     atomCommonAttributes,
 *     (element atom:name { text }
 *      &amp; element atom:uri { atomUri }?
 *      &amp; element atom:email { atomEmailAddress }?
 *      &amp; extensionElement*)
 *
 * </pre>
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Person 
  extends ExtensibleElement, ExtensionElement {

  /**
   * The "atom:name" element's content conveys a human-readable name for
   * the person.  The content of atom:name is Language-Sensitive.  Person
   * constructs MUST contain exactly one "atom:name" element.
   */
  StringElement getNameElement();
  
  /**
   * The "atom:name" element's content conveys a human-readable name for
   * the person.  The content of atom:name is Language-Sensitive.  Person
   * constructs MUST contain exactly one "atom:name" element.
   */
  void setNameElement(StringElement element);
  
  /**
   * The "atom:name" element's content conveys a human-readable name for
   * the person.  The content of atom:name is Language-Sensitive.  Person
   * constructs MUST contain exactly one "atom:name" element.
   */
  StringElement setName(String name);
  
  /**
   * The "atom:name" element's content conveys a human-readable name for
   * the person.  The content of atom:name is Language-Sensitive.  Person
   * constructs MUST contain exactly one "atom:name" element.
   */
  String getName();
  
  /**
   * The "atom:email" element's content conveys an e-mail address
   * associated with the person.  Person constructs MAY contain an
   * atom:email element, but MUST NOT contain more than one.  Its content
   * MUST conform to the "addr-spec" production in [RFC2822].
   */
  StringElement getEmailElement();
  
  /**
   * The "atom:email" element's content conveys an e-mail address
   * associated with the person.  Person constructs MAY contain an
   * atom:email element, but MUST NOT contain more than one.  Its content
   * MUST conform to the "addr-spec" production in [RFC2822].
   */
  void setEmailElement(StringElement element);
  
  /**
   * The "atom:email" element's content conveys an e-mail address
   * associated with the person.  Person constructs MAY contain an
   * atom:email element, but MUST NOT contain more than one.  Its content
   * MUST conform to the "addr-spec" production in [RFC2822].
   */
  StringElement setEmail(String email);
  
  /**
   * The "atom:email" element's content conveys an e-mail address
   * associated with the person.  Person constructs MAY contain an
   * atom:email element, but MUST NOT contain more than one.  Its content
   * MUST conform to the "addr-spec" production in [RFC2822].
   */
  String getEmail();  
  
  /**
   * The "atom:uri" element's content conveys an IRI associated with the
   * person.  Person constructs MAY contain an atom:uri element, but MUST
   * NOT contain more than one.  The content of atom:uri in a Person
   * construct MUST be an IRI reference [RFC3987].
   */
  IRI getUriElement();
  
  /**
   * The "atom:uri" element's content conveys an IRI associated with the
   * person.  Person constructs MAY contain an atom:uri element, but MUST
   * NOT contain more than one.  The content of atom:uri in a Person
   * construct MUST be an IRI reference [RFC3987].
   */
  void setUriElement(IRI uri);
  
  /**
   * The "atom:uri" element's content conveys an IRI associated with the
   * person.  Person constructs MAY contain an atom:uri element, but MUST
   * NOT contain more than one.  The content of atom:uri in a Person
   * construct MUST be an IRI reference [RFC3987].
   */
  IRI setUri(URI uri);

  /**
   * The "atom:uri" element's content conveys an IRI associated with the
   * person.  Person constructs MAY contain an atom:uri element, but MUST
   * NOT contain more than one.  The content of atom:uri in a Person
   * construct MUST be an IRI reference [RFC3987].
   * @throws URISyntaxException 
   */
  IRI setUri(String uri) throws URISyntaxException;
  
  /**
   * The "atom:uri" element's content conveys an IRI associated with the
   * person.  Person constructs MAY contain an atom:uri element, but MUST
   * NOT contain more than one.  The content of atom:uri in a Person
   * construct MUST be an IRI reference [RFC3987].
   * @throws URISyntaxException 
   */
  URI getUri() throws URISyntaxException;
}
