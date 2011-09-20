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
package org.apache.abdera2.common.http;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.text.Codec;
import static org.apache.abdera2.common.text.CharUtils.unquote;
import static org.apache.abdera2.common.text.CharUtils.quotedIfNotToken;
import static org.apache.abdera2.common.text.CharUtils.isToken;
import static org.apache.abdera2.common.text.CharUtils.quoted;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Implementation of the HTTP Challenge/Credentials Construct. This is helpful when 
 * using custom authentication mechanisms with an HTTP Request. It provides
 * a simple means of working with the WWW-Authenticate and Proxy-Authenticate
 * headers.
 **/
public class Authentication implements Iterable<String>, Serializable {

  private static final long serialVersionUID = 7237004814934751226L;
  public final static String BASIC = "basic";
  public final static String DIGEST = "digest";
  
  private final static String TOKEN = "[\\!\\#\\$\\%\\&\\'\\*\\+\\-\\.\\^\\_\\`\\|\\~a-zA-Z0-9]+";
  private final static String B64 = "([a-zA-Z0-9\\-\\.\\_\\~\\+\\/]+\\=*)";
  private final static String PARAM = TOKEN+"\\s*={1}\\s*(?:(?:\"[^\"]+\")|(?:"+TOKEN+"))";
  private final static String PARAMS = "(" + PARAM + "(?:\\s*,\\s*"+PARAM+")*)";
  private final static String B64orPARAM = "(?:" + PARAMS + "|" + B64 + ")";
  private final static String PATTERN = "("+TOKEN+")(?:\\s+" + B64orPARAM + ")?";
  private final static Pattern pattern = 
    Pattern.compile(PATTERN);
  private final static Pattern param = 
    Pattern.compile("("+PARAM+")");
  
  private final static Set<String> ALWAYS = 
    new HashSet<String>();
  static {
      ALWAYS.add("domain");
      ALWAYS.add("nonce");
      ALWAYS.add("opaque");
      ALWAYS.add("qop");
      ALWAYS.add("realm");
  }
  
  public static void alwaysQuote(String name) {
    if (name == null)
      throw new IllegalArgumentException();
    ALWAYS.add(name);
  }
  
  public static Iterable<Authentication> parse(String challenge) {
    List<Authentication> challenges = new ArrayList<Authentication>();
    Matcher matcher = pattern.matcher(challenge);
    while (matcher.find()) {
      String scheme = matcher.group(1);
      String params = matcher.group(2);
      String b64token = matcher.group(3); 
      Authentication chal = 
        new Authentication(scheme,b64token);
      if (params != null) {
        Matcher mparams = param.matcher(params);
        while(mparams.find()) {
          String p = mparams.group(1);
          String[] ps = p.split("\\s*=\\s*", 2);
          String name = ps[0];
          if (name.charAt(name.length()-1)=='*')
            name = name.substring(0,name.length()-1);
          chal.add(name, Codec.decode(unquote(ps[1])));
        }
      }
      challenges.add(chal);
    }
    return challenges;
  }
  
  private final String scheme;
  private final String b64token;
  private final Map<String,String> params = 
    new HashMap<String,String>();
  private final Set<String> quoted = 
    new HashSet<String>();
    
  public Authentication(String scheme) {
    this(scheme,null);
  }
  
  public Authentication(String scheme, String b64token) {
    if (scheme == null)
      throw new IllegalArgumentException();
    this.scheme = scheme.toLowerCase(Locale.US);
    this.b64token = b64token;
  }
  
  private void add(String name, String value) {
    params.put(name,value);
  }
  
  public String getScheme() {
    return scheme;
  }
  
  public String getBase64Token() {
    return b64token;
  }
  
  public String getParam(String name) {
    return params.get(name);
  }
  
  private static boolean is_always_quoted(String name) {
    return name != null ? ALWAYS.contains(name) : false;
  }
  
  public void setParam(String name, String val) {
    if (val == null)
      params.remove(name);
    else
      params.put(name, val);
  }
  
  public void setQuotedParam(String name, String val) {
    setParam(name,val);
    quoted.add(name);
  }
  
  public void setQuotedParam(String name) {
    quoted.add(name);
  }
  
  public boolean hasParam(String name) {
    return params.containsKey(name);
  }
  
  public Iterator<String> iterator() {
    return params.keySet().iterator();
  }
  
  private boolean isquoted(String param) {
    return quoted.contains(param);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder(scheme);
    if (b64token != null || params.size() > 0)
      buf.append(' ');
    if (b64token != null)
      buf.append(b64token);
    else {
      boolean first = true;
      for (String param : this) {
        if (!first) {
          buf.append(',').append(' ');
        } else first = false;
        String val = getParam(param);
        buf.append(param);
        boolean always = is_always_quoted(param) || isquoted(param);
        if (!isToken(val) && !always) {
          buf.append('*')
             .append('=')
             .append(Codec.encode(val,Codec.STAR));
        } else {
          buf.append('=')
             .append(always?quoted(val,true):quotedIfNotToken(val));
        }
      }
    }
    return buf.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((b64token == null) ? 0 : b64token.hashCode());
    result = prime * result + ((params == null) ? 0 : params.hashCode());
    result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
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
    Authentication other = (Authentication) obj;
    if (b64token == null) {
      if (other.b64token != null)
        return false;
    } else if (!b64token.equals(other.b64token))
      return false;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (scheme == null) {
      if (other.scheme != null)
        return false;
    } else if (!scheme.equals(other.scheme))
      return false;
    return true;
  }
  
  public static Authentication basic(String userid, String password) {
    String b64token = 
      StringUtils.newStringUsAscii(
        Base64.encodeBase64(
          bytes(userid,":",password)));
    return new Authentication(BASIC, b64token);
  }
  
  private static byte[] bytes(String val, String... vals) {
    if (val == null) return new byte[0];
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    try {
      out.write(StringUtils.getBytesUtf8(val));
      for (String v : vals)
        out.write(StringUtils.getBytesUtf8(v));
    } catch (Throwable t) {}
    return out.toByteArray();
  }
  
  public static String toString(Authentication auth, Authentication... auths) {
    if (auth == null) return null;
    StringBuilder buf = new StringBuilder();
    buf.append(auth.toString());
    for (Authentication a : auths)
      buf.append(", ").append(a.toString());
    return buf.toString();
  }
  
  public static String toString(Iterable<Authentication> auths) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (Authentication auth : auths) {
      if (!first) buf.append(", ");
      else first = !first;
      buf.append(auth.toString());
    }
    return buf.toString();
  }
}
