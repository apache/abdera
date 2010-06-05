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
package org.apache.abdera.model;

/**
 * <p>
 * Represents an Atom Publishing Protocol <code>control</code> element.
 * </p>
 * <p>
 * The purpose of the control extension is to provide a means for content publishers to embed various
 * publishing-operation specific control parameters within the body of the entry. For instance, the client may wish to
 * mark the entry as a draft, or may wish to ask the publishing server to enable or disable specific extended features
 * for a given entry.
 * </p>
 * <p>
 * Per APP Draft-08:
 * </p>
 * 
 * <pre>
 *   pubControl =
 *      element pub:control {
 *      atomCommonAttributes,
 *      pubDraft?
 *      &amp; extensionElement
 *   }
 * 
 *   pubDraft =
 *     element pub:draft { "yes" | "no" }
 * 
 *  The "pub:control" element MAY appear as a child of an "atom:entry"
 *  which is being created or updated via the Atom Publishing Protocol.
 *  The "pub:control" element, if it does appear in an entry, MUST only
 *  appear at most one time.  The "pub:control" element is considered
 *  foreign markup as defined in Section 6 of [RFC4287].
 * 
 *  The "pub:control" element and its child elements MAY be included in
 *  Atom Feed or Entry Documents.
 * 
 *  The "pub:control" element MAY contain exactly one "pub:draft" element
 *  as defined here, and MAY contain zero or more extension elements as
 *  outlined in Section 6 of [RFC4287].  Both clients and servers MUST
 *  ignore foreign markup present in the pub:control element.
 * </pre>
 */
public interface Control extends ExtensibleElement {

    /**
     * <p>
     * Returns true if the entry should <i>not</i> be made publicly visible.
     * </p>
     * <p>
     * APP Draft-08: The number of "pub:draft" elements in "pub:control" MUST be zero or one. Its value MUST be one of
     * "yes" or "no". A value of "no" means that the entry MAY be made publicly visible. If the "pub:draft" element is
     * missing then the value MUST be understood to be "no". The pub:draft element MAY be ignored.
     * </p>
     * 
     * @return True if this is a draft
     */
    boolean isDraft();

    /**
     * <p>
     * Set to "true" if the entry should <i>not</i> be made publicly visible.
     * </p>
     * <p>
     * APP Draft-08: The number of "pub:draft" elements in "pub:control" MUST be zero or one. Its value MUST be one of
     * "yes" or "no". A value of "no" means that the entry MAY be made publicly visible. If the "pub:draft" element is
     * missing then the value MUST be understood to be "no". The pub:draft element MAY be ignored.
     * </p>
     * 
     * @param draft true if app:draft should be set to "yes"
     */
    Control setDraft(boolean draft);

    /**
     * <p>
     * Removes the draft setting completely from the control element.
     * </p>
     * <p>
     * APP Draft-08: The number of "pub:draft" elements in "pub:control" MUST be zero or one. Its value MUST be one of
     * "yes" or "no". A value of "no" means that the entry MAY be made publicly visible. If the "pub:draft" element is
     * missing then the value MUST be understood to be "no". The pub:draft element MAY be ignored.
     * </p>
     */
    Control unsetDraft();

}
