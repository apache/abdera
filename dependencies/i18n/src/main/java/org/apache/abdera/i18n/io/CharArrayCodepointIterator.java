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
package org.apache.abdera.i18n.io;

import org.apache.abdera.i18n.io.CodepointIterator;

/**
 * Iterate over Unicode codepoints contained in a char array
 */
public class CharArrayCodepointIterator 
  extends CodepointIterator {

  protected char[] buffer;
  
  protected CharArrayCodepointIterator() {}
  
  public CharArrayCodepointIterator(char[] buffer) {
    this(buffer,0,buffer.length);
  }
  
  public CharArrayCodepointIterator(char[] buffer, int n, int e) {
    this.buffer = buffer;
    this.position = n;
    this.limit = Math.min(buffer.length-n,e);
  }
  
  protected char get() {
    return (position < limit) ? buffer[position++] : (char)-1;
  }
  
  protected char get(int index) {
    if (index < 0 || index >= limit) 
      throw new ArrayIndexOutOfBoundsException(index);
    return buffer[index];
  }
  
}
