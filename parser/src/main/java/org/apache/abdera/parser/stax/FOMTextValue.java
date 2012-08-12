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
package org.apache.abdera.parser.stax;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.TextValue;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMTextImpl;

@SuppressWarnings("unchecked")
public class FOMTextValue extends OMTextImpl implements TextValue {

    public FOMTextValue(Object dataHandler, boolean optimize, OMFactory factory) {
        super(dataHandler, optimize, factory);
    }

    public FOMTextValue(Object dataHandler, OMFactory factory) {
        super(dataHandler, factory);
    }

    public FOMTextValue(OMContainer parent, char[] charArray, int nodeType, OMFactory factory) {
        super(parent, charArray, nodeType, factory);
    }

    public FOMTextValue(OMContainer parent, QName text, int nodeType, OMFactory factory) {
        super(parent, text, nodeType, factory);
    }

    public FOMTextValue(OMContainer parent, QName text, OMFactory factory) {
        super(parent, text, factory);
    }

    public FOMTextValue(OMContainer parent, String text, int nodeType, OMFactory factory) {
        super(parent, text, nodeType, factory);
    }

    public FOMTextValue(OMContainer parent, String text, OMFactory factory) {
        super(parent, text, factory);
    }

    public FOMTextValue(OMContainer parent, String s, String mimeType, boolean optimize, OMFactory factory) {
        super(parent, s, mimeType, optimize, factory);
    }

    public FOMTextValue(String text, int nodeType, OMFactory factory) {
        super(text, nodeType, factory);
    }

    public FOMTextValue(String contentID, OMContainer parent, OMXMLParserWrapper builder, OMFactory factory) {
        super(contentID, parent, builder, factory);
    }

    public FOMTextValue(String text, OMFactory factory) {
        super(text, factory);
    }

    public FOMTextValue(String s, String mimeType, boolean optimize, OMFactory factory) {
        super(s, mimeType, optimize, factory);
    }

    public DataHandler getDataHandler() {
        return (DataHandler)super.getDataHandler();
    }

    public <T extends Base> T getParentElement() {
        T parent = (T)super.getParent();
        return (T)((parent instanceof Element) ? getWrapped((Element)parent) : parent);
    }

    protected Element getWrapped(Element internal) {
        if (internal == null)
            return null;
        FOMFactory factory = (FOMFactory)getFactory();
        return factory.getElementWrapper(internal);
    }

    public Factory getFactory() {
        return (Factory)this.factory;
    }

    @Override
    public String toString() {
        return getText();
    }

}
