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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.Expression;
import org.apache.abdera2.common.templates.Operation;

public class Expression 
  implements Iterable<Expression.VarSpec>, Serializable {
  
  private static final long serialVersionUID = 1457650843240079628L;
  private static final Pattern EXPRESSION = Pattern.compile("([\\+\\#\\.\\/\\;\\?\\&\\=\\,\\!\\@\\|\\$\\(\\)])?(.*){1}");
  private static final Pattern VARSPEC = Pattern.compile("([^(?:\\:\\d+)\\*\\^]+)([(?:\\:\\d+)\\*\\^]+)?");
  private static final Pattern LENGTH = Pattern.compile("\\:(\\d+)");
  
  private final String EXP;
  private Operation op;
  private List<VarSpec> varspecs = 
    new ArrayList<VarSpec>();
  
  public Expression(String exp) {
    this.EXP = exp;
    parse();
  }
  
  public String toString() {
    return EXP;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((EXP == null) ? 0 : EXP.hashCode());
    result = prime * result + ((op == null) ? 0 : op.hashCode());
    result = prime * result + ((varspecs == null) ? 0 : varspecs.hashCode());
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
    Expression other = (Expression) obj;
    if (EXP == null) {
      if (other.EXP != null)
        return false;
    } else if (!EXP.equals(other.EXP))
      return false;
    if (op == null) {
      if (other.op != null)
        return false;
    } else if (!op.equals(other.op))
      return false;
    if (varspecs == null) {
      if (other.varspecs != null)
        return false;
    } else if (!varspecs.equals(other.varspecs))
      return false;
    return true;
  }

  public Operation getOperation() {
    return op;
  }
  
  public String evaluate(Context context) {
    return getOperation().evaluate(this, context);
  }
  
  public Iterator<Expression.VarSpec> iterator() {
    return varspecs.iterator();
  }

  private void parse() {
    Matcher mt = EXPRESSION.matcher(EXP);
    if (mt.find()) {
      this.op = Operation.get(mt.group(1)); // grab the operation
      String varlist = mt.group(2);
      if (varlist == null)
          throw new IllegalArgumentException(
              "Invalid Expression: No variables");
      String[] vars = 
        varlist.split("\\s*,\\s*");
      for (String var : vars) {
        Matcher vt = VARSPEC.matcher(var);
        if (vt.find()) {
          VarSpec spec = new VarSpec(vt.group(1),vt.group(2));
          varspecs.add(spec);
        } else {
          throw new IllegalArgumentException(
            "Invalid Expression: Invalid variable spec");
        }
      }
    } else {
      throw new IllegalArgumentException("Invalid Expression");
    }
  }
  
  
  public static class VarSpec {
    private final String name;
    private final int length;
    private final boolean explode;
    private final boolean noval;
    public VarSpec(String name, String modifier) {
      this.name = name;
      this.length = getLength(modifier);
      this.explode = isExplode(modifier);
      this.noval = isNoval(modifier);
    }
    private int getLength(String modifier) {
      if (modifier == null || "".equals(modifier)) 
        return -1;
      Matcher m = LENGTH.matcher(modifier);
      if (m.find()) {
          return Integer.parseInt(m.group(1));
      } else return -1;
    }
    private boolean isNoval(String modifier) {
      return "^".equals(modifier);
    }
    private boolean isExplode(String modifier) {
      return "*".equals(modifier);
    }
    public String getName() {
      return name;
    }
    public int getLength() {
      return length;
    }
    public boolean isNoval() {
      return noval;
    }
    public boolean isExplode() {
      return explode;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (explode ? 1231 : 1237);
      result = prime * result + length;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      VarSpec other = (VarSpec) obj;
      if (explode != other.explode)
        return false;
      if (length != other.length)
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }
  }

}
