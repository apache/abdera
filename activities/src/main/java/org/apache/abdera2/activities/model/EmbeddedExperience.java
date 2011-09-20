package org.apache.abdera2.activities.model;

import java.util.Map;

import org.apache.abdera2.common.iri.IRI;

public class EmbeddedExperience 
  extends ASBase {

  private static final long serialVersionUID = -4311572934016006379L;

  public EmbeddedExperience() {}
  
  public IRI getUrl() {
    return getProperty("url");
  }
  
  public void setUrl(IRI iri) {
    setProperty("url", iri);
  }
  
  public void setUrl(String iri) {
    setUrl(new IRI(iri));
  }
  
  public IRI getGadget() {
    return getProperty("gadget");
  }
  
  public void setGadget(IRI iri) {
    setProperty("gadget", iri);
  }
  
  public void setGadget(String iri) {
    setGadget(new IRI(iri));
  }
  
  public ASBase getContext() {
    return getProperty("context");
  }
  
  public void setContext(ASBase context) {
    setProperty("context", context);
  }
  
  public void setContext(Map<String,Object> context) {
    setProperty("context", context);
  }
  
  public IRI getPreviewImage() {
    return getProperty("previewImage");
  }
  
  public void setPreviewImage(IRI iri) {
    setProperty("previewImage", iri);
  }
  
  public void setPreviewImage(String iri) {
    setGadget(new IRI(iri));
  }
}
