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
package org.apache.abdera.ext.oauth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OAuthSchemeTest {

    OAuthScheme scheme = new OAuthScheme();
    OAuthCredentials credentials;

    @Test
    public void testAuthenticate() throws Exception {

        credentials =
            new OAuthCredentials("dpf43f3p2l4k3l03", "nnch734d00sl2jdk", "HMAC-SHA1", "http://photos.example.net/");

        String header = scheme.authenticate(credentials, "get", "http://photos.example.net?file=vacation.jpg");
        assertNotNull(header);

        String regex =
            "OAuth realm=\"[^\"]+\", oauth_consumer_key=\"[^\"]+\", " + "oauth_token=\"[^\"]+\", oauth_signature_method=\"[^\"]+\", oauth_signature=\"[^\"]+\", "
                + "oauth_timestamp=\"[^\"]+\", oauth_nonce=\"[^\"]+\"(, oauth_version=\"[^\"]+\")?";

        assertTrue(header.matches(regex));

    }

}
