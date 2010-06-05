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
package org.apache.abdera.spring;

import java.util.List;

import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.springframework.beans.factory.FactoryBean;

@SuppressWarnings("unchecked")
public class RegexTargetResolverFactoryBean implements FactoryBean {
    private List<String> services;
    private List<String> collections;
    private List<String> entries;
    private List<String> media;
    private List<String> categories;

    public Object getObject() throws Exception {
        RegexTargetResolver resolver = new RegexTargetResolver();

        init(resolver, services, TargetType.TYPE_SERVICE);
        init(resolver, collections, TargetType.TYPE_COLLECTION);
        init(resolver, entries, TargetType.TYPE_ENTRY);
        init(resolver, media, TargetType.TYPE_MEDIA);
        init(resolver, categories, TargetType.TYPE_CATEGORIES);
        return resolver;
    }

    private void init(RegexTargetResolver resolver, List<String> patterns, TargetType t) {
        if (patterns == null)
            return;

        for (String s : patterns) {
            resolver.setPattern(s, t);
        }
    }

    public Class getObjectType() {
        return RegexTargetResolver.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public List<String> getCollections() {
        return collections;
    }

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

}
