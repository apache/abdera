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
package org.apache.abdera.util.unicode;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.abdera.util.io.CharUtils;


/**
 * An implementation of the Unicode Character Database modeled after the 
 * sample normalization demo available at: 
 * 
 * http://www.unicode.org/unicode/reports/tr15/Normalizer.html
 * 
 * for now, this has been implemented and tested against Unicode 3.2.0.  We 
 * need to test is against Unicode 4.0.
 */
final class UnicodeCharacterDatabase 
  implements Serializable, 
             Cloneable {

  private static final long serialVersionUID = 1596950870716625345L;

  private static final String UCD = "org/apache/abdera/util/unicode/data/ucd.res";
  
  private final HashMap<Integer,Integer> cc = new HashMap<Integer,Integer>();
  private final HashMap<Integer,String>  decompose = new HashMap<Integer,String>();
  private final HashMap<Integer,Integer> compose = new HashMap<Integer,Integer>();
  private final BitSet compatibility = new BitSet();
  private final BitSet excluded = new BitSet();
  
  private static UnicodeCharacterDatabase ucd = null;
  
  public synchronized static UnicodeCharacterDatabase getInstance() {
    if (ucd == null) {
      try {
        ucd = load();
      } catch (Exception e) {
      }
    }
    return ucd;
  }
  
  UnicodeCharacterDatabase() {}
  
  public int getCanonicalClass(int c) {
    return (cc.containsKey(c)) ? cc.get(c) : 0;
  }
  
  public boolean isComposite(int f, int s) {
    return !(f < 0 || f > 0x10FFFF || s < 0 || s > 0x10FFFF);
  }
  
  public char getPairComposition(int f, int s) {
    if (f < 0 || s > 0x10FFFF || s < 0 || s > 0x10FFFF) return '\uFFFF';
    Integer i = compose.get((f << 16) | s);
    return (i != null) ? (char)i.intValue() : '\uFFFF';
  }
  
  public void decompose(
    int c, 
    boolean canonical, 
    StringBuffer buf) {
      String d = decompose.get(c);
      if (d != null && !(canonical && compatibility.get(c))) {
        for (int i = 0; i < d.length(); ++i) {
            decompose(d.charAt(i), canonical, buf);
        }
      } else CharUtils.append(buf, c);
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public static UnicodeCharacterDatabase load() 
    throws IOException, 
           ClassNotFoundException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream is = cl.getResourceAsStream(UCD);
    GZIPInputStream gzip = new GZIPInputStream(is);
    ObjectInputStream ois = new ObjectInputStream(gzip);
    UnicodeCharacterDatabase ucd = (UnicodeCharacterDatabase) ois.readObject();
    ois.close();
    gzip.close();
    is.close();
    return ucd;
  }
  
  private static void save(
    UnicodeCharacterDatabase ucd, 
    String to) 
      throws IOException {
    FileOutputStream fos = new FileOutputStream(to);
    GZIPOutputStream gzip = new GZIPOutputStream(fos);
    ObjectOutputStream oos = new ObjectOutputStream(gzip);
    oos.writeObject(ucd);
    oos.close();
    gzip.close();
    fos.close();    
  }

  
  private static String base;
  private static String version;
  

  /**
   * Load the Unicode Character Database from the source files and save as 
   * a gzip compressed, serialized Java class.
   */
  public static void main(String... args) throws Exception {
    if (args.length == 0) usage();
    base = (args.length > 1) ? args[1]: "org/apache/abdera/util/unicode/data/";
    version = (args.length > 2) ? args[2]: "3.2.0";
    UnicodeCharacterDatabase ucd = UnicodeCharacterDatabase.getInstance();
    if (ucd == null) {
      ucd = new UnicodeCharacterDatabase();
      Loader.load(ucd);
    }
    save(ucd, args[0]);
  }
  
  private static void usage() {
    System.out.println("Usage:\n  java -cp $CLASSPATH com.ibm.usmall.UnicodeCharacterDatabase $filename $datapath");
    System.exit(0);
  }
  
  private static class Loader {
    
    private static final String EXCLUSIONS = "CompositionExclusions";
    private static final String UNICODEDATA = "UnicodeData";
    
//    private static final String EXCLUSIONS = 
//      "org/apache/abdera/util/unicode/data/CompositionExclusions-3.2.0.txt";
//    
//    private static final String UNICODEDATA =
//      "org/apache/abdera/util/unicode/data/UnicodeData-3.2.0.txt";
    
    static String filename(String target) {
      return base + (!base.endsWith("/")?"/":"") + target + "-" + version + ".txt";
    }
    
    static void load(UnicodeCharacterDatabase ucd) throws IOException {
      exclusions(ucd);
      decomposition(ucd);
    }
    
    static String stripcomments(String s) {
      int n = s.indexOf('#');
      return (n != -1) ? s.substring(0,n) : s;
    }
    
    static void exclusions(UnicodeCharacterDatabase ucd) throws IOException {
      BufferedReader r = read(filename(EXCLUSIONS));
      String line = null;
      while ((line = r.readLine()) != null) {
        line = stripcomments(line);
        if (line.length() == 0) continue;
        int v = Integer.parseInt(line.trim(),16);
        ucd.excluded.set(v);
      }
      r.close();
    }
    
    static String dehex(String t) {
      String[] ts = t.split(" ");
      StringBuffer buf = new StringBuffer();
      for (String token : ts) {
        if (token.charAt(0) != '<') {
          int n = Integer.parseInt(token.trim(), 16);
          buf.append((char)n);
        }
      }
      return buf.toString();
    }
    
    static void decomposition(UnicodeCharacterDatabase ucd) throws IOException {
      BufferedReader r = read(filename(UNICODEDATA));
      String line = null;
      while ((line = r.readLine()) != null) {
        line = stripcomments(line);
        if (line.length() == 0) continue;
        String[] tokens = line.split(";");
        int val = Integer.parseInt(tokens[0], 16);
        int cc = Integer.parseInt(tokens[3]);
        ucd.cc.put(val, cc);
        String decomp = tokens[5];
        if (decomp.length() != 0) {
          if (decomp.startsWith("<")) {
            ucd.compatibility.set(val);
          }
          decomp = dehex(decomp);
          ucd.decompose.put(val, decomp);
          if (!ucd.compatibility.get(val) && 
             !ucd.excluded.get(val)) {
            char f = (decomp.length() > 1) ? 
              decomp.charAt(0) : '\u0000';
            char l = (decomp.length() > 1) ? 
              decomp.charAt(1) : decomp.charAt(0);
            ucd.compose.put((f << 16) | l, val);
          }
        }
      }
      hanguls(ucd);
      r.close();
    }
    
    // Use the algorithm used in http://www.unicode.org/unicode/reports/tr15/NormalizerBuilder.java
    static void hanguls(UnicodeCharacterDatabase ucd) throws IOException {
      for (int s = 0; s < 0x2BA4; ++s) {
        int t = s % 0x001C;
        char f = (t != 0) ? 
          (char)(0xAC00 + s - t) : 
          (char)(0x1100 + s / 0x024C);
        char e = (t != 0) ? 
          (char)(0x11A7 + t) : 
          (char)(0x1161 + (s % 0x024C) / 0x001C);
        int pair = (f << 16) | e;
        int value = s + 0xAC00;
        ucd.decompose.put(value, String.valueOf(f) + e);
        ucd.compose.put(pair, value);
      }
    }
    
    static BufferedReader read(String f) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      InputStream in = cl.getResourceAsStream(f);
      InputStreamReader r = new InputStreamReader(in);
      BufferedReader buf = new BufferedReader(r);
      return buf;
    }
    
  }
  
}
