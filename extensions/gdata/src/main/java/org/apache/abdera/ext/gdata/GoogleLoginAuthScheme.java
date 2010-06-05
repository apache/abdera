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
package org.apache.abdera.ext.gdata;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Formatter;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * <p>
 * Implements the GoogleLogin auth scheme used by gdata (Blogger, Google Calendar, etc). Warning: this scheme is slow!
 * </p>
 * 
 * <pre>
 * GoogleLoginAuthScheme.register();
 * 
 * AbderaClient client = new CommonsClient();
 * client.addCredentials(&quot;http://beta.blogger.com&quot;, null, &quot;GoogleLogin&quot;, new UsernamePasswordCredentials(&quot;email&quot;,
 *                                                                                                       &quot;password&quot;));
 * </pre>
 */
public class GoogleLoginAuthScheme extends RFC2617Scheme implements AuthScheme {

    public static void register(AbderaClient abderaClient, boolean exclusive) {
        AbderaClient.registerScheme("GoogleLogin", GoogleLoginAuthScheme.class);
        if (exclusive)
            ((AbderaClient)abderaClient).setAuthenticationSchemePriority("GoogleLogin");
        else
            ((AbderaClient)abderaClient).setAuthenticationSchemeDefaults();
    }

    private String service = null;

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {
        super.processChallenge(challenge);
        service = getParameter("service");
    }

    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        String auth = null;
        if (credentials instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials usercreds = (UsernamePasswordCredentials)credentials;
            String id = usercreds.getUserName();
            String pwd = usercreds.getPassword();
            auth = getAuth(id, pwd);
        } else if (credentials instanceof GoogleLoginAuthCredentials) {
            GoogleLoginAuthCredentials gcreds = (GoogleLoginAuthCredentials)credentials;
            service = gcreds.getService();
            auth = gcreds.getAuth();
        } else {
            throw new AuthenticationException("Cannot use credentials for GoogleLogin authentication");
        }
        StringBuffer buf = new StringBuffer("GoogleLogin ");
        buf.append(auth);
        return buf.toString();
    }

    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        return authenticate(credentials, null);
    }

    public String getSchemeName() {
        return "GoogleLogin";
    }

    public boolean isComplete() {
        return true;
    }

    public boolean isConnectionBased() {
        return false;
    }

    protected String getAuth(String id, String pwd) {
        return getAuth(id, pwd, service);
    }

    protected String getAuth(String id, String pwd, String service) {
        try {
            AbderaClient abderaClient = new AbderaClient();
            Formatter f = new Formatter();
            f.format("Email=%s&Passwd=%s&service=%s&source=%s", URLEncoder.encode(id, "utf-8"), URLEncoder
                .encode(pwd, "utf-8"), (service != null) ? URLEncoder.encode(service, "utf-8") : "", URLEncoder
                .encode(Version.APP_NAME, "utf-8"));
            StringRequestEntity stringreq =
                new StringRequestEntity(f.toString(), "application/x-www-form-urlencoded", "utf-8");
            String uri = "https://www.google.com/accounts/ClientLogin";
            RequestOptions options = abderaClient.getDefaultRequestOptions();
            options.setContentType("application/x-www-form-urlencoded");
            ClientResponse response = abderaClient.post(uri, stringreq, options);
            InputStream in = response.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int n = -1;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
            out.flush();
            response.release();
            String auth = new String(out.toByteArray());
            return auth.split("\n")[2].replaceAll("Auth=", "auth=");
        } catch (Exception e) {
        }
        return null;
    }

    public static String getGoogleLogin(String id, String pwd, String service) {
        String auth = (new GoogleLoginAuthScheme()).getAuth(id, pwd, service);
        StringBuffer buf = new StringBuffer("GoogleLogin ");
        buf.append(auth);
        return buf.toString();
    }
}
