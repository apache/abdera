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

import org.apache.abdera.model.Categories;

/**
 * Metadata interface used by WorkspaceManager and Provider implementations to construct Atompub Service Documents. The
 * CategoriesInfo interface provides information used to construct an app:categories element within an app:collection.
 */
public interface CategoriesInfo extends Iterable<CategoryInfo> {

    /**
     * Return true of the app:categories element fixed attribute should be set
     */
    boolean isFixed(RequestContext request);

    /**
     * Return the value of the app:categories element scheme attribute or null if the scheme should be omitted
     */
    String getScheme(RequestContext request);

    /**
     * Return the value of the app:categories element href attribute or null if the href should be omitted
     */
    String getHref(RequestContext request);

    /**
     * Convert this into an instance of the FOM Categories interface
     */
    Categories asCategoriesElement(RequestContext request);
}
