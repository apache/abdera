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
package org.apache.abdera.ext.tombstones;

import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;

public class Tombstone extends ExtensibleElementWrapper {

    public Tombstone(Element internal) {
        super(internal);
    }

    public Tombstone(Factory factory) {
        super(factory, TombstonesHelper.DELETED_ENTRY);
    }

    public String getRef() {
        return getAttributeValue("ref");
    }

    public Tombstone setRef(String id) {
        if (id != null) {
            setAttributeValue("ref", id);
        } else {
            removeAttribute("ref");
        }
        return this;
    }

    public Tombstone setRef(IRI id) {
        return setRef(id.toString());
    }

    public Date getWhen() {
        String v = getAttributeValue("when");
        return v != null ? AtomDate.parse(v) : null;
    }

    public Tombstone setWhen(Date date) {
        return setWhen(AtomDate.format(date));
    }

    public Tombstone setWhen(String date) {
        if (date != null) {
            setAttributeValue("when", date);
        } else {
            removeAttribute("when");
        }
        return this;
    }

    public Tombstone setWhen(long date) {
        return setWhen(AtomDate.valueOf(date));
    }

    public Tombstone setWhen(Calendar date) {
        return setWhen(AtomDate.valueOf(date));
    }

    public Tombstone setWhen(AtomDate date) {
        return setWhen(date.toString());
    }

    public Person getBy() {
        return getExtension(TombstonesHelper.BY);
    }

    public Tombstone setBy(Person person) {
        if (getBy() != null)
            getBy().discard();
        addExtension(person);
        return this;
    }

    public Person setBy(String name) {
        return setBy(name, null, null);
    }

    public Person setBy(String name, String email, String uri) {
        if (name != null) {
            Person person = getFactory().newPerson(TombstonesHelper.BY, this);
            person.setName(name);
            person.setEmail(email);
            person.setUri(uri);
            return person;
        } else {
            if (getBy() != null)
                getBy().discard();
            return null;
        }
    }

    public Text getComment() {
        return getExtension(TombstonesHelper.COMMENT);
    }

    public Text setComment(String comment) {
        return setComment(Text.Type.TEXT, comment);
    }

    public Text setComment(Text.Type type, String comment) {
        if (comment != null) {
            Text text = getFactory().newText(TombstonesHelper.COMMENT, type, this);
            text.setValue(comment);
            return text;
        } else {
            if (getComment() != null)
                getComment().discard();
            return null;
        }
    }
}
