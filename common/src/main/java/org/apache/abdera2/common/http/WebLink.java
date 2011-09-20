package org.apache.abdera2.common.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.mediatype.MimeTypeParseException;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.CharUtils.Profile;

import static org.apache.abdera2.common.text.CharUtils.scanFor;
import static org.apache.abdera2.common.text.CharUtils.quotedIfNotToken;

/**
 * Implements the HTTP Link Header
 * (http://tools.ietf.org/html/rfc5988)
 */
public class WebLink implements Serializable {

  private static final long serialVersionUID = 3875558439575297581L;
  public static final String MEDIA_SCREEN = "screen";
  public static final String MEDIA_TTY = "tty";
  public static final String MEDIA_TV = "tv";
  public static final String MEDIA_PROJECTION = "projection";
  public static final String MEDIA_HANDHELD = "handheld";
  public static final String MEDIA_PRINT = "print";
  public static final String MEDIA_BRAILLE = "braille";
  public static final String MEDIA_AURAL = "aural";
  public static final String MEDIA_ALL = "all";
  
  private final IRI iri;
  private final Set<String> rel = 
    new LinkedHashSet<String>();
  private IRI anchor;
  private final Set<String> rev =
    new LinkedHashSet<String>();
  private Lang lang;
  private final Set<String> media = 
    new LinkedHashSet<String>();
  private String title;
  private MimeType mediaType;
  private final Map<String,String> params = 
    new HashMap<String,String>();
  
  public WebLink(String iri) {
    this(new IRI(iri));
  }
  
  public WebLink(String iri, String rel) {
    this(new IRI(iri),rel);
  }
  
  public WebLink(IRI iri, String rel) {
    if (iri == null) 
      throw new IllegalArgumentException();
    this.iri = iri.normalize();
    if (rel != null) this.rel.add(rel); // verify
    this.anchor = null;
    this.lang = null;
    this.title = null;
    this.mediaType = null;
  }
  
  public WebLink(IRI iri) {
    if (iri == null) 
      throw new IllegalArgumentException();
    this.iri = iri;
    this.anchor = null;
    this.lang = null;
    this.title = null;
    this.mediaType = null;
  }
  
  public IRI getResolvedIri(IRI base) {
    IRI context = getContextIri(base);
    return context != null ? context.resolve(iri) : iri;
  }
  
  public IRI getContextIri(IRI base) {
    if (anchor == null) return base;
    return base != null ? base.resolve(anchor) : anchor;
  }
  
  public IRI getIri() {
    return iri;
  }
  
  public void addRel(String rel) {
    this.rel.add(new IRI(rel).toASCIIString());
  }
  
  public void addRel(IRI rel) {
    addRel(rel.toASCIIString());
  }
  
  public Iterable<String> getRel() {
    return this.rel;
  }
  
  public IRI getAnchor() {
    return anchor;
  }
  
  public void setAnchor(IRI iri) {
    this.anchor = iri;
  }
  
  public void setAnchor(String iri) {
    setAnchor(new IRI(iri));
  }
  
  public void addRev(String rev) {
    this.rev.add(new IRI(rev).toASCIIString());
  }
  
  public void addRev(IRI rev) {
    addRev(rev.toASCIIString());
  }
  
  public Iterable<String> getRev() {
    return this.rev;
  }
  
  public Lang getHrefLang() {
    return lang;
  }
  
  public void setHrefLang(Lang lang) {
    this.lang = lang;
  }
  
  public void setHrefLang(String lang) {
    setHrefLang(Lang.parse(lang));
  }
  
  public void setHrefLang(Locale locale) {
    setHrefLang(Lang.fromLocale(locale));
  }
  
  public void addMedia(String media) {
    CharUtils.verify(media, Profile.TOKEN);
    this.media.add(media.toLowerCase());
  }
  
  public Iterable<String> getMedia() {
    return this.media;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public MimeType getMediaType() {
    return mediaType;
  }
  
  public void setMediaType(MimeType mediaType) {
    this.mediaType = mediaType;
  }
  
  public void setMediaType(String mediaType) {
    try {
      setMediaType(new MimeType(mediaType));
    } catch (javax.activation.MimeTypeParseException t) {
      throw new MimeTypeParseException(t);
    }
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((anchor == null) ? 0 : anchor.hashCode());
    result = prime * result + ((iri == null) ? 0 : iri.hashCode());
    result = prime * result + ((lang == null) ? 0 : lang.hashCode());
    result = prime * result + ((media == null) ? 0 : media.hashCode());
    result = prime * result + ((mediaType == null) ? 0 : mediaType.toString().hashCode());
    result = prime * result + ((params == null) ? 0 : params.hashCode());
    result = prime * result + ((rel == null) ? 0 : rel.hashCode());
    result = prime * result + ((rev == null) ? 0 : rev.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WebLink other = (WebLink) obj;
    if (anchor == null) {
      if (other.anchor != null)
        return false;
    } else if (!anchor.equals(other.anchor))
      return false;
    if (iri == null) {
      if (other.iri != null)
        return false;
    } else if (!iri.equals(other.iri))
      return false;
    if (lang == null) {
      if (other.lang != null)
        return false;
    } else if (!lang.equals(other.lang))
      return false;
    if (media == null) {
      if (other.media != null)
        return false;
    } else if (!media.equals(other.media))
      return false;
    if (mediaType == null) {
      if (other.mediaType != null)
        return false;
    } else if (!mediaType.equals(other.mediaType))
      return false;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (rel == null) {
      if (other.rel != null)
        return false;
    } else if (!rel.equals(other.rel))
      return false;
    if (rev == null) {
      if (other.rev != null)
        return false;
    } else if (!rev.equals(other.rev))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }



  private static final Set<String> reserved = 
    new HashSet<String>();
  static {
    reserved.add("rel");
    reserved.add("anchor");
    reserved.add("rev");
    reserved.add("hreflang");
    reserved.add("media");
    reserved.add("title");
    reserved.add("type");
    reserved.add("type");
  }
  private static boolean reserved(String name) {
    return reserved.contains(name);
  }
  
  public void addParam(String name, String value) {
    if (name == null || reserved(name)) 
      throw new IllegalArgumentException();
    if (value == null && params.containsKey(name))
      params.remove(name);
    else {
      params.put(name, value);
    }
  }
  
  public String getParam(String name) {
    if (name == null || reserved(name))
      throw new IllegalArgumentException();
    return params.get(name);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('<')
       .append(iri.toASCIIString())
       .append('>');
    
    if (rel.size() > 0) {
      buf.append(';')
         .append("rel=");
      boolean first = true;
      if (rel.size() > 1)
        buf.append('"');
      for (String r : rel) {
        if (!first) buf.append(' ');
        else first = false;
        buf.append(quotedIfNotToken(r));
      }
      if (rel.size() > 1)
        buf.append('"');
    }
    
    if (anchor != null) {
      buf.append(';')
         .append("anchor=<")
         .append(anchor.toASCIIString())
         .append('>');
    }
    
    if (rev.size() > 0) {
      buf.append(';')
         .append("rev=");
      boolean first = true;
      if (rev.size() > 1)
        buf.append('"');
      for (String r : rev) {
        if (!first) buf.append(' ');
        else first = false;
        buf.append(quotedIfNotToken(r));
      }
      if (rev.size() > 1)
        buf.append('"');
    }
    
    if (lang != null) {
      buf.append(';')
         .append("hreflang=")
         .append(lang.toString());
    }
    
    if (media.size() > 0) {
      buf.append(';')
         .append("media=");
      boolean first = true;
      if (media.size() > 1)
        buf.append('"');
      for (String r : media) {
        if (!first) buf.append(' ');
        else first = false;
        buf.append(quotedIfNotToken(r));
      }
      if (media.size() > 1)
        buf.append('"');
    }
    
    if (title != null) {
      String enctitle = Codec.encode(title,Codec.STAR);
      buf.append(';')
         .append("title");
      if (!title.equals(enctitle))
        buf.append('*')
           .append('=')
           .append(enctitle);
      else
        buf.append('=')
           .append(quotedIfNotToken(title));
    }
    
   if (mediaType != null) {
     buf.append(';')
        .append("type=")
        .append(quotedIfNotToken(mediaType.toString()));
   }
   
   for (Map.Entry<String, String> entry : params.entrySet()) {
     String val = entry.getValue();
     String encval = Codec.encode(val,Codec.STAR);
     buf.append(';')
        .append(entry.getKey());
     if (!val.equals(encval)) {
       buf.append('*')
          .append('=')
          .append(encval);
     } else {
       buf.append('=')
          .append(quotedIfNotToken(entry.getValue()));
     }
   }
    
    return buf.toString();
  }

  public static Iterable<WebLink> parse(String text) {
    List<WebLink> links = new ArrayList<WebLink>();
    WebLink weblink = null;
    if (text == null) return Collections.emptyList();
    
    int z = scanFor('<', text, 0, true);

    while(z != -1) {
      int s = z;
      int e = scanFor('>', text, s, false);
      if (e == -1)
        throw new IllegalArgumentException();
      
      String uri = text.substring(s+1,e).trim();
      weblink = new WebLink(uri);
      
      s = scanFor(';', text,e+1,false);
      while(s != -1 && text.charAt(s) != ',') {
        e = scanFor('=', text,s+1,false);
        String name = text.substring(s+1,text.charAt(e-1)=='*'?e-1:e).trim();
        s = scanFor(';', text,e+1,false);
        String val = s!=-1?text.substring(e+1,s).trim():text.substring(e+1).trim();
        val = Codec.decode(val);
        if (name.equalsIgnoreCase("rel")) {
          String[] vals = CharUtils.unquote(val).split("\\s+");
          for (String v : vals)
            weblink.addRel(v);
        } else if (name.equalsIgnoreCase("anchor")) {
          weblink.setAnchor(CharUtils.unwrap(val, '<', '>'));
        } else if (name.equalsIgnoreCase("rev")) {
          String[] vals = CharUtils.unquote(val).split("\\s+");
          for (String v : vals)
            weblink.addRev(v);
        } else if (name.equalsIgnoreCase("hreflang")) {
          weblink.setHrefLang(CharUtils.unquote(val));
        } else if (name.equalsIgnoreCase("media")) {
          String[] vals = CharUtils.unquote(val).split("\\s+");
          for (String v : vals)
            weblink.addMedia(v);
        } else if (name.equalsIgnoreCase("title")) {
          weblink.setTitle(CharUtils.unquote(val));
        } else if (name.equalsIgnoreCase("type")) {
          weblink.setMediaType(CharUtils.unquote(val));
        } else {
          weblink.addParam(name,CharUtils.unquote(val));
        }
      }
      links.add(weblink);
      if (s == -1) break;
      z = scanFor('<', text, s+1, false);
    }
    return links;
  }
    
  public static String toString(WebLink link, WebLink... links) {
    if (link == null) return null;
    StringBuilder buf = new StringBuilder();
    buf.append(link.toString());
    for (WebLink l : links)
      buf.append(", ").append(l.toString());
    return buf.toString();
  }
  
  public static String toString(Iterable<WebLink> links ) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (WebLink link : links) {
      if (!first) buf.append(", ");
      else first = !first;
      buf.append(link.toString());
    }
    return buf.toString();
  }
}
