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
package org.apache.abdera.protocol.server.provider;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.i18n.iri.IRI;

public abstract class AbstractProvider 
  implements Provider {

  private static final String NS        = "http://incubator.apache.org/abdera";
  private static final String PFX       = "a";
  private static final QName ERROR         = new QName(NS, "error", PFX);
  private static final QName CODE          = new QName(NS, "code", PFX);
  private static final QName MESSAGE       = new QName(NS, "message", PFX);
  private static final QName TRACE         = new QName(NS, "trace", PFX);
  
  protected boolean isDebug() {
    return false;
  }
  
  protected Document createErrorDocument(
    Abdera abdera, 
    int code, 
    String message, 
    Throwable e) {
      Document doc = abdera.getFactory().newDocument();
      ExtensibleElement root = 
        (ExtensibleElement) abdera.getFactory().newElement(ERROR, doc);
      root.addSimpleExtension(CODE, (code != -1) ? String.valueOf(code) : "");
      root.addSimpleExtension(MESSAGE, (message != null) ? message : "");
      if (isDebug()) {
        if (e != null) {
          CharArrayWriter out = new CharArrayWriter();
          e.printStackTrace(new PrintWriter(out));
          root.addSimpleExtension(TRACE, out.toString());
        }
      }
      return doc;
  }

  /**
   * Return a server error
   */
  protected ResponseContext servererror(
    Abdera abdera,
    RequestContext request,
    String reason,
    Throwable t) {
      return returnBase(
        createErrorDocument(
          abdera, 500, 
          reason, t), 
        500, null);
  }
     
  /**
   * Return an unauthorized error
   */
  protected ResponseContext unauthorized(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 401, 
          reason, null), 
        401, null);
  }
  
  /**
   * Return an unauthorized error
   */
  protected ResponseContext forbidden(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 403, 
          reason, null), 
        403, null);
  }
  
  /**
   * Return a 404 not found error
   */
  protected ResponseContext unknown(
    Abdera abdera,
    RequestContext request,
    String reason) {
    return returnBase(
      createErrorDocument(
        abdera, 404, 
        reason, null), 
      404, null);
  }
  
  /**
   * Return a forbidden error
   */
  protected ResponseContext notallowed(
    Abdera abdera, 
    RequestContext request,
    String reason,
    String... methods) {
      BaseResponseContext resp = 
        (BaseResponseContext)returnBase(
          createErrorDocument(
            abdera, 403, 
            reason, null), 
          403, null);
        resp.setAllow(methods);
        return resp;
  }
  
  /**
   * Return a 400 bad request error
   */
  protected ResponseContext badrequest(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 400, 
          reason, null), 
        400, null);
  }
  
  /**
   * Return a 409 conflict error
   */
  protected ResponseContext conflict(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 409, 
          reason, null), 
        409, null);
  }
  
  /**
   * Return a service unavailable error
   */
  protected ResponseContext unavailable(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 503, 
          reason, null), 
        503, null);
  }
  
  protected ResponseContext notmodified(
    Abdera abdera, 
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 304, 
          reason, null), 
        304, null);
  }

  protected ResponseContext preconditionfailed(
    Abdera abdera, 
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 412, 
          reason, null), 
        412, null);
  }
  
  /**
   * Return a 415 media type not-supported error
   */
  protected ResponseContext notsupported(
    Abdera abdera,
    RequestContext request,
    String reason) {
      return returnBase(
        createErrorDocument(
          abdera, 415, 
          reason, null),
        415,null);
  }

  /**
   * Return a document
   */
  @SuppressWarnings("unchecked")
  protected ResponseContext returnBase(
    Base base, 
    int status,
    Date lastModified) {
      BaseResponseContext response = new BaseResponseContext(base);
      response.setStatus(status);
      if (lastModified != null) response.setLastModified(lastModified);
      response.setContentType(MimeTypeHelper.getMimeType(base));
      
      Document doc = base instanceof Document ? (Document)base : ((Element)base).getDocument();
      if (doc.getEntityTag() != null) {
        response.setEntityTag(doc.getEntityTag());
      } else if (doc.getLastModified() != null) {
        response.setLastModified(doc.getLastModified());
      }
      
      return response;
  }

  /**
   * Sanitize the value of a Slug header. Any non alphanumeric characters in
   * the slug are replaced with an underscore
   */
  protected String sanitizeSlug(String slug) {
    if (slug == null) throw new IllegalArgumentException("Slug cannot be null");
    return EncodingUtil.sanitize(slug);
  }

  protected abstract int getDefaultPageSize();
  

  protected int getPageSize(
    RequestContext request, 
    String pagesizeparam) {
      int max = getDefaultPageSize();
      try {
        String _ps = request.getParameter(pagesizeparam);
        return (_ps != null) ? 
          Math.min(Math.max(Integer.parseInt(_ps),0),max) : max;
      } catch (Exception e) {
        return max;
      }
  }
  
  protected int getOffset(
    RequestContext request, 
    String pageparam, 
    int pageSize) {
      try {
        String _page = request.getParameter(pageparam);
        int page =(_page != null) ? Integer.parseInt(_page) : 1;
        page = Math.max(page, 1) - 1;
        return pageSize * page;
      } catch (Exception e) {
        return 0;
      }
  }
  
  /**
   * Check to see if the entry is minimally valid according to RFC4287.
   * This is not a complete check.  It just verifies that the appropriate
   * elements are present and that their values can be accessed.
   */
  protected static boolean isValidEntry(Entry entry) {
    try {
      IRI id = entry.getId();
      if (id == null || 
          id.toString().trim().length() == 0 ||
          !id.isAbsolute()) return false;
      if (entry.getTitle() == null) return false;
      if (entry.getAuthor() == null) return false;
      if (entry.getUpdated() == null) return false;
      Content content = entry.getContentElement();
      if (content == null) {
        if (entry.getAlternateLink() == null) return false;
      } else {
        if ((content.getSrc() != null ||
            content.getContentType() == Content.Type.MEDIA || 
            content.getContentType() == Content.Type.XML) &&
            entry.getSummary() == null) {
          return false;
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  

  /**
   * Implementations should override this method to add additional namespaces
   * to the ignore list.
   */
  protected void checkEntryAddAdditionalNamespaces(List ignore) {}
  
  /**
   * Checks the entry for unknown extension elements.  Returns false if the 
   * entry contains any extension elements that are not supported
   */
  protected boolean checkEntryNamespaces(
    RequestContext request, 
    Entry entry) {
      List<String> ignore = new ArrayList<String>();
      ignore.add(org.apache.abdera.util.Constants.APP_NS);
      ignore.add(org.apache.abdera.util.Constants.XHTML_NS);
      ignore.add(org.apache.abdera.util.Constants.XML_NS);
      checkEntryAddAdditionalNamespaces(ignore);
      return (checkElement(entry,ignore));
  }
  
  /**
   * Return false if the element contains any extension elements that are not 
   * supported
   */
  private boolean checkElement(
    Element element, 
    List ignore) {
      List<QName> attrs = element.getExtensionAttributes();
      for (QName qname : attrs) {
        String ns = qname.getNamespaceURI();
        if (!ignore.contains(ns)) return false; 
      }
      if (element instanceof ExtensibleElement) {
        ExtensibleElement ext = (ExtensibleElement) element;
        List<Element> extensions = ext.getExtensions();
        for (Element el : extensions) {
          QName qname = el.getQName();
          String ns = qname.getNamespaceURI();
          if (!ignore.contains(ns)) return false; 
          if (!checkElement(el, ignore)) return false;
        }
      }
      return true;
  }
  
  protected static boolean beforeOrEqual(Date d1, Date d2) {
    long l1 = d1.getTime() / 1000; // drop milliseconds
    long l2 = d2.getTime() / 1000; // drop milliseconds
    return l1 <= l2;
  }
  
  protected IRI resolveBase(RequestContext request) {
    return request.getBaseUri().resolve(request.getUri());
  }
}
