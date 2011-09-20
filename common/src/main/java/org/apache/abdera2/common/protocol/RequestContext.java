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
package org.apache.abdera2.common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.security.auth.Subject;

import org.apache.abdera2.common.iri.IRI;

/**
 * The RequestContext provides access to every detail of the Request.
 */
public interface RequestContext
  extends Request, Iterable<RequestContext.Property> {

    /**
     * RequestContext attributes can either have Session or Request scope. Request scope attributes are only valid
     * within the context of the current request. Session scope attributes, however, will remain valid as long as the
     * Session is active. Container scope attributes are set on the Web container (e.g. the ServletContext)
     */
    public enum Scope {
        REQUEST, SESSION, CONTAINER
    };

    /**
     * Special properties provided by the server
     */
    public enum Property {
        SESSIONID, 
        SESSIONCREATED, 
        SESSIONACCESSED, 
        SESSIONTIMEOUT, 
        CHARACTERENCODING, 
        LOCALES, 
        PROTOCOL, 
        REMOTEADDRESS, 
        REMOTEHOST, 
        REMOTEUSER, 
        SCHEME, 
        PRINCIPAL, 
        AUTHTYPE, 
        CONTENTLENGTH, 
        CONTENTTYPE, 
        CONTEXTPATH, 
        LOCALADDR, 
        LOCALNAME, 
        SERVERNAME, 
        SERVERPORT, 
        SECURE, 
        PARTS,
    };

    /**
     * Get the Provider associated with this request
     */
    <P extends Provider>P getProvider();

    /**
     * Get this requests resolved Target
     */
    Target getTarget();

    /**
     * Get this requests resolved Subject
     */
    Subject getSubject();

    /**
     * Get this requests authenticated Principal object
     */
    Principal getPrincipal();

    /**
     * Get the client's preferred locale as specified in the request
     */
    Locale getPreferredLocale();

    /**
     * Get a listing of the client's preferred locales as specified in the request. The listing will be sorted in order
     * of preference.
     */
    Iterable<Locale> getPreferredLocales();

    /**
     * Get the HTTP method
     */
    String getMethod();

    /**
     * Get the request URI
     */
    IRI getUri();

    /**
     * Get the absolute request URI (includes server name, port, etc)
     */
    IRI getResolvedUri();

    /**
     * Get the absolute base URI ... this is the request URI up to the Context Path of the web application within which
     * the Abdera Servlet is deployed
     */
    IRI getBaseUri();

    /**
     * Get the specified system property
     */
    <T>T getProperty(Property property);

    /**
     * Get the specified request parameter
     */
    String getParameter(String name);

    /**
     * Return the listing of parameter names
     */
    Iterable<String> getParameterNames();

    /**
     * Return all the values for the specified parameter
     */
    List<String> getParameters(String name);

    /**
     * Get the named attribute from the specified scope
     */
    Object getAttribute(Scope scope, String name);

    /**
     * Return the list of attribute names in the specified scope
     */
    Iterable<String> getAttributeNames(Scope scope);

    /**
     * Set the named attribute in the request scope
     */
    <T extends RequestContext>T setAttribute(String name, Object value);

    /**
     * Set the named attribute in the specified scope. If Session scope is specific, a new session will be created if
     * one does not already exist
     */
    <T extends RequestContext>T setAttribute(Scope scope, String name, Object value);

    /**
     * Get the InputStream containing the request entity
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get a Reader containing the request entity
     */
    Reader getReader() throws IOException;
    
    /**
     * Check to see if the authenticated user is in the specified role
     */
    boolean isUserInRole(String role);

    /**
     * Return the web applications context path
     */
    String getContextPath();

    /**
     * Returns the subset of the request URI that is to be used to resolve the Target (everything after the context
     * path)
     */
    String getTargetPath();

    /**
     * Returns the subset of the request URI that is the base of the target path (e.g.
     * HttpServletRequest.getServletPath())
     * 
     * @return
     */
    String getTargetBasePath();

    /**
     * Construct a URL using the Provider's Target Builder
     */
    String urlFor(Object key, Object param);

    /**
     * Construct an absolute URL using the Provider's Target Builder. Relative URL's are resolved against the base URI
     */
    String absoluteUrlFor(Object key, Object param);

}
