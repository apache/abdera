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
package org.apache.abdera.ext.sharing;

import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class History extends ElementWrapper {

    public History(Element internal) {
        super(internal);
    }

    public History(Factory factory, QName qname) {
        super(factory, qname);
    }

    public int getSequence() {
        String sequence = getAttributeValue("sequence");
        return sequence != null ? Integer.parseInt(sequence) : 0;
    }

    public void setSequence(int sequence) {
        if (sequence > 0) {
            setAttributeValue("sequence", Integer.toString(sequence));
        } else {
            removeAttribute(new QName("sequence"));
        }
    }

    public Date getWhen() {
        String when = getAttributeValue("when");
        return when != null ? AtomDate.parse(when) : null;
    }

    public void setWhen(Date when) {
        if (when != null) {
            setAttributeValue("when", AtomDate.format(when));
        } else {
            removeAttribute(new QName("when"));
        }
    }

    public String getBy() {
        return getAttributeValue("by");
    }

    public void setBy(String by) {
        if (by != null) {
            if (!SharingHelper.isValidEndpointIdentifier(by))
                throw new IllegalArgumentException("Invalid Endpoint Identifier");
            setAttributeValue("by", by);
        } else {
            removeAttribute(new QName("by"));
        }
    }

}
