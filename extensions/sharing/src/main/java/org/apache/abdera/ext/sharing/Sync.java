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
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

public class Sync extends ExtensibleElementWrapper {

    public Sync(Element internal) {
        super(internal);
    }

    public Sync(Factory factory, QName qname) {
        super(factory, qname);
    }

    public String getId() {
        return getAttributeValue("id");
    }

    public void setId(String id) {
        if (id != null) {
            setAttributeValue("id", id);
        } else {
            removeAttribute(new QName("id"));
        }
    }

    public int getUpdates() {
        String updates = getAttributeValue("updates");
        return updates != null ? Integer.parseInt(updates) : 0;
    }

    public void setUpdates(int updates) {
        if (updates > 0) {
            setAttributeValue("updates", Integer.toString(updates));
        } else {
            removeAttribute(new QName("updates"));
        }
    }

    public void incrementUpdates() {
        setUpdates(getUpdates() + 1);
    }

    public boolean isDeleted() {
        String deleted = getAttributeValue("deleted");
        return deleted != null ? SharingHelper.isTrue(deleted) : false;
    }

    public void setDeleted(boolean deleted) {
        if (deleted) {
            setAttributeValue("deleted", "true");
        } else {
            removeAttribute(new QName("deleted"));
        }
    }

    public boolean isNoConflicts() {
        String noconflicts = getAttributeValue("noconflicts");
        return noconflicts != null ? SharingHelper.isTrue(noconflicts) : false;
    }

    public void setNoConflicts(boolean noconflicts) {
        if (noconflicts) {
            setAttributeValue("noconflicts", "true");
        } else {
            removeAttribute(new QName("noconflicts"));
        }
    }

    public List<History> getHistory() {
        return getExtensions(SharingHelper.SSE_HISTORY);
    }

    public void addHistory(History history) {
        this.addExtension(history);
    }

    public History addHistory() {
        return this.addExtension(SharingHelper.SSE_HISTORY, SharingHelper.SSE_HISTORY);
    }

    public History addHistory(int sequence, Date when, String by) {
        History history = addHistory();
        history.setSequence(sequence);
        history.setWhen(when);
        history.setBy(by);
        return history;
    }

    public History getTopmostHistory() {
        return this.getFirstChild(SharingHelper.SSE_HISTORY);
    }

    public Conflicts getConflicts() {
        return getConflicts(false);
    }

    public void setConflicts(Conflicts conflicts) {
        Conflicts con = getConflicts();
        if (con != null)
            con.discard();
        if (conflicts != null)
            addExtension(conflicts);
    }

    public Conflicts getConflicts(boolean create) {
        Conflicts con = getExtension(SharingHelper.SSE_CONFLICTS);
        if (con == null && create)
            con = getFactory().newElement(SharingHelper.SSE_CONFLICTS, this.getInternal());
        return con;
    }

}
