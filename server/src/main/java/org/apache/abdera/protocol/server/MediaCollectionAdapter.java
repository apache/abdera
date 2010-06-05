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

/**
 * Extends CollectionAdapter with methods specific to the handling of Atompub Media Link Entries
 */
public interface MediaCollectionAdapter extends CollectionAdapter {

    /**
     * Add a new media resource to the collection, resulting in the creation of a new Media Link Entry.
     */
    ResponseContext postMedia(RequestContext request);

    /**
     * Delete a media resource from the collection
     */
    ResponseContext deleteMedia(RequestContext request);

    /**
     * Get a media resource
     */
    ResponseContext getMedia(RequestContext request);

    /**
     * Get metdata for a media resource
     */
    ResponseContext headMedia(RequestContext request);

    /**
     * Get a media resource's options.
     */
    ResponseContext optionsMedia(RequestContext request);

    /**
     * Update a media resource
     */
    ResponseContext putMedia(RequestContext request);

}
