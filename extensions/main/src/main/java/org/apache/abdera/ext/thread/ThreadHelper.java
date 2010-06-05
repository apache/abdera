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
package org.apache.abdera.ext.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;
import org.apache.abdera.i18n.iri.IRI;

public final class ThreadHelper {

    ThreadHelper() {
    }

    public static int getCount(Link link) {
        String val = link.getAttributeValue(ThreadConstants.THRCOUNT);
        return (val != null) ? Integer.parseInt(val) : 0;
    }

    @SuppressWarnings("deprecation")
    public static AtomDate getUpdated(Link link) {
        String val = link.getAttributeValue(ThreadConstants.THRUPDATED);
        if (val == null) // thr:when was updated to thr:updated, some old impls may still be using thr:when
            val = link.getAttributeValue(ThreadConstants.THRWHEN);
        return (val != null) ? AtomDate.valueOf(val) : null;
    }

    public static void setCount(Link link, int count) {
        link.setAttributeValue(ThreadConstants.THRCOUNT, String.valueOf(count).trim());
    }

    public static void setUpdated(Link link, Date when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, Calendar when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, long when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, String when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static Total addTotal(Entry entry, int total) {
        Factory factory = entry.getFactory();
        Total totalelement = (Total)factory.newExtensionElement(ThreadConstants.THRTOTAL, entry);
        totalelement.setValue(total);
        return totalelement;
    }

    public static Total getTotal(Entry entry) {
        return entry.getFirstChild(ThreadConstants.THRTOTAL);
    }

    public static void addInReplyTo(Entry entry, InReplyTo replyTo) {
        entry.addExtension(replyTo);
    }

    public static InReplyTo addInReplyTo(Entry entry) {
        return entry.addExtension(ThreadConstants.IN_REPLY_TO);
    }

    public static InReplyTo addInReplyTo(Entry entry, Entry ref) {
        if (ref.equals(entry))
            return null;
        InReplyTo irt = addInReplyTo(entry);
        try {
            irt.setRef(ref.getId());
            Link altlink = ref.getAlternateLink();
            if (altlink != null) {
                irt.setHref(altlink.getResolvedHref());
                if (altlink.getMimeType() != null)
                    irt.setMimeType(altlink.getMimeType());
            }
            Source src = ref.getSource();
            if (src != null) {
                Link selflink = src.getSelfLink();
                if (selflink != null)
                    irt.setSource(selflink.getResolvedHref());
            }
        } catch (Exception e) {
        }
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, IRI ref) {
        try {
            if (entry.getId() != null && entry.getId().equals(ref))
                return null;
        } catch (Exception e) {
        }
        InReplyTo irt = addInReplyTo(entry);
        irt.setRef(ref);
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, String ref) {
        return addInReplyTo(entry, new IRI(ref));
    }

    public static InReplyTo addInReplyTo(Entry entry, IRI ref, IRI source, IRI href, MimeType type) {
        InReplyTo irt = addInReplyTo(entry, ref);
        if (irt != null) {
            if (source != null)
                irt.setSource(source);
            if (href != null)
                irt.setHref(href);
            if (type != null)
                irt.setMimeType(type);
        }
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, String ref, String source, String href, String type) {
        InReplyTo irt = addInReplyTo(entry, ref);
        if (irt != null) {
            if (source != null)
                irt.setSource(source);
            if (href != null)
                irt.setHref(href);
            if (type != null)
                irt.setMimeType(type);
        }
        return irt;
    }

    public static InReplyTo getInReplyTo(Entry entry) {
        return entry.getFirstChild(ThreadConstants.IN_REPLY_TO);
    }

    @SuppressWarnings("unchecked")
    public static List<InReplyTo> getInReplyTos(Entry entry) {
        List list = entry.getExtensions(ThreadConstants.IN_REPLY_TO);
        return list;
    }

    public static InReplyTo newInReplyTo(Factory factory) {
        return new InReplyTo(factory);
    }

    public static Total newTotal(Factory factory) {
        return new Total(factory);
    }

}
