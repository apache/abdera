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
package org.apache.abdera.protocol.server.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.writer.StreamWriter;

public class OpenSearchFilter implements Filter {

    public static final String OSDNS = "http://a9.com/-/spec/opensearch/1.1/";
    public static final String OS_PREFIX = "OpenSearch_";

    public static final TargetType TYPE_OPENSEARCH_DESCRIPTION = TargetType.get("OPENSEARCH_DESCRIPTION", true);

    private String shortName;
    private String description;
    private String[] tags;
    private String contact;
    private String template;
    private Map<String, String> map = new HashMap<String, String>();

    public OpenSearchFilter() {
    }

    public String getShortName() {
        return shortName;
    }

    public OpenSearchFilter setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OpenSearchFilter setDescription(String description) {
        this.description = description;
        return this;
    }

    public String[] getTags() {
        return tags;
    }

    public OpenSearchFilter setTags(String... tags) {
        this.tags = tags;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public OpenSearchFilter setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public OpenSearchFilter setTemplate(String template) {
        this.template = template;
        return this;
    }

    public OpenSearchFilter mapTargetParameter(String targetParam, String openSearchParam) {
        map.put(targetParam, openSearchParam);
        return this;
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {

        Target target = request.getTarget();
        TargetType type = target.getType();
        if (type == TYPE_OPENSEARCH_DESCRIPTION) {
            return getOpenSearchDescription(request);
        } else {
            for (Entry<String, String> entry : map.entrySet()) {
                String value = target.getParameter(entry.getKey());
                if (value != null)
                    request.setAttribute(OS_PREFIX + "_" + entry.getValue(), value);
            }
            return chain.next(request);
        }
    }

    private String combineTags() {
        if (tags == null)
            return "";
        StringBuilder buf = new StringBuilder();
        for (String tag : tags) {
            if (buf.length() > 0)
                buf.append(" ");
            buf.append(tag);
        }
        return buf.toString();
    }

    private ResponseContext getOpenSearchDescription(RequestContext request) {
        return new StreamWriterResponseContext(request.getAbdera()) {
            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startElement("OpenSearchDescription", OSDNS, "")
                    .startElement("ShortName", OSDNS, "").writeElementText(getShortName()).endElement()
                    .startElement("Description", OSDNS, "").writeElementText(getDescription()).endElement()
                    .startElement("Tags", OSDNS, "").writeElementText(combineTags()).endElement()
                    .startElement("Contact", OSDNS, "").writeElementText(getContact()).endElement().startElement("Url",
                                                                                                                 OSDNS,
                                                                                                                 "")
                    .writeAttribute("type", "application/atom+xml").writeAttribute("template", getTemplate())
                    .endElement().endElement().endDocument();
            }
        }.setStatus(200).setContentType("application/opensearchdescription+xml");
    }
}
