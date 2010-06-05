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
package org.apache.abdera.examples.unicode;

import org.apache.abdera.i18n.text.Normalizer;

/**
 * Example that demonstrates Abdera's basic support for Unicode normalization.
 */
public class NormalizationExample {

    public static void main(String... args) throws Exception {

        // Three different representations of the same character (Angstrom)
        String s1 = "\u00c5";
        String s2 = "\u0041\u030A";
        String s3 = "\u212B";

        System.out.println(s1 + "=" + s2 + " ?\t" + s1.equals(s2)); // false
        System.out.println(s1 + "=" + s3 + " ?\t" + s1.equals(s3)); // false
        System.out.println(s2 + "=" + s3 + " ?\t" + s2.equals(s3)); // false

        // Normalize to NFC
        String n1 = Normalizer.normalize(s1, Normalizer.Form.C);
        String n2 = Normalizer.normalize(s2, Normalizer.Form.C);
        String n3 = Normalizer.normalize(s3, Normalizer.Form.C);

        System.out.println(n1 + "=" + n2 + " ?\t" + n1.equals(n2)); // true
        System.out.println(n1 + "=" + n3 + " ?\t" + n1.equals(n3)); // true
        System.out.println(n2 + "=" + n3 + " ?\t" + n2.equals(n3)); // true

        // s1 is already normalized to NFC
        System.out.println(n1.equals(s1)); // true

    }

}
