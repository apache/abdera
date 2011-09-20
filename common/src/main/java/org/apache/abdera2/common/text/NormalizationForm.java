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

import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.Normalizer2.Mode;

public enum NormalizationForm {
    D(Mode.DECOMPOSE,  "nfc"), 
    C(Mode.COMPOSE,    "nfc"), 
    KD(Mode.DECOMPOSE, "nfkc"), 
    KC(Mode.COMPOSE,   "nfkc");
    
    private final Normalizer2.Mode mode;
    private final String name;
    
    NormalizationForm(
        Normalizer2.Mode mode, 
        String name) {
      this.mode = mode;
      this.name = name;
    }
    
    public String normalize(CharSequence s) {
      return Normalizer2.getInstance(null, name, mode).normalize(s);
    }
}