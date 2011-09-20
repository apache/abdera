package org.apache.abdera2.activities.model.objects;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.iri.IRI;

@org.apache.abdera2.common.anno.Name("name")
public class NameObject 
  extends ASObject {

  private static final long serialVersionUID = 5503270370616022868L;

  public NameObject() {
    super();
  }

  public String getFamilyName() {
    return getProperty("familyName");
  }
  
  public void setFamilyName(String val) {
    setProperty("familyName", val);
  }
  
  public String getFormatted() {
    return getProperty("formatted");
  }
  
  public void setFormatted(String val) {
    setProperty("formatted", val);
  }
  
  public String getGivenName() {
    return getProperty("givenName");
  }
  
  public void setGivenName(String val) {
    setProperty("givenName", val);
  }
  
  public String getHonorificPrefix() {
    return getProperty("honorificPrefix");
  }
  
  public void setHonorificPrefix(String val) {
    setProperty("honorificPrefix", val);
  }
  
  public String getHonorificSuffix() {
    return getProperty("honorificSuffix");
  }
  
  public void setHonorificSuffix(String val) {
    setProperty("honorificSuffix", val);
  }
  
  public String getMiddleName() {
    return getProperty("middleName");
  }
  
  public void setMiddleName(String val) {
    setProperty("middleName", val);
  }
  
  public String getPronunciation() {
    return getProperty("pronunciation");
  }
  
  public void setPronunciation(String val) {
    setProperty("pronunciation", val);
  }
  
  public IRI getPronunciationUrl() {
    return getProperty("pronunciationUrl");
  }
  
  public void setPronunciationUrl(IRI val) {
    setProperty("pronunciationUrl", val);
  }
  
  public void setPronunciationUrl(String val) {
    setPronunciationUrl(new IRI(val));
  }
}
