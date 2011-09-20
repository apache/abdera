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
package org.apache.abdera2.common.text;

import org.apache.abdera2.common.xml.XMLVersion;

/**
 * General utilities for dealing with Unicode characters
 */
public final class CharUtils {

    private CharUtils() {
    }

    private static boolean inRange(int codepoint, int low, int high) {
        return codepoint >= low && codepoint <= high;
    }

    /**
     * True if the codepoint is a valid hex digit
     */
    private static boolean isHex (int codepoint){
        return isDigit(codepoint) || 
               CharUtils.inRange(codepoint, 'a', 'f') || 
               CharUtils.inRange(codepoint, 'A', 'F');
    }
    
    private interface Filter {
      /**
       * Return true if the codepoint should be rejected, false if it 
       * should be accepted
       */
      boolean filter(int c);

      public static final Filter NONOPFILTER = new Filter() {
          public boolean filter(int c) {
              return true;
          }
      };
  }


    public static enum Profile {
        NONE(new Filter() {
            public boolean filter(int codepoint) {
                return true;
            }
        }), NONOP(Filter.NONOPFILTER)
          , ALPHA(new Filter() {
            public boolean filter(int codepoint) {
                return !isAlpha(codepoint);
            }
        }), ALPHANUM(new Filter() {
            public boolean filter(int codepoint) {
                return !isAlphaDigit(codepoint);
            }
        }), FRAGMENT(new Filter() {
            public boolean filter(int codepoint) {
                return !isFragment(codepoint);
            }
        }), IFRAGMENT(new Filter() {
            public boolean filter(int codepoint) {
                return !is_ifragment(codepoint);
            }
        }), PATH(new Filter() {
            public boolean filter(int codepoint) {
                return !isPath(codepoint);
            }
        }), IPATH(new Filter() {
            public boolean filter(int codepoint) {
                return !is_ipath(codepoint);
            }
        }), IUSERINFO(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iuserinfo(codepoint);
            }
        }), USERINFO(new Filter() {
            public boolean filter(int codepoint) {
                return !isUserInfo(codepoint);
            }
        }), QUERY(new Filter() {
            public boolean filter(int codepoint) {
                return !isQuery(codepoint);
            }
        }), IQUERY(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iquery(codepoint);
            }
        }), SCHEME(new Filter() {
            public boolean filter(int codepoint) {
                return !isScheme(codepoint);
            }
        }), PATHNODELIMS(new Filter() {
            public boolean filter(int codepoint) {
                return !isPathNoDelims(codepoint);
            }
        }), IPATHNODELIMS(new Filter() {
            public boolean filter(int codepoint) {
                return !is_ipathnodelims(codepoint);
            }
        }), IPATHNODELIMS_SEG(new Filter() {
            public boolean filter(int codepoint) {
                return !is_ipathnodelims(codepoint) && codepoint != '@' && codepoint != ':';
            }
        }), IREGNAME(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iregname(codepoint);
            }
        }), IHOST (new Filter(){
            public boolean filter(int codepoint){
                return !is_ihost(codepoint);
            }
        }), IPRIVATE(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iprivate(codepoint);
            }
        }), RESERVED(new Filter() {
            public boolean filter(int codepoint) {
                return !isReserved(codepoint);
            }
        }), IUNRESERVED(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iunreserved(codepoint);
            }
        }), UNRESERVED(new Filter() {
            public boolean filter(int codepoint) {
                return !isUnreserved(codepoint);
            }            

    }), RESERVEDANDUNRESERVED(new Filter() {
        public boolean filter(int codepoint) {
          return !isUnreserved(codepoint) && 
                 !isReserved(codepoint);
        }
    }), RESERVEDANDIUNRESERVED(new Filter() {
        public boolean filter(int codepoint) {
          return !is_iunreserved(codepoint) && 
                 !isReserved(codepoint);
        }
        
    }), XML1RESTRICTED(new Filter() {
        public boolean filter(int codepoint) {
          return restricted(XMLVersion.XML10, codepoint);
        }
    }), XML11RESTRICTED(new Filter() {
      public boolean filter(int codepoint) {
        return restricted(XMLVersion.XML11, codepoint);
      }
    }), RFC5987(new Filter() {
      public boolean filter(int codepoint) {
        return !is5987(codepoint);
      }
    }), TOKEN(new Filter() {
      public boolean filter(int codepoint) {
        return !isToken(codepoint);
      }
        }), SCHEMESPECIFICPART(new Filter() {
            public boolean filter(int codepoint) {
                return !is_iunreserved(codepoint) && !isReserved(codepoint)
                    && !is_iprivate(codepoint)
                    && !isPctEnc(codepoint)
                    && codepoint != '#';
            }
        }), AUTHORITY(new Filter() {
            public boolean filter(int codepoint) {
                return !is_regname(codepoint) && !isUserInfo(codepoint) && !isGenDelim(codepoint);
            }
        });
        
        private final Filter filter;

        Profile(Filter filter) {
            this.filter = filter;
        }

        public Filter filter() {
            return filter;
        }

        public boolean filter(int codepoint) {
            return filter.filter(codepoint);
        }
    }

    public static boolean is5987(int codepoint) {
      return isAlphaDigit(codepoint) || 
             codepoint == '!' || 
             codepoint == '#' ||
             codepoint == '$' || 
             codepoint == '&' || 
             codepoint == '+' || 
             codepoint == '-' || 
             codepoint == '.' ||
             codepoint == '^' || 
             codepoint == '_' || 
             codepoint == '`' || 
             codepoint == '|' || 
             codepoint == '~';
    }
    
    public static boolean isPctEnc(int codepoint) {
        return codepoint == '%' || isDigit(codepoint)
            || CharUtils.inRange(codepoint, 'A', 'F')
            || CharUtils.inRange(codepoint, 'a', 'f');
    }

    public static boolean isMark(int codepoint) {
        return codepoint == '-' || codepoint == '_'
            || codepoint == '.'
            || codepoint == '!'
            || codepoint == '~'
            || codepoint == '*'
            || codepoint == '\\'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '`'; // JIRA: https://issues.apache.org/jira/browse/ABDERA-238
    }

    public static boolean isUnreserved(int codepoint) {
        return isAlphaDigit(codepoint) || 
               codepoint == '-' || 
               codepoint == '.' || 
               codepoint == '_' || 
               codepoint == '~' || 
               codepoint == '`';
    }

    public static boolean isReserved(int codepoint) {
        return isGenDelim(codepoint) || isSubDelim(codepoint);
    }

    public static boolean isGenDelim(int codepoint) {
        return codepoint == ':' 
            || codepoint == '/'
            || codepoint == '?'
            || codepoint == '#'
            || codepoint == '['
            || codepoint == ']'
            || codepoint == '@';
    }

    public static boolean isSubDelim(int codepoint) {
        return codepoint == '!' 
            || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '\\';
    }

    public static boolean isPchar(int codepoint) {
        return isUnreserved(codepoint) || codepoint == ':'
            || codepoint == '@'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$'
            || codepoint == ',';
    }

    public static boolean isPath(int codepoint) {
        return isPchar(codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '%' || codepoint == ',';
    }

    public static boolean isPathNoDelims(int codepoint) {
        return isPath(codepoint) && !isGenDelim(codepoint);
    }

    public static boolean isScheme(int codepoint) {
        return isAlphaDigit(codepoint) || codepoint == '+' || codepoint == '-' || codepoint == '.';
    }

    public static boolean isUserInfo(int codepoint) {
        return isUnreserved(codepoint) || isSubDelim(codepoint) || isPctEnc(codepoint);
    }

    public static boolean isQuery(int codepoint) {
        return isPchar(codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '?' || codepoint == '%';
    }

    public static boolean isFragment(int codepoint) {
        return isPchar(codepoint) || codepoint == '/' || codepoint == '?' || codepoint == '%';
    }

    public static boolean is_ucschar(int codepoint) {
        return CharUtils.inRange(codepoint, '\u00A0', '\uD7FF') 
            || CharUtils.inRange(codepoint, '\uF900', '\uFDCF')
            || CharUtils.inRange(codepoint, '\uFDF0', '\uFFEF')
            || CharUtils.inRange(codepoint, 0x10000, 0x1FFFD)
            || CharUtils.inRange(codepoint, 0x20000, 0x2FFFD)
            || CharUtils.inRange(codepoint, 0x30000, 0x3FFFD)
            || CharUtils.inRange(codepoint, 0x40000, 0x4FFFD)
            || CharUtils.inRange(codepoint, 0x50000, 0x5FFFD)
            || CharUtils.inRange(codepoint, 0x60000, 0x6FFFD)
            || CharUtils.inRange(codepoint, 0x70000, 0x7FFFD)
            || CharUtils.inRange(codepoint, 0x80000, 0x8FFFD)
            || CharUtils.inRange(codepoint, 0x90000, 0x9FFFD)
            || CharUtils.inRange(codepoint, 0xA0000, 0xAFFFD)
            || CharUtils.inRange(codepoint, 0xB0000, 0xBFFFD)
            || CharUtils.inRange(codepoint, 0xC0000, 0xCFFFD)
            || CharUtils.inRange(codepoint, 0xD0000, 0xDFFFD)
            || CharUtils.inRange(codepoint, 0xE1000, 0xEFFFD);
    }

    public static boolean is_iprivate(int codepoint) {
        return CharUtils.inRange(codepoint, '\uE000', '\uF8FF') 
            || CharUtils.inRange(codepoint, 0xF0000, 0xFFFFD)
            || CharUtils.inRange(codepoint, 0x100000, 0x10FFFD);
    }

    public static boolean is_iunreserved(int codepoint) {
        return isAlphaDigit(codepoint) 
            || isMark(codepoint) 
            || is_ucschar(codepoint);
    }

    public static boolean is_ipchar(int codepoint) {
        return is_iunreserved(codepoint) || isSubDelim(codepoint)
            || codepoint == ':'
            || codepoint == '@'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$';
    }

    public static boolean is_ipath(int codepoint) {
        return is_ipchar(codepoint) 
            || codepoint == ';' 
            || codepoint == '/' 
            || codepoint == '%' 
            || codepoint == ',';
    }

    public static boolean is_ipathnodelims(int codepoint) {
        return is_ipath(codepoint) && !isGenDelim(codepoint);
    }

    public static boolean is_iquery(int codepoint) {
        return is_ipchar(codepoint) || is_iprivate(codepoint)
            || codepoint == ';'
            || codepoint == '/'
            || codepoint == '?'
            || codepoint == '%';
    }

    public static boolean is_ifragment(int codepoint) {
        return is_ipchar(codepoint) || is_iprivate(codepoint)
            || codepoint == '/'
            || codepoint == '?'
            || codepoint == '%';
    }

    public static boolean is_iregname(int codepoint) {
        return is_iunreserved(codepoint) || codepoint == '!'
            || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '"';
    }
    
    public static boolean is_ipliteral (int codepoint){
        return isHex(codepoint) || codepoint==':' 
            || codepoint =='[' 
            || codepoint==']';
    }
    
    public static boolean is_ihost (int codepoint){
        return is_iregname(codepoint) || is_ipliteral(codepoint);
    }

    public static boolean is_regname(int codepoint) {
        return isUnreserved(codepoint) || codepoint == '!'
            || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '"';
    }

    public static boolean is_iuserinfo(int codepoint) {
        return is_iunreserved(codepoint) || codepoint == ';'
            || codepoint == ':'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$'
            || codepoint == ',';
    }

    public static boolean is_iserver(int codepoint) {
        return is_iuserinfo(codepoint) || is_iregname(codepoint)
            || isAlphaDigit(codepoint)
            || codepoint == '.'
            || codepoint == ':'
            || codepoint == '@'
            || codepoint == '['
            || codepoint == ']'
            || codepoint == '%'
            || codepoint == '-';
    }

    private static boolean check(int cp, Filter filter, boolean invert) {
      boolean answer = !filter.filter(cp);
      return (!invert) ? !answer : answer;
  }
    
    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verify(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
        while (ci.hasNext()) {
            int cp = ci.next();
            if (check(cp,filter,false))
              throw new InvalidCharacterException(cp);
        }
    }

    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verify(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        verify(ci, profile.filter());
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verify(char[] s, Profile profile) throws InvalidCharacterException {
        if (s == null)
            return;
        verify(CodepointIterator.getInstance(s), profile);
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verify(String s, Profile profile) throws InvalidCharacterException {
        if (s == null)
            return;
        verify(CodepointIterator.getInstance(s), profile);
    }

    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verifyNot(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
      while (ci.hasNext()) {
        int cp = ci.next();
        if (check(cp,filter,true))
          throw new InvalidCharacterException(cp);
      }
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verifyNot(String s, Profile profile) throws InvalidCharacterException {
        if (s == null)
            return;
        verifyNot(CodepointIterator.getInstance(s), profile);
    }
    
    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verifyNot(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        verifyNot(ci,profile.filter());
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verifyNot(char[] array, Profile profile) throws InvalidCharacterException {
      if (array == null)
        return;
      verifyNot(CodepointIterator.getInstance(array), profile);
    }
    
    /**
     * True if the codepoint is a digit
     */
    public static boolean isDigit(int codepoint) {
        return CharUtils.inRange(codepoint, '0', '9');
    }

    /**
     * True if the codepoint is part of the ASCII alphabet (a-z, A-Z)
     */
    public static boolean isAlpha(int codepoint) {
        return CharUtils.inRange(codepoint, 'A', 'Z') || CharUtils.inRange(codepoint, 'a', 'z');
    }

    /**
     * True if isAlpha and isDigit both return true
     */
    public static boolean isAlphaDigit(int codepoint) {
        return isAlpha(codepoint) || isDigit(codepoint);
    }
    
    public static boolean isToken(int codepoint) {
      return isAscii(codepoint) && !isCtl(codepoint) && !isSep(codepoint);
    }
    
    public static boolean isToken(String token) {
      if (token == null) return false;
      int l = token.length();
      for (int n = 0; n < l; n++)
        if (!isToken(token.charAt(n)))
          return false;
      return true;
    }
    
    public static boolean isAscii(int codepoint) {
      return codepoint >= 0 && codepoint <= 127;
    }
    
    public static boolean isCtl(int codepoint) {
      return (codepoint >= 0 && codepoint <= 31) || codepoint == 127;
    }
    
    public static boolean isSep(int codepoint) {
      return codepoint == '(' ||
             codepoint == ')' ||
             codepoint == '<' ||
             codepoint == '>' ||
             codepoint == '@' ||
             codepoint == ',' || 
             codepoint == ';' ||
             codepoint == ':' ||
             codepoint == '\\' ||
             codepoint == '"' ||
             codepoint == '/' ||
             codepoint == '[' ||
             codepoint == ']' ||
             codepoint == '?' ||
             codepoint == '=' ||
             codepoint == '{' ||
             codepoint == '}' ||
             codepoint == 32 ||
             codepoint == 9;
    }

    public static String unwrap(String st, char x, char y) {
      if (st == null || st.length() == 0)
        return st;
      int n = 0, e = st.length();
      if (st.charAt(0) == x) n++;
      if (st.charAt(e-1) == y) e--;
      return st.substring(n,e);
    }
    
    public static String unquote(String s) {
      if (s == null || s.length() == 0)
        return s;
      int n = 0, e = s.length();
      if (s.charAt(0) == '"') n++;
      if (s.charAt(e-1) == '"' && s.charAt(e-2) != '\\') e--;
      return s.substring(n,e);
    }

    public static String[] splitAndTrim(
        String value) {
          if (value == null || value.length() == 0)
            return new String[0];
          return unquote(value).split("\\s*,\\s*");
    }

    /**
     * Treats the specified int array as an Inversion Set and returns true if the value is located within the set. This
     * will only work correctly if the values in the int array are monotonically increasing
     */
    public static boolean invset_contains(int[] set, int value) {
        int s = 0, e = set.length;
        while (e - s > 8) {
            int i = (e + s) >> 1;
            s = set[i] <= value ? i : s;
            e = set[i] > value ? i : e;
        }
        while (s < e) {
            if (value < set[s])
                break;
            s++;
        }
        return ((s - 1) & 1) == 0;
    }
    
    // inversion set
    private static int[] RESTRICTED_SET_v1 = {0, 9, 11, 13, 14, 32, 55296, 57344, 65534, 65536};

    // inversion set
    private static int[] RESTRICTED_SET_v11 = {11, 13, 14, 32, 127, 160, 55296, 57344, 65534, 65536};

    public static boolean restricted(XMLVersion version, char c) {
        return restricted(version, (int)c);
    }

    public static boolean restricted(XMLVersion version, int c) {
        return CharUtils.invset_contains(version == XMLVersion.XML10 ? RESTRICTED_SET_v1 : RESTRICTED_SET_v11, c);
    }
    
    public static String quotedIfNotToken(String value) {
      return isToken(value)?value:quoted(value,true);
    }

    public static String quoted(String val, boolean wrap) {
      StringBuilder buf = new StringBuilder();
      if (wrap) buf.append('"');
      int l = val.length();
      for (int n = 0; n < l; n++) {
        char c = val.charAt(n);
        if (c == '"')
          buf.append('\\');
        buf.append(c);
      }
      if (wrap) buf.append('"');
      return buf.toString();
    }

    public static int scanFor(char c, String text, int s, boolean errifnotws) {
      return scanFor(c,text,s,errifnotws,',');
    }
    
    public static int scanFor(char c, String text, int s, boolean errifnotws, char breakat) {
      boolean inquoted = false;
      int l = text.length();
      for (int n = s; n < l; n++) {
        char ch = text.charAt(n);
          if (ch == '"') inquoted = !inquoted;
          if (ch == breakat && !inquoted) return n;
          if (ch == c) return n;
          if (errifnotws && Character.isWhitespace(ch))
            throw new InvalidCharacterException(ch);
      }
      return -1;
    }
    
}
