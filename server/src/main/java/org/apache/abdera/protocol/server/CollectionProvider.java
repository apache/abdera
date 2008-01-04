/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.protocol.server;

import java.io.InputStream;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.impl.ResponseContextException;

public interface CollectionProvider {

  void begin(RequestContext request) throws ResponseContextException;
  
  void end(RequestContext request, ResponseContext response);
  
  String getTitle(RequestContext request);

  ResponseContext getFeed(Feed feed, RequestContext request);

  ResponseContext createEntry(RequestContext request);

  ResponseContext getMedia(RequestContext request);

  ResponseContext deleteEntry(RequestContext request);

  ResponseContext getEntry(RequestContext request, IRI entryBaseIri);

  ResponseContext getFeed(RequestContext request);

  ResponseContext updateEntry(RequestContext request, IRI entryBaseIri);

}
