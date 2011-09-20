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
package org.apache.abdera2.activities.model.objects;

import java.util.HashSet;
import java.util.Set;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

@Name("question")
public class QuestionObject 
  extends ASObject {

  private static final long serialVersionUID = -691354277218118929L;
  public static final String OPTIONS = "options";
  
  public QuestionObject() {}

  public QuestionObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public Iterable<ASObject> getOptions() {
    return getProperty(OPTIONS);
  }
  
  public void setOptions(Set<ASObject> options) {
    setProperty(OPTIONS, options);
    
  }
  
  public void addOption(ASObject option) {
    Set<ASObject> list = getProperty(OPTIONS);
    if (list == null) {
      list = new HashSet<ASObject>();
      setProperty(OPTIONS, list);
    }
    list.add(option); 
  }

}

