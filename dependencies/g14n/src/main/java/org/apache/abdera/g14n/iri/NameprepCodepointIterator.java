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
package org.apache.abdera.g14n.iri;

import org.apache.abdera.g14n.io.CodepointIterator;
import org.apache.abdera.g14n.io.FilterCodepointIterator;
import org.apache.abdera.g14n.io.InvalidCharacterException;
import org.apache.abdera.g14n.ChainableBitSet;
import org.apache.abdera.g14n.iri.Nameprep;


/**
 * CodepointIterator implementation that implements the bulk of the 
 * Nameprep details
 */
class NameprepCodepointIterator extends FilterCodepointIterator {

  private int[] rep = null;
  private int reppos = 0;
  private boolean haslcat = false;
  private boolean hasrandalcat = false;
  private boolean firstisrandalcat = false;
  
  private final ChainableBitSet PROHIBITED;
  
  @Override
  public boolean hasNext() {
    return rep != null || super.hasNext();
  }

  protected NameprepCodepointIterator(
    CodepointIterator internal) {
      this(internal,false);        
  }
  
  private boolean islcat(int r) {
    return (Nameprep.LCat.get(r));
  }
  
  private boolean israndalcat(int r) {
    return (Nameprep.RandAL.get(r));
  }
  
  protected NameprepCodepointIterator(
  CodepointIterator internal, boolean allowunassigned) {
    super(internal);
    PROHIBITED = (!allowunassigned) ? 
        Nameprep.PROHIBITED : 
        ((ChainableBitSet)Nameprep.PROHIBITED.clone()).set2(
            Nameprep.UNASSIGNED,false);
  }

  @Override
  public int next() throws InvalidCharacterException {
    int r = -1;
    if (this.rep == null) {
      r = super.next();
      if (r != -1) {
        if (islcat(r)) haslcat = true;
        if (israndalcat(r)) {
          hasrandalcat = true;
          if (position() == 1) firstisrandalcat = true;
        }
        if (haslcat && hasrandalcat) throw new RuntimeException("Bidi Exception");
        
        while(r != -1 && Nameprep.B1.get(r)) { 
          r = super.next();
        }
        
        if (r != -1) {
          if (PROHIBITED.get(r)) throw new InvalidCharacterException(r);
          int[] rep = Nameprep.B2(r);
          if (rep != null) {
            if (rep.length > 1) {
              this.rep = rep;
              reppos = 0;
            }
            r = rep[0];
          }
        }
      }
    } else { 
      r = rep[++reppos];
      if (reppos+1 >= rep.length) rep = null;
    }
    if ((r == -1 || !hasNext()) && 
        hasrandalcat && 
        (!firstisrandalcat || 
         !israndalcat((r ==-1)?peek(position()):r))) {
      throw new RuntimeException("Bidi Exception");
    }
    return r;
  }

  @Override
  public char[] nextChars() throws InvalidCharacterException {
    return super.nextChars();
  }

}
