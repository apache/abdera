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
package org.apache.abdera.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void testDefaultConfigurationProperties() {
        Configuration config1 = new AbderaConfiguration();
        assertEquals("org.apache.abdera.parser.stax.FOMFactory", config1
            .getConfigurationOption(Constants.CONFIG_FACTORY, Constants.DEFAULT_FACTORY));
        assertEquals("org.apache.abdera.parser.stax.FOMParser", config1
            .getConfigurationOption(Constants.CONFIG_PARSER, Constants.DEFAULT_PARSER));
        assertEquals("org.apache.abdera.parser.stax.FOMXPath", config1.getConfigurationOption(Constants.CONFIG_XPATH,
                                                                                              Constants.DEFAULT_XPATH));

        Configuration config2 = AbderaConfiguration.getDefault();
        assertEquals("org.apache.abdera.parser.stax.FOMFactory", config2
            .getConfigurationOption(Constants.CONFIG_FACTORY, Constants.DEFAULT_FACTORY));
        assertEquals("org.apache.abdera.parser.stax.FOMParser", config2
            .getConfigurationOption(Constants.CONFIG_PARSER, Constants.DEFAULT_PARSER));
        assertEquals("org.apache.abdera.parser.stax.FOMXPath", config2.getConfigurationOption(Constants.CONFIG_XPATH,
                                                                                              Constants.DEFAULT_XPATH));

    }

}
