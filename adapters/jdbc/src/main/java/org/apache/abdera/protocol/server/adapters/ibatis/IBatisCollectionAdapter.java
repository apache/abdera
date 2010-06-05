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
package org.apache.abdera.protocol.server.adapters.ibatis;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.provider.basic.BasicAdapter;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Adapter that uses IBatis and a database to store Atompub collection entries. As an extension to BasicAdapter, the
 * adapter is intended to be used with BasicProvider and is configured using /abdera/adapter/*.properties files.
 */
public class IBatisCollectionAdapter extends BasicAdapter {

    // this class needs to be public - so that Adapter Manager can invoke it
    // to create an instance of this adapter
    public IBatisCollectionAdapter(Abdera abdera, FeedConfiguration config) {
        super(abdera, config);
    }

    protected Map<String, SqlMapClient> sqlMapClients = new HashMap<String, SqlMapClient>();

    protected SqlMapClient getSqlMapClient() throws Exception {
        String dataSourceId = config.getFeedConfigLocation();
        if (sqlMapClients.containsKey(dataSourceId)) {
            return sqlMapClients.get(dataSourceId);
        } else {
            SqlMapClient client =
                SqlMapClientBuilder.buildSqlMapClient(config.getAdapterConfiguration().getAdapterConfigAsReader());
            sqlMapClients.put(dataSourceId, client);
            return client;
        }
    }

    @SuppressWarnings("unchecked")
    public Feed getFeed() throws Exception {
        SqlMapClient client = getSqlMapClient();
        String queryId = config.getFeedId() + "-get-feed";
        List<Map<String, Object>> rows = client.queryForList(queryId);
        Feed feed = createFeed();
        ServerConfiguration serverConfig = config.getServerConfiguration();
        if (serverConfig.getFeedNamespacePrefix() != null && serverConfig.getFeedNamespacePrefix().length() > 0) {
            feed.declareNS(serverConfig.getFeedNamespace(), serverConfig.getFeedNamespacePrefix());
        }
        for (Map<String, Object> row : rows)
            createEntryFromRow(feed, row);
        return feed;
    }

    @SuppressWarnings("unchecked")
    public Entry getEntry(Object entryId) throws Exception {
        String queryId = config.getFeedId() + "-get-entry";
        SqlMapClient client = getSqlMapClient();
        Map<String, Object> row = (Map<String, Object>)client.queryForObject(queryId, entryId);
        if (row == null) {
            // didn't find the entry.
            return null;
        }
        return createEntryFromRow(null, row);
    }

    public Entry createEntry(Entry entry) throws Exception {
        SqlMapClient client = getSqlMapClient();
        String queryId = config.getFeedId() + "-insert-entry";
        Object newEntryId = client.insert(queryId, collectColumns(entry));
        return getEntry(newEntryId);
    }

    public Entry updateEntry(Object entryId, Entry entry) throws Exception {
        SqlMapClient client = getSqlMapClient();
        String queryId = config.getFeedId() + "-update-entry";
        return client.update(queryId, collectColumns(entry)) > 0 ? getEntry(entryId) : null;
    }

    public boolean deleteEntry(Object entryId) throws Exception {
        String queryId = config.getFeedId() + "-delete-entry";
        SqlMapClient client = getSqlMapClient();
        return client.delete(queryId, entryId) > 0;
    }

    protected Entry createEntryFromRow(Feed feed, Map<String, Object> row) throws Exception {
        Entry entry = feed != null ? feed.addEntry() : abdera.newEntry();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element entity = doc.createElement("entity");
        doc.appendChild(entity);
        for (String columnName : row.keySet()) {
            if (row.get(columnName) == null) {
                continue;
            }
            Object value = row.get(columnName);
            if (FeedConfiguration.ENTRY_ELEM_NAME_ID.equals(columnName)) {
                entry.setId(createEntryIdUri(value.toString()));
            } else if (FeedConfiguration.ENTRY_ELEM_NAME_TITLE.equals(columnName)) {
                entry.setTitle(value.toString());
            } else if (FeedConfiguration.ENTRY_ELEM_NAME_AUTHOR.equals(columnName)) {
                entry.addAuthor(value.toString());
            } else if (FeedConfiguration.ENTRY_ELEM_NAME_UPDATED.equals(columnName) && value instanceof java.util.Date) {
                entry.setUpdated((Date)value);
            } else if (FeedConfiguration.ENTRY_ELEM_NAME_LINK.equals(columnName)) {
                entry.addLink(value.toString());
            } else {
                Element node = doc.createElement(columnName);
                node.appendChild(doc.createTextNode(value.toString()));
                entity.appendChild(node);
            }
        }
        if (entry.getUpdated() == null) {
            entry.setUpdated(new Date());
        }
        if (entry.getAuthor() == null) {
            entry.addAuthor(config.getFeedAuthor());
        }
        if (entry.getTitle() == null) {
            entry.setTitle((String)config.getProperty(FeedConfiguration.PROP_ENTRY_TITLE_NAME));
        }
        entry.setContent(getDocumentAsXml(doc), "text/xml");
        addEditLinkToEntry(entry);
        return entry;
    }

    public static String getDocumentAsXml(Document doc) throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        String str = sw.toString();
        logger.finest(str);
        return str;
    }

    protected Map<String, Object> collectColumns(Entry entry) throws Exception {
        Map<String, Object> columns = new HashMap<String, Object>();

        if (entry.getId() != null) {
            columns.put(FeedConfiguration.ENTRY_ELEM_NAME_ID, entry.getId().toString());
        }
        if (entry.getAuthor() != null) {
            columns.put(FeedConfiguration.ENTRY_ELEM_NAME_AUTHOR, entry.getAuthor().getText());
        }
        if (entry.getTitle() != null) {
            columns.put(FeedConfiguration.ENTRY_ELEM_NAME_TITLE, entry.getTitle());
        }
        if (entry.getUpdated() != null) {
            columns.put(FeedConfiguration.ENTRY_ELEM_NAME_UPDATED, entry.getUpdated());
        }

        Content content = entry.getContentElement();
        if (content != null) {
            String contentStr = content.getValue();
            parseContent(contentStr, columns);
        }

        return columns;
    }

    static void parseContent(String str, Map<String, Object> columns) throws Exception {
        ByteArrayInputStream inStr = new ByteArrayInputStream(str.getBytes());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inStr);

        while (true) {
            int event = parser.next();
            if (event == XMLStreamConstants.END_DOCUMENT) {
                parser.close();
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = parser.getLocalName();
                int eventType = parser.next();
                if (eventType == XMLStreamConstants.CHARACTERS) {
                    String value = parser.getText();
                    columns.put(name, value);
                }
            }
        }
    }

}
