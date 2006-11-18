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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.util.io.CharUtils;
import org.apache.abdera.util.io.InvalidCharacterException;
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
  
  public IRI(java.net.URI uri, String enc) throws IRISyntaxException, UnsupportedEncodingException {
    this(Escaping.encode(
        Escaping.decode(uri.toString(),enc), enc,
        Constants.IUNRESERVED, 
        Constants.RESERVED, 
        Constants.PCTENC,
        Constants.GENDELIMS));
  }
  
  public IRI(String iri) throws IRISyntaxException {
    Builder b = new Builder();
    parse(CharUtils.stripBidi(iri), b);
    init(
      b.schemeobj,
      b.scheme,
      b.authority,
      b.userinfo,
      b.host,
      b.port,
      b.path,
      b.query,
      b.fragment);
  }
  
  public IRI(String iri, Normalizer.Form nf) throws IRISyntaxException, IOException {
    this(Normalizer.normalize(CharUtils.stripBidi(iri),nf).toString());
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
      init(_scheme,scheme,authority,userinfo,
        host,port,path,query,fragment);
  }
  
  public IRI(
    String scheme,
    String authority,
    String path,
    String query,
    String fragment) 
      throws IRISyntaxException {
    Builder builder = new Builder();
    Parser.parseAuthority(authority, builder);
    SchemeRegistry reg = SchemeRegistry.getInstance();
    Scheme _scheme = reg.getScheme(scheme);
    init(_scheme,scheme,authority,builder.userinfo,
      builder.host,builder.port,path,query,
      fragment);
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
    String fragment) {
      init(_scheme,scheme,authority,userinfo,
         host,port,path,query,fragment);
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
      String fragment) {
    this._scheme = _scheme;
    this.scheme = scheme;
    this.authority = authority;
    this.userinfo = userinfo;
    this.host = host;
    this.port = port;
    this.path = (path != null) ? path : "";
    this.query = query;
    this.fragment = fragment;
    
    d_authority = Escaping.decode(authority);
    d_userinfo = Escaping.decode(userinfo);
    d_path = Escaping.decode(path);
    d_query = Escaping.decode(query);
    d_fragment = Escaping.decode(fragment);
    d_host = IDNA.toUnicode(Escaping.decode(host));

    a_host = IDNA.toASCII(d_host);
    a_fragment = Escaping.encode(getFragment(),Constants.FRAGMENT);
    a_path = Escaping.encode(getPath(), Constants.PATH);
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
    if (_scheme instanceof HttpScheme) {
      StringBuffer buf = new StringBuffer();
      String aui = getASCIIUserInfo();
      String ah = getASCIIHost();
      int port = getPort();
      buildAuthority(buf,aui,ah,port);
      return buf.toString();
    } else {
      return Escaping.encode(
        getAuthority(), 
        Constants.USERINFO, 
        Constants.REGNAME, 
        Constants.GENDELIMS);
    }
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
      if (authority != null) {
        buf.append("//");
        buf.append(authority);
      }
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
    String bpath = normalize(b._scheme,b.getPath());
    String cpath = normalize(c._scheme,c.getPath());
    bpath = (bpath != null) ? bpath : "/";
    cpath = (cpath != null) ? cpath : "/";
    if (!bpath.equals(cpath)) {
      if (bpath.charAt(bpath.length()-1) != '/') bpath += "/";
      if (!cpath.startsWith(bpath)) return c;
    } 
    IRI iri = new IRI(
      null,
      null,null,null,null,-1,
      normalize(b._scheme,cpath.substring(bpath.length())), 
      c.getQuery(), 
      c.getFragment());
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
  
  public static IRI resolve(
    IRI b, 
    String c) 
      throws IRISyntaxException, 
             IOException {
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
          normalize(b._scheme,b.getPath()),
          b.getQuery(),
          cfragment
        );
      }
    }
    if (c.isAbsolute()) return c;
    
    Scheme _scheme = b._scheme;
    String scheme = b.scheme;
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
          normalize(b._scheme,c.getPath()) : 
          resolve(b.getPath(),c.getPath());
    } else {
      authority = c.getAuthority();
      userinfo = c.getUserInfo();
      host = c.getHost();
      port = c.getPort();
      path = normalize(b._scheme,c.getPath());
    }
    return new IRI(
      _scheme,
      scheme,
      authority,
      userinfo,
      host,
      port,
      path,
      query,
      fragment);
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
        normalize(iri._scheme,iri.getPath()),
        iri.getQuery(),
        iri.getFragment()
      );
  }

  static String normalize(Scheme scheme, String path) {
    if (scheme != null) {
      String n = scheme.normalizePath(path);
      if (n != null) return n;
    }
    if (path == null || path.length() == 0) return "/";
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
    return normalize(new HttpScheme(),buf.toString());
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
    return CharUtils.bidiLRE(toString());
  }
  
  public java.net.URI toURI() 
    throws URISyntaxException {
      return new java.net.URI(toASCIIString());
  }
  
  public java.net.URL toURL() 
    throws MalformedURLException, 
           URISyntaxException {
    return toURI().toURL();
  }
  
  ////////// parse implementation
  
  private static void parse(
    String uri,
    Builder builder) 
      throws IRISyntaxException {
    try {
      Parser.parse(uri, builder, SchemeRegistry.getInstance());
    } catch (IOException e) {
      throw new IRISyntaxException(e);
    }
  }
  
  public static IRI create(
    String iri) 
      throws IRISyntaxException {
    return new IRI(iri);
  }
  
  public static IRI create(
    String iri, 
    Normalizer.Form nf) 
      throws IRISyntaxException, 
             IOException {
    return new IRI(iri,nf);
  }
  
  static class Builder {
    private Scheme schemeobj;
    private String scheme;
    private String authority;
    private String userinfo;
    private String host;
    private int port = -1;
    private String path;
    private String query;
    private String fragment;
    
    public IRI getAtomURI() {
      return new IRI(
        schemeobj,
        scheme,authority,userinfo,
        host,port,path,query,fragment);
    }
  }
  
  static class Parser {
    
    static final Pattern p = 
      Pattern.compile(
        "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
    
    static final Pattern a =
      Pattern.compile("^((.*)?@)?(\\[.*\\])?([^:]*)?(:(\\d*))?");
    
    static void parseAuthority(String authority, Builder builder) throws IRISyntaxException {
      if (authority != null) {
        Matcher auth = a.matcher(authority);
        if (auth.find()) {
          if (auth.group(2) != null) builder.userinfo = auth.group(2);
          if (auth.group(3) != null) builder.host = auth.group(3);
          else builder.host = auth.group(4);
          if (auth.group(6) != null) builder.port = Integer.parseInt(auth.group(6));
        }
        try {
          CharUtils.verify(builder.userinfo, Constants.IUSERINFO);
          CharUtils.verify(builder.host, Constants.IREGNAME);
        } catch (InvalidCharacterException e) {
          throw new IRISyntaxException(e);
        }
      }
    }
    
    static void parse(String iri, Builder builder, SchemeRegistry reg) 
      throws IRISyntaxException, 
             IOException {
      Matcher irim = p.matcher(iri);
      if (irim.find()) {
        
        builder.scheme = irim.group(2);
        builder.schemeobj = reg.getScheme(builder.scheme);
        builder.authority = irim.group(4);
        builder.path = irim.group(5);
        builder.query = irim.group(7);
        builder.fragment = irim.group(9);
        
        parseAuthority(builder.authority, builder);
        
        try {
          CharUtils.verify(builder.scheme, Constants.SCHEME);
          CharUtils.verify(builder.path, Constants.IPATH);
          CharUtils.verify(builder.query, Constants.IQUERY);
          CharUtils.verify(builder.fragment, Constants.IFRAGMENT);
        } catch (InvalidCharacterException e) {
          throw new IRISyntaxException(e);
        }
      } else {
        throw new IRISyntaxException("Invalid Syntax");
      }
    }
  }
  
}
