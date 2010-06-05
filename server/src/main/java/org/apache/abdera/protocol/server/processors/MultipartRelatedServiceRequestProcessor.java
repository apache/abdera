package org.apache.abdera.protocol.server.processors;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.multipart.MultipartRelatedCollectionInfo;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation which processes requests for service
 * documents. It writes multipart/related accept attributes when is enabled.
 */
public class MultipartRelatedServiceRequestProcessor extends ServiceRequestProcessor {

    @Override
    protected ResponseContext getServiceDocument(final RequestContext request, final WorkspaceManager workspaceManager) {
        return new StreamWriterResponseContext(request.getAbdera()) {

            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startService();
                for (WorkspaceInfo wi : workspaceManager.getWorkspaces(request)) {
                    sw.startWorkspace().writeTitle(wi.getTitle(request));
                    Collection<CollectionInfo> collections = wi.getCollections(request);

                    if (collections != null) {
                        for (CollectionInfo ci : collections) {
                            sw.startCollection(ci.getHref(request)).writeTitle(ci.getTitle(request));
                            if (ci instanceof MultipartRelatedCollectionInfo) {
                                MultipartRelatedCollectionInfo multipartCi = (MultipartRelatedCollectionInfo)ci;
                                for (Map.Entry<String, String> accept : multipartCi.getAlternateAccepts(request)
                                    .entrySet()) {
                                    sw.startElement(Constants.ACCEPT);
                                    if (accept.getValue() != null && accept.getValue().length() > 0) {
                                        sw.writeAttribute(Constants.LN_ALTERNATE, accept.getValue());
                                    }
                                    sw.writeElementText(accept.getKey()).endElement();
                                }
                            } else {
                                sw.writeAccepts(ci.getAccepts(request));
                            }
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
