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
package org.apache.abdera.protocol.server.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.RequestContext.Scope;

/**
 * Resolves targets based on a simple assumed URI structure. 
 */
public class StructuredTargetResolver
  extends TemplateTargetBuilder
  implements Resolver<Target> {

  public static final String URI_PARAMETER_ATTRIBUTE_PREFIX = "uriParameter";

  private Pattern servicesPattern = Pattern.compile("^/");

  private WorkspaceManager workspaceManager;
  

  public StructuredTargetResolver(WorkspaceManager workspaceManager) {
    this(workspaceManager, null);
  }

  public StructuredTargetResolver(WorkspaceManager workspaceManager, String servicesPattern) {
    this.workspaceManager = workspaceManager;
    if (servicesPattern != null) {
      this.servicesPattern = Pattern.compile(servicesPattern);
    }
    
    setTemplate(TargetType.TYPE_SERVICE, "{target_base}");
    setTemplate(TargetType.TYPE_COLLECTION, "{target_base}/{collection}");
    setTemplate(TargetType.TYPE_CATEGORIES, "{target_base}/{collection};categories");
    setTemplate(TargetType.TYPE_ENTRY, "{target_base}/{collection}/{entryid}");
  }

  public Target resolve(Request request) {
    RequestContext context = (RequestContext)request;
    String uri = context.getTargetPath();
    
    if (servicesPattern == null) {
      throw new RuntimeException("You must set the servicesPattern property on the ServiceProvider.");
    }

    Matcher uriMatcher = servicesPattern.matcher(uri);
    TargetType tt = null;
    if (uriMatcher.matches()) {
      tt = TargetType.TYPE_SERVICE;
    } else {
      uriMatcher.reset();
      if (uriMatcher.find()) {
        String path = uri.substring(uriMatcher.start());
        int q = path.indexOf("?");
        if (q != -1) {
          path = path.substring(0, q);
        }

        path = UrlEncoding.decode(path);

        CollectionInfo collection = null;
        String href = null;
        for (WorkspaceInfo wi : workspaceManager.getWorkspaces(context)) {
          for (CollectionInfo c : wi.getCollections(context)) {
            href = c.getHref(context);
            if (path.startsWith(href)) {
              collection = c;
              break;
            }
          }
        }

        if (collection != null) {
          context.setAttribute(Scope.REQUEST, DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, collection);

          if (href.equals(path)) {
            tt = TargetType.TYPE_COLLECTION;
          } else {
            tt = getOtherTargetType(context, path, href, collection);
          }
        }
      }
    }

    if (tt == null) {
      tt = TargetType.TYPE_UNKNOWN;
    } else {
      for (int i = 1; i <= uriMatcher.groupCount(); i++) {
        if (uriMatcher.group(i) != null) {
          context.setAttribute(Scope.REQUEST, URI_PARAMETER_ATTRIBUTE_PREFIX + Integer.toString(i),
                               uriMatcher.group(i));
        }
      }
    }

    return new DefaultTarget(tt, context);
  }

  @SuppressWarnings("unchecked")
  protected TargetType getOtherTargetType(RequestContext context, 
                                          String path, String providerHref,
                                          CollectionInfo collection) {
    if (path.endsWith(";categories")) {
      return TargetType.TYPE_CATEGORIES;
    } else if (context.getContentType() != null && !ProviderHelper.isAtom(context)) {
      return TargetType.TYPE_MEDIA;
    } else {
      return TargetType.TYPE_ENTRY;
    }
  }
}
