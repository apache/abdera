package org.apache.abdera.spring;

import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

public class DummyFilter
    implements Filter {

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        return chain.next(request);
    }

}
