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

import javax.xml.namespace.QName;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.io.CharUtils;

/**
 * <p>This is (hopefully) temporary.  Ideally, this would be wrapped into the 
 * core model API so that the bidi stuff is handled seamlessly.  There are 
 * still details being worked out on the Atom WG list and it's likely that
 * at least one other impl (mozilla) will do something slightly different.</p>
 * 
 * <p>Based on http://www.ietf.org/internet-drafts/draft-snell-atompub-bidi-01.txt</p>
 * 
 * <p>Example:</p>
 * <pre>
 *   &lt;feed xmlns="http://www.w3.org/2005/Atom" dir="rtl">
 *     ...
 *   &lt;/feed>
 * </pre>
 * 
 * <p>The getBidi___ elements use the in-scope direction to wrap the text with 
 * the appropriate Unicode control characters. e.g. if dir="rlo", the text is
 * wrapped with the RLO and PDF controls.  If the text already contains the 
 * control chars, the dir attribute is ignored.</p>
 * 
 * <pre>
 *    org.apache.abdera.Abdera abdera = new org.apache.abdera.Abdera();
 *    org.apache.abdera.model.Feed feed = abdera.getFactory().newFeed();
 *    feed.setAttributeValue("dir", "rlo");
 *    feed.setTitle("Testing");
 *    feed.addCategory("foo");
 *    
 *    System.out.println(
 *      BidiHelper.getBidiElementText(
 *        feed.getTitleElement()));
 *    System.out.println(
 *      BidiHelper.getBidiAttributeValue(
 *        feed.getCategories().get(0),"term"));
 *    
 *    // Output: 
 *    //
 *    // > gnitseT
 *    // > oof
 *    //
 * </pre>
 * 
 */
public final class BidiHelper {

  private static final QName DIR = new QName("dir");
  
  BidiHelper() {}
  
  public enum Direction { UNSPECIFIED, LTR, RTL, LRO, RLO };
  
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
      case LTR: return CharUtils.bidiLRE(text);
      case RTL: return CharUtils.bidiRLE(text);
      case LRO: return CharUtils.bidiLRO(text);
      case RLO: return CharUtils.bidiRLO(text);
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
  
}
