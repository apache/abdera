package org.apache.abdera.protocol.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.content.CollectionProvider;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.Messages;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides support methods for {@link Provider} and {@link CollectionProvider}.
 */
public class ProviderSupport {
  private final static Log log = LogFactory.getLog(ProviderSupport.class);

  protected int defaultpagesize = 10;
  
  protected ProviderSupport() {}
  
  protected ProviderSupport(int defaultpagesize) {
    this.defaultpagesize = defaultpagesize;
  }
  
  public int getDefaultpagesize() {
    return defaultpagesize;
  }

  public void setDefaultpagesize(int defaultpagesize) {
    this.defaultpagesize = defaultpagesize;
  }

  protected Document<Error> createErrorDocument(
    Abdera abdera, 
    int code, 
    String message, 
    Throwable e) {
      Error error = Error.create(abdera,code,message);
      return error.getDocument();
  }

  /**
   * Return a server error
   */
  protected ResponseContext servererror(
    Abdera abdera,
    RequestContext request,
    String reason,
    Throwable t) {
      log.debug(Messages.get("SERVER_ERROR"));
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
      log.debug(Messages.get("UNAUTHORIZED"));
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
      log.debug(Messages.get("FORBIDDEN"));
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
    log.debug(Messages.get("UNKNOWN"));
    return returnBase(
      createErrorDocument(
        abdera, 404, 
        reason, null), 
      404, null);
  }

  /**
   * Return a 405 method not allowed error
   */
  protected ResponseContext notallowed(
    Abdera abdera, 
    RequestContext request,
    String reason,
    String... methods) {
      log.debug(Messages.get("NOT.ALLOWED")); 
      BaseResponseContext resp = 
        (BaseResponseContext)returnBase(
          createErrorDocument(
            abdera, 405, 
            reason, null), 
          405, null);
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
      log.debug(Messages.get("BAD.REQUEST"));
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
    log.debug(Messages.get("CONFLICT"));
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
      log.debug(Messages.get("UNAVAILABLE"));
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
      log.debug(Messages.get("NOT.MODIFIED"));
      EmptyResponseContext rc = new EmptyResponseContext(304);
      rc.setStatusText(reason);
      return rc;
  }

  protected ResponseContext preconditionfailed(
    Abdera abdera, 
    RequestContext request,
    String reason) {
      log.debug(Messages.get("PRECONDITION.FAILED"));
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
      log.debug(Messages.get("NOT.SUPPORTED"));
      return returnBase(
        createErrorDocument(
          abdera, 415, 
          reason, null),
        415,null);
  }

  /**
   * Return a 423 locked error
   */
  protected ResponseContext locked(
    Abdera abdera,
    RequestContext request,
    String reason) {
      log.debug(Messages.get("LOCKED"));
      return returnBase(
        createErrorDocument(
          abdera, 423,
          reason, null),
        423,null);
    }

  /**
   * Return a document
   */
  @SuppressWarnings("unchecked")
  protected ResponseContext returnBase(
    Base base, 
    int status,
    Date lastModified) {
      log.debug(Messages.get("RETURNING.DOCUMENT"));
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
    if (slug == null) throw new IllegalArgumentException(Messages.get("SLUG.NOT.NULL"));
    String sanitized = EncodingUtil.sanitize(slug);
    log.debug(Messages.format("SLUG.SANITIZED", slug, sanitized));
    return sanitized;
  }

  protected int getDefaultPageSize() {
    log.debug(Messages.format("DEFAULT.PAGE.SIZE",defaultpagesize));
    return defaultpagesize;
  }

  protected int getPageSize(
    RequestContext request, 
    String pagesizeparam) {
      int max = getDefaultPageSize();
      int size = max;
      try {
        String _ps = request.getParameter(pagesizeparam);
        size = (_ps != null) ? 
          Math.min(Math.max(Integer.parseInt(_ps),0),max) : max;
      } catch (Exception e) {}
      log.debug(Messages.format("PAGE.SIZE",size));
      return size;
  }

  protected int getOffset(
    RequestContext request, 
    String pageparam, 
    int pageSize) {
      int offset = 0;
      try {
        String _page = request.getParameter(pageparam);
        int page =(_page != null) ? Integer.parseInt(_page) : 1;
        page = Math.max(page, 1) - 1;
        offset = pageSize * page;
      } catch (Exception e) {}
      log.debug(Messages.format("OFFSET",offset));
      return offset;
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
          log.debug(Messages.format("CHECKING.VALID.ENTRY",false));
          return false;
        }
      }
    } catch (Exception e) {
      log.debug(Messages.format("CHECKING.VALID.ENTRY",false));
      return false;
    }
    log.debug(Messages.format("CHECKING.VALID.ENTRY",true));
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
      boolean answer = checkElement(entry,ignore);
      log.debug(Messages.format("CHECKING.ENTRY.NAMESPACES",answer));
      return answer;
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
