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

import java.io.Serializable;
import java.util.Arrays;

public final class QualityHelper {

  private QualityHelper() {}

  /**
   * A "Qualified Token"... HTTP Headers used for content negotation 
   * (e.g. Accept-Language) assign a Q-value for each acceptable option.
   * The QValue is between 0.0 and 1.0 inclusive, with 1.0 being the 
   * most acceptable option and 0.0 being completely unacceptable.
   * 
   * The QToken class wraps a qualified option and provides access to
   * it's Q-Value. 
   */
  public final static class QToken 
    implements Comparable<QToken>, Serializable {

    private static final long serialVersionUID = -4461686265000839487L;
      private final String token;
      private final double qvalue;
      QToken(String token, double qvalue) {
          this.token = token;
          this.qvalue = Math.max(0,Math.min(qvalue,1.0));
      }
      public int compareTo(QToken other) {
        return Double.compare(other.qvalue, qvalue);
      }
      public String token() {
        return token;
      }
      public double q() {
        return qvalue;
      }
      public String toString() {
        return token;
      }
      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(qvalue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((token == null) ? 0 : token.hashCode());
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
        QToken other = (QToken) obj;
        if (Double.doubleToLongBits(qvalue) != Double
            .doubleToLongBits(other.qvalue))
          return false;
        if (token == null) {
          if (other.token != null)
            return false;
        } else if (!token.equals(other.token))
          return false;
        return true;
      }
  }

  /**
   * <p>
   * Utility method for parsing HTTP content negotiation headers and sorting their 
   * qualified tokens according to q parameter values.
   * </p>
   * <p>
   * e.g. Accept: audio/*; q=0.2, audio/basic, audio/mpeg; q=0.1
   * </p>
   * <p>
   * would sort into:
   * </p>
   * 
   * <pre>
   *   audio/basic
   *   audio/*
   *   audio/mpeg
   * </pre>
   */
  public static QToken[] orderByQ(String header) {
      if (header == null || header.length() == 0)
          return new QToken[0];
      String[] tokens = header.split("\\s*,\\s*");
      QToken[] qtokens = new QToken[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
          String[] qvalues = tokens[i].split("\\s*;\\s*");
          String t = qvalues[0];
          if (qvalues.length > 1) {
              for (String qvalue : qvalues) {
                  String[] v = qvalue.split("\\s*=\\s*");
                  if (v[0].equalsIgnoreCase("q")) {
                      double qv = Double.parseDouble(v[1]);
                      qtokens[i] = new QToken(t, qv);
                      break;
                  }
              }
          }
          if (qtokens[i] == null)
              qtokens[i] = new QToken(t, 1.0);
      }
      Arrays.sort(qtokens);
      return qtokens;
  }
  
}
