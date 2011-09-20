package org.apache.abdera2.activities.extra;

import java.util.Date;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

@Name("offer")
@Properties({
  @Property(name="validFrom",to=Date.class),
  @Property(name="validUntil",to=Date.class)
})
public class OfferObject extends ASObject {

  private static final long serialVersionUID = 8693274483912587801L;

  public OfferObject() {}
  
  public OfferObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public <T extends ASObject>T getAvailability() {
    return getProperty("availability");
  }
  
  public void setAvailability(ASObject availability) {
    setProperty("availability", availability);
  }
  
  public <T extends ASObject>T getCondition() {
    return getProperty("condition");
  }
  
  public void setCondition(ASObject condition) {
    setProperty("condition", condition);
  }
  
  public <T extends ASObject>T getItem() {
    return getProperty("item");
  }
  
  public void setItem(ASObject item) {
    setProperty("item", item);
  }
  
  public String getPrice() {
    return getProperty("price");
  }
  
  public void setPrice(String price) {
    setProperty("price", price);
  }
  
  public String getCurrency() {
    return getProperty("currency");
  }
  
  public void setCurrency(String currency) {
    setProperty("currency",currency);
  }
  
  public Date getValidUntil() {
    return getProperty("validUntil");
  }
  
  public void setValidUntil(Date date) {
    setProperty("validUntil", date);
  }
  
  public Date getValidFrom() {
    return getProperty("validFrom");
  }
  
  public void setValidFrom(Date date) {
    setProperty("validFrom", date);
  }

  public <T extends ASObject>T getRestriction() {
    return getProperty("restriction");
  }
  
  public void setRestriction(ASObject restriction) {
    setProperty("restriction", restriction);
  }
}
