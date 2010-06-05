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
package org.apache.abdera.test.security;

import java.security.Provider;
import java.security.Security;

import org.apache.abdera.test.security.filter.SecurityFilterTest;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class TestSuite {

    public static void main(String[] args) throws Exception {
        Security.addProvider(getProvider(args[0]));
        JUnitCore runner = new JUnitCore();
        runner.addListener(new TextListener(System.out));
        runner.run(DigitalSignatureTest.class, EncryptionTest.class, SecurityFilterTest.class);
    }

    private static Provider getProvider(String provider) throws Exception {
        return (Provider)Class.forName(provider).newInstance();
    }
}
