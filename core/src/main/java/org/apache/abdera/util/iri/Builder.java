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
package org.apache.abdera.util.iri;

/**
 * Interface implemented by the IRI builder (used by custom scheme parsers
 * to set the parsed IRI data.
 */
public interface Builder {

  public abstract void scheme(int s, int l);

  public abstract void authority(int s, int l);

  public abstract void path(int s, int l);

  public abstract void query(int s, int l);

  public abstract void fragment(int s, int l);

}