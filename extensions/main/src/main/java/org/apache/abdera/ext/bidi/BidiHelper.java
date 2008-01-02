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
package org.apache.abdera.ext.bidi;

import java.text.AttributedString;
import java.text.Bidi;
import java.util.Arrays;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/**
 * <p>This is (hopefully) temporary.  Ideally, this would be wrapped into the 
 * core model API so that the bidi stuff is handled seamlessly.  There are 
 * still details being worked out on the Atom WG list and it's likely that
 * at least one other impl (mozilla) will do something slightly different.</p>
 * 
 * <p>Based on http://www.ietf.org/internet-drafts/draft-snell-atompub-bidi-04.txt</p>
 * 
 * <p>Example:</p>
 * <pre>
 *   &lt;feed xmlns="http://www.w3.org/2005/Atom" dir="rtl">
 *     ...
 *   &lt;/feed>
 * </pre>
 * 
 * <p>The getBidi___ elements use the in-scope direction to wrap the text with 
 * the appropriate Unicode control characters. e.g. if dir="rtl", the text is
 * wrapped with the RLE and PDF controls.  If the text already contains the 
 * control chars, the dir attribute is ignored.</p>
 * 
 * <pre>
 *    org.apache.abdera.Abdera abdera = new org.apache.abdera.Abdera();
 *    org.apache.abdera.model.Feed feed = abdera.getFactory().newFeed();
 *    feed.setAttributeValue("dir", "rtl");
 *    feed.setTitle("Testing");
 *    feed.addCategory("foo");
 *    
 *    System.out.println(
 *      BidiHelper.getBidiElementText(
 *        feed.getTitleElement()));
 *    System.out.println(
 *      BidiHelper.getBidiAttributeValue(
 *        feed.getCategories().get(0),"term"));
 * </pre>
 * 
 */
public final class BidiHelper {

  private static final QName DIR = new QName("dir");
  
  BidiHelper() {}
  
  public enum Direction { UNSPECIFIED, LTR, RTL};
  
  /**
   * Set the value of dir attribute
   */
  public static <T extends Element>void setDirection(
    Direction direction, 
    T element) {
      if (direction != Direction.UNSPECIFIED)
        element.setAttributeValue(
          DIR, 
          direction.toString().toLowerCase());
      else if (direction == Direction.UNSPECIFIED)
        element.setAttributeValue(DIR,"");
      else if (direction == null)
        element.removeAttribute(DIR);
  }
  
  /**
   * Get the in-scope direction for an element.
   */
  public static <T extends Element>Direction getDirection(T element) {
    Direction direction = Direction.UNSPECIFIED;
    String dir = element.getAttributeValue("dir");
    if (dir != null && dir.length() > 0)
      direction = Direction.valueOf(dir.toUpperCase());
    else if (dir == null) {
      // if the direction is unspecified on this element, 
      // let's see if we've inherited it
      Base parent = element.getParentElement(); 
      if (parent != null && 
          parent instanceof Element)
        direction = getDirection((Element)parent);
    }
    return direction;
  }
  
  /**
   * Return the specified text with appropriate Unicode Control Characters given
   * the specified Direction.
   * @param direction The Directionality of the text
   * @param text The text to wrap within Unicode Control Characters
   * @return The directionally-wrapped text  
   */
  public static String getBidiText(Direction direction, String text) {
    switch (direction) {
      case LTR: return CharUtils.wrapBidi(text,CharUtils.LRE);
      case RTL: return CharUtils.wrapBidi(text,CharUtils.RLE);
      default:  return text;
    }
  }
  
  /**
   * Return the textual content of a child element using the in-scope directionality
   * @param element The parent element
   * @param child The XML QName of the child element
   * @return The directionally-wrapped text of the child element
   */
  public static <T extends Element>String getBidiChildText(T element, QName child) {
    Element el = element.getFirstChild(child);
    return (el != null) ? getBidiText(getDirection(el),el.getText()) : null;
  }
  
  /**
   * Return the textual content of the specified element
   * @param element An element containing directionally-sensitive text
   * @return The directionally-wrapped text of the element
   */
  public static <T extends Element>String getBidiElementText(T element) {
    return getBidiText(getDirection(element),element.getText());
  }
  
  /**
   * Return the text content of the specified attribute using the in-scope directionality
   * @param element The parent element
   * @param name the name of the attribute
   * @return The directionally-wrapped text of the attribute
   */
  public static <T extends Element>String getBidiAttributeValue(T element, String name) {
    return getBidiText(getDirection(element),element.getAttributeValue(name));
  }
  
  /**
   * Return the text content of the specified attribute using the in-scope directionality
   * @param element The parent element
   * @param name the name of the attribute
   * @return The directionally-wrapped text of the attribute
   */
  public static <T extends Element>String getBidiAttributeValue(T element, QName name) {
    return getBidiText(getDirection(element),element.getAttributeValue(name));
  }
  
  
  /**
   * Attempt to guess the base direction using the in-scope language.  
   * Implements the method used by Internet Explorer 7's feed view
   * documented here: http://blogs.msdn.com/rssteam/archive/2007/05/17/reading-feeds-in-right-to-left-order.aspx.
   * 
   * This algorithm differs slightly from the method documented in that the 
   * primary language tag is case insensitive.
   * 
   * If the language tag is not specified, then the default Locale is used to 
   * determine the direction.
   * 
   * If the dir attribute is specified, the direction will be determine using it's value 
   * instead of the language
   */
  public static <T extends Element>Direction guessDirectionFromLanguage(T element) {
    return guessDirectionFromLanguage(element, false);
  }
  
  private static final String[] RTL_LANGS = {
    "ar",
    "dv",
    "fa",
    "he",
    "ps",
    "syr",
    "ur",
    "yi"};
  
  private static final String[] RTL_SCRIPTS = {
    "arab","avst","hebr","hung","lydi","mand",
    "mani","mero","mong","nkoo","orkh","phlv",
    "phnx","samr","syrc","syre","syrj","syrn",
    "tfng","thaa"
  };
  // charset encodings that one may typically expect to be RTL
  private static final String[] RTL_ENCODINGS = {
    "iso-8859-6", "iso-8859-6-bidi", 
    "iso-8859-6-i", "iso-ir-127", 
    "ecma-114", "asmo-708", "arabic", 
    "csisolatinarabic", "windows-1256", 
    "ibm-864", "macarabic", "macfarsi", 
    "iso-8859-8-i", "iso-8859-8-bidi", 
    "windows-1255", "iso-8859-8", "ibm-862", 
    "machebrew", "asmo-449", "iso-9036", 
    "arabic7", "iso-ir-89", "csiso89asmo449", 
    "iso-unicode-ibm-1264", "csunicodeibm1264", 
    "iso_8859-8:1988", "iso-ir-138", "hebrew", 
    "csisolatinhebrew", "iso-unicode-ibm-1265", 
    "csunicodeibm1265", "cp862", "862", 
    "cspc862latinhebrew"
  };
  
  /**
   * Attempt to guess the base direction using the in-scope language.  
   * Implements the method used by Internet Explorer 7's feed view
   * documented here: http://blogs.msdn.com/rssteam/archive/2007/05/17/reading-feeds-in-right-to-left-order.aspx.
   * 
   * This algorithm differs slightly from the method documented in that the 
   * primary language tag is case insensitive.
   * 
   * If the language tag is not specified, then the default Locale is used to 
   * determine the direction.
   * 
   * According to the Atom Bidi spec, if the dir attribute is set explicitly, we 
   * should not do language guessing.  This restriction can be bypassed by setting
   * ignoredir to true.
   */
  public static <T extends Element>Direction guessDirectionFromLanguage(T element, boolean ignoredir) {
    if (!ignoredir && hasDirection(element)) return getDirection(element);
    String language = element.getLanguage();
    Lang lang = 
      language != null ? 
        new Lang(language) :
        new Lang(Locale.getDefault());
    if (lang.getScript() != null) {
      String script = lang.getScript().getName();
      if (Arrays.binarySearch(RTL_SCRIPTS, script.toLowerCase()) > -1)
        return Direction.RTL;
    }
    String primary = lang.getLanguage().getName();
    if (Arrays.binarySearch(RTL_LANGS, primary.toLowerCase()) > -1) 
          return Direction.RTL;
    return Direction.UNSPECIFIED;
  }

  /**
   * Attempt to guess the base direction using the charset encoding.  This 
   * is a bit of a last resort approach
   */
  public static <T extends Element>Direction guessDirectionFromEncoding(T element) {
    return guessDirectionFromEncoding(element,false);
  }

  /**
   * Attempt to guess the base direction using the charset encoding.  This 
   * is a bit of a last resort approach
   */
  @SuppressWarnings("unchecked") 
  public static <T extends Element>Direction guessDirectionFromEncoding(T element, boolean ignoredir) {
    if (!ignoredir && hasDirection(element)) return getDirection(element);
    Document doc = element.getDocument();
    if (doc == null) return Direction.UNSPECIFIED;
    String charset = doc.getCharset();
    if (charset == null) return Direction.UNSPECIFIED;
    charset = charset.replace('_', '-');
    Arrays.sort(RTL_ENCODINGS);
    if (Arrays.binarySearch(RTL_ENCODINGS, charset.toLowerCase()) > -1) 
      return Direction.RTL;
    return Direction.UNSPECIFIED;
  }
  
  /**
   * Attempt to guess the base direction of an element using an analysis of
   * the directional properties of the characters used.  This is a brute-force
   * style approach that can achieve fairly reasonable results when the element
   * text consists primarily of characters with the same bidi properties.  This
   * approach is implemented by the Snarfer feed reader as is documented at
   * http://www.xn--8ws00zhy3a.com/blog/2006/12/right-to-left-rss   
   * 
   * If the dir attribute is specified, the direction will be determine using it's value 
   * instead of the characteristics of the text
   */
  public static <T extends Element>Direction guessDirectionFromTextProperties(T element) {
    return guessDirectionFromTextProperties(element, false);
  }
  
  /**
   * Attempt to guess the base direction of an element using an analysis of
   * the directional properties of the characters used.  This is a brute-force
   * style approach that can achieve fairly reasonable results when the element
   * text consists primarily of characters with the same bidi properties.  This
   * approach is implemented by the Snarfer feed reader as is documented at
   * http://www.xn--8ws00zhy3a.com/blog/2006/12/right-to-left-rss   
   * 
   * According to the Atom Bidi spec, if the dir attribute is set explicitly, we 
   * should not do language guessing.  This restriction can be bypassed by setting
   * ignoredir to true.
   */
  public static <T extends Element>Direction guessDirectionFromTextProperties(T element, boolean ignoredir) {
    Direction dir = Direction.UNSPECIFIED;
    if (!ignoredir && hasDirection(element)) return getDirection(element);
    String text = element.getText();
    if (text != null && text.length() > 0) {
      if (text.charAt(0) == 0x200F) return Direction.RTL; // if using the unicode right-to-left mark
      if (text.charAt(0) == 0x200E) return Direction.LTR; // if using the unicode left-to-right mark
      int c = 0;
      for (int n = 0; n < text.length(); n++) {
        char ch = text.charAt(n);
        if (Bidi.requiresBidi(new char[] {ch}, 0, 1)) c++;
        else c--;
      }
      dir = (c > 0) ? Direction.RTL : Direction.LTR;
    }
    return dir;
  }

  /**
   * Use Java's built in support for bidi text to determine the base directionality
   * of the element's text.  The response to this only indicates the *base* directionality,
   * it does not indicate whether or not there are any RTL characters in the text.
   * 
   * If the dir attribute is specified, the direction will be determine using it's value 
   * instead of the characteristics of the text
   */
  public static <T extends Element>Direction guessDirectionFromJavaBidi(T element) {
    return guessDirectionFromJavaBidi(element, false);
  }
  
  /**
   * Use Java's built in support for bidi text to determine the base directionality
   * of the element's text.  The response to this only indicates the *base* directionality,
   * it does not indicate whether or not there are any RTL characters in the text.
   * 
   * According to the Atom Bidi spec, if the dir attribute is set explicitly, we 
   * should not do language guessing.  This restriction can be bypassed by setting
   * ignoredir to true.
   */
  public static <T extends Element>Direction guessDirectionFromJavaBidi(T element, boolean ignoredir) {
    Direction dir = Direction.UNSPECIFIED;
    if (!ignoredir && hasDirection(element)) return getDirection(element);
    String text = element.getText();
    if (text != null) {
      AttributedString s = new AttributedString(text);
      Bidi bidi = new Bidi(s.getIterator());
      dir = (bidi.baseIsLeftToRight()) ? Direction.LTR : Direction.RTL;
    }
    return dir;
  }
  
  private static <T extends Element>boolean hasDirection(T element) {
    boolean answer = false;
    String dir = element.getAttributeValue("dir");
    if (dir != null && dir.length() > 0)
      answer = true;
    else if (dir == null) {
      // if the direction is unspecified on this element, 
      // let's see if we've inherited it
      Base parent = element.getParentElement(); 
      if (parent != null && 
          parent instanceof Element)
        answer = hasDirection((Element)parent);
    }
    return answer;
  }
}
