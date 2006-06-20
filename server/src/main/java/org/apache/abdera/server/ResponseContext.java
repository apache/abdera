package org.apache.abdera.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

import javax.activation.MimeType;

public interface ResponseContext {

  int getStatus();
  
  String getStatusText();
  
  Date getLastModified();
  
  String getEntityTag();
  
  String getContentLanguage();
  
  URI getContentLocation();
   
  MimeType getContentType();
  
  URI getLocation();
  
  CachePolicy getCachePolicy();
  
  boolean hasEntity();
  
  void writeTo(OutputStream out) throws IOException;
  
}