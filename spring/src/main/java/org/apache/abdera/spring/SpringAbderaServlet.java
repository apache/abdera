/**
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
package org.apache.abdera.spring;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Loads a Spring ServiceContext from a Spring WebApplicationContext. By default it looks for a bean with the name
 * "org.apache.abdera.protocol.server.ServiceContext". This can be overridden by supplying the "serviceContextBeanName"
 * initialization parameter.
 */
public class SpringAbderaServlet extends AbderaServlet {

    private static final long serialVersionUID = -7579564455804753809L;

    protected Provider createProvider() {
        String providerName = getInitParameter("providerBeanName");
        if (providerName == null) {
            providerName = Provider.class.getName();
        }

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        Provider p = (Provider)ctx.getBean(providerName);
        p.init(getAbdera(), getProperties(getServletConfig()));
        return p;
    }
}
