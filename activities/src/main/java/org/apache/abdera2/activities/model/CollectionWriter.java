package org.apache.abdera2.activities.model;

public interface CollectionWriter {

  void writeHeader(ASBase base);
  
  void writeObject(ASObject object);
  
  void complete();
  
}
