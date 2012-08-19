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
package org.apache.abdera.parser.stax;

import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDateTime extends FOMElement implements DateTime {

    private static final long serialVersionUID = -6611503566172011733L;
    private AtomDate value;

    protected FOMDateTime(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMDateTime(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMDateTime(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    public AtomDate getValue() {
        if (value == null) {
            String v = getText();
            if (v != null) {
                value = AtomDate.valueOf(v);
            }
        }
        return value;
    }

    public DateTime setValue(AtomDate dateTime) {
        complete();
        value = null;
        if (dateTime != null)
            ((Element)this).setText(dateTime.getValue());
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setDate(Date date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(AtomDate.valueOf(date).getValue());
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setCalendar(Calendar date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(AtomDate.valueOf(date).getValue());
        else
            _removeAllChildren();
        return this;
    }

    public DateTime setTime(long date) {
        complete();
        value = null;
        ((Element)this).setText(AtomDate.valueOf(date).getValue());
        return this;
    }

    public DateTime setString(String date) {
        complete();
        value = null;
        if (date != null)
            ((Element)this).setText(AtomDate.valueOf(date).getValue());
        else
            _removeAllChildren();
        return this;
    }

    public Date getDate() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getDate() : null;
    }

    public Calendar getCalendar() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getCalendar() : null;
    }

    public long getTime() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getTime() : null;
    }

    public String getString() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getValue() : null;
    }

}
