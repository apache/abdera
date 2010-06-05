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

import org.apache.commons.httpclient.Credentials;

/**
 * <p>
 * When using the GoogleLoginAuthScheme with the typical Commons AbderaClient UsernamePasswordCredentials, the
 * AuthScheme implementation will request a new auth token from the Google server for every request. To make it a more
 * efficient, clients can use GoogleLoginAuthCredentials which will perform the Google Auth once to get the auth token
 * which will be reused for every request.
 * </p>
 * 
 * <pre>
 * GoogleLoginAuthScheme.register();
 * 
 * AbderaClient client = new CommonsClient();
 * 
 * GoogleLoginAuthCredentials credentials = new GoogleLoginAuthCredentials(&quot;email&quot;, &quot;password&quot;, &quot;blogger&quot;);
 * client.addCredentials(&quot;http://beta.blogger.com&quot;, null, null, credentials);
 * </pre>
 */
public final class GoogleLoginAuthCredentials implements Credentials {

    private final String auth;
    private final String service;

    public GoogleLoginAuthCredentials(String auth) {
        this.auth = auth;
        this.service = null;
    }

    public GoogleLoginAuthCredentials(String id, String pwd, String service) {
        this.auth = (new GoogleLoginAuthScheme()).getAuth(id, pwd, service);
        this.service = service;
    }

    public String getAuth() {
        return auth;
    }

    public String getService() {
        return service;
    }
}
