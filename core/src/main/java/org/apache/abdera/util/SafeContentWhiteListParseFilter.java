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
package org.apache.abdera.util;

import javax.xml.namespace.QName;

public class SafeContentWhiteListParseFilter 
  extends WhiteListParseFilter {

  private static enum xhtml_elements {
    a, abbr, acronym, address, area, b, big, blockquote, 
    br, button, caption, center, cite, code, col, colgroup, 
    dd, del, dfn, dir, div, dl, dt, em, fieldset, font, form, 
    h1, h2, h3, h4, h5, h6, hr, i, img, input, ins, kbd, label, 
    legend, li, map, menu, ol, optgroup, option, p, pre, q, s, 
    samp, select, small, span, strike, strong, sub, sup, table, 
    tbody, td, textarea, tfoot, th, thead, tr, tt, u, ul, var
  };
  
  private static enum xhtml_attributes {
    abbr, accept, accept_charset, accesskey, action, align, alt, 
    axis, border, cellpadding, cellspacing, CHAR, charoff, charset, 
    checked, cite, CLASS, clear, cols, colspan, color, compact, coords, 
    datetime, dir, disabled, enctype, FOR, frame, headers, height, href, 
    hreflang, hspace, id, ismap, label, lang, longdesc, maxlength, media, 
    method, multiple, name, nohref, noshade, nowrap, prompt, readonly, rel, 
    rev, rows, rowspan, rules, scope, selected, shape, size, span, src, 
    start, summary, tabindex, target, title, type, usemap, valign, value, 
    vspace, width,
  };
  
  @Override
  public boolean acceptable(QName qname) {
    if (qname.getNamespaceURI().equals(Constants.XHTML_NS)) {
      try {
        xhtml_elements.valueOf(qname.getLocalPart());
        return true;
      } catch (Exception e) {}
      return false;
    } else {
      return true;
    }
  }

  @Override
  public boolean acceptableAttribute(QName qname, QName attribute) {
    if (qname.getNamespaceURI().equals(Constants.XHTML_NS)) {
      try {
        String lp = attribute.getLocalPart();
        lp = lp.replace('-', '_');
        lp = (lp.equals("char")) ? "CHAR" : lp;
        lp = (lp.equals("for")) ? "FOR" : lp;
        lp = (lp.equals("class")) ? "CLASS" : lp;
        xhtml_attributes.valueOf(lp);
        return true;
      } catch (Exception e) {}
      return false;
    } else {
      return true;
    }
  }
  
  @Override
  public void add(QName qname) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAttribute(QName parent, QName attribute) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(QName qname) {
    return acceptable(qname);
  }

  @Override
  public boolean containsAttribute(QName qname, QName attribute) {
    return acceptableAttribute(qname, attribute);
  }

}
