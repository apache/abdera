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
package org.apache.abdera.i18n.text;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Wraps ResourceBundle with a couple of additional, useful methods. Used for l10n
 */
public final class Localizer {

    private static Localizer instance = null;

    public static synchronized Localizer getInstance() {
        if (instance == null)
            instance = new Localizer();
        return instance;
    }

    public static synchronized void setInstance(Localizer localizer) {
        Localizer.instance = localizer;
    }

    public static String get(String key) {
        return getInstance().getValue(key);
    }

    public static String get(String key, String defaultValue) {
        return getInstance().getValue(key, defaultValue);
    }

    /**
     * @deprecated
     */
    public static String format(String key, Object... args) {
        return getInstance().formatValue(key, args);
    }

    public static String sprintf(String key, Object... args) {
        return getInstance().sprintfValue(key, args);
    }

    private static final String DEFAULT_BUNDLE = "abderamessages";

    private final Locale locale;
    private final ResourceBundle bundle;

    public Localizer() {
        this(Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    }

    public Localizer(Locale locale, ClassLoader loader) {
        this(initResourceBundle(DEFAULT_BUNDLE, locale, loader), locale);
    }

    public Localizer(String bundle) {
        this(initResourceBundle(bundle, Locale.getDefault(), Thread.currentThread().getContextClassLoader()));
    }

    public Localizer(String bundle, Locale locale) {
        this(initResourceBundle(bundle, locale, Thread.currentThread().getContextClassLoader()));
    }

    public Localizer(ResourceBundle bundle) {
        this(bundle, bundle.getLocale());
    }

    public Localizer(ResourceBundle bundle, Locale locale) {
        this.bundle = bundle;
        this.locale = locale;
    }

    private static ResourceBundle initResourceBundle(String bundle, Locale locale, ClassLoader loader) {
        try {
            return ResourceBundle.getBundle(bundle, locale, loader);
        } catch (Exception e) {
            return null;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public String getValue(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Use the pre-JDK 1.5 MessageFormat. This should be avoided. Applications should use the sprintfValue method
     * instead
     * 
     * @deprecated
     * @see springfValue
     */
    public String formatValue(String key, Object... args) {
        String value = getValue(key);
        return value != null ? MessageFormat.format(value, args) : null;
    }

    /**
     * Use the JDK 1.5 sprintf style Formatter
     */
    public String sprintfValue(String key, Object... args) {
        String value = getValue(key);
        return value != null ? String.format(locale, value, args) : null;
    }
}
