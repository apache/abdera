package org.apache.abdera.protocol.server.content;

import java.util.Map;
import java.util.Set;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractProvider;
import org.apache.abdera.protocol.server.impl.AbstractResponseContext;
import org.apache.abdera.protocol.server.impl.BaseResponseContext;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractWorkspaceProvider extends AbstractProvider {

    private EntityTag service_etag = new EntityTag("simple");
    
    protected AbstractWorkspaceProvider(int count) {
      super(count);
    }

    public ResponseContext getService(RequestContext request) {
      Abdera abdera = request.getAbdera();
      Document<Service> service = getServicesDocument(abdera);
      AbstractResponseContext rc = new BaseResponseContext<Document<Service>>(service);
      rc.setEntityTag(service_etag);
      return rc;
    }

    @SuppressWarnings("unchecked")
    private Document<Service> getServicesDocument(Abdera abdera) {
      Factory factory = abdera.getFactory();
      Service service = factory.newService();
      for (WorkspaceInfo wp : getWorkspaces()) {
        Workspace workspace = service.addWorkspace(wp.getName());
        Set<Map.Entry<String, CollectionProvider>> entrySet = 
          (Set<Map.Entry<String, CollectionProvider>>) (wp.getCollectionProviders().entrySet());
        for (Map.Entry<String, CollectionProvider> entry : entrySet) {
          CollectionProvider<?> cp = entry.getValue();
          Collection collection = workspace.addCollection(cp.getTitle(), 
                                                          wp.getId() + "/" + entry.getKey());
          collection.setAccept("entry");
          // collection.addCategories().setFixed(false);
        }
      }
      return service.getDocument();
    }

    public abstract java.util.Collection<WorkspaceInfo> getWorkspaces();

    public ResponseContext getFeed(RequestContext request) {
      CollectionProvider provider = getCollectionProvider(resolveBase(request), request);
      
      return provider.getFeed(request);
    }

    @SuppressWarnings("unchecked")
    private <T> CollectionProvider<T> getCollectionProvider(IRI resolveBase, 
                                                      RequestContext request) {
      String path = resolveBase.getPath();
      String[] paths = path.split("/");
      if (paths.length < 1) {
        // TODO:
        throw new RuntimeException();
      } else if (paths.length == 1) {
        WorkspaceInfo wp = getWorkspaceInfo("");
        if (wp == null) {
          // TODO: 404
          throw new RuntimeException();
        }
        return wp.getCollectionProvider(paths[0]);
      } else {    
        WorkspaceInfo wp = getWorkspaceInfo(paths[paths.length - 2]);
        if (wp == null) {
          // TODO: 404
          throw new RuntimeException();
        }
        return wp.getCollectionProvider(paths[paths.length - 1]);
      }
    }

    protected abstract WorkspaceInfo<?> getWorkspaceInfo(String string);

    public ResponseContext createEntry(RequestContext request) {
      CollectionProvider provider = getCollectionProvider(request.getUri(), request);
      
      return provider.createEntry(request);
    }

    @Override
    public ResponseContext getMedia(RequestContext request) {
      IRI entryBaseIri = resolveBase(request).resolve("../");
      CollectionProvider provider = getCollectionProvider(entryBaseIri, request);

      return provider.getMedia(request);
    }


    @Override
    public ResponseContext updateMedia(RequestContext request) {
      // TODO Auto-generated method stub
      return super.updateMedia(request);
    }

    public ResponseContext deleteEntry(RequestContext request) {
      CollectionProvider<?> provider = getCollectionProvider(resolveBase(request).resolve("./"), request);

      return provider.deleteEntry(request);
    }

    
    public ResponseContext getEntry(RequestContext request) {
      IRI entryBaseIri = resolveBase(request).resolve("./");
      CollectionProvider provider = getCollectionProvider(entryBaseIri, request);

      return provider.getEntry(request, entryBaseIri);
    }

    @SuppressWarnings("unchecked")
    public ResponseContext updateEntry(RequestContext request) {
      IRI entryBaseIri = resolveBase(request).resolve("./");
      CollectionProvider provider = getCollectionProvider(entryBaseIri, request);

      return provider.updateEntry(request, entryBaseIri);
    }


}
