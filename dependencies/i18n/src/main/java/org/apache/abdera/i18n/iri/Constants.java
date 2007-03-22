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
package org.apache.abdera.i18n.iri;

import java.util.BitSet;

import org.apache.abdera.i18n.ChainableBitSet;


public class Constants {

  public final static BitSet get(char c) {
    return (new ChainableBitSet()).set2(c);
  }
  
  public final static char[] hex = {
    '0', '1', '2', '3', '4', '5', '6', '7', 
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public final static BitSet COLON      = new ChainableBitSet().set2(':');
  public final static BitSet QUERYMARK  = new ChainableBitSet().set2('?');
  public final static BitSet HASH       = new ChainableBitSet().set2('#');
  public final static BitSet SLASH      = new ChainableBitSet().set2('/');
  public final static BitSet SEPS       = new ChainableBitSet().set2("/?#");
  
  public final static BitSet DIGIT      = new ChainableBitSet().set2('0','9');
  public final static BitSet ALPHA      = new ChainableBitSet().set2('A', 'Z').set2('a','z');
  public final static BitSet ALPHANUM   = new ChainableBitSet().set2(DIGIT).set2(ALPHA);
  public final static BitSet HEX        = new ChainableBitSet().set2(DIGIT).set2('A','F').set2('a','f');
  public final static BitSet MARK       = new ChainableBitSet().set2("-_.!~*\'()");
  public final static BitSet UNRESERVED = new ChainableBitSet().set2(ALPHANUM).set2(MARK);
  public final static BitSet RESERVED   = new ChainableBitSet().set2(";/?:@&=+$,[]");
  public final static BitSet ESCAPED    = new ChainableBitSet().set2(0);
  public final static BitSet GENDELIMS  = new ChainableBitSet().set2(":/?#[]@");
  public final static BitSet SUBDELIMS  = new ChainableBitSet().set2("!$&\\'()*+,;=");
  public final static BitSet PCHAR      = new ChainableBitSet().set2(UNRESERVED).set2(ESCAPED).set2(":@&=+$,");
  public final static BitSet PATH       = new ChainableBitSet().set2(PCHAR).set2(";/%");
  public final static BitSet PATHNODELIMS = new ChainableBitSet().set2(PATH).addNot2(GENDELIMS);
  public final static BitSet SCHEME     = new ChainableBitSet().set2(ALPHA).set2(DIGIT).set2("+-.");
  public final static BitSet USERINFO   = new ChainableBitSet().set2(UNRESERVED).set2(SUBDELIMS).set2('%').set2(HEX);
  public final static BitSet QUERY      = new ChainableBitSet().set2(PCHAR).set2('/','?');
  public final static BitSet FRAGMENT   = new ChainableBitSet().set2(PCHAR).set2('/','?');
  public final static BitSet PCTENC     = new ChainableBitSet().set2('%').set2(HEX);
  
  //userinfo    = *( unreserved / pct-encoded / sub-delims / ":" )
  
  public final static BitSet BIDI = new ChainableBitSet()
  .set2('\u200E', // Left-to-right mark
        '\u200F', // Right-to-left mark
        '\u202A', // Left-to-right embedding
        '\u202B', // Right-to-left embedding
        '\u202D', // Left-to-right override
        '\u202E', // Right-to-left override
        '\u202C');// Pop directional formatting
  
  public final static BitSet UCSCHAR    = new ChainableBitSet().set2('\u00A0', '\uD7FF')
                                            .set2('\uF900','\uFDCF') 
                                            .set2('\uFDF0','\uFFEF')
                                            .set2(0x10000,0x1FFFD)
                                            .set2(0x20000,0x2FFFD)
                                            .set2(0x30000,0x3FFFD)
                                            .set2(0x40000,0x4FFFD)
                                            .set2(0x50000,0x5FFFD)
                                            .set2(0x60000,0x6FFFD)
                                            .set2(0x70000,0x7FFFD)
                                            .set2(0x80000,0x8FFFD)
                                            .set2(0x90000,0x9FFFD)
                                            .set2(0xA0000,0xAFFFD)
                                            .set2(0xB0000,0xBFFFD)
                                            .set2(0xC0000,0xCFFFD)
                                            .set2(0xD0000,0xDFFFD)
                                            .set2(0xE1000,0xEFFFD);
  
  public final static BitSet IPRIVATE   = new ChainableBitSet().set2('\uE000', '\uF8FF')
                                            .set2(0xF0000,0xFFFFD)
                                            .set2(0x100000,0x10FFFD);
  
  public final static BitSet IUNRESERVED= new ChainableBitSet().set2(ALPHANUM)
                                            .set2(MARK)
                                            .set2(UCSCHAR)
                                            .set2(BIDI,false);
  
  public final static BitSet IPCHAR     = new ChainableBitSet().set2(IUNRESERVED)
                                            .set2(ESCAPED)
                                            .set2(":@&=+$,");
  
  public final static BitSet IPATH       = new ChainableBitSet().set2(IPCHAR).set2(";/%");
  
  public final static BitSet IQUERY     = new ChainableBitSet().set2(IPCHAR)
                                            .set2(IPRIVATE)
                                            .set2(";/?%");
  
  public final static BitSet IFRAGMENT  = new ChainableBitSet().set2(IPCHAR).set2("/?%");
  
  public final static BitSet IREGNAME   = new ChainableBitSet().set2(IUNRESERVED)
                                            .set2(ESCAPED)
                                            .set2("!$&'()*+,;=");
  public final static BitSet REGNAME    = new ChainableBitSet().set2(UNRESERVED)
                                            .set2(ESCAPED)
                                            .set2("!$&'()*+,;=");
  
  public final static BitSet IUSERINFO  = new ChainableBitSet().set2(IUNRESERVED)
                                            .set2(ESCAPED)
                                            .set2(";:&=+$,");
  
  public final static BitSet ISERVER    = new ChainableBitSet().set2(IUSERINFO)
                                            .set2(IREGNAME)
                                            .set2(ALPHANUM)
                                            .set2(".:@[]%-")
                                            .set2('?',false);
  
  public final static BitSet STD3ASCIIRULES = new ChainableBitSet()
                                                .set2(0x0000,0x002C)
                                                .set2(0x002E,0x002F)
                                                .set2(0x003A,0x0040)
                                                .set2(0x005B,0x0060)
                                                .set2(0x007B,0x007F);
  

}
