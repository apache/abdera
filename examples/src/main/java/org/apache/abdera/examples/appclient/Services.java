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

import java.io.FileInputStream;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.gdata.GoogleLoginAuthCredentials;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;

public class Services {
    /**
     * Posting to Blogger Beta
     */
    public static void postToBlogger() throws Exception {

        Abdera abdera = new Abdera();

        // Create the entry that will be posted
        Factory factory = abdera.getFactory();
        Entry entry = factory.newEntry();
        entry.setId(FOMHelper.generateUuid());
        entry.setUpdated(new java.util.Date());
        entry.addAuthor("James");
        entry.setTitle("Posting to Blogger");
        entry.setContentAsXhtml("<p>This is an example post to the new blogger beta</p>");

        // Initialize the client
        AbderaClient abderaClient = new AbderaClient(abdera);

        // Get and set the GoogleLogin authentication token
        GoogleLoginAuthCredentials creds = new GoogleLoginAuthCredentials("username", "password", "blogger");
        abderaClient.addCredentials("http://beta.blogger.com", null, "GoogleLogin", creds);

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        Response response =
            abderaClient.post("http://beta.blogger.com/feeds/7352231422284704069/posts/full", entry, options);

        // Check the response.
        if (response.getStatus() == 201)
            System.out.println("Success!");
        else
            System.out.println("Failed!");

    }

    /**
     * Posting to Roller
     */
    public static void postToRoller() throws Exception {

        // Set the URI of the Introspection document
        String start = "http://example.org/app";

        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();

        // Create the entry that will be posted
        Entry entry = factory.newEntry();
        entry.setId(FOMHelper.generateUuid());
        entry.setUpdated(new java.util.Date());
        entry.addAuthor("James");
        entry.setTitle("Posting to Roller");
        entry.setContentAsHtml("<p>This is an example post to Roller</p>");

        // Initialize the client and set the authentication credentials
        AbderaClient abderaClient = new AbderaClient(abdera);
        abderaClient.addCredentials(start, null, null, new UsernamePasswordCredentials("username", "password"));

        // Get the service document and look up the collection uri
        Document<Service> service_doc = abderaClient.get(start).getDocument();
        Service service = service_doc.getRoot();
        Collection collection = service.getWorkspaces().get(0).getCollections().get(0);
        String uri = collection.getHref().toString();

        // Post the entry to the collection
        Response response = abderaClient.post(uri, entry);

        // Check the result
        if (response.getStatus() == 201)
            System.out.println("Success!");
        else
            System.out.println("Failed!");
    }

    /**
     * Posting a Podcast to Roller
     */
    public static void postMediaToRoller() throws Exception {

        // Set the introspection document
        String start = "http://example.org/app";

        Abdera abdera = new Abdera();

        // Prepare the media resource to be sent
        FileInputStream fis = new FileInputStream("mypodcast.mp3");
        InputStreamRequestEntity re = new InputStreamRequestEntity(fis, "audio/mp3");

        // Initialize the client and set the auth credentials
        AbderaClient abderaClient = new AbderaClient(abdera);
        abderaClient.addCredentials(start, null, null, new UsernamePasswordCredentials("username", "password"));

        // Get the service doc and locate the href of the collection
        Document<Service> service_doc = abderaClient.get(start).getDocument();
        Service service = service_doc.getRoot();
        Collection collection = service.getWorkspaces().get(0).getCollections().get(1);
        String uri = collection.getHref().toString();

        // Set the filename. Note: the Title header was used by older drafts
        // of the Atom Publishing Protocol and should no longer be used. The
        // current Roller APP implementation still currently requires it.
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setHeader("Title", "mypodcast.mp3");

        // Post the entry
        Response response = abderaClient.post(uri, re, options);

        // Check the response
        if (response.getStatus() == 201)
            System.out.println("Success!");
        else
            System.out.println("Failed!");
    }

    /**
     * Post to Google Calendar
     */
    public static void postToCalendar() throws Exception {

        Abdera abdera = new Abdera();

        // Prepare the entry
        Factory factory = abdera.getFactory();
        Entry entry = factory.newEntry();
        entry.setId(FOMHelper.generateUuid());
        entry.setUpdated(new java.util.Date());
        entry.addAuthor("James");
        entry.setTitle("New Calendar Event");
        entry.setContentAsXhtml("<p>A new calendar event</p>");

        // Add the Google Specific extensions
        ((Element)entry.addExtension(new QName("http://schemas.google.com/g/2005", "transparency")))
            .setAttributeValue("value", "http://schemas.google.com/g/2005#event.opaque");
        ((Element)entry.addExtension(new QName("http://schemas.google.com/g/2005", "eventStatus")))
            .setAttributeValue("value", "http://schemas.google.com/g/2005#event.confirmed");
        ((Element)entry.addExtension(new QName("http://schemas.google.com/g/2005", "where")))
            .setAttributeValue("valueString", "Rolling Lawn Courts");
        Element el = entry.addExtension(new QName("http://schemas.google.com/g/2005", "when"));
        el.setAttributeValue("startTime", AtomDate.valueOf(new Date()).toString());
        el.setAttributeValue("endTime", AtomDate.valueOf(new Date()).toString());

        // Prepare the client
        AbderaClient abderaClient = new AbderaClient(abdera);

        // Get and set the GoogleLogin auth token
        GoogleLoginAuthCredentials creds = new GoogleLoginAuthCredentials("username", "password", "cl");
        abderaClient.addCredentials("http://www.google.com/calendar", null, "GoogleLogin", creds);

        String uri = "http://www.google.com/calendar/feeds/default/private/full";

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        Response response = abderaClient.post(uri, entry, options);

        // Google Calendar might return a 302 response with a new POST URI.
        // If it does, get the new URI and post again
        if (response.getStatus() == 302) {
            uri = response.getLocation().toString();
            response = abderaClient.post(uri, entry, options);
        }

        // Check the response
        if (response.getStatus() == 201)
            System.out.println("Success!");
        else
            System.out.println("Failed!");

    }
}
