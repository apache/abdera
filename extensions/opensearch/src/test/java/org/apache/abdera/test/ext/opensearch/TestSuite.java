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
package org.apache.abdera.test.ext.opensearch;

import org.apache.abdera.test.ext.opensearch.model.OpenSearchAtomTest;
import org.apache.abdera.test.ext.opensearch.model.OpenSearchDescriptionTest;
import org.apache.abdera.test.ext.opensearch.model.TestSelectNodes;
import org.apache.abdera.test.ext.opensearch.server.impl.AbstractOpenSearchUrlAdapterTest;
import org.apache.abdera.test.ext.opensearch.server.impl.SimpleOpenSearchInfoTest;
import org.apache.abdera.test.ext.opensearch.server.processors.OpenSearchDescriptionRequestProcessorTest;
import org.apache.abdera.test.ext.opensearch.server.processors.OpenSearchUrlRequestProcessorTest;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class TestSuite {
    public static void main(String[] args) {
        JUnitCore runner = new JUnitCore();
        runner.addListener(new TextListener(System.out));
        runner.run(OpenSearchAtomTest.class,
                   OpenSearchDescriptionTest.class,
                   AbstractOpenSearchUrlAdapterTest.class,
                   SimpleOpenSearchInfoTest.class,
                   OpenSearchDescriptionRequestProcessorTest.class,
                   OpenSearchUrlRequestProcessorTest.class,
                   TestSelectNodes.class);
    }
}
