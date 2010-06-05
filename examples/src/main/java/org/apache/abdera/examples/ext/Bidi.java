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
import org.apache.abdera.ext.bidi.BidiHelper;
import org.apache.abdera.i18n.text.Bidi.Direction;
import org.apache.abdera.model.Entry;

/**
 * The Atom Bidi Extension is described in an IETF Internet-Draft and is used to communicate information about the base
 * directionality of text in an Atom document.
 */
public class Bidi {

    public static void main(String... args) throws Exception {

        String text = "\u05e4\u05e2\u05d9\u05dc\u05d5\u05ea \u05d4\u05d1\u05d9\u05e0\u05d0\u05d5\u05dd, W3C";

        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        BidiHelper.setDirection(Direction.RTL, entry);

        entry.setTitle(text);

        // non bidi, incorrectly displayed
        System.out.println(entry.getTitle());

        // with bidi, correctly displayed
        System.out.println(BidiHelper.getBidiElementText(entry.getTitleElement()));

        // with bidi, correctly displayed
        System.out.println(BidiHelper.getBidiText(BidiHelper.getDirection(entry), entry.getTitle()));

        // there are also direction guessing algorithms available
        entry = abdera.newEntry();
        entry.setTitle(text);
        entry.setLanguage("ar");

        System.out.println(BidiHelper.guessDirectionFromJavaBidi(entry.getTitleElement()));
        System.out.println(BidiHelper.guessDirectionFromTextProperties(entry.getTitleElement()));
        System.out.println(BidiHelper.guessDirectionFromLanguage(entry.getTitleElement()));
    }

}
