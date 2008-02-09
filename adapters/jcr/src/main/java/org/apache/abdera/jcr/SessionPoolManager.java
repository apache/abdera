package org.apache.abdera.jcr;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.AbstractItemManager;

public class SessionPoolManager extends AbstractItemManager<Session> {
  private Repository repository;
  private Credentials credentials;
  
  public SessionPoolManager(int maxSize, Repository repository, Credentials credentials) {
    super(maxSize);
    this.repository = repository;
    this.credentials = credentials;
  }
  
  @Override
  protected Session internalNewInstance() {
    try {
      return repository.login(credentials);
    } catch (RepositoryException e) {
      throw new RuntimeException(e);
    }
  }

  public Session get(Request request) {
    return getInstance();
  }
}
