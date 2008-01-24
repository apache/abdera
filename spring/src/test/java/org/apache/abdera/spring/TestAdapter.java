package org.apache.abdera.spring;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;

public class TestAdapter extends AbstractCollectionAdapter {

    @Override
    public String getAuthor() throws ResponseContextException {
        return null;
    }

    @Override
    public String getId(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getHref(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTitle(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext deleteEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext getEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext getFeed(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext postEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseContext putEntry(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

}
