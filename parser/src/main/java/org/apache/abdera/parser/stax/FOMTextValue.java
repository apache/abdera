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

    public FOMTextValue(Object arg0, boolean arg1, OMFactory arg2) {
        super(arg0, arg1, arg2);
    }

    public FOMTextValue(Object arg0, OMFactory arg1) {
        super(arg0, arg1);
    }

    public FOMTextValue(OMContainer arg0, char[] arg1, int arg2, OMFactory arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public FOMTextValue(OMContainer arg0, QName arg1, int arg2, OMFactory arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public FOMTextValue(OMContainer arg0, QName arg1, OMFactory arg2) {
        super(arg0, arg1, arg2);
    }

    public FOMTextValue(OMContainer arg0, String arg1, int arg2, OMFactory arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public FOMTextValue(OMContainer arg0, String arg1, OMFactory arg2) {
        super(arg0, arg1, arg2);
    }

    public FOMTextValue(OMContainer arg0, String arg1, String arg2, boolean arg3, OMFactory arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public FOMTextValue(String arg0, int arg1, OMFactory arg2) {
        super(arg0, arg1, arg2);
    }

    public FOMTextValue(String arg0, OMContainer arg1, OMXMLParserWrapper arg2, OMFactory arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public FOMTextValue(String arg0, OMFactory arg1) {
        super(arg0, arg1);
    }

    public FOMTextValue(String arg0, String arg1, boolean arg2, OMFactory arg3) {
        super(arg0, arg1, arg2, arg3);
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
