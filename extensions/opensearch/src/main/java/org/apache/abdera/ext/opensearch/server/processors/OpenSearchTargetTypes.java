package org.apache.abdera.ext.opensearch.server.processors;

import org.apache.abdera.protocol.server.TargetType;

/**
 * Simple container of {@link org.apache.abdera.protocol.server.TargetType}s related to Open Search.
 */
public interface OpenSearchTargetTypes {

    public static final TargetType OPENSEARCH_DESCRIPTION = TargetType.get("OPENSEARCH_DESCRIPTION", true);
    public static final TargetType OPENSEARCH_URL = TargetType.get("OPENSEARCH_URL", true);
}
