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
package org.apache.abdera.examples.ext;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadHelper;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

/**
 * The Atom Threading Extensions are described in RFC4685 and provide a means of representing threaded discussions and
 * parent/child relationships in Atom.
 */
public class Thread {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();
        Entry e1 = abdera.newEntry();
        Entry e2 = abdera.newEntry();

        e1.newId();
        e2.newId();

        // Entry e2 is a reply to Entry e1
        ThreadHelper.addInReplyTo(e2, e1);

        // Get the in-reply-to information
        InReplyTo irt = ThreadHelper.getInReplyTo(e2);
        System.out.println(irt.getRef());

        // Add a link to a feed containing replies to e1
        Link replies = e1.addLink("replies.xml", Link.REL_REPLIES);

        // Set the known number of replies as an attribute on the link
        ThreadHelper.setCount(replies, 10);

        // alternatively, use the thr:total element to specify the number of replies
        ThreadHelper.addTotal(e1, 10);

    }

}
