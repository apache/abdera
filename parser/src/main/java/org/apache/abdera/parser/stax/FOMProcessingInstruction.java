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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ProcessingInstruction;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;

@SuppressWarnings("unchecked")
public class FOMProcessingInstruction extends OMProcessingInstructionImpl implements ProcessingInstruction {

    public FOMProcessingInstruction(OMContainer parent, String target, String value,
            OMFactory factory, boolean fromBuilder) {
        super(parent, target, value, factory, fromBuilder);
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
        return (Factory)this.getOMFactory();
    }

    public String getText() {
        return getValue();
    }

    public <T extends ProcessingInstruction> T setText(String text) {
        setValue(text);
        return (T)this;
    }

    public String toString() {
        java.io.CharArrayWriter w = new java.io.CharArrayWriter();
        try {
            super.serialize(w);
        } catch (Exception e) {
        }
        return w.toString();
    }

    public void setTarget(String target) {
        super.setTarget(target);
    }

}
