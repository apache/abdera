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

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.TextValue;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.CharacterDataImpl;

@SuppressWarnings("unchecked")
public class FOMCharacterData extends CharacterDataImpl implements TextValue {

    public FOMCharacterData(OMFactory factory) {
        super(factory);
    }

    public DataHandler getDataHandler() {
        return (DataHandler)super.getDataHandler();
    }

    public InputStream getInputStream() {
        try {
            return getDataHandler().getInputStream();
        } catch (IOException ex) {
            throw new FOMException(ex);
        }
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

    @Override
    public String toString() {
        return getText();
    }

}
