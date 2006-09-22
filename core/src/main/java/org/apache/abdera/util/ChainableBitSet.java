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

import java.util.BitSet;

/**
 * Extension to java.util.BitSet that allows calls to set to be chained, e.g.
 * bs.set2(1).set(2).set(3), making it easier to define a complex bit set in 
 * a single declaration.
 */
public class ChainableBitSet 
  extends BitSet {

  private static final long serialVersionUID = -1105957441212997513L;

  public ChainableBitSet and2(BitSet set) {
    and(set);
    return this;
  }
  
  public ChainableBitSet addNot2(BitSet set) {
    andNot(set);
    return this;
  }
  
  public ChainableBitSet clear2(int index) {
    clear(index);
    return this;
  }
  
  public ChainableBitSet clear2(int... indexes) {
    for (int i : indexes) clear(i);
    return this;
  }
  
  public ChainableBitSet clear2(int startIndex, int endIndex) {
    clear(startIndex, endIndex);
    return this;
  }
  
  public ChainableBitSet flip2(int index) {
    flip(index);
    return this;
  }
  
  public ChainableBitSet flip2(int... indexes) {
    for (int i : indexes) flip(i);
    return this;
  }
  
  public ChainableBitSet flip2(int startIndex, int endIndex) {
    flip(startIndex,endIndex);
    return this;
  }
  
  public ChainableBitSet or2(BitSet set) {
    or(set);
    return this;
  }
  
  public ChainableBitSet xor2(BitSet set) {
    xor(set);
    return this;
  }
  
  public ChainableBitSet set2(String s) {
    char[] chars = s.toCharArray();
    for (char c : chars) set(c);
    return this;
  }
  
  public ChainableBitSet set2(BitSet set) {
    this.or(set);
    return this;
  }
  
  public ChainableBitSet set2(int ... bits) {
    for (int n : bits) set(n);
    return this;
  }
  
  public ChainableBitSet set2(int fromIndex, int toIndex) {
    super.set(fromIndex, toIndex+1);
    return this;
  }

  public ChainableBitSet set2(int bitIndex) {
    super.set(bitIndex);
    return this;
  }

  public ChainableBitSet set2(int bitIndex, boolean value) {
    super.set(bitIndex, value);
    return this;
  }
  
  public ChainableBitSet set2(BitSet set, boolean value) {
    if (value) return set2(set);
    else this.andNot(set);
    return this;
  }
  
}
