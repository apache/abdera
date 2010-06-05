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
package org.apache.abdera.protocol.server.provider.managed;

import java.util.Map;
import java.util.Properties;

import org.apache.abdera.model.Collection;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;

public class FeedConfiguration extends Configuration implements CollectionInfo {
    public static final String PROP_NAME_ADAPTER_CLASS = "adapterClassName";
    public static final String PROP_SUB_URI_NAME = "subUri";
    public static final String PROP_AUTHOR_NAME = "author";
    public static final String PROP_TITLE_NAME = "title";
    public static final String PROP_ACCEPTS = "accepts";
    public static final String PROP_ENTRY_TITLE_NAME = "entryTitle";
    public static final String PROP_FEED_CONFIG_LOCATION_NAME = "configFile";

    public static final String ENTRY_ELEM_NAME_ID = "id";
    public static final String ENTRY_ELEM_NAME_TITLE = "title";
    public static final String ENTRY_ELEM_NAME_CONTENT = "content";
    public static final String ENTRY_ELEM_NAME_AUTHOR = "author";
    public static final String ENTRY_ELEM_NAME_UPDATED = "updated";
    public static final String ENTRY_ELEM_NAME_LINK = "link";

    private final String feedId;
    private final String subUri;
    private final String adapterClassName;
    private final String feedConfigLocation;
    private final ServerConfiguration serverConfiguration;
    private String feedTitle = "unknown";
    private String feedAuthor = "unknown";
    private Map<Object, Object> optionalProperties;
    private final CollectionAdapterConfiguration adapterConfiguration;

    public FeedConfiguration(String feedId,
                             String subUri,
                             String adapterClassName,
                             String feedConfigLocation,
                             ServerConfiguration serverConfiguration) {
        this.feedId = feedId;
        this.subUri = subUri;
        this.adapterClassName = adapterClassName;
        this.feedConfigLocation = feedConfigLocation;
        this.adapterConfiguration = new CollectionAdapterConfiguration(serverConfiguration, feedConfigLocation);
        this.serverConfiguration = serverConfiguration;
    }

    public static FeedConfiguration getFeedConfiguration(String feedId,
                                                         Properties properties,
                                                         ServerConfiguration serverConfiguration) {
        FeedConfiguration feedConfiguration =
            new FeedConfiguration(feedId, Configuration.getProperty(properties, PROP_SUB_URI_NAME), Configuration
                .getProperty(properties, PROP_NAME_ADAPTER_CLASS), Configuration
                .getProperty(properties, PROP_FEED_CONFIG_LOCATION_NAME), serverConfiguration);
        if (properties.containsKey(PROP_AUTHOR_NAME)) {
            feedConfiguration.setFeedAuthor(Configuration.getProperty(properties, PROP_AUTHOR_NAME));
        }

        if (properties.containsKey(PROP_TITLE_NAME)) {
            feedConfiguration.setFeedTitle(Configuration.getProperty(properties, PROP_TITLE_NAME));
        }
        feedConfiguration.optionalProperties = properties;
        return feedConfiguration;
    }

    public String getAdapterClassName() {
        return adapterClassName;
    }

    public String getFeedAuthor() {
        return feedAuthor;
    }

    public String getFeedConfigLocation() {
        return feedConfigLocation;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public String getSubUri() {
        return subUri;
    }

    public void setFeedAuthor(String feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedUri() {
        return serverConfiguration.getServerUri() + "/" + getSubUri();
    }

    public boolean hasProperty(String key) {
        return optionalProperties.containsKey(key);
    }

    public Object getProperty(String key) {
        return optionalProperties.get(key);
    }

    public CollectionAdapterConfiguration getAdapterConfiguration() {
        return adapterConfiguration;
    }

    public Collection asCollectionElement(RequestContext request) {
        Collection collection = request.getAbdera().getFactory().newCollection();
        collection.setHref(getHref(request));
        collection.setTitle(getTitle(request));
        collection.setAccept(getAccepts(request));
        for (CategoriesInfo catsinfo : getCategoriesInfo(request)) {
            collection.addCategories(catsinfo.asCategoriesElement(request));
        }
        return collection;
    }

    public String[] getAccepts(RequestContext request) {
        Object accepts = optionalProperties.get(PROP_ACCEPTS);
        if (accepts == null || !(accepts instanceof String))
            return new String[] {"application/atom+xml;type=entry"};
        return ((String)accepts).split("\\s*,\\s*");
    }

    public CategoriesInfo[] getCategoriesInfo(RequestContext request) {
        return new CategoriesInfo[0];
    }

    public String getHref(RequestContext request) {
        return getFeedUri();
    }

    public String getTitle(RequestContext request) {
        return getFeedTitle();
    }

    public ServerConfiguration getServerConfiguration() {
        return adapterConfiguration.getServerConfiguration();
    }
}
