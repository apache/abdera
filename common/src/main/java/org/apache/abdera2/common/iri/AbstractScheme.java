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
package org.apache.abdera2.common.iri;

/**
 * Base implementation for IRI scheme providers
 */
public abstract class AbstractScheme implements Scheme {

  private static final long serialVersionUID = 1458641384259566421L;
    protected final String name;
    protected final int port;

    protected AbstractScheme(String name, int port) {
        if (name == null)
          throw new IllegalArgumentException();
        this.name = name.toLowerCase();
        this.port = port;
    }

    public int port() {
        return port;
    }

    public String name() {
        return name;
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + port;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AbstractScheme other = (AbstractScheme) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (port != other.port)
        return false;
      return true;
    }
    
    public String toString() {
      return name();
    }
}
