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
package org.apache.abdera.ext.media;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class MediaRestriction extends ElementWrapper {

    public MediaRestriction(Element internal) {
        super(internal);
    }

    public MediaRestriction(Factory factory) {
        super(factory, MediaConstants.RESTRICTION);
    }

    public void setRelationship(MediaConstants.Relationship type) {
        switch (type) {
            case ALLOW:
                setAttributeValue("relationship", "allow");
                break;
            case DENY:
                setAttributeValue("relationship", "deny");
                break;
            default:
                removeAttribute(new QName("relationship"));
        }
    }

    public MediaConstants.Relationship getRelationship() {
        String rel = getAttributeValue("relationship");
        return (rel != null) ? MediaConstants.Relationship.valueOf(rel.toUpperCase()) : null;
    }

    public void setType(MediaConstants.RestrictionType type) {
        switch (type) {
            case COUNTRY:
                setAttributeValue("type", "country");
                break;
            case URI:
                setAttributeValue("type", "uri");
                break;
            default:
                removeAttribute(new QName("type"));
        }
    }

    public MediaConstants.RestrictionType getType() {
        String type = getAttributeValue("type");
        return (type != null) ? MediaConstants.RestrictionType.valueOf(type.toUpperCase()) : null;
    }
}
