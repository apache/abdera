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
import org.apache.abdera.model.Comment;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.OMCommentImpl;

@SuppressWarnings("unchecked")
public class FOMComment extends OMCommentImpl implements Comment {

    public FOMComment(OMContainer parent, String contentText,
            OMFactory factory, boolean fromBuilder) {
        super(parent, contentText, factory, fromBuilder);
    }

    public String getText() {
        return super.getValue();
    }

    public Comment setText(String text) {
        super.setValue(text);
        return this;
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

    public String toString() {
        java.io.CharArrayWriter w = new java.io.CharArrayWriter();
        try {
            super.serialize(w);
        } catch (Exception e) {
        }
        return w.toString();
    }
}
