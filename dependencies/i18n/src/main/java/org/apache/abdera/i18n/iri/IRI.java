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
package org.apache.abdera.i18n.iri;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.i18n.text.InvalidCharacterException;
import org.apache.abdera.i18n.text.Nameprep;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.data.UnicodeCharacterDatabase;

public final class IRI implements Serializable, Cloneable {

    private static final long serialVersionUID = -4530530782760282284L;
    protected Scheme _scheme;
    private String scheme;
    private String authority;
    private String userinfo;
    private String host;
    private int port = -1;
    private String path;
    private String query;
    private String fragment;

    private String a_host;
    private String a_fragment;
    private String a_path;
    private String a_query;
    private String a_userinfo;
    private String a_authority;

    public IRI(java.net.URL url) {
        this(url.toString());
    }

    public IRI(java.net.URI uri) {
        this(uri.toString());
    }

    public IRI(String iri) {
        parse(CharUtils.stripBidi(iri));
        init();
    }

    public IRI(String iri, Normalizer.Form nf) throws IOException {
        this(Normalizer.normalize(CharUtils.stripBidi(iri), nf).toString());
    }

    public IRI(String scheme, String userinfo, String host, int port, String path, String query, String fragment) {
        this.scheme = scheme;
        this._scheme = SchemeRegistry.getInstance().getScheme(scheme);
        this.userinfo = userinfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        StringBuilder buf = new StringBuilder();
        buildAuthority(buf, userinfo, host, port);
        this.authority = (buf.length() != 0) ? buf.toString() : null;
        init();
    }

    public IRI(String scheme, String authority, String path, String query, String fragment) {
        this.scheme = scheme;
        this._scheme = SchemeRegistry.getInstance().getScheme(scheme);
        this.authority = authority;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        parseAuthority();
        init();
    }

    public IRI(String scheme, String host, String path, String fragment) {
        this(scheme, null, host, -1, path, null, fragment);
    }

    IRI(Scheme _scheme,
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
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        init();
    }

    private void init() {
        if (host != null && host.startsWith("[")) {
            a_host = host;
        } else {
            a_host = IDNA.toASCII(host);
        }
        a_fragment = UrlEncoding.encode(fragment, Profile.FRAGMENT.filter());
        a_path = UrlEncoding.encode(path, Profile.PATH.filter());
        a_query = UrlEncoding.encode(query, Profile.QUERY.filter(), Profile.PATH.filter());
        a_userinfo = UrlEncoding.encode(userinfo, Profile.USERINFO.filter());
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
        final IRI other = (IRI)obj;
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

    public String getAuthority() {
        return (authority != null && authority.length() > 0) ? authority : null;
    }

    public String getFragment() {
        return fragment;
    }

    public String getHost() {
        return (host != null && host.length() > 0) ? host : null;
    }

    public IDNA getIDN() {
        return new IDNA(host);
    }

    public String getASCIIHost() {
        return (a_host != null && a_host.length() > 0) ? a_host : null;
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public String getQuery() {
        return query;
    }

    public String getScheme() {
        return (scheme != null) ? scheme.toLowerCase() : null;
    }

    public String getSchemeSpecificPart() {
        return buildSchemeSpecificPart(authority, path, query, fragment);
    }

    public String getUserInfo() {
        return userinfo;
    }

    void buildAuthority(StringBuilder buf, String aui, String ah, int port) {
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
            StringBuilder buf = new StringBuilder();
            String aui = getASCIIUserInfo();
            String ah = getASCIIHost();
            int port = getPort();
            buildAuthority(buf, aui, ah, port);
            return buf.toString();
        } else {
            return UrlEncoding.encode(authority, Profile.AUTHORITY.filter());
        }
    }

    public String getASCIIAuthority() {
        return (a_authority != null && a_authority.length() > 0) ? a_authority : null;
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
        return buildSchemeSpecificPart(a_authority, a_path, a_query, a_fragment);
    }

    private String buildSchemeSpecificPart(String authority, String path, String query, String fragment) {
        StringBuilder buf = new StringBuilder();
        if (authority != null) {
            buf.append("//");
            buf.append(authority);
        }
        if (path != null && path.length() != 0) {
            buf.append(path);
        }
        if (query != null) {
            buf.append('?');
            buf.append(query);
        }
        if (fragment != null) {
            buf.append('#');
            buf.append(fragment);
        }
        return buf.toString();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new IRI(toString()); // not going to happen, but we have to
                                        // catch it
        }
    }

    public boolean isAbsolute() {
        return scheme != null;
    }

    public boolean isOpaque() {
        return path == null;
    }

    public static IRI relativize(IRI b, IRI c) {
        if (c.isOpaque() || b.isOpaque())
            return c;
        if ((b.scheme == null && c.scheme != null) || (b.scheme != null && c.scheme == null)
            || (b.scheme != null && c.scheme != null && !b.scheme.equalsIgnoreCase(c.scheme)))
            return c;
        String bpath = normalize(b.getPath());
        String cpath = normalize(c.getPath());
        bpath = (bpath != null) ? bpath : "/";
        cpath = (cpath != null) ? cpath : "/";
        if (!bpath.equals(cpath)) {
            if (bpath.charAt(bpath.length() - 1) != '/')
                bpath += "/";
            if (!cpath.startsWith(bpath))
                return c;
        }
        IRI iri =
            new IRI(null, null, null, null, null, -1, normalize(cpath.substring(bpath.length())), c.getQuery(), c
                .getFragment());
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
        return scheme == null && authority == null
            && (path == null || path.length() == 0 || path.equals("."))
            && query == null;
    }

    public static IRI resolve(IRI b, String c) throws IOException {
        return resolve(b, new IRI(c));
    }

    public static IRI resolve(IRI b, IRI c) {

        if (c == null)
            return null;
        if ("".equals(c.toString()) || "#".equals(c.toString())
            || ".".equals(c.toString())
            || "./".equals(c.toString()))
            return b;
        if (b == null)
            return c;

        if (c.isOpaque() || b.isOpaque())
            return c;
        if (c.isSameDocumentReference()) {
            String cfragment = c.getFragment();
            String bfragment = b.getFragment();
            if ((cfragment == null && bfragment == null) || (cfragment != null && cfragment.equals(bfragment))) {
                return (IRI)b.clone();
            } else {
                return new IRI(b._scheme, b.getScheme(), b.getAuthority(), b.getUserInfo(), b.getHost(), b.getPort(),
                               normalize(b.getPath()), b.getQuery(), cfragment);
            }
        }
        if (c.isAbsolute())
            return c;

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
            path = c.isPathAbsolute() ? normalize(c.getPath()) : resolve(b.getPath(), c.getPath());
        } else {
            authority = c.getAuthority();
            userinfo = c.getUserInfo();
            host = c.getHost();
            port = c.getPort();
            path = normalize(c.getPath());
        }
        return new IRI(_scheme, scheme, authority, userinfo, host, port, path, query, fragment);
    }

    public IRI normalize() {
        return normalize(this);
    }

    public static String normalizeString(String iri) {
        return normalize(new IRI(iri)).toString();
    }

    public static IRI normalize(IRI iri) {
        if (iri.isOpaque() || iri.getPath() == null)
            return iri;
        IRI normalized = null;
        if (iri._scheme != null)
            normalized = iri._scheme.normalize(iri);
        return (normalized != null) ? normalized : new IRI(iri._scheme, iri.getScheme(), iri.getAuthority(), iri
            .getUserInfo(), iri.getHost(), iri.getPort(), normalize(iri.getPath()), UrlEncoding.encode(UrlEncoding
            .decode(iri.getQuery()), Profile.IQUERY.filter()), UrlEncoding
            .encode(UrlEncoding.decode(iri.getFragment()), Profile.IFRAGMENT.filter()));
    }

    protected static String normalize(String path) {
        if (path == null || path.length() == 0)
            return "/";
        String[] segments = path.split("/");
        if (segments.length < 2)
            return path;
        StringBuilder buf = new StringBuilder("/");
        for (int n = 0; n < segments.length; n++) {
            String segment = segments[n].intern();
            if (segment == ".") {
                segments[n] = null;
            } else if (segment == "..") {
                segments[n] = null;
                int i = n;
                while (--i > -1) {
                    if (segments[i] != null)
                        break;
                }
                if (i > -1)
                    segments[i] = null;
            }
        }
        for (int n = 0; n < segments.length; n++) {
            if (segments[n] != null) {
                if (buf.length() > 1)
                    buf.append('/');
                buf.append(UrlEncoding.encode(UrlEncoding.decode(segments[n]), Profile.IPATHNODELIMS_SEG.filter()));
            }
        }
        if (path.endsWith("/") || path.endsWith("/."))
            buf.append('/');
        return buf.toString();
    }

    private static String resolve(String bpath, String cpath) {
        if (bpath == null && cpath == null)
            return null;
        if (bpath == null && cpath != null) {
            return (!cpath.startsWith("/")) ? "/" + cpath : cpath;
        }
        if (bpath != null && cpath == null)
            return bpath;
        StringBuilder buf = new StringBuilder("");
        int n = bpath.lastIndexOf('/');
        if (n > -1)
            buf.append(bpath.substring(0, n + 1));
        if (cpath.length() != 0)
            buf.append(cpath);
        if (buf.charAt(0) != '/')
            buf.insert(0, '/');
        return normalize(buf.toString());
    }

    public IRI resolve(IRI iri) {
        return resolve(this, iri);
    }

    public IRI resolve(String iri) {
        return resolve(this, new IRI(iri));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        String scheme = getScheme();
        if (scheme != null && scheme.length() != 0) {
            buf.append(scheme);
            buf.append(':');
        }
        buf.append(getSchemeSpecificPart());
        return UrlEncoding.encode(buf.toString(), Profile.SCHEMESPECIFICPART.filter());
    }

    public String toASCIIString() {
        StringBuilder buf = new StringBuilder();
        String scheme = getScheme();
        if (scheme != null && scheme.length() != 0) {
            buf.append(scheme);
            buf.append(':');
        }
        buf.append(getASCIISchemeSpecificPart());
        return buf.toString();
    }

    public String toBIDIString() {
        return CharUtils.wrapBidi(toString(), CharUtils.LRE);
    }

    public java.net.URI toURI() throws URISyntaxException {
        return new java.net.URI(toASCIIString());
    }

    public java.net.URL toURL() throws MalformedURLException, URISyntaxException {
        return toURI().toURL();
    }

    private void parseAuthority() {
        if (authority != null) {
            Matcher auth = AUTHORITYPATTERN.matcher(authority);
            if (auth.find()) {
                userinfo = auth.group(1);
                host = auth.group(2);
                if (auth.group(3) != null)
                    port = Integer.parseInt(auth.group(3));
                else
                    port = -1;
            }
            try {
                CharUtils.verify(userinfo, Profile.IUSERINFO);
                CharUtils.verify(host, Profile.IHOST);
            } catch (InvalidCharacterException e) {
                throw new IRISyntaxException(e);
            }
        }
    }

    private void parse(String iri) {
        try {
            SchemeRegistry reg = SchemeRegistry.getInstance();
            Matcher irim = IRIPATTERN.matcher(iri);
            if (irim.find()) {
                scheme = irim.group(1);
                _scheme = reg.getScheme(scheme);
                authority = irim.group(2);
                path = irim.group(3);
                query = irim.group(4);
                fragment = irim.group(5);
                parseAuthority();
                try {
                    CharUtils.verify(scheme, Profile.SCHEME);
                    CharUtils.verify(path, Profile.IPATH);
                    CharUtils.verify(query, Profile.IQUERY);
                    CharUtils.verify(fragment, Profile.IFRAGMENT);
                } catch (InvalidCharacterException e) {
                    throw new IRISyntaxException(e);
                }
            } else {
                throw new IRISyntaxException("Invalid Syntax");
            }
        } catch (IRISyntaxException e) {
            throw e;
        } catch (Exception e) {
            throw new IRISyntaxException(e);
        }
    }

    private static final Pattern IRIPATTERN =
        Pattern.compile("^(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");

    private static final Pattern AUTHORITYPATTERN =
        Pattern.compile("^(?:(.*)?@)?((?:\\[.*\\])|(?:[^:]*))?(?::(\\d+))?");

    public static void preinit() {
        UnicodeCharacterDatabase.getCanonicalClass(1);
        Nameprep.prep("");
    }

    /**
     * Returns a new IRI with a trailing slash appended to the path, if necessary
     */
    public IRI trailingSlash() {
        return new IRI(_scheme, scheme, authority, userinfo, host, port, path.endsWith("/") ? path : path + "/", query,
                       fragment);
    }

}
