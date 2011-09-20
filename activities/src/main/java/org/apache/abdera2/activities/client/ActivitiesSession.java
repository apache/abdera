package org.apache.abdera2.activities.client;

import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.protocol.ProtocolException;
import org.apache.abdera2.protocol.client.Client;
import org.apache.abdera2.protocol.client.ClientResponse;
import org.apache.abdera2.protocol.client.RequestOptions;
import org.apache.abdera2.protocol.client.Session;

public class ActivitiesSession 
  extends Session {

  private final IO io;
  
  protected ActivitiesSession(Client client) {
    super(client);
    this.io = IO.get();
  }

  public IO getIO() {
    return io;
  }
  
  protected ActivitiesClient getActivitiesClient() {
    return (ActivitiesClient) client;
  }

  public <T extends Collection<?>>T getCollection(String uri) {
    return getCollection(uri, this.getDefaultRequestOptions());
  }
  
  public <T extends ClientResponse>T post(String uri, ASBase base) {
    return post(uri,base, this.getDefaultRequestOptions());
  }
  
  public <T extends ClientResponse>T post(String uri, ASBase base, RequestOptions options) {
    ActivityEntity entity = new ActivityEntity(base);
    return post(uri, entity, options);
  }

  public <T extends ClientResponse>T put(String uri, ASBase base) {
    return put(uri,base, this.getDefaultRequestOptions());
  }
  
  public <T extends ClientResponse>T put(String uri, ASBase base, RequestOptions options) {
    ActivityEntity entity = new ActivityEntity(base);
    return put(uri, entity, options);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Collection<?>>T getCollection(String uri, RequestOptions options) {
    ClientResponse cr = get(uri, options);
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readCollection(cr.getReader());
            setDocProperties(cr,t);
            return t;
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  public <T extends Activity>T getActivity(String uri) {
    return getActivity(uri,this.getDefaultRequestOptions());
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Activity>T getActivity(String uri, RequestOptions options) {
    ClientResponse cr = get(uri, options);
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readActivity(cr.getReader());
            setDocProperties(cr,t);
            return t;
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  public <T extends ASObject>T getObject(String uri) {
    return getObject(uri, this.getDefaultRequestOptions());
  }
  
  @SuppressWarnings("unchecked")
  public <T extends ASObject>T getObject(String uri, RequestOptions options) {
    ClientResponse cr = get(uri, options);
    try {
      if (cr != null) {
        switch(cr.getType()) {
        case SUCCESSFUL:
          try {
            T t = (T)io.readObject(cr.getReader());
            setDocProperties(cr,t);
            return t;
          } catch (Throwable t) {
            throw new ProtocolException(601, t.getMessage());
          }
        default:
          throw new ProtocolException(cr.getStatus(), cr.getStatusText());
        }
      } else {
        throw new ProtocolException(600, "Null Response");
      }
    } finally {
      if (cr != null) cr.release();
    }
  }
  
  private void setDocProperties(ClientResponse resp, ASBase base) {
    EntityTag etag = resp.getEntityTag();
    if (etag != null)
        base.setEntityTag(etag);
    Date lm = resp.getLastModified();
    if (lm != null)
        base.setLastModified(lm);
    MimeType mt = resp.getContentType();
    if (mt != null)
        base.setContentType(mt.toString());
    String language = resp.getContentLanguage();
    if (language != null)
        base.setLanguage(language);
    String slug = resp.getSlug();
    if (slug != null)
        base.setSlug(slug);
  }
}
