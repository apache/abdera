package org.apache.abdera2.activities.extra;

import org.apache.abdera2.activities.model.ASObject;

public abstract class CreativeWork extends ASObject {

  private static final long serialVersionUID = -178336535850006357L;

  public CreativeWork() {}
  
  public CreativeWork(String displayName) {
    setDisplayName(displayName);
  }
  
  public <T extends ASObject>T getAbout() {
    return getProperty("about");
  }
  
  public void setAbout(ASObject about) {
    setProperty("about", about);
  }
  
  public <T extends ASObject>T getGenre() {
    return getProperty("genre");
  }
  
  public void setGenre(ASObject genre) {
    setProperty("genre", genre);
  }
  
  public <T extends ASObject>T getPublisher() {
    return getProperty("publisher");
  }
  
  public void setPublisher(ASObject publisher) {
    setProperty("publisher", publisher);
  }
  
  public <T extends ASObject>T getProvider() {
    return getProperty("provider");
  }
  
  public void setProvider(ASObject provider) {
    setProperty("provider", provider);
  }
  
  public <T extends ASObject>T getContributor() {
    return getProperty("contributor");
  }
  
  public void setContributor(ASObject contributor) {
    setProperty("contributor", contributor);
  }
  
  public <T extends ASObject>T getEditor() {
    return getProperty("editor");
  }
  
  public void setEditor(ASObject editor) {
    setProperty("editor", editor);
  }
 
}
