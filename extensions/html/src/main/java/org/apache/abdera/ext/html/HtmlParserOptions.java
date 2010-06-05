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
package org.apache.abdera.ext.html;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.AbstractParserOptions;

public class HtmlParserOptions extends AbstractParserOptions {

    private boolean fragment = false;

    @Override
    protected void checkFactory(Factory factory) {
        if (!(factory instanceof FOMFactory))
            throw new FOMException(Localizer.sprintf("WRONG.PARSER.INSTANCE", FOMFactory.class.getName()));
    }

    @Override
    protected void initFactory() {
        if (factory == null)
            factory = new FOMFactory();
    }

    public boolean isHtmlFragment() {
        return fragment;
    }

    public void setHtmlFragment(boolean fragment) {
        this.fragment = fragment;
    }
}
