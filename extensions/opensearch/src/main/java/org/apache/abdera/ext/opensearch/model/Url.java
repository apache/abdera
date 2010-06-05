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
package org.apache.abdera.ext.opensearch.model;

import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

/**
 * Open Search 1.1 Url element: describes an interface by which a search client can make search requests.<br>
 * This supports all parts of the Open Search 1.1 Url specification.
 */
public class Url extends ElementWrapper {

    public Url(Factory factory) {
        super(factory, OpenSearchConstants.URL);
        this.setAttributeValue(OpenSearchConstants.URL_INDEXOFFSET_LN, Integer.valueOf(1).toString());
        this.setAttributeValue(OpenSearchConstants.URL_PAGEOFFSET_LN, Integer.valueOf(1).toString());
    }

    public Url(Abdera abdera) {
        this(abdera.getFactory());
    }

    public Url(Element internal) {
        super(internal);
    }

    public void setType(String type) {
        if (type != null) {
            this.setAttributeValue(OpenSearchConstants.URL_TYPE_LN, type);
        } else {
            this.removeAttribute(OpenSearchConstants.URL_TYPE_LN);
        }
    }

    public String getType() {
        return this.getAttributeValue(OpenSearchConstants.URL_TYPE_LN);
    }

    public void setTemplate(String template) {
        if (template != null) {
            this.setAttributeValue(OpenSearchConstants.URL_TEMPLATE_LN, template);
        } else {
            this.removeAttribute(OpenSearchConstants.URL_TEMPLATE_LN);
        }
    }

    public String getTemplate() {
        return this.getAttributeValue(OpenSearchConstants.URL_TEMPLATE_LN);
    }

    public void setIndexOffset(int offset) {
        if (offset > -1) {
            this.setAttributeValue(OpenSearchConstants.URL_INDEXOFFSET_LN, String.valueOf(offset));
        } else {
            this.removeAttribute(OpenSearchConstants.URL_INDEXOFFSET_LN);
        }
    }

    public int getIndexOffset() {
        String val = this.getAttributeValue(OpenSearchConstants.URL_INDEXOFFSET_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }

    public void setPageOffset(int offset) {
        if (offset > -1) {
            this.setAttributeValue(OpenSearchConstants.URL_PAGEOFFSET_LN, String.valueOf(offset));
        } else {
            this.removeAttribute(OpenSearchConstants.URL_PAGEOFFSET_LN);
        }
    }

    public int getPageOffset() {
        String val = this.getAttributeValue(OpenSearchConstants.URL_PAGEOFFSET_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }
}
