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
package org.apache.abdera.util.filter;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.namespace.QName;

import org.junit.Test;

public class AbstractListParseFilterTest {

    private static final class SerializedImpl extends AbstractListParseFilter {

        private static final long serialVersionUID = -1695184231548373283L;

        @Override
        public boolean acceptable(QName qname, QName attribute) {
            return true;
        }

        @Override
        public boolean acceptable(QName qname) {
            return true;
        }

    }

    @Test
    public void testSerialization() throws Exception {
        SerializedImpl si = new SerializedImpl();
        si.add(new QName("lp0"));
        si.add(new QName("parentLp0"), new QName("lp1"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(si);
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        si = (SerializedImpl)ois.readObject();
        assertTrue(si.contains(new QName("lp0")));
        assertTrue(si.contains(new QName("parentLp0"), new QName("lp1")));
    }

}
