package org.apache.abdera2.activities.extra;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.MediaLink;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.date.Duration;

@Name("movie")
@Properties({
  @Property(name="preview",to=MediaLink.class),
  @Property(name="duration",to=Duration.class)
})
public class MovieObject extends CreativeWork {

  private static final long serialVersionUID = -1551754630697817614L;

  public MovieObject() {}
  
  public MovieObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public <T extends ASObject>T getActors() {
    return getProperty("actors");
  }
  
  public void setActors(ASObject actors) {
    setProperty("actors", actors);
  }
  
  public <T extends ASObject>T getDirector() {
    return getProperty("director");
  }
  
  public void setDirector(ASObject director) {
    setProperty("director", director);
  }
  
  public Duration getDuration() {
    return getProperty("duration");
  }
  
  public void setDuration(Duration duration) {
    setProperty("duration", duration);
  }
  
  public <T extends ASObject>T getMusicBy() {
    return getProperty("musicBy");
  }
  
  public void setMusicBy(ASObject musicBy) {
    setProperty("musicBy", musicBy);
  }
  
  public <T extends ASObject>T getProducer() {
    return getProperty("producer");
  }
  
  public void setProducer(ASObject producer) {
    setProperty("producer", producer);
  }
  
  public <T extends ASObject>T getProductionCompany() {
    return getProperty("productionCompany");
  }
  
  public void setProductionCompany(ASObject org) {
    setProperty("productionCompany", org);
  }
  
  public MediaLink getPreview() {
    return getProperty("preview");
  }
  
  public void setPreview(MediaLink link) {
    setProperty("preview", link);
  }
}
