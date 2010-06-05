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
package org.apache.abdera.i18n.templates;

/**
 * Abstract base for custom Context implementations
 */
public abstract class AbstractContext implements Context {

    protected boolean iri = false;
    protected boolean normalizing = false;

    public boolean isIri() {
        return iri;
    }

    public void setIri(boolean isiri) {
        this.iri = isiri;
    }

    public boolean isNormalizing() {
        return normalizing;
    }

    public void setNormalizing(boolean normalizing) {
        this.normalizing = normalizing;
    }

}
