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
package org.apache.abdera.protocol.server.processors;

import java.io.IOException;
import java.util.Collection;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation which processes requests for service
 * documents.
 */
public class ServiceRequestProcessor implements RequestProcessor {

    public ResponseContext process(RequestContext context,
                                   WorkspaceManager workspaceManager,
                                   CollectionAdapter collectionAdapter) {
        return this.processService(context, workspaceManager);
    }

    private ResponseContext processService(RequestContext context, WorkspaceManager workspaceManager) {
        String method = context.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            return this.getServiceDocument(context, workspaceManager);
        } else {
            return null;
        }
    }

    protected ResponseContext getServiceDocument(final RequestContext request, final WorkspaceManager workspaceManager) {
        return new StreamWriterResponseContext(request.getAbdera()) {

            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startService();
                for (WorkspaceInfo wi : workspaceManager.getWorkspaces(request)) {
                    sw.startWorkspace().writeTitle(wi.getTitle(request));
                    Collection<CollectionInfo> collections = wi.getCollections(request);
                    if (collections != null) {
                        for (CollectionInfo ci : collections) {
                            sw.startCollection(ci.getHref(request)).writeTitle(ci.getTitle(request)).writeAccepts(ci
                                .getAccepts(request));
                            CategoriesInfo[] catinfos = ci.getCategoriesInfo(request);
                            if (catinfos != null) {
                                for (CategoriesInfo catinfo : catinfos) {
                                    String cathref = catinfo.getHref(request);
                                    if (cathref != null) {
                                        sw.startCategories().writeAttribute("href",
                                                                            request.getTargetBasePath() + cathref)
                                            .endCategories();
                                    } else {
                                        sw.startCategories(catinfo.isFixed(request), catinfo.getScheme(request));
                                        for (CategoryInfo cat : catinfo) {
                                            sw.writeCategory(cat.getTerm(request), cat.getScheme(request), cat
                                                .getLabel(request));
                                        }
                                        sw.endCategories();
                                    }
                                }
                            }
                            sw.endCollection();
                        }
                    }
                    sw.endWorkspace();
                }
                sw.endService().endDocument();
            }
        }.setStatus(200).setContentType(Constants.APP_MEDIA_TYPE);
    }
}
