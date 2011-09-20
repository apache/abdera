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
package org.apache.abdera2.common.protocol;

import java.util.Map;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.ObjectContext;
import org.apache.abdera2.common.templates.TemplateManager;

public class TemplateManagerTargetBuilder<T> 
  extends TemplateManager<T>
  implements TargetBuilder<T> {

  public TemplateManagerTargetBuilder() {
    super();
  }

  public TemplateManagerTargetBuilder(boolean iri) {
    super(iri);
  }

  public TemplateManagerTargetBuilder(Context defaults) {
    super(defaults);
  }

  public TemplateManagerTargetBuilder(IRI base, boolean iri) {
    super(base, iri);
  }

  public TemplateManagerTargetBuilder(IRI base, Context defaults) {
    super(base, defaults);
  }

  public TemplateManagerTargetBuilder(IRI base, Object defaults, boolean isiri) {
    super(base, defaults, isiri);
  }

  public TemplateManagerTargetBuilder(IRI base, Object defaults) {
    super(base, defaults);
  }

  public TemplateManagerTargetBuilder(IRI base) {
    super(base);
  }

  public TemplateManagerTargetBuilder(Object defaults, boolean isiri) {
    super(defaults, isiri);
  }

  public TemplateManagerTargetBuilder(Object defaults) {
    super(defaults);
  }

  public TemplateManagerTargetBuilder(String base, boolean iri) {
    super(base, iri);
  }

  public TemplateManagerTargetBuilder(String base, Context defaults) {
    super(base, defaults);
  }

  public TemplateManagerTargetBuilder(String base, Object defaults,
      boolean isiri) {
    super(base, defaults, isiri);
  }

  public TemplateManagerTargetBuilder(String base, Object defaults) {
    super(base, defaults);
  }

  public TemplateManagerTargetBuilder(String base) {
    super(base);
  }

  public String urlFor(Request request, T key, Object param) {
    RequestContext rc = (RequestContext) request;
    if (param == null) param = new MapContext(true);
    return expand(key,getContext(rc,param));
  }

  @SuppressWarnings("unchecked")
  public static Context getContext(RequestContext request, Object param) {
    Context context = null;
    if (param != null) {
        if (param instanceof Map) {
            context = new MapContext((Map<String, Object>)param, true);
        } else if (param instanceof Context) {
            context = (Context)param;
        } else {
            context = new ObjectContext(param, true);
        }
    }
    return new RequestTemplateContext(request, context);
  }
}
