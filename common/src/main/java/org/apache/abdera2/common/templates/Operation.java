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
package org.apache.abdera2.common.templates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.Expression;
import org.apache.abdera2.common.templates.Operation;
import org.apache.abdera2.common.templates.Expression.VarSpec;
import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.UrlEncoding;

import com.ibm.icu.text.Normalizer2;

@SuppressWarnings("unchecked")
public abstract class Operation implements Serializable {
  
    private static final long serialVersionUID = -1734350302144527120L;
    public abstract String evaluate(Expression exp, Context context);

    private static Map<String, Operation> operations = getOperations();

    private static Map<String, Operation> getOperations() {
        Map<String, Operation> ops = new HashMap<String, Operation>();
        ops.put("", new DefaultOperation());
        ops.put("+", new ReservedExpansionOperation());
        ops.put("#", new FragmentExpansionOperation());
        ops.put(".", new DotExpansionOperation());
        ops.put("/", new PathExpansionOperation());
        ops.put(";", new PathParamExpansionOperation());
        ops.put("?", new FormExpansionOperation());
        ops.put("&", new QueryExpansionOperation());
        return ops;
    }

    /**
     * Register a new operation. The built in operations cannot be 
     * overridden. Key should be a single character. This method
     * is not synchronized; it is not recommended that registrations 
     * be allowed from multiple threads or while multiple threads are
     * expanding templates. Perform all registrations <i>before</i>
     * any template expansion occurs.
     */
    public static void register(String key, Operation operation) {
        if ("+#./;?&".contains(key))
          throw new IllegalArgumentException(
            "Cannot override reserved operators");
        operations.put(key, operation);
    }

    public static Operation get(String name) {
        if (name == null)
            name = "";
        Operation op = operations.get(name);
        if (op != null)
            return op;
        throw new UnsupportedOperationException(name);
    }

    protected static String eval(
        VarSpec varspec, 
        Context context,
        boolean reserved, 
        String explodeDelim, 
        String explodePfx) {
        String name = varspec.getName();
        Object rep = context.resolve(name);
        String val = toString(
            rep, 
            context, 
            reserved, 
            varspec.isExplode(), 
            explodeDelim, 
            explodePfx,
            varspec.getLength());
        return val;
    }
    
    private static CharSequence trim(CharSequence val, int len) {
      if (val != null && len > -1 && val.length() > len)
        val = val.subSequence(0,len);
      return val;
    }
    
    private static String normalize(CharSequence s) {
      return Normalizer2.getInstance(
        null, 
        "nfc", 
        Normalizer2.Mode.COMPOSE)
          .normalize(s);
    }

    private static String toString(
        Object val, 
        Context context, 
        boolean reserved, 
        boolean explode, 
        String explodeDelim, 
        String explodePfx,
        int len) {
        if (val == null)
            return null;
        if (val.getClass().isArray()) {
            if (val instanceof byte[]) {
                return UrlEncoding.encode((byte[])val);
            } else if (val instanceof char[]) {
                String chars = (String)trim(new String((char[])val),len);
                return !reserved ?
                  UrlEncoding.encode(
                    normalize(chars), 
                    context.isIri()
                      ? CharUtils.Profile.IUNRESERVED : 
                        CharUtils.Profile.UNRESERVED) :
                  UrlEncoding.encode(
                      normalize(chars),
                      context.isIri()
                        ? CharUtils.Profile.RESERVEDANDIUNRESERVED : 
                          CharUtils.Profile.RESERVEDANDUNRESERVED);                
            } else if (val instanceof short[]) {
                StringBuilder buf = new StringBuilder();
                short[] array = (short[])val;
                for (short obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else if (val instanceof int[]) {
                StringBuilder buf = new StringBuilder();
                int[] array = (int[])val;
                for (int obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else if (val instanceof long[]) {
                StringBuilder buf = new StringBuilder();
                long[] array = (long[])val;
                for (long obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else if (val instanceof double[]) {
                StringBuilder buf = new StringBuilder();
                double[] array = (double[])val;
                for (double obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else if (val instanceof float[]) {
                StringBuilder buf = new StringBuilder();
                float[] array = (float[])val;
                for (float obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else if (val instanceof boolean[]) {
                StringBuilder buf = new StringBuilder();
                boolean[] array = (boolean[])val;
                for (boolean obj : array) {
                    if (buf.length() > 0)
                        buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                    if (explode && explodePfx != null) 
                      buf.append(explodePfx);
                    buf.append(trim(String.valueOf(obj),len));
                }
                return buf.toString();
            } else {
                StringBuilder buf = new StringBuilder();
                Object[] array = (Object[])val;
                for (Object obj : array) {
                  if (buf.length() > 0)
                    buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                  if (explode && explodePfx != null) 
                    buf.append(explodePfx);
                  buf.append(toString(obj, context, reserved, false, null, null, len));
                }
                return buf.toString();
            }
        } else if (val instanceof InputStream) {
            try {
                if (len > -1) {
                  byte[] buf = new byte[len];
                  int r = ((InputStream)val).read(buf);
                  byte[] dat = new byte[r];
                  System.arraycopy(buf, 0, dat, 0, r);
                  val = new ByteArrayInputStream(dat);
                }
                return UrlEncoding.encode((InputStream)val);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (val instanceof Readable) {
            try { 
                if (len > -1) {
                  CharBuffer buf = CharBuffer.allocate(len);
                  int r = ((Readable)val).read(buf);
                  buf.limit(r);
                  buf.position(0);
                  val = buf;
                }
                return !reserved ?
                    UrlEncoding.encode(
                      (Readable)val, 
                      "UTF-8", 
                      context.isIri() ? 
                          CharUtils.Profile.IUNRESERVED : 
                          CharUtils.Profile.UNRESERVED) :
                    UrlEncoding.encode(
                      (Readable)val,
                      "UTF-8",
                      context.isIri() ?
                          CharUtils.Profile.RESERVEDANDIUNRESERVED :
                          CharUtils.Profile.RESERVEDANDUNRESERVED);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (val instanceof CharSequence) {
            val = normalize((CharSequence)val);
            return encode((CharSequence)val, context.isIri(), reserved);
        } else if (val instanceof Byte) {
            return UrlEncoding.encode(((Byte)val).byteValue());
        } else if (val instanceof Iterable) {
            StringBuilder buf = new StringBuilder();
            Iterable<Object> i = (Iterable<Object>)val;
            for (Object obj : i) {
                if (buf.length() > 0)
                  buf.append(explode && explodeDelim != null ? explodeDelim : ",");
                if (explode && explodePfx != null) 
                  buf.append(explodePfx);
                buf.append(toString(obj, context, reserved, false, null, null, len));
            }
            return buf.toString();
        } else if (val instanceof Iterator) {
          StringBuilder buf = new StringBuilder();
          Iterator<Object> i = (Iterator<Object>)val;
          while (i.hasNext()) {
              Object obj = i.next();
              if (buf.length() > 0)
                buf.append(explode && explodeDelim != null ? explodeDelim : ",");
              if (explode && explodePfx != null) 
                buf.append(explodePfx);
              buf.append(toString(obj, context, reserved, false, null, null, len));
          }
          return buf.toString();
        } else if (val instanceof Enumeration) {
          StringBuilder buf = new StringBuilder();
          Enumeration<Object> i = (Enumeration<Object>)val;
          while (i.hasMoreElements()) {
              Object obj = i.nextElement();
              if (buf.length() > 0)
                buf.append(explode && explodeDelim != null ? explodeDelim : ",");
              if (explode && explodePfx != null) 
                buf.append(explodePfx);
              buf.append(toString(obj, context, reserved, false, null, null, len));
          }
          return buf.toString();
        } else if (val instanceof Map) {
            StringBuilder buf = new StringBuilder();
            Map<Object,Object> map = (Map<Object,Object>)val;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
              String _key = toString(entry.getKey(), context, reserved, false, null, null, len);
              String _val = toString(entry.getValue(), context, reserved, false, null, null, len);
              if (buf.length() > 0)
                buf.append(explode && explodeDelim != null ? explodeDelim : ",");
              buf.append(_key)
                 .append(explode ? '=' : ',')
                 .append(_val);
            }
            return buf.toString();
        } else {
            if (val != null)
              val = normalize(val.toString());
            return encode(val != null ? val.toString() : null, context.isIri(), reserved);
        }
    }

    private static String encode(CharSequence val, boolean isiri, boolean reserved) {
        String v = normalize(val);
        return !reserved ?
          UrlEncoding.encode(v, 
            isiri
              ? CharUtils.Profile.IUNRESERVED : 
                CharUtils.Profile.UNRESERVED) :
          UrlEncoding.encode(v, 
              isiri
              ? CharUtils.Profile.RESERVEDANDIUNRESERVED : 
                CharUtils.Profile.RESERVEDANDUNRESERVED);
    }

    /**
     * Simple String Expansion ({VAR})
     */
    private static final class DefaultOperation extends Operation {
      private static final long serialVersionUID = 8676696520810767327L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, false, ",", null);
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }
    
    /**
     * Reserved Expansion Operation ({+VAR})
     */
    private static final class ReservedExpansionOperation extends Operation {
        private static final long serialVersionUID = 1736980072492867748L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, true, ",", null);
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }

    /**
     * Fragment Expansion Operation ({#VAR})
     */
    private static final class FragmentExpansionOperation extends Operation {
        private static final long serialVersionUID = -2207953454022197435L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (VarSpec varspec : exp) {
              if (!first) buf.append(',');
              String val = eval(varspec, context, true, ",", null);
              if (first && val != null)
                buf.append('#');
              buf.append(val != null ? val : "");
              first = false;
            }
            return buf.toString();
        }
    }    

    /**
     * Dot Expansion Operation ({.VAR})
     */
    private static final class DotExpansionOperation extends Operation {
        private static final long serialVersionUID = -4357734926260213270L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, true, ".", null);
              if (val != null)
                buf.append('.');
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    } 
    

    /**
     * Path Expansion Operation ({/VAR})
     */
    private static final class PathExpansionOperation extends Operation {
        private static final long serialVersionUID = 5578346646541533713L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "/", null);
              if (val != null)
                buf.append('/');
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    }
    
    /**
     * Path Param Expansion Operation ({;VAR})
     */
    private static final class PathParamExpansionOperation extends Operation {
        private static final long serialVersionUID = 4556090632293646419L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, ";", null);
              if (val != null)
                buf.append(';');
              if (!varspec.isExplode()) {
                if (val != null)
                  buf.append(varspec.getName());
                if (val != null && val.length() > 0)
                  buf.append("=");
              }
              buf.append(val != null ? val : "");
            }
            return buf.toString();
        }
    }
    
    /**
     * Form Expansion Operation ({?VAR})
     */
    private static final class FormExpansionOperation extends Operation {  
        private static final long serialVersionUID = -2166695868296435715L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            buf.append("?");
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "&", varspec.getName() + "=");
              if (context.contains(varspec.getName())) {
                if (!first) buf.append('&');
                if ((val != null && !varspec.isExplode()) || varspec.isNoval()) {
                  buf.append(varspec.getName());
                }
                if (val != null && !varspec.isExplode() && (!varspec.isNoval() || val.length() > 0) )
                  buf.append("=");
                if (val != null && val.length() > 0)
                  buf.append(val);
              }
              first = false;
            } 
            return buf.toString();
        }
    }
    
    /**
     * Query Expansion Operation ({&VAR})
     */
    private static final class QueryExpansionOperation extends Operation {
        private static final long serialVersionUID = 4029538625501399067L;
        public String evaluate(Expression exp, Context context) {
            StringBuilder buf = new StringBuilder();
            for (VarSpec varspec : exp) {
              String val = eval(varspec, context, false, "&", varspec.getName() + "=");
              if (context.contains(varspec.getName())) {
                if ((val != null && !varspec.isExplode()) || varspec.isNoval())
                  buf.append('&').append(varspec.getName());
                if (val != null && !varspec.isExplode() && (!varspec.isNoval() || val.length() > 0) )
                  buf.append("=");
                  if (val != null && val.length() > 0)
                    buf.append(val);
                }
            }
            return buf.toString();
        }
    }
}
