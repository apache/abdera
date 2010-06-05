package org.apache.abdera.protocol.server.test.multipart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.multipart.AbstractMultipartCollectionAdapter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;

public class MultipartRelatedAdapter extends AbstractMultipartCollectionAdapter {

    @Override
    public String getAuthor(RequestContext request) {
        return "Acme Industries";
    }

    @Override
    public String getId(RequestContext request) {
        return "tag:example.org,2008:feed";
    }

    public ResponseContext deleteEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext getEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext getFeed(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext postEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext putEntry(RequestContext request) {
        return ProviderHelper.notallowed(request);
    }

    public ResponseContext postMedia(RequestContext request) {
        try {
            if (MimeTypeHelper.isMultipart(request.getContentType().toString())) {
                MultipartRelatedPost post = getMultipartRelatedData(request);
                // Post object is a wrapper for the media resource and the media link entry.
                // Once we get it we can save them following the rfc specification.
            }

            return new EmptyResponseContext(201);
        } catch (ParseException pe) {
            return new EmptyResponseContext(415, pe.getLocalizedMessage());
        } catch (IOException ioe) {
            return new EmptyResponseContext(500, ioe.getLocalizedMessage());
        } catch (MessagingException e) {
            return new EmptyResponseContext(500, e.getLocalizedMessage());
        }
    }

    public String getTitle(RequestContext request) {
        return "Acme Multipart/related adapter";
    }

    @SuppressWarnings("serial")
    public Map<String, String> getAlternateAccepts(RequestContext request) {
        if (accepts == null) {
            accepts = new HashMap<String, String>() {
                {
                    put("video/*", null); /* doesn't accept multipart related */
                    put("image/jpg", ""); /* doesn't accept multipart related */
                    put("image/png", Constants.LN_ALTERNATE_MULTIPART_RELATED /* multipart-related */);
                }
            };
        }
        return accepts;
    }

}
