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
package org.apache.abdera2.common.templates;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public abstract class DelegatingContext extends AbstractContext {

  private static final long serialVersionUID = 5513709111934286216L;
    protected final Context subcontext;

    protected DelegatingContext(Context subcontext) {
        this.subcontext = subcontext;
    }

    protected <T> T resolveActual(String var) {
        return (T)this.subcontext.resolve(var);
    }

    public Iterator<String> iterator() {
        return this.subcontext.iterator();
    }

    public boolean isIri() {
      return this.subcontext.isIri();
    }
    
    public void setIri(boolean isiri) {
      this.subcontext.setIri(isiri);
    }
    
    public boolean contains(String var) {
      return this.subcontext.contains(var);
    }

    public <T> T resolve(String var) {
      return this.subcontext.resolve(var);
    }

    public void clear() {
      this.subcontext.clear();
    }
}
