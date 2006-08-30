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
package org.apache.abdera.protocol.server.util;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.WriterFactory;

public class ServerUtils {

  private static class QTokenComparator 
    implements Comparator<QToken> {
      public int compare(QToken o1, QToken o2) {
        if (o1.qvalue > o2.qvalue) 
          return -1;
        if (o1.qvalue < o2.qvalue) 
          return 1;
        return 0;
      }
  }
  
  private static class QToken {
    String token;
    double qvalue = 1.0;
    QToken(String token,double qvalue) {
      this.token = token;
      this.qvalue = qvalue;
    }
  }
  
  /**
   * <p>Utility method for parsing HTTP content negotiation headers and sorting
   * their tokens according to their q parameter values.</p>
   * 
   * <p>e.g. Accept: audio/*; q=0.2, audio/basic, audio/mpeg; q=0.1</p>
   * 
   * <p>would sort into:</p>
   * <pre>
   *   audio/basic
   *   audio/*
   *   audio/mpeg
   * </pre>
   */
  public static String[] orderByQ(String header) {
    if (header == null || header.length() == 0) return new String[0];
    String[] tokens = header.split(",");
    QToken[] qtokens = new QToken[tokens.length];
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i];
      String[] qvalues = token.trim().split(";");
      String t = qvalues[0];
      if (qvalues.length > 1) {
        for (int n = 1; n < qvalues.length; n++) {
          String[] v = qvalues[n].trim().split("=");
          if (v[0].trim().equals("q")) {
            double qv = Double.parseDouble(v[1]);
            qtokens[i] = new QToken(t,qv);
            break;
          }
        }
      }
      if (qtokens[i] == null) qtokens[i] = new QToken(t,1.0);
    }
    Arrays.sort(qtokens, new QTokenComparator());
    tokens = new String[qtokens.length];
    for (int n = 0; n < qtokens.length; n++) {
      tokens[n] = qtokens[n].token;
    }
    return tokens;
  }
  
  /**
   * Returns an appropriate NamedWriter instance given an appropriately
   * formatted HTTP Accept header.  The header will be parsed and sorted
   * according to it's q parameter values.  The first named writer capable
   * of supporting the specified type, in order of q-value preference, will
   * be returned.  The results on this are not always predictable.  For instance,
   * if the Accept header says "application/*" it could end up with either the
   * JSON writer or the PrettyXML writer, or any other writer that supports
   * any writer that supports a specific form of "application/*".  It's always
   * best to be very specific in the Accept headers.
   */
  public static NamedWriter getAcceptableNamedWriter(
    Abdera abdera, String accept_header) {
      String[] sorted_accepts = orderByQ(accept_header);
      WriterFactory factory = abdera.getWriterFactory();
      if (factory == null) return null;
      for (String accept : sorted_accepts) {
        NamedWriter writer = (NamedWriter) factory.getWriterByMediaType(accept);
        if (writer != null) return writer;
      }
      return null;
  }
  
}
