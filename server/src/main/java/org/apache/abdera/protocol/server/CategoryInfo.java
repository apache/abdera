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

import org.apache.abdera.model.Category;

/**
 * Metadata interface used by WorkspaceManager and Provider implementations to construct Atompub Service Documents. The
 * CategoryInfo interface provides information used to construct an atom:category element within an app:categories
 */
public interface CategoryInfo {

    /**
     * Return the value of the atom:category scheme attribute or null if the scheme should be omitted
     */
    String getScheme(RequestContext request);

    /**
     * Return the value of the atom:category term attribute. This value MUST be provided
     */
    String getTerm(RequestContext request);

    /**
     * Return the value of the atom:category label attribute or null if the label should be omitted. This value is
     * language-sensitive
     */
    String getLabel(RequestContext request);

    /**
     * Convert this into an instance of the FOM Category interface
     */
    Category asCategoryElement(RequestContext request);
}
