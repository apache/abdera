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
package org.apache.abdera.ext.media;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class MediaHash extends ElementWrapper {

    public MediaHash(Element internal) {
        super(internal);
    }

    public MediaHash(Factory factory) {
        super(factory, MediaConstants.HASH);
    }

    public MediaConstants.Algo getAlgorithm() {
        String algo = getAttributeValue("algo");
        if (algo == null)
            return null;
        if (algo.equalsIgnoreCase("sha-1"))
            return MediaConstants.Algo.SHA1;
        if (algo.equalsIgnoreCase("md5"))
            return MediaConstants.Algo.MD5;
        return null;
    }

    public void setAlgorithm(MediaConstants.Algo algorithm) {
        switch (algorithm) {
            case SHA1:
                setAttributeValue("algo", "sha-1");
                break;
            case MD5:
                setAttributeValue("algo", "md5");
                break;
            default:
                removeAttribute(new QName("algo"));
        }
    }

    // TODO: Helper methods for calculating the hash
}
