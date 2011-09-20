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
package org.apache.abdera2.activities.protocol;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;

@Properties(
@Property(name="code",to=Integer.class)
)
public class ErrorObject extends ASObject {
  private static final long serialVersionUID = 2361855511692036786L;
  public static final String TYPE = "error";
  public ErrorObject() {
    super(TYPE);
  }  
  public ErrorObject(String displayName) {
    super(TYPE);
    setDisplayName(displayName);
  }
  public String getObjectType() {
    return TYPE;
  }
  public void setObjectType(String objectType) {
    throw new IllegalStateException();
  }
  public int getCode() {
    return (Integer)getProperty("code");
  }
  public void setCode(int code) {
    setProperty("code",code);
  }
}
