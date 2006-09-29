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
package org.apache.abdera.util.iri;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.BitSet;

import org.apache.abdera.util.io.CharUtils;
import org.apache.abdera.util.io.CodepointIterator;
import org.apache.abdera.util.unicode.Normalizer;


public class IRI 
  implements Serializable, 
             Cloneable {

  private static final long serialVersionUID = -4530530782760282284L;
  Scheme _scheme;
  private String scheme;
  private String authority;
  private String userinfo;
  private String host;
  private int port;
  private String path;
  private String query;
  private String fragment;
  boolean doubleslash;

  private String a_host;
  private String a_fragment;
  private String a_path;
  private String a_query;
  private String a_userinfo;
  private String a_authority;
  
  private String d_authority;
  private String d_userinfo;
  private String d_host;
  private String d_path;
  private String d_query;
  private String d_fragment;
  
  public IRI(java.net.URL url) throws IRISyntaxException {
    this(Escaping.encode(
        Escaping.decode(url.toString()), 
        Constants.IUNRESERVED, 
        Constants.RESERVED, 
        Constants.PCTENC));
  }
  
  public IRI(java.net.URI uri) throws IRISyntaxException {
    this(Escaping.encode(
      Escaping.decode(uri.toString()), 
      Constants.IUNRESERVED, 
      Constants.RESERVED, 
      Constants.PCTENC,
      Constants.GENDELIMS));
  }
  
  public IRI(String iri) throws IRISyntaxException {
    Builder b = new Builder();
    parse(iri, b);
    init(
      b.schemeobj,
      b.scheme,
      b.authority,
      b.userinfo,
      b.host,
      b.port,
      b.path,
      b.query,
      b.fragment,
      b.doubleslash);
  }
  
  public IRI(String iri, Normalizer.Form nf) throws IRISyntaxException, IOException {
    this(Normalizer.normalize(iri,nf).toString());
  }
  
  public IRI(
    String scheme, 
    String userinfo, 
    String host, 
    int port, 
    String path, 
    String query, 
    String fragment) {
      SchemeRegistry reg = SchemeRegistry.getInstance();
      Scheme _scheme = reg.getScheme(scheme);
      StringBuffer buf = new StringBuffer();
      buildAuthority(buf,userinfo, host, port);
      String authority = (buf.length()!=0)?buf.toString():null;
      boolean doubleslash = (authority != null);
      init(_scheme,scheme,authority,userinfo,
        host,port,path,query,fragment,doubleslash);
  }
  
  public IRI(
    String scheme,
    String authority,
    String path,
    String query,
    String fragment) {
      Builder builder = new Builder();
      if (authority != null)
        splitAuthority(authority, builder);
      SchemeRegistry reg = SchemeRegistry.getInstance();
      Scheme _scheme = reg.getScheme(scheme);
      boolean doubleslash = (authority != null);
      init(_scheme,scheme,authority,builder.userinfo,
        builder.host,builder.port,path,query,
        fragment,doubleslash);
  }
  
  public IRI(
    String scheme,
    String host,
    String path,
    String fragment) {
      this(scheme, null, host, -1, path, null, fragment);
  }
  
  IRI(
    Scheme _scheme,
    String scheme,
    String authority,
    String userinfo,
    String host,
    int port,
    String path,
    String query,
    String fragment,
    boolean doubleslash) {
      init(_scheme,scheme,authority,userinfo,
         host,port,path,query,fragment,doubleslash);
  }
  
  private void init(
      Scheme _scheme,
      String scheme,
      String authority,
      String userinfo,
      String host,
      int port,
      String path,
      String query,
      String fragment,
      boolean doubleslash) {
    this._scheme = _scheme;
    this.scheme = scheme;
    this.authority = authority;
    this.userinfo = userinfo;
    this.host = host;
    this.port = port;
    this.path = (path != null) ? path : "";
    this.query = query;
    this.fragment = fragment;
    this.doubleslash = doubleslash;
    
    d_authority = Escaping.decode(authority);
    d_userinfo = Escaping.decode(userinfo);
    d_path = Escaping.decode(path);
    d_query = Escaping.decode(query);
    d_fragment = Escaping.decode(fragment);
    d_host = Escaping.decode(host);

    a_host = IDNA.toASCII(d_host);
    a_fragment = Escaping.encode(getFragment(),Constants.FRAGMENT);
    a_path = normalize(Escaping.encode(getPath(), Constants.PATH));
    a_query = Escaping.encode(getQuery(),Constants.QUERY);
    a_userinfo = Escaping.encode(getUserInfo(),Constants.USERINFO);
    a_authority = buildASCIIAuthority();
  }
    
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((authority == null) ? 0 : authority.hashCode());
    result = PRIME * result + ((fragment == null) ? 0 : fragment.hashCode());
    result = PRIME * result + ((host == null) ? 0 : host.hashCode());
    result = PRIME * result + ((path == null) ? 0 : path.hashCode());
    result = PRIME * result + port;
    result = PRIME * result + ((query == null) ? 0 : query.hashCode());
    result = PRIME * result + ((scheme == null) ? 0 : scheme.hashCode());
    result = PRIME * result + ((userinfo == null) ? 0 : userinfo.hashCode());
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
    final IRI other = (IRI) obj;
    if (authority == null) {
      if (other.authority != null)
        return false;
    } else if (!authority.equals(other.authority))
      return false;
    if (fragment == null) {
      if (other.fragment != null)
        return false;
    } else if (!fragment.equals(other.fragment))
      return false;
    if (host == null) {
      if (other.host != null)
        return false;
    } else if (!host.equals(other.host))
      return false;
    if (path == null) {
      if (other.path != null)
        return false;
    } else if (!path.equals(other.path))
      return false;
    if (port != other.port)
      return false;
    if (query == null) {
      if (other.query != null)
        return false;
    } else if (!query.equals(other.query))
      return false;
    if (scheme == null) {
      if (other.scheme != null)
        return false;
    } else if (!scheme.equals(other.scheme))
      return false;
    if (userinfo == null) {
      if (other.userinfo != null)
        return false;
    } else if (!userinfo.equals(other.userinfo))
      return false;
    return true;
  }

  public boolean equivalent(IRI uri) {
    if (_scheme != null) return _scheme.equivalent(this, uri);
    else {
      String s2 = uri.normalize().toASCIIString();
      String s1 = this.normalize().toASCIIString();
      return s1.compareTo(s2) == 0;
    }
  }
  
  public String getAuthority() {
    return d_authority;
  }
  
  public String getFragment() {
    return d_fragment;
  }
  
  public String getHost() {
    return d_host;
  }
  
  public IDNA getIDN() {
    return new IDNA(d_host);
  }
  
  public String getASCIIHost() {
    return a_host;
  }
  
  public String getPath() {
    return d_path;
  }
  
  public int getPort() {
    return port;
  }
  
  public String getQuery() {
    return d_query;
  }
  
  public String getScheme() {
    return (scheme != null) ? scheme.toLowerCase() : null;
  }
  
  public String getSchemeSpecificPart() {
    return buildSchemeSpecificPart(
      authority, 
      path, 
      query, 
      fragment);
  }
  
  public String getUserInfo() {
    return d_userinfo;
  }
  
  public String getRawAuthority() {
    return authority;
  }
  
  public String getRawFragment() {
    return fragment;
  }
  
  public String getRawPath() {
    return path;
  }
  
  public String getRawQuery() {
    return query;
  }
  
  public String getRawSchemeSpecificPart() {
    return buildSchemeSpecificPart(
      authority,
      path, 
      query, 
      fragment);
  }
  
  public String getRawUserInfo() {
    return userinfo;
  }
  
  void buildAuthority(
    StringBuffer buf, 
    String aui, 
    String ah, 
    int port) {
    if (aui != null && aui.length() != 0) {
      buf.append(aui);
      buf.append('@');
    }
    if (ah != null && ah.length() != 0) {
      buf.append(ah);
    }
    if (port != -1) {
      buf.append(':');
      buf.append(port);
    }
  }
  
  private String buildASCIIAuthority() {
    StringBuffer buf = new StringBuffer();
    String aui = getASCIIUserInfo();
    String ah = getASCIIHost();
    int port = getPort();
    buildAuthority(buf,aui,ah,port);
    return buf.toString();
  }
  
  public String getASCIIAuthority() {
    return a_authority;
  }
  
  public String getASCIIFragment() {
    return a_fragment;
  }
  
  public String getASCIIPath() {
    return a_path;
  }
  
  public String getASCIIQuery() {
    return a_query;
  }
  
  public String getASCIIUserInfo() {
    return a_userinfo;
  }
  
  public String getASCIISchemeSpecificPart() {
    return buildSchemeSpecificPart(
      getASCIIAuthority(), 
      getASCIIPath(), 
      getASCIIQuery(), 
      getASCIIFragment());
  }
  
  private String buildSchemeSpecificPart(
    String authority,
    String path,
    String query,
    String fragment) {
      StringBuffer buf = new StringBuffer();
      if (doubleslash) buf.append("//");
      if (authority != null) buf.append(authority);
      if (path != null && path.length() != 0) {
        buf.append(path);
      }
      if (query != null && query.length() != 0) {
        buf.append('?');
        buf.append(query);
      }
      if (fragment != null && fragment.length() != 0) {
        buf.append('#');
        buf.append(fragment);
      }
      return buf.toString();
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public boolean isAbsolute() {
    return scheme != null;
  }
  
  public boolean isOpaque() {
    return path == null;
  }
  
  public static IRI relativize(IRI b, IRI c) {
    if (c.isOpaque() || b.isOpaque()) return c;
    if ((b.scheme == null && c.scheme != null) ||
        (b.scheme != null && c.scheme == null) ||
        (b.scheme != null && c.scheme != null && 
          !b.scheme.equalsIgnoreCase(c.scheme))) return c;
    String bpath = normalize(b.getPath());
    String cpath = normalize(c.getPath());
    bpath = (bpath != null) ? bpath : "/";
    cpath = (cpath != null) ? cpath : "/";
    if (!bpath.equals(cpath)) {
      if (bpath.charAt(bpath.length()-1) != '/') bpath += "/";
      if (!cpath.startsWith(bpath)) return c;
    } 
    IRI iri = new IRI(
      null,
      null,null,null,null,-1,
      normalize(cpath.substring(bpath.length())), 
      c.getQuery(), 
      c.getFragment(), 
      false);
    return iri;
  }
  
  public IRI relativize(IRI iri) {
    return relativize(this, iri);
  }
  
  public boolean isPathAbsolute() {
    String path = getPath();
    return (path != null) && path.length() > 0 && path.charAt(0) == '/';
  }
  
  public boolean isSameDocumentReference() {
    return scheme == null &&
           authority == null &&
           (path == null || 
            path.length() == 0 || 
            path.equals(".")) &&
           query == null;
  }
  
  public static IRI resolve(IRI b, String c) throws IRISyntaxException, IOException {
    return resolve(b, IRI.create(c));
  }
  
  public static IRI resolve(IRI b, IRI c) {
    if (c.isOpaque() || b.isOpaque()) return c;
    if (c.isSameDocumentReference()) {
      String cfragment = c.getFragment();
      String bfragment = b.getFragment();
      if ((cfragment == null && bfragment == null) ||
          (cfragment != null && cfragment.equals(bfragment))) {
          try {
            return (IRI) b.clone();
          } catch (Exception e) {
            return null; // Not going to happen
          } 
      } else {
        return new IRI(
          b._scheme,
          b.getScheme(),
          b.getAuthority(),
          b.getUserInfo(),
          b.getHost(),
          b.getPort(),
          normalize(b.getPath()),
          b.getQuery(),
          cfragment,
          b.doubleslash
        );
      }
    }
    if (c.isAbsolute()) return c;
    
    Scheme _scheme = b._scheme;
    String scheme = b.scheme;
    boolean ds = b.doubleslash;
    String query = c.getQuery();
    String fragment = c.getFragment();
    String userinfo = null;
    String authority = null;
    String host = null;
    int port = -1;
    String path = null;
    if (c.getAuthority() == null) {
      authority = b.getAuthority();
      userinfo = b.getUserInfo();
      host = b.getHost();
      port = b.getPort();
      path = c.isPathAbsolute() ? 
          normalize(c.getPath()) : 
          resolve(b.getPath(),c.getPath());
    } else {
      authority = c.getAuthority();
      userinfo = c.getUserInfo();
      host = c.getHost();
      port = c.getPort();
      path = normalize(c.getPath());
    }
    return new IRI(_scheme,scheme,authority,userinfo,host,port,path,query,fragment,ds);
  }
  
  public IRI normalize() {
    return normalize(this);
  }
  
  public static IRI normalize(IRI iri) {
    if (iri.isOpaque() || iri.getPath() == null) return iri;
    IRI normalized = null;
    if (iri._scheme != null) normalized = iri._scheme.normalize(iri);
    return (normalized != null) ? 
      normalized : 
      new IRI(
        iri._scheme,
        iri.getScheme(),
        iri.getAuthority(),
        iri.getUserInfo(),
        iri.getHost(),
        iri.getPort(),
        normalize(iri.getPath()),
        iri.getQuery(),
        iri.getFragment(),
        iri.doubleslash
      );
  }

  static String normalize(String path) {
    if (path == null) return "/";
    String[] segments = path.split("/");
    if (segments.length < 2) return path;
    StringBuffer buf = new StringBuffer("/");
    for (int n = 0; n < segments.length; n++) {
      String segment = segments[n].intern();
      if (segment == ".") {
        segments[n] = null;
      } else if (segment == "..") {
        segments[n] = null;
        int i = n;
        while(--i > -1) {
          if (segments[i] != null) break;
        }
        if (i > -1) segments[i] = null;
      }
    }
    for (int n = 0; n < segments.length; n++) {
      if (segments[n] != null) {
        if (buf.length() > 1) buf.append('/');
        buf.append(segments[n]);
      }
    }
    if (path.endsWith("/") || path.endsWith("/.")) 
      buf.append('/');
    return buf.toString();
  }
  
  private static String resolve(String bpath, String cpath) {
    if (bpath == null && cpath == null) return null;
    if (bpath == null && cpath != null) {
      return (!cpath.startsWith("/")) ? "/" + cpath : cpath;
    }
    if (bpath != null && cpath == null) return bpath;
    StringBuffer buf = new StringBuffer("");
    int n = bpath.lastIndexOf('/');
    if (n > -1) buf.append(bpath.substring(0,n+1));
    if (cpath.length() != 0) buf.append(cpath);
    return normalize(buf.toString());
  }
  
  public IRI resolve(IRI iri) {
    return resolve(this,iri);
  }
  
  public IRI resolve(String iri) throws IRISyntaxException {
    return resolve(this,IRI.create(iri));
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    String scheme = getScheme();
    if (scheme != null && scheme.length() != 0) {
      buf.append(scheme);
      buf.append(':');
    }
    buf.append(getSchemeSpecificPart());
    return buf.toString();
  }
  
  public String toASCIIString() {
    StringBuffer buf = new StringBuffer();
    String scheme = getScheme();
    if (scheme != null && scheme.length() != 0) {
      buf.append(scheme);
      buf.append(':');
    }
    buf.append(getASCIISchemeSpecificPart());
    return buf.toString();
  }
  
  public String toBIDIString() {
    StringBuffer buf = new StringBuffer(toString());
    if (buf.length() > 0) {
      if (buf.charAt(0) != '\u202A') buf.insert(0,'\u202A');
      if (buf.charAt(buf.length()-1) != '\u202C') buf.append('\u202C');
    }
    return buf.toString();
  }
  
  public java.net.URI toURI() throws URISyntaxException {
    return new java.net.URI(toASCIIString());
  }
  
  public java.net.URL toURL() throws MalformedURLException, URISyntaxException {
    return toURI().toURL();
  }
  
  ////////// parse implementation
  
  private static void parse(String uri, Builder builder) throws IRISyntaxException {
    SchemeRegistry reg = SchemeRegistry.getInstance();
    builder.chars = uri.toCharArray();
    CodepointIterator ci = CodepointIterator.forCharArray(builder.chars);
    try {
      Parser.parse(ci, builder, reg);
    } catch (IOException e) {
      throw new IRISyntaxException(e);
    }
  }
  
  public static IRI create(String iri) throws IRISyntaxException {
    return new IRI(iri);
  }
  
  public static IRI create(String iri, Normalizer.Form nf) throws IRISyntaxException, IOException {
    return new IRI(iri,nf);
  }
  
  static void splitAuthority(String authority, Builder builder) {
    if (authority != null) {
      int n = authority.indexOf('@');
      if (n > -1) builder.userinfo = authority.substring(0,n);
      int a = authority.indexOf('[',n);
      if (a > -1) {
        int m = authority.indexOf(']',a);
        if (m > -1) a = m;
        a = authority.indexOf(':',a);
      } else
      a = authority.indexOf(':',n);
      if (a > -1) {
        builder.host = authority.substring(n+1,a);
        String p = authority.substring(a+1);
        if (p.length() > 0) {
          try {
            builder.port = Integer.parseInt(p);
          } catch (Exception e) {}
        }
      } else builder.host = authority.substring(n+1);
    }
  }
  
  static class Builder implements org.apache.abdera.util.iri.Builder {
    private Scheme schemeobj;
    private char[] chars;
    private String scheme;
    private String authority;
    private String userinfo;
    private String host;
    private int port = -1;
    private String path;
    private String query;
    private String fragment;
    private boolean doubleslash;
    
    private void setScheme(Scheme scheme) {
      this.schemeobj = scheme;
    }
    
    public void scheme(int s, int l) {
      scheme = (l > 0) ? new String(chars,s,l).toLowerCase() : null;
    }
    public void authority(int s, int l) {
      authority = (l > 0) ? new String(chars,s,l) : null;
      splitAuthority(authority, this);
    }
    public void path(int s, int l) {
      path = (l > 0) ? new String(chars,s,l) : null;
    }
    public void query(int s, int l) {
      query = (l > 0) ? new String(chars,s,l) : null;
    }
    public void fragment(int s, int l) {
      fragment = (l > 0) ? new String(chars,s,l) : null;
    }
    
    public IRI getAtomURI() {
      return new IRI(
        schemeobj,
        scheme,authority,userinfo,
        host,port,path,query,fragment, 
        doubleslash);
    }
  }
  
  static class Parser {
    static void parse(CodepointIterator ci, Builder builder, SchemeRegistry reg) 
      throws IRISyntaxException, 
             IOException {
      int e = ci.position();
      scan(ci,Constants.SCHEME,-1);
      if (ci.peek() == ':')
        builder.scheme(e,ci.position()-e);
      Scheme _scheme = null;
      if (builder.scheme != null && builder.scheme.length() != 0) {
        _scheme = reg.getScheme(builder.scheme);
      } else { ci.position(e); }
      if (_scheme != null) {
        // allow for scheme specific parsing. if the resolved scheme
        // does parse the result, skip the rest, otherwise, do the 
        // default parsing
        builder.setScheme(_scheme);
        if (_scheme.parse(ci, builder)) return;
      }
      // default parsing. works for most common schemes
      scan(ci, Constants.COLON,1);
      e = ci.position();
      if (ci.peek() == '/' && 
          ci.peek(ci.position() + 1) == '/') {
        scan(ci,Constants.SLASH,2);
        builder.doubleslash = true;
      }
      e = ci.position();
      int f = find(ci,Constants.SEPS);
      if(f != 0) {
        scan(ci,Constants.ISERVER,-1);
      if (ci.peek() == -1 || CharUtils.isSet(ci.peek(), Constants.SEPS)) {        
        builder.authority(e,ci.position()-e);
      }
      else ci.position(e);
      e = ci.position();
      }
      scan(ci,Constants.IPATH,-1);
      builder.path(e,ci.position()-e);
      scan(ci,Constants.QUERYMARK,-1);
      e = ci.position();
      scan(ci,Constants.IQUERY,-1);
      builder.query(e,ci.position()-e);
      scan(ci,Constants.HASH,-1);
      e = ci.position();
      scan(ci,Constants.IFRAGMENT,-1);
      builder.fragment(e,ci.position()-e);
    }
  }
  
  private static int find(CodepointIterator ci, BitSet set) throws IOException {
    int n = ci.position();
    int c = -1;
    while((c = ci.peek(n++)) != -1 && set.get(c)) { n++; } 
    return n-1;
  }
  
  private static int scan(CodepointIterator ci, BitSet set, int count) throws IOException, IRISyntaxException {
    while (ci.hasNext() && ci.peek() != -1 && set.get(ci.peek())){ 
      int p = ci.next();
      if (!set.get(p)) {
        if (!CharUtils.isSet(p, Constants.RESERVED, Constants.IUNRESERVED, Constants.HASH)) 
          throw new IRISyntaxException("Invalid Character (0x" + Integer.toHexString(p) + ") In URI");
        return -1;
      }
    }
    return -1;
  }
}
