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
package org.apache.abdera.g14n.iri;

import org.apache.abdera.g14n.iri.IRI;
import org.apache.abdera.g14n.iri.Scheme;

/**
 * Base implementation for IRI scheme providers
 */
public abstract class AbstractScheme 
  implements Scheme {

  protected final String name;
  
  protected AbstractScheme(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  /**
   * Default to use normalization-based comparison
   */
  public boolean equivalent(IRI iri1, IRI iri2) {
    String s2 = iri2.normalize().toASCIIString();
    String s1 = iri1.normalize().toASCIIString();
    return s1.compareTo(s2) == 0;
  }
  
  /**
   * Default return unmodified
   */
  public IRI normalize(IRI iri) {
    return iri;
  }
  
  /**
   * Default return unmodified
   */
  public String normalizePath(String path) {
    return path;
  }
}
