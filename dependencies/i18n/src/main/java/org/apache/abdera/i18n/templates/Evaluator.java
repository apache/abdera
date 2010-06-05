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
package org.apache.abdera.i18n.templates;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluates template tokens
 */
@SuppressWarnings("unchecked")
final class Evaluator {

    protected static final Pattern PATTERN = Pattern.compile("(?:-([^\\|]+)\\|)?(?:([^\\|]+)\\|)?(.*)");

    /**
     * Returns a listing of variable names specified by the given template token
     */
    String[] getVariables(String token) {
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String var = matcher.group(3);
            return Operation.get(op).getVariables(var);
        }
        return new String[0];
    }

    /**
     * Writes a plain-text description of the template token
     * 
     * @throws IOException
     */
    void explain(String token, Appendable buf) throws IOException {
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String arg = matcher.group(2);
            String var = matcher.group(3);
            Operation.get(op).explain(var, arg, buf);
        }
    }

    /**
     * Evaluates the template token and returns the resolved value
     */
    String evaluate(String token, String defaultValue, Context context) {
        String value = null;
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String arg = matcher.group(2);
            String var = matcher.group(3);
            value = Operation.get(op).evaluate(var, arg, context);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value != null ? value : "";
    }

    /**
     * Evaluates the template token and returns the resolved value
     */
    String evaluate(String token, Context context) {
        return evaluate(token, "", context);
    }
}
