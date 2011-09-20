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
package org.apache.abdera2.common.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.lang.Subtag.Type;

/**
 * Implementation of RFC 4646 Language Tags. Instances are immutable and 
 * safe for use by multiple threads. Iterators returned by calling 
 * the iterator() method, however, are not threadsafe and should only
 * ever be accessed by a single thread.
 */
public final class Lang 
  extends SubtagSet {

    private static final long serialVersionUID = -7095560018906537331L;
    private final Locale locale;

    private Subtag t_extlang, t_script, t_variant, 
                   t_region, t_extension, t_privateuse;
    

    public Lang() {
        this(init(Locale.getDefault()));
    }

    public Lang(Locale locale) {
        this(init(locale));
    }

    private static Subtag init(Locale locale) {
        try {
            return parse(locale.toString()).root;
        } catch (Exception e) {
            Subtag c = null, primary = new Subtag(Type.LANGUAGE, locale.getLanguage());
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (country != null)
                c = new Subtag(Type.REGION, country, primary);
            if (variant != null)
                new Subtag(Type.VARIANT, variant, c);
            return primary;
        }
    }

    public Lang(String lang) {
        this(parse(lang).root);
    }

    Lang(Subtag root) {
        super(root);
        scan();
        this.locale = initLocale();
    }

    private Locale initLocale() {
        Subtag primary = language();
        Subtag region = region();
        Subtag variant = variant();
        if (variant != null && region != null)
            return new Locale(primary.toString(), region.toString(), variant.toString());
        else if (region != null)
            return new Locale(primary.toString(), region.toString());
        else
            return new Locale(primary.toString());
    }

    public Subtag language() {
        return root;
    }

    public Locale locale() {
        return locale;
    }

    private void scan() {
      for (Subtag subtag : this) {
        switch (subtag.type()) {
          case EXTLANG:
            t_extlang = subtag; 
            break;
          case SCRIPT:
            t_script = subtag; 
            break;
          case VARIANT:
            t_variant = subtag; 
            break;
          case REGION:
            t_region = subtag; 
            break;
          case SINGLETON:
            if (subtag.isExtensionSingleton() && t_extension == null)
              t_extension = subtag;
            else if (subtag.isPrivateSingleton())
              t_privateuse = subtag;
          default: break;
        }
      }
    }
    
    public Subtag extlang() {
      return t_extlang;
    }

    public Subtag script() {
      return t_script;
    }

    public Subtag region() {
      return t_region;
    }

    public Subtag variant() {
      return t_variant;
    }

    public Subtag extension() {
      return t_extension;
    }
    
    public Subtag[] extensions() {
      List<Subtag> list = new ArrayList<Subtag>();
      Subtag extension = extension();
      while(extension != null) {
        list.add(extension);
        extension = nextExtension(extension);
      }
      return list.toArray(new Subtag[list.size()]);
    }
    
    public Subtag extension(String name) {
      Subtag subtag = extension(); // go to first extension;
      while(subtag != null && !subtag.name().equals(name))
        subtag = nextExtension(subtag);
      return subtag;
    }
    
    private static Subtag nextExtension(Subtag subtag) {
      subtag = subtag.next();
      while (!subtag.isSingleton())
        subtag = subtag.next();
      return subtag.isExtensionSingleton() ? subtag : null;
    }

    public Subtag privateUse() {
      return t_privateuse;
    }

    public Range asRange() {
        return new Range(toString());
    }
    
    public Lang parent() {
        Subtag root, prev, temp;
        Iterator<Subtag> i = iterator();
        temp = i.next();
        root = new Subtag(
          temp.type(), 
          temp.name());
        prev = root;
        while (i.hasNext()) {
          temp = i.next();
          if (i.hasNext())
            prev = new Subtag(
              temp.type(), 
              temp.name(), 
              prev);
        }
        return new Lang(root);
    }
    
    public Lang copy() {
      Subtag root, prev, temp;
      Iterator<Subtag> i = iterator();
      temp = i.next();
      root = new Subtag(
        temp.type(), 
        temp.name());
      prev = root;
      while (i.hasNext()) {
        temp = i.next();
        prev = new Subtag(
          temp.type(), 
          temp.name(), 
          prev);
      }
      return new Lang(root);
  }

    public boolean isGrandfathered() {
      for (Subtag tag : this)
        if (tag.type() == Type.GRANDFATHERED)
          return true;
      return false;
    }
    
    public boolean isChildOf(Lang lang) {
        Range range = new Range(lang).appendWildcard();
        return range.matches(this);
    }

    public boolean isParentOf(Lang lang) {
        return lang.isChildOf(this);
    }

    // Parsing Logic

    private static final String SEP = "\\s*[-_]\\s*";
    private static final String language = "((?:[a-zA-Z]{2,3}(?:[-_][a-zA-Z]{3}){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8})";
    private static final String script = "((?:[-_][a-zA-Z]{4})?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})))*)";
    private static final String extension = "((?:[-_][a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_]" + privateuse + ")?)";
    private static final String grandfathered =
        "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final String langtag = "^" + language + script + region + variant + extension + _privateuse + "$";

    private static final Pattern p_langtag = Pattern.compile(langtag);
    private static final Pattern p_privateuse = Pattern.compile("^" + privateuse + "$");
    private static final Pattern p_grandfathered = Pattern.compile(grandfathered);

    /**
     * Parse a Lang tag
     */
    public static Lang parse(String lang) {
        if (lang == null || lang.length() == 0)
          throw new IllegalArgumentException();
        Subtag primary = null;
        Matcher m = p_grandfathered.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split(SEP);
            Subtag current = null;
            for (String tag : tags)
                current = current == null ?
                    primary = new Subtag(Type.GRANDFATHERED, tag) :
                    new Subtag(Type.GRANDFATHERED, tag, current);
            return new Lang(primary);
        }
        m = p_privateuse.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split(SEP);
            Subtag current = null;
            for (String tag : tags)
                current = current == null ?
                    primary = new Subtag(Type.SINGLETON, tag) :
                    new Subtag(Type.PRIVATEUSE, tag, current);
            return new Lang(primary);
        }
        m = p_langtag.matcher(lang);
        if (m.find()) {
            String langtag = m.group(1);
            String script = m.group(2);
            String region = m.group(3);
            String variant = m.group(4);
            String extension = m.group(5);
            String privateuse = m.group(6);
            Subtag current = null;
            String[] tags = langtag.split(SEP);
            for (String tag : tags)
                current = current == null ? 
                    primary = new Subtag(Type.LANGUAGE, tag) :
                    new Subtag(Type.EXTLANG, tag, current);
            if (script != null && script.length() > 0)
                current = new Subtag(Type.SCRIPT, script.substring(1), current);
            if (region != null && region.length() > 0)
                current = new Subtag(Type.REGION, region.substring(1), current);
            if (variant != null && variant.length() > 0) {
                variant = variant.substring(1);
                tags = variant.split(SEP);
                for (String tag : tags)
                    current = new Subtag(Type.VARIANT, tag, current);
            }
            if (extension != null && extension.length() > 0) {
                extension = extension.substring(1);
                tags = extension.split(SEP);
                current = new Subtag(Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; i++) {
                    String tag = tags[i];
                    current = new Subtag(
                        tag.length() == 1 ? 
                            Type.SINGLETON : 
                            Type.EXTENSION, 
                        tag, 
                        current);
                }
            }
            if (privateuse != null && privateuse.length() > 0) {
                privateuse = privateuse.substring(1);
                tags = privateuse.split(SEP);
                current = new Subtag(Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; i++)
                    current = new Subtag(Type.PRIVATEUSE, tags[i], current);
            }
            return new Lang(primary);
        }
        throw new IllegalArgumentException();
    }

    public static String fromLocale(Locale locale) {
        return new Lang(locale).toString();
    }
}


