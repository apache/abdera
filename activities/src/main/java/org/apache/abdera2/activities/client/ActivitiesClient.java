package org.apache.abdera2.activities.client;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.protocol.client.BasicClient;
import org.apache.abdera2.protocol.client.Client;
import org.apache.abdera2.protocol.client.ClientWrapper;
import org.apache.abdera2.protocol.client.RequestOptions;
import org.apache.abdera2.protocol.client.Session;

public class ActivitiesClient 
  extends ClientWrapper {

  public ActivitiesClient() {
    super(new BasicClient());
  }
  
  public ActivitiesClient(Client client) {
    super(client);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Session> T newSession() {
    return (T)new ActivitiesSession(this);
  }

  public <T extends Collection<?>>T getCollection(String uri) {
    ActivitiesSession session = newSession();
    return session.getCollection(uri);
  }
  
  public <T extends Collection<?>>T getCollection(String uri, RequestOptions options) {
    ActivitiesSession session = newSession();
    return session.getCollection(uri,options);
  }
  
  public <T extends Activity>T getActivity(String uri) {
    ActivitiesSession session = newSession();
    return session.getActivity(uri);
  }
  
  public <T extends Activity>T getActivity(String uri, RequestOptions options) {
    ActivitiesSession session = newSession();
    return session.getActivity(uri,options);
  }
  
  public <T extends ASObject>T getObject(String uri) {
    ActivitiesSession session = newSession();
    return session.getObject(uri);
  }
  
  public <T extends ASObject>T getObject(String uri, RequestOptions options) {
    ActivitiesSession session = newSession();
    return session.getObject(uri,options);
  }
}
