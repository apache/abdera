package org.apache.abdera2.common.protocol;

public interface PatchAdapter 
  extends CollectionAdapter {

  /**
   * Post a new entry to the collection
   */
  <S extends ResponseContext>S patchItem(RequestContext request);
  
}
