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
package org.apache.abdera.protocol.server;

/**
 * Identifies the target of the request.
 */
public interface Target extends Iterable<String> {

    /**
     * Return the resolved Target Type
     */
    TargetType getType();

    /**
     * Return the identity of this target. Usually this will just be the request URI
     */
    String getIdentity();

    /**
     * Return the named target parameter
     */
    String getParameter(String name);

    /**
     * Return a listing of all parameter names
     */
    String[] getParameterNames();

    /**
     * Return the object that matches with the uri
     * 
     * @return
     */
    public <T> T getMatcher();

}
