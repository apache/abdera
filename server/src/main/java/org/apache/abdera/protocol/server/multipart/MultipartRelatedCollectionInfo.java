package org.apache.abdera.protocol.server.multipart;

import java.util.Map;

import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;

public interface MultipartRelatedCollectionInfo extends CollectionInfo {

    /**
     * Returns a map of MIME media types for the app:collection element's app:accept elements. These tell a client which
     * media types the collection will accept on a POST. The key element is the default media type and the value element
     * is the alternate type or null if it doesn't accept alternates.
     */
    public Map<String, String> getAlternateAccepts(RequestContext request);
}
