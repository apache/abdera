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
package org.apache.abdera.examples.appclient;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.i18n.iri.IRI;

public class Main {

    public static void main(String[] args) throws Exception {

        Abdera abdera = new Abdera();
        AbderaClient abderaClient = new AbderaClient(abdera);
        Factory factory = abdera.getFactory();

        // Perform introspection. This is an optional step. If you already
        // know the URI of the APP collection to POST to, you can skip it.
        Document<Service> introspection = abderaClient.get(args[0]).getDocument();
        Service service = introspection.getRoot();
        Collection collection = service.getCollection(args[1], args[2]);
        report("The Collection Element", collection.toString());

        // Create the entry to post to the collection
        Entry entry = factory.newEntry();
        entry.setId("tag:example.org,2006:foo");
        entry.setTitle("This is the title");
        entry.setUpdated(new Date());
        entry.addAuthor("James");
        entry.setContent("This is the content");
        report("The Entry to Post", entry.toString());

        // Post the entry. Be sure to grab the resolved HREF of the collection
        Document<Entry> doc = abderaClient.post(collection.getResolvedHref().toString(), entry).getDocument();

        // In some implementations (such as Google's GData API, the entry URI is
        // distinct from it's edit URI. To be safe, we should assume it may be
        // different
        IRI entryUri = doc.getBaseUri();
        report("The Created Entry", doc.getRoot().toString());

        // Grab the Edit URI from the entry. The entry MAY have more than one
        // edit link. We need to make sure we grab the right one.
        IRI editUri = getEditUri(doc.getRoot());

        // If there is an Edit Link, we can edit the entry
        if (editUri != null) {
            // Before we can edit, we need to grab an "editable" representation
            doc = abderaClient.get(editUri.toString()).getDocument();

            // Change whatever you want in the retrieved entry
            doc.getRoot().getTitleElement().setValue("This is the changed title");

            // Put it back to the server
            abderaClient.put(editUri.toString(), doc.getRoot());

            // This is just to show that the entry has been modified
            doc = abderaClient.get(entryUri.toString()).getDocument();
            report("The Modified Entry", doc.getRoot().toString());
        } else {
            // Otherwise, the entry cannot be modified (no suitable edit link was found)
            report("The Entry cannot be modified", null);
        }

        // Delete the entry. Again, we need to make sure that we have the current
        // edit link for the entry
        doc = abderaClient.get(entryUri.toString()).getDocument();
        editUri = getEditUri(doc.getRoot());
        if (editUri != null) {
            abderaClient.delete(editUri.toString());
            report("The Enry has been deleted", null);
        } else {
            report("The Entry cannot be deleted", null);
        }
    }

    private static IRI getEditUri(Entry entry) throws Exception {
        IRI editUri = null;
        List<Link> editLinks = entry.getLinks("edit");
        for (Link link : editLinks) {
            // if there is more than one edit link, we should not automatically
            // assume that it's always going to point to an Atom document
            // representation.
            if (link.getMimeType() != null) {
                if (link.getMimeType().match("application/atom+xml")) {
                    editUri = link.getResolvedHref();
                    break;
                }
            } else { // assume that an edit link with no type attribute is the right one to use
                editUri = link.getResolvedHref();
                break;
            }
        }
        return editUri;
    }

    private static void report(String title, String message) {
        System.out.println("== " + title + " ==");
        if (message != null)
            System.out.println(message);
        System.out.println();
    }
}
