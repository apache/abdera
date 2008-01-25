package org.apache.abdera.protocol.server;


/**
 * The TargetBuilder component is responsible for constructing appropriate 
 * IRIs/URIs for various kinds of targets based on specified input parameters.
 * The input params are specific to the Target Manager implementation.
 *
 */
public interface TargetBuilder {

  String resolveIri(RequestContext context, Object key, Object param);
  
}
