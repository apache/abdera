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
package org.apache.abdera.parser.stax.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

public class ResolveFunction implements Function {

    public static final QName QNAME = new QName("http://abdera.apache.org", "resolve");

    @SuppressWarnings("unchecked")
    public Object call(Context context, List args) throws FunctionCallException {
        List<String> results = new ArrayList<String>();
        if (args.isEmpty())
            return null;
        Navigator navigator = context.getNavigator();
        for (Object obj : args) {
            if (obj instanceof List) {
                for (Object o : (List)obj) {
                    try {
                        String value = StringFunction.evaluate(o, navigator);
                        IRI resolved = null;
                        IRI baseUri = null;
                        if (o instanceof OMNode) {
                            OMNode node = (OMNode)o;
                            OMContainer el = node.getParent();
                            if (el instanceof Document) {
                                Document<Element> doc = (Document<Element>)el;
                                baseUri = doc.getBaseUri();
                            } else if (el instanceof Element) {
                                Element element = (Element)el;
                                baseUri = element.getBaseUri();
                            }
                        } else if (o instanceof OMAttribute) {
                            OMAttribute attr = (OMAttribute)o;
                            Element element = (Element)context.getNavigator().getParentNode(attr);
                            baseUri = element.getBaseUri();
                        }
                        if (baseUri != null) {
                            resolved = baseUri.resolve(value);
                            results.add(resolved.toString());
                        }
                    } catch (Exception e) {
                    }
                }
            } else {
                // nothing to do
            }
        }
        if (results.size() == 1) {
            return results.get(0);
        } else if (results.size() > 1) {
            return results;
        } else
            return null;
    }
}
