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

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;

public class FOMMetaFactory implements OMMetaFactory {
    private final OMFactory omFactory = new FOMFactory();
    private final SOAPFactory soap11Factory = new SOAP11Factory();
    private final SOAPFactory soap12Factory = new SOAP12Factory();
    
    public OMFactory getOMFactory() {
        return omFactory;
    }
    
    public SOAPFactory getSOAP11Factory() {
        return soap11Factory;
    }
    
    public SOAPFactory getSOAP12Factory() {
        return soap12Factory;
    }
}
