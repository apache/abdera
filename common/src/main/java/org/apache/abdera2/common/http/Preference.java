package org.apache.abdera2.common.http;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.text.CharUtils;
import static org.apache.abdera2.common.text.CharUtils.quotedIfNotToken;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.CharUtils.Profile;

/**
 * Implementation of the Prefer HTTP Header, e.g.
 * 
 * Prefer: return-no-content, my-preference=abc;xyz=123
 */
public class Preference implements Serializable {
  
  public static final String RETURN_NO_CONTENT = "return-no-content";
  public static final String RETURN_ACCEPTED = "return-accepted";
  public static final String RETURN_CONTENT = "return-content";
  public static final String RETURN_STATUS = "return-status";
  
  /** 
   * The "return-no-content" token indicates that the client prefers that
   * the server not include an entity in the response to a successful
   * request.  Typically, such responses would use the 204 No Content
   * status code as defined in Section 10.2.5 of [RFC2616], but other
   * status codes can be used as appropriate.
   */
  public static final Preference PREF_RETURN_NO_CONTENT = 
    new Preference(RETURN_NO_CONTENT);
  
  /**
   * The "return-accepted" token indicates that the client prefers that
   * the server respond with a 202 Accepted response indicating that the
   * request has been accepted for processing.
   */
  public static final Preference PREF_RETURN_ACCEPTED =
    new Preference(RETURN_ACCEPTED);
  
  /**
   * The "return-content" token indicates that the client prefers that the
   * server include an entity representing the current state of the
   * resource in the response to a successful request.
   */
  public static final Preference PREF_RETURN_CONTENT =
    new Preference(RETURN_CONTENT);
  
  /**
   * The "return-status" token indicates that the client prefers that the
   * server include an entity describing the status of the request in the
   * response to a successful request.
   */
  public static final Preference PREF_RETURN_STATUS = 
    new Preference(RETURN_STATUS);
  
  private static final long serialVersionUID = -6238673046322517740L;
  private final String token;
  private String value;
  private final Map<String,String> params = 
    new HashMap<String,String>();
  
  public Preference(String token) {
    CharUtils.verify(token, Profile.TOKEN);
    this.token = token.toLowerCase();
  }
  
  public String getToken() {
    return token;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }

  private static final Set<String> reserved = 
    new HashSet<String>();
  static {
    // no reserved yet
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
  
  public boolean matches(String token) {
    if (token == null) return false;
    return this.token.equalsIgnoreCase(token.toLowerCase());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((params == null) ? 0 : params.hashCode());
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    Preference other = (Preference) obj;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (!params.equals(other.params))
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  public String getParam(String name) {
    if (name == null || reserved(name))
      throw new IllegalArgumentException();
    return params.get(name);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(token);
    
    if (value != null) {
      String encval = Codec.encode(value, Codec.STAR);
      if (value.equals(encval)) {
        buf.append('=')
           .append(quotedIfNotToken(value));
      } else {
        buf.append('*')
           .append('=')
           .append(encval);
      }
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
          .append(quotedIfNotToken(val));
     }
   }
    
    return buf.toString();
  }
  
  private final static String TOKEN = "[\\!\\#\\$\\%\\&\\'\\*\\+\\-\\.\\^\\_\\`\\|\\~a-zA-Z0-9]+";
  private final static String PARAM = TOKEN+"\\s*={1}\\s*(?:(?:\"[^\"]+\")|(?:"+TOKEN+"))";
  private final static String PREF = TOKEN+"(?:\\s*={1}\\s*(?:(?:\"[^\"]+\")|(?:"+TOKEN+"))){0,1}";
  private final static String PARAMS = "(?:\\s*;\\s*(" + PARAM + "(?:\\s*;\\s*"+PARAM+")))*";
  private final static String PATTERN = "("+PREF+")" + PARAMS;

  private final static Pattern pattern = 
    Pattern.compile(PATTERN);
  private final static Pattern param = 
    Pattern.compile("("+PARAM+")");
  
  public static Iterable<Preference> parse(String text) {
    List<Preference> prefs = new ArrayList<Preference>();
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String pref = matcher.group(1);
      String params = matcher.group(2);
      String token = null, tokenval = null;
      
      if (pref != null) {
        String[] ps = pref.split("\\s*=\\s*", 2);
        token = ps[0].trim();
        if (ps.length == 2)
          tokenval = Codec.decode(CharUtils.unquote(ps[1]));
      }
      
      Preference preference = new Preference(token);
      preference.setValue(tokenval);
      prefs.add(preference);
      
      if (params != null) {
        Matcher mparams = param.matcher(params);
        while(mparams.find()) {
          String p = mparams.group(1);
          String[] ps = p.split("\\s*=\\s*", 2);
          preference.addParam(ps[0], Codec.decode(CharUtils.unquote(ps[1])));
        }
      }
    }
    return prefs;
  }
  
  public static String toString(
    Preference preference, 
    Preference... preferences) {
    if (preference == null)
      return null;
    StringBuilder buf = new StringBuilder();
    buf.append(preference.toString());
    for (Preference pref : preferences) {
      buf.append(',').append(pref.toString());
    }
    return buf.toString();
  }
  
  public static String toString(Iterable<Preference> preferences) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (Preference pref : preferences) {
      if (!first) buf.append(',');
      else first = !first;
      buf.append(pref.toString());
    }
    return buf.toString();
  }
  
  public static boolean contains(
    Iterable<Preference> preferences, 
    String token) {
    for (Preference pref : preferences)
      if (pref.matches(token))
        return true;
    return false;
  }
}
