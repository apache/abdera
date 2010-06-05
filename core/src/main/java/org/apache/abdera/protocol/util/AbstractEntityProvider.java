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
package org.apache.abdera.protocol.util;

import java.util.Date;

import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractEntityProvider implements EntityProvider {

    public String getContentType() {
        return "application/xml";
    }

    public EntityTag getEntityTag() {
        return null;
    }

    public Date getLastModified() {
        return null;
    }

}
