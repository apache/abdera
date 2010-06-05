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
package org.apache.abdera.examples.simple;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.filter.ListParseFilter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.util.filter.BlackListParseFilter;

public class UnacceptableElementsExample {

    public static void main(String[] args) throws Exception {

        Parser parser = Abdera.getNewParser();

        /**
         * By subclassing BlackListParseFilter, we can throw an error when the parsed XML contains any content we don't
         * want
         */
        ListParseFilter exceptionFilter = new BlackListParseFilter() {
            private static final long serialVersionUID = 7564587859561916928L;

            @Override
            public boolean acceptable(QName qname) {
                boolean answer = super.acceptable(qname);
                if (!(answer)) {
                    throw new FOMException("Unacceptable element ::" + qname);
                }
                return answer;
            }

            @Override
            public boolean acceptable(QName qname, QName attribute) {
                return true;
            }
        };
        exceptionFilter.add(new QName("http://example.org", "a"));

        ParserOptions options = parser.getDefaultParserOptions();
        options.setParseFilter(exceptionFilter);
        Document<Feed> doc =
            parser.parse(UnacceptableElementsExample.class.getResourceAsStream("/xmlcontent.xml"), null, options);

        // this will throw a FOMException
        doc.writeTo(System.out);

    }

}
