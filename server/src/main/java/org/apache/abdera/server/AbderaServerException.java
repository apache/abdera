package org.apache.abdera.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

import javax.activation.MimeType;

@SuppressWarnings("serial")
// TODO: need to finish this
public class AbderaServerException 
  extends Exception 
  implements ResponseContext {

  private int status = 0;
  private String statusText = null;
  private Date lastModified = null;
  private String etag = null;
  private String language = null;
  private URI contentLocation = null;
  private MimeType contentType = null;
  private URI location = null;
  
  public AbderaServerException(int status, String text) {
    this.status = status;
    this.statusText = text;
  }
  
  public int getStatus() {
    return status;
  }

  public String getStatusText() {
    return statusText;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getEntityTag() {
    return etag;
  }

  public String getContentLanguage() {
    return language;
  }

  public URI getContentLocation() {
    return contentLocation;
  }

  public MimeType getContentType() {
    return contentType;
  }

  public URI getLocation() {
    return location;
  }

  public void writeTo(OutputStream out) throws IOException {
    // TODO
  }

  public CachePolicy getCachePolicy() {
    return null;
  }

  public boolean hasEntity() {
    return false;
  }

}
