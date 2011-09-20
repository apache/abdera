package org.apache.abdera2.activities.model.objects;

import org.apache.abdera2.common.anno.Name;

@Name("account")
public class AccountObject 
  extends ServiceObject {

  private static final long serialVersionUID = 1058258637558799759L;

  public AccountObject() {
    super();
  }

  public AccountObject(String displayName) {
    super(displayName);
  }

  public String getDomain() {
    return getProperty("domain");
  }
  
  public void setDomain(String domain) {
    setProperty("domain", domain);
  }
  
  public String getUsername() {
    return getProperty("username");
  }
  
  public void setUsername(String username) {
    setProperty("username", username);
  }
  
  public String getUserId() {
    return getProperty("userId");
  }
  
  public void setUserId(String userid) {
    setProperty("userId", userid);
  }
}
