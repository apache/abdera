package org.apache.abdera2.common.protocol;

public interface PatchMediaAdapter 
  extends MediaCollectionAdapter {

  /**
   * Post a new entry to the collection
   */
  <S extends ResponseContext>S patchMedia(RequestContext request);
  
}
