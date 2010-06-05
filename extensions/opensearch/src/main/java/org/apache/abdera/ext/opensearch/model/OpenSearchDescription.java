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

import java.util.List;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * Open Search 1.1 Description Document element: used to describe the web interface of an Open Search based search
 * engine.<br>
 * Currently, this supports only a subset of the Open Search 1.1 Description Document element specification.
 */
public class OpenSearchDescription extends ExtensibleElementWrapper {

    public OpenSearchDescription(Factory factory) {
        super(factory, OpenSearchConstants.OPENSEARCH_DESCRIPTION);
    }

    public OpenSearchDescription(Abdera abdera) {
        this(abdera.getFactory());
    }

    public OpenSearchDescription(Element internal) {
        super(internal);
    }

    public void setShortName(String shortName) {
        if (shortName == null) {
            shortName = "";
        }
        this.setExtensionStringValue(OpenSearchConstants.SHORT_NAME, shortName);
    }

    public String getShortName() {
        StringElement element = this.getExtension(OpenSearchConstants.SHORT_NAME);
        return element.getValue();
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.setExtensionStringValue(OpenSearchConstants.DESCRIPTION, description);
    }

    public String getDescription() {
        StringElement element = this.getExtension(OpenSearchConstants.DESCRIPTION);
        return element.getValue();
    }

    public void setTags(String... tags) {
        if (tags != null && tags.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tags.length - 1; i++) {
                builder.append(tags[i]).append(" ");
            }
            builder.append(tags[tags.length - 1]);
            this.setExtensionStringValue(OpenSearchConstants.TAGS, builder.toString());
        } else {
            this.setExtensionStringValue(OpenSearchConstants.TAGS, "");
        }
    }

    public String getTags() {
        StringElement element = this.getExtension(OpenSearchConstants.TAGS);
        return element.getValue();
    }

    public void addUrls(Url... urls) {
        if (urls != null && urls.length > 0) {
            for (Url url : urls) {
                this.addExtension(url);
            }
        }
    }

    public List<Url> getUrls() {
        return this.getExtensions(OpenSearchConstants.URL);
    }

    public void addQueries(Query... queries) {
        if (queries != null && queries.length > 0) {
            for (Query query : queries) {
                this.addExtension(query);
            }
        }
    }

    public List<Query> getQueries() {
        return this.getExtensions(OpenSearchConstants.QUERY);
    }

    private void setExtensionStringValue(QName extension, String value) {
        StringElement element = this.getExtension(extension);
        if (element == null) {
            element = this.addExtension(extension);
        }
        element.setValue(value);
    }
}
