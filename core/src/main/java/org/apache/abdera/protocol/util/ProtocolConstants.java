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
package org.apache.abdera.protocol.util;

public interface ProtocolConstants {

    public final static int NOCACHE = 1;
    public final static int NOSTORE = 2;
    public final static int NOTRANSFORM = 4;
    public final static int PUBLIC = 8;
    public final static int PRIVATE = 16;
    public final static int REVALIDATE = 32;
    public final static int PROXYREVALIDATE = 64;
    public final static int ONLYIFCACHED = 128;

}
