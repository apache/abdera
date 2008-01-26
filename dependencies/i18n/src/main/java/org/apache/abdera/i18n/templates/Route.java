package org.apache.abdera.i18n.templates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.CharUtils;

/**
 * A type of URI Template loosely based on Ruby on Rails style Routes.
 * 
 * Example: 
 *   Route feed_route = new Route("feed",":feed/:entry");
 *   
 */
@SuppressWarnings("unchecked") 
public class Route
  implements Iterable<String>,
             Cloneable,
             Serializable {

  private static final long serialVersionUID = -8979172281494208841L;
  
  private static final Evaluator EVALUATOR = new Evaluator();
  private static final Pattern VARIABLE = Pattern.compile("[\\*\\:](?:\\()?[^\\/,;\\.#\\)]+(?:\\))?");
  
  private final String name;
  private final String pattern;
  private final String[] tokens;
  private final String[] variables;
  private final Pattern parser;
  private final String[] index;
  
  public Route(
    String name,
    String pattern) {
      this.name = name;
      this.pattern = CharUtils.stripBidiInternal(pattern);
      this.tokens = initTokens();
      this.variables = initVariables();
      this.parser = compile();
      this.index = index();
  }
  
  private String[] initTokens() {
    Matcher matcher = VARIABLE.matcher(pattern);
    List<String> tokens = new ArrayList<String>();
    while (matcher.find()) {
      String token = matcher.group();
      if (!tokens.contains(token))
        tokens.add(token);
    }
    return tokens.toArray(new String[tokens.size()]);
  }
  
  private String[] initVariables() {
    List<String> list = new ArrayList<String>();
    for (String token : this) {
      String var = var(token);
      if (!list.contains(var)) list.add(var);
    }
    String[] vars = list.toArray(new String[list.size()]);
    Arrays.sort(vars);
    return vars;
  }
  
  private Pattern compile() {
    Matcher m = VARIABLE.matcher(pattern);
    StringBuilder buf = new StringBuilder();
    int e = -1, s = 0;
    while(m.find(s)) {
      e = m.start();
      if (s != 0) {
        String q = "(?:" + Pattern.quote(pattern.substring(s,e)) + ")";
        buf.append(q);
        buf.append("]+))?");
        buf.append(q);
      }
      buf.append("(?:([^\\/,;\\.#\\)\\?");
      s = m.end();
    }
    if (s > 0) {
      if (s < pattern.length()) {
        String q = "(?:" + Pattern.quote(pattern.substring(s)) + ")";
        buf.append(q);
      }
      buf.append("]+))?");
    }
    return Pattern.compile(buf.toString());
  }
  
  private String[] index() {
    List<String> index = new ArrayList<String>();
    Matcher m = VARIABLE.matcher(pattern);
    int s = 0;
    while(m.find(s)) {
      String var = var(m.group(0));
      if (!index.contains(var)) index.add(var);
      s = m.end();
    }
    return index.toArray(new String[index.size()]);
  }
  
  /**
   * Returns true if the given uri matches the route pattern
   */
  public boolean match(String pattern) {
    Matcher m = parser.matcher(pattern);
    return m.find();
  }
  
  /**
   * Parses the given uri using the route pattern
   */
  public Map<String,String> parse(String pattern) {
    Matcher m = parser.matcher(pattern);
    Map<String,String> results = new HashMap<String,String>();
    if (m.find()) {
      for (int n = 1; n <= m.groupCount(); n++) {
        String label = index[n-1];
        results.put(label, m.group(n));
      }
    }
    return results;
  }

  /**
   * Expand the route pattern given the specified context
   */
  public String expand(
    Context context) {
      String pattern = this.pattern;
      for(String token : this) {
        pattern = replace(
          pattern, 
          token, 
          EVALUATOR.evaluate(
            var(token), 
            context));
      }
      StringBuffer buf = new StringBuffer(pattern);
      boolean qs = false;
      for (String var : context) {
        if (Arrays.binarySearch(variables, var) < 0) {
          if (!qs) {
            buf.append("?");
            qs = true;
          } else {
            buf.append("&");
          }
          buf.append(var)
             .append("=")
             .append(EVALUATOR.evaluate(var, context));
        }
      }
      
      return buf.toString();
  }
  
  private String var(String token) {
    token = token.substring(1);
    if (token.startsWith("("))
      token = token.substring(1);
    if (token.endsWith(")"))
      token = token.substring(0,token.length()-1);
    return token;
  }

  /**
   * Expand the route pattern given the specified context object
   **/
  public String expand(Object object) {
    return expand(object,false);
  }

  /**
   * Expand the route pattern using IRI escaping rules
   */
  public String expand(Object object, boolean isiri) {
    return expand(
      object instanceof Context ? 
        (Context)object :
        object instanceof Map ? 
          new HashMapContext((Map)object,isiri) :
          new ObjectContext(object,isiri));
  }
  
  private String replace(
    String pattern, 
    String token, 
    String value) {
      return pattern.replaceAll(
        Pattern.quote(token),
        value);
  }

  
  public String getName() {
    return name;
  }
  
  public String getPattern() {
    return pattern;
  }

  public Iterator<String> iterator() {
    return Arrays.asList(tokens).iterator();
  }
  
  public String[] getVariables() {
    return variables;
  }
  
  public Route clone() {
    try {
      return (Route)super.clone();
    } catch (Throwable e) {
      return new Route(name,pattern);  // not going to happen, but just in case
    }
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Route other = (Route) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (pattern == null) {
      if (other.pattern != null) return false;
    } else if (!pattern.equals(other.pattern)) return false;
    return true;
  }

  public String toString() {
    return pattern;
  }

  public static String expand(String pattern, Context context) {
    if (context == null || pattern == null) throw new IllegalArgumentException();
    Route route = new Route(null, pattern);
    return route.expand(context);
  }
  
  public static String expand(String pattern, Object object) {
    return expand(pattern,object,false);
  }
  
  public static String expand(String pattern, Object object, boolean isiri) {
    if (object == null || pattern == null) throw new IllegalArgumentException();
    Route route = new Route(null,pattern);
    return route.expand(object,isiri);
  }
  
  public static String expandAnnotated(Object object) {
    if (object == null) throw new IllegalArgumentException();
    Class _class = object.getClass();
    URIRoute uriroute = (URIRoute) _class.getAnnotation(URIRoute.class);
    if (uriroute != null) {
      return expand(uriroute.value(),object,uriroute.isiri());
    } else {
      throw new IllegalArgumentException("No Route provided");
    }
  }

}
