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

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * Open Search 1.1 Query element: defines a search query that can be performed by search clients.<br>
 * This supports all parts of the Open Search 1.1 Url specification.
 */
public class Query extends ExtensibleElementWrapper {

    public Query(Factory factory) {
        super(factory, OpenSearchConstants.QUERY);
    }

    public Query(Abdera abdera) {
        this(abdera.getFactory());
    }

    public Query(Element internal) {
        super(internal);
    }

    public Role getRole() {
        String role = this.getAttributeValue(OpenSearchConstants.QUERY_ROLE_LN);
        if (role == null) {
            return null;
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void setRole(Role role) {
        if (role != null) {
            this.setAttributeValue(OpenSearchConstants.QUERY_ROLE_LN, role.name().toLowerCase());
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_ROLE_LN);
        }
    }

    public String getTitle() {
        return this.getAttributeValue(OpenSearchConstants.QUERY_TITLE_LN);
    }

    public void setTitle(String title) {
        if (title != null) {
            if (title.length() > 256) {
                throw new IllegalArgumentException("Title too long (max 256 characters)");
            }
            this.setAttributeValue(OpenSearchConstants.QUERY_TITLE_LN, title);
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_TITLE_LN);
        }
    }

    public int getTotalResults() {
        String val = this.getAttributeValue(OpenSearchConstants.QUERY_TOTALRESULTS_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }

    public void setTotalResults(int totalResults) {
        if (totalResults > -1) {
            this.setAttributeValue(OpenSearchConstants.QUERY_TOTALRESULTS_LN, String.valueOf(totalResults));
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_TOTALRESULTS_LN);
        }
    }

    public String getSearchTerms() {
        return this.getAttributeValue(OpenSearchConstants.QUERY_SEARCHTERMS_LN);
    }

    public void setSearchTerms(String terms) {
        if (terms != null) {
            this.setAttributeValue(OpenSearchConstants.QUERY_SEARCHTERMS_LN, terms);
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_SEARCHTERMS_LN);
        }
    }

    public int getCount() {
        String val = this.getAttributeValue(OpenSearchConstants.QUERY_COUNT_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }

    public void setCount(int count) {
        if (count > -1) {
            this.setAttributeValue(OpenSearchConstants.QUERY_COUNT_LN, String.valueOf(count));
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_COUNT_LN);
        }
    }

    public int getStartIndex() {
        String val = this.getAttributeValue(OpenSearchConstants.QUERY_STARTINDEX_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }

    public void setStartIndex(int startIndex) {
        if (startIndex > -1) {
            this.setAttributeValue(OpenSearchConstants.QUERY_STARTINDEX_LN, String.valueOf(startIndex));
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_STARTINDEX_LN);
        }
    }

    public int getStartPage() {
        String val = this.getAttributeValue(OpenSearchConstants.QUERY_STARTPAGE_LN);
        return (val != null) ? Integer.parseInt(val) : -1;
    }

    public void setStartPage(int startPage) {
        if (startPage > -1) {
            this.setAttributeValue(OpenSearchConstants.QUERY_STARTPAGE_LN, String.valueOf(startPage));
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_STARTPAGE_LN);
        }
    }

    public String getResultsLanguage() {
        return this.getAttributeValue(OpenSearchConstants.QUERY_LANGUAGE_LN);
    }

    public void setResultsLanguage(String language) {
        if (language != null) {
            this.setAttributeValue(OpenSearchConstants.QUERY_LANGUAGE_LN, language);
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_LANGUAGE_LN);
        }
    }

    public String getInputEncoding() {
        return this.getAttributeValue(OpenSearchConstants.QUERY_INPUTENCODING_LN);
    }

    public void setInputEncoding(String encoding) {
        if (encoding != null) {
            this.setAttributeValue(OpenSearchConstants.QUERY_INPUTENCODING_LN, encoding);
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_INPUTENCODING_LN);
        }
    }

    public String getOutputEncoding() {
        return this.getAttributeValue(OpenSearchConstants.QUERY_OUTPUTENCODING_LN);
    }

    public void setOutputEncoding(String encoding) {
        if (encoding != null) {
            this.setAttributeValue(OpenSearchConstants.QUERY_OUTPUTENCODING_LN, encoding);
        } else {
            this.removeAttribute(OpenSearchConstants.QUERY_OUTPUTENCODING_LN);
        }
    }

    public enum Role {

        CORRECTION, EXAMPLE, RELATED, REQUEST, SUBSET, SUPERSET;
    }
}
