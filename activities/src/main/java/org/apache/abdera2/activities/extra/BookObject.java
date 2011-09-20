package org.apache.abdera2.activities.extra;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

@Name("book")
public class BookObject extends CreativeWork {

  private static final long serialVersionUID = -178336535850006357L;

  public BookObject() {}
  
  public BookObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public <T extends ASObject>T getFormat() {
    return getProperty("format");
  }
  
  public void setFormat(ASObject format) {
    setProperty("format", format);
  }
  
  public String getEdition() {
    return getProperty("edition");
  }
  
  public void setEdition(String edition) {
    setProperty("edition", edition);
  }
  
  public String getIsbn() {
    return getProperty("isbn");
  }
  
  public void setIsbn(String isbn) {
    setProperty("isbn", isbn);
  }
  
  public int getPageCount() {
    return (Integer)getProperty("pageCount");
  }
  
  public void setPageCount(int pageCount) {
    setProperty("pageCount", pageCount);
  }

  public <T extends ASObject>T getIllustrator() {
    return getProperty("illustrator");
  }
  
  public void setIllustrator(ASObject illustrator) {
    setProperty("illustrator", illustrator);
  }
  
}
