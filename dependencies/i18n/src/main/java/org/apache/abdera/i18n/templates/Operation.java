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
package org.apache.abdera.i18n.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.io.CharUtils;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.unicode.Normalizer;

@SuppressWarnings("unchecked") 
public abstract class Operation
  implements Serializable {

  protected final String name;
  protected final boolean multivar;
  
  protected Operation(String name) {
    this(name, false);
  }
  
  protected Operation(String name, boolean multivar) {
    this.name = name;
    this.multivar = multivar;
  }
  
  public final String name() {
    return name;
  }
  
  public abstract String evaluate(String var, String arg, Context context);
  
  public abstract void explain(String var, String arg, StringBuilder buf);
  
  public String[] getVariables(String var) {
    List<String> list = new ArrayList<String>();
    if (!multivar) {
      String name = tokenName(var);
      if (!list.contains(name)) list.add(name);
    } else {
      String[] vardefs = var.split("\\+?\\s*,\\s*");
      for (int n = 0; n < vardefs.length; n++) {
        String vardef = vardefs[n];
        String name = vardef.split("=",2)[0];
        if (!list.contains(name)) list.add(name);
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
  private static Map<String,Operation> operations = getOperations();
  
  private static Map<String,Operation> getOperations() {
    Map<String,Operation> ops = new HashMap<String,Operation>();
    ops.put("", new DefaultOperation());
    ops.put("prefix", new PrefixOperation());
    ops.put("append", new AppendOperation());
    ops.put("join", new JoinOperation());
    ops.put("listjoin", new ListJoinOperation());
    ops.put("opt", new OptOperation());
    ops.put("neg", new NegOperation());
    return ops;
  }
  
  public static void register(Operation operation) {
    operations.put(operation.name(),operation);
  }
  
  public static Operation get(String name) {
    if (name == null) name = "";
    Operation op = operations.get(name);
    if (op != null) return op;
    throw new UnsupportedOperationException(name);
  }
  
  private static String tokenName(String token) {
    String[] vardef = token.split("=",2);
    return vardef[0];
  }
  
  private static String evallist(String token, Context context, String sep) {
    StringBuilder buf = new StringBuilder();
    Object value = context.resolve(token);
    if (value != null) {
      if (value instanceof String) {
        String val = toString(value,context);
        if (val != null && val.length() > 0)
          buf.append(val);
      } else if (value.getClass().isArray()) {
        Object[] values = (Object[])value;
        for (Object obj : values) {
          String val = toString(obj,context);
          if (val != null && val.length() > 0) {
            if (buf.length() > 0) buf.append(sep);
            buf.append(val);
          }
        }
      } else if (value instanceof Iterable) {
        Iterable iterable = (Iterable)value;
        for (Object obj : iterable) {
          String val = toString(obj,context);
          if (val != null && val.length() > 0) {
            if (buf.length() > 0) buf.append(sep);
            buf.append(val);
          }          
        }
      }
    }
    return buf.toString();
  }
  
  protected static String eval(String token, Context context) {
    String[] vardef = token.split("=",2);
    String var = vardef[0];
    String def = vardef.length > 1 ? vardef[1] : null;
    Object rep = context.resolve(var);
    String val = toString(rep,context);
    return val != null && val.length() > 0 ? 
        val : def != null ? def : null;
  }
  
  private static String toString(Object val, Context context) {
    if (val == null) return null;
    if (val.getClass().isArray()) {
      if (val instanceof byte[]) {
        return Escaping.encode((byte[])val);
      } else if (val instanceof char[]) {
        try {
          String chars = new String((char[])val);
          return Escaping.encode(
              !context.isNormalizing() ? chars : 
              Normalizer.normalize(
                chars, 
                Normalizer.Form.C).toString(), 
              context.isIri() ? 
                CharUtils.Profile.IUNRESERVED : 
                CharUtils.Profile.UNRESERVED);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else if (val instanceof short[]) {
        StringBuilder buf = new StringBuilder();
        short[] array = (short[]) val;
        for (short obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else if (val instanceof int[]) {
        StringBuilder buf = new StringBuilder();
        int[] array = (int[]) val;
        for (int obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else if (val instanceof long[]) {
        StringBuilder buf = new StringBuilder();
        long[] array = (long[]) val;
        for (long obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else if (val instanceof double[]) {
        StringBuilder buf = new StringBuilder();
        double[] array = (double[]) val;
        for (double obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else if (val instanceof float[]) {
        StringBuilder buf = new StringBuilder();
        float[] array = (float[]) val;
        for (float obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else if (val instanceof boolean[]) {
        StringBuilder buf = new StringBuilder();
        boolean[] array = (boolean[]) val;
        for (boolean obj : array)
          buf.append(String.valueOf(obj));
        return buf.toString();
      } else {
        StringBuilder buf = new StringBuilder();
        Object[] array = (Object[]) val;
        for (Object obj : array)
          buf.append(toString(obj,context));
        return buf.toString();
      }
    } else if (val instanceof Template) {
      return toString(((Template)val).getPattern(),context);
    } else if (val instanceof InputStream) {
      try {
        return Escaping.encode((InputStream)val);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else if (val instanceof Readable) {
      try {
        return Escaping.encode(
          (Readable)val, 
          "UTF-8",
          context.isIri() ? 
            CharUtils.Profile.IUNRESERVED : 
            CharUtils.Profile.UNRESERVED);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else if (val instanceof CharSequence) {
      return encode(
        (CharSequence)val,
        context.isIri(),
        context.isNormalizing());
    } else if (val instanceof Byte) {
      return Escaping.encode(((Byte)val).byteValue());
    } else if (val instanceof Iterable) {
      StringBuilder buf = new StringBuilder();
      Iterable i = (Iterable) val;
      for (Object obj : i)
        buf.append(toString(obj,context));
      return buf.toString();
    } else {
      return encode(
        val != null ? 
          val.toString() : 
          null,
        context.isIri(),
        context.isNormalizing());
    }
  }
  
  protected static String eval(String token, String arg, Context context) {
    String[] vardef = token.split("=",2);
    String var = vardef[0];
    String def = vardef.length > 1 ? vardef[1] : null;
    Object rep = context.resolve(var);
    if (rep != null) {
      StringBuilder buf = new StringBuilder();
      if (rep.getClass().isArray()) {
        Object[] array = (Object[]) rep;
        for (Object obj : array) {
          String val = toString(obj,context);
          if (val != null && val.length() > 0) {
            if (buf.length() > 0) buf.append(arg);
            buf.append(var);
            buf.append("=");
            buf.append(val);
          }
        }
      } else if (rep instanceof Iterable) {
        Iterable list = (Iterable)rep;
        for (Object obj : list) {
          String val = toString(obj,context);
          if (val != null && val.length() > 0) {
            if (buf.length() > 0) buf.append(arg);
            buf.append(var);
            buf.append("=");
            buf.append(val);
          }
        }
      } else {
        String val = toString(rep,context);
        if (val != null && val.length() > 0) {
          buf.append(var);
          buf.append("=");
          buf.append(val);
        }
      }
      return buf.toString();
    } else if (def != null && def.length() > 0){
      StringBuilder buf = new StringBuilder();
      buf.append(var);
      buf.append("=");
      buf.append(def);
      return buf.toString();
    } else return null;
  }
  
  protected static boolean isdefined(String token, Context context) {
    String[] vardef = token.split("=",2);
    String var = vardef[0];
    String def = vardef.length > 1 ? vardef[1] : null;
    Object rep = context.resolve(var);
    if (rep == null) rep = def;
    if (rep == null) return false;
    if (rep.getClass().isArray()) {
      Object[] a = (Object[])rep;
      return a.length > 0;
    } else return toString(rep,context).length() > 0;
  }
  
  private static String encode(
    CharSequence val, 
    boolean isiri, 
    boolean normalizing) {
      try {
        return Escaping.encode(
            !normalizing ? val : 
            Normalizer.normalize(
              val, 
              Normalizer.Form.C).toString(), 
            isiri ? 
              CharUtils.Profile.IUNRESERVED : 
              CharUtils.Profile.UNRESERVED);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }  
  
  private static final class DefaultOperation extends Operation {
    private static final long serialVersionUID = -1279818778391836528L;
    public DefaultOperation() { super(""); }
    public String evaluate(String var, String arg, Context context) {
      return eval(var, context);
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("Replaced with the value of '");
      buf.append(var);
      buf.append("'");
    }
  }
  
  private static final class PrefixOperation extends Operation {
    private static final long serialVersionUID = 2738115969196268525L;
    public PrefixOperation() { super("prefix"); }    
    public String evaluate(String var, String arg, Context context) {
      String value = eval(var,context);
      return value == null || value.length() == 0 ? "" : arg != null ? arg + value : value;
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("If '");
      buf.append(var);
      buf.append("' is defined then prefix the value of '");
      buf.append(var);
      buf.append("' with '");
      buf.append(arg);
      buf.append("'");
    }
  }
  
  private static final class AppendOperation extends Operation {
    private static final long serialVersionUID = -2742793539643289075L;
    public AppendOperation() { super("append"); }    
    public String evaluate(String var, String arg, Context context) {
      String value = eval(var,context);
      return value == null || value.length() == 0 ? "" : arg != null ? value + arg : value;
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("If '");
      buf.append(var);
      buf.append("' is defined then append '");
      buf.append(arg);
      buf.append("' to the value of '");
      buf.append(var);
      buf.append("'");
    }
  }
  
  private static final class JoinOperation extends Operation {
    private static final long serialVersionUID = -4102440981071994082L;
    public JoinOperation() { super("join",true); }
    public String evaluate(String var, String arg, Context context) {
      StringBuilder buf = new StringBuilder();
      String[] vardefs = var.split("\\+?\\s*,\\s*");
      String val = null;
      for (int n = 0; n < vardefs.length; n++) {
        String vardef = vardefs[n];
        val = eval(vardef,arg,context);
        if (val != null && val.length() > 0) {
          if (buf.length() > 0) buf.append(arg);
          buf.append(val);
        }
      }
      String value = buf.toString();
      return value;
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("Join 'var=value' with '" + arg + "' for each variable in [");
      String[] vars = getVariables(var);
      boolean b = false;
      for (String v : vars) {
        if (b) buf.append(',');
        else b = true;
        buf.append("'");
        buf.append(v);
        buf.append("'");
      }
      buf.append("]");
    }
  }
  
  private static final class ListJoinOperation extends Operation {
    private static final long serialVersionUID = -8314383556644740425L;
    public ListJoinOperation() { super("listjoin"); }
    public String evaluate(String var, String arg, Context context) {
      return evallist(var,context,arg);
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("Join the members of the list '");
      buf.append(var);
      buf.append("' together with '");
      buf.append(arg);
      buf.append("'");
    }
  }
  
  private static final class OptOperation extends Operation {  
    private static final long serialVersionUID = 7808433764609641180L;
    public OptOperation() { super("opt",true); }
    public String evaluate(String var, String arg, Context context) {
      String[] vardefs = var.split("\\s*,\\s*");
      for (String v : vardefs) {
        if (isdefined(v,context)) return arg;
      }
      return null;
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("If [");
      String[] vars = getVariables(var);
      boolean b = false;
      for (String v : vars) {
        if (b) buf.append(',');
        else b = true;
        buf.append("'");
        buf.append(v);
        buf.append("'");
      }
      buf.append("] is defined and a string, or a list with one or more members, then insert '");
      buf.append(arg);
      buf.append("'");
    }
  }
  
  private static final class NegOperation extends Operation {    
    private static final long serialVersionUID = 1936380358902743528L;
    public NegOperation() { super("neg",true); }
    public String evaluate(String var, String arg, Context context) {
      String[] vardefs = var.split("\\s*,\\s*");
      for (String v : vardefs) {
        if (!isdefined(v,context)) return arg;
      }
      return null;
    }
    public void explain(String var, String arg, StringBuilder buf) {
      buf.append("If [");
      String[] vars = getVariables(var);
      boolean b = false;
      for (String v : vars) {
        if (b) buf.append(',');
        else b = true;
        buf.append("'");
        buf.append(v);
        buf.append("'");
      }
      buf.append("] is undefined, or a zero length list, then insert '");
      buf.append(arg);
      buf.append("'");
    }
  }
  
}
