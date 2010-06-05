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
package org.apache.abdera.ext.rss;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

public class RssSkipHours extends ExtensibleElementWrapper {

    public RssSkipHours(Element internal) {
        super(internal);
    }

    public RssSkipHours(Factory factory, QName qname) {
        super(factory, qname);
    }

    public boolean skip(int hour) {
        List<Element> hours = getExtensions(RssConstants.QNAME_HOUR);
        for (Element h : hours) {
            try {
                int check = Integer.parseInt(h.getText());
                if (check == hour)
                    return true;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
