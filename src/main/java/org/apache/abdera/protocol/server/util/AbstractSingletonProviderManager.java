package org.apache.abdera.protocol.server.util;

import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.ProviderManager;

public abstract class AbstractSingletonProviderManager 
  implements ProviderManager {

  protected Provider provider;
  
  public Provider getProvider() {
    if (provider == null) {
      synchronized(this) {
        provider = initProvider();
      }
    }
    return provider;
  }

  protected abstract Provider initProvider();
  
  public void release(Provider provider) {
    // nothing to release. subclasses could choose to do reference counting
    // if they want
  }

}
