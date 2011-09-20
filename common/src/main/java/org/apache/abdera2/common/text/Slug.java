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

import org.apache.abdera2.common.text.CharUtils.Profile;

public class Slug {

    public static final String SANITIZE_PATTERN = "[^A-Za-z0-9\\%!$&\\\\'()*+,;=_]+";

    public static Slug create(
        String slug) {
        return create(slug, "", true, NormalizationForm.D, SANITIZE_PATTERN);
    }

    public static Slug create(
        String slug, 
        String filler) {
        return create(slug, filler, true, NormalizationForm.D, SANITIZE_PATTERN);
    }

    public static Slug create(
        String slug, 
        String filler, 
        boolean lower) {
        return create(slug, filler, lower, NormalizationForm.D, SANITIZE_PATTERN);
    }

    public static Slug create(
        String slug, 
        String filler, 
        String pattern) {
        return create(slug, filler, true, NormalizationForm.D, pattern);
    }

    public static Slug create(
        String slug, 
        String filler, 
        boolean lower, 
        String pattern) {
        return create(slug, filler, lower, NormalizationForm.D, pattern);
    }

    public static Slug create(
        String slug, 
        String filler, 
        boolean lower, 
        NormalizationForm form) {
        return create(slug, filler, lower, form, SANITIZE_PATTERN);
    }

    /**
     * Used to sanitize a string. Optionally performs Unicode normalization on the string to break extended
     * characters down, then replaces non alphanumeric characters with a specified filler replacement.
     * 
     * @param slug The source string
     * @param filler The replacement string
     * @param lower True if the result should be lowercase
     * @param form Unicode Normalization form to use (or null)
     */
    public static Slug create(
        String slug, 
        String filler, 
        boolean lower, 
        NormalizationForm form, 
        String pattern) {
        if (slug == null)
            return null;
        if (lower)
            slug = slug.toLowerCase();
        if (form != null) {
            slug = form.normalize(slug);
        }
        slug = slug.replaceAll("\\s+", "_");
        if (filler != null) {
            slug = slug.replaceAll(pattern, filler);
        } else {
            slug = UrlEncoding.encode(slug, Profile.PATHNODELIMS);
        }
        return new Slug(slug);
    }
    
    private final String slug;
    
    private Slug(String slug) {
      this.slug = slug;
    }
    
    public String toString() {
      return slug;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((slug == null) ? 0 : slug.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Slug other = (Slug) obj;
      if (slug == null) {
        if (other.slug != null)
          return false;
      } else if (!slug.equals(other.slug))
        return false;
      return true;
    }
    
}
