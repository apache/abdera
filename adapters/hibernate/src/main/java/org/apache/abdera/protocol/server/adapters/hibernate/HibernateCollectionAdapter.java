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
package org.apache.abdera.protocol.server.adapters.hibernate;

import static org.apache.abdera.protocol.server.provider.managed.FeedConfiguration.ENTRY_ELEM_NAME_AUTHOR;
import static org.apache.abdera.protocol.server.provider.managed.FeedConfiguration.ENTRY_ELEM_NAME_CONTENT;
import static org.apache.abdera.protocol.server.provider.managed.FeedConfiguration.ENTRY_ELEM_NAME_ID;
import static org.apache.abdera.protocol.server.provider.managed.FeedConfiguration.ENTRY_ELEM_NAME_TITLE;
import static org.apache.abdera.protocol.server.provider.managed.FeedConfiguration.ENTRY_ELEM_NAME_UPDATED;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.provider.basic.BasicAdapter;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * An adapter implementation that uses Hibernate and a backend database to store
 * Atompub collection entries.  As an extension of the BasicAdapter, the adapter 
 * is intended to be used with the BasicProvider and is configured using an
 * /abdera/adapter/*.properties file.
 */
public class HibernateCollectionAdapter extends BasicAdapter {

	private static Log logger = LogFactory.getLog(HibernateCollectionAdapter.class);
	
	protected static Configuration hibernateConfig;
    protected static SessionFactory sessionFactory;
    
    public static final String HIBERNATE_ANNOTATION_CONFIG = "hibernateAnnotationConfig";
    public static final String HIBERNATE_CFG_PATH = "hibernateCfgPath";
    public static final String ENTRY_MAPPING_CLASS_NAME = "entryMappingClassName";
    
    public HibernateCollectionAdapter(Abdera abdera, FeedConfiguration config) {
        super(abdera, config);
        loadHibernateConfiguration();
    }
    
    private void loadHibernateConfiguration() {
    	if (hibernateConfig == null) {
    		if (config.hasProperty(HIBERNATE_ANNOTATION_CONFIG) &&
    				config.getProperty(HIBERNATE_ANNOTATION_CONFIG)
    				.toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
    			hibernateConfig = new AnnotationConfiguration();
    		} else {
    			hibernateConfig = new Configuration();
    		}
    		
    		if (config.hasProperty(HIBERNATE_CFG_PATH)) {
    			hibernateConfig.configure((String) config.getProperty(HIBERNATE_CFG_PATH));
    		} else {
    			hibernateConfig.configure();
    		}    		
    		rebuildSessionFactory(hibernateConfig);
    	}    	
    }
    
    protected SessionFactory getSessionFactory() {
        String sfName = hibernateConfig.getProperty(Environment.SESSION_FACTORY_NAME);
        if ( sfName != null) {
            logger.debug("Looking up SessionFactory in JNDI");
            try {
                return (SessionFactory) new InitialContext().lookup(sfName);
            } catch (NamingException ex) {
                throw new RuntimeException(ex);
            }
        } else if (sessionFactory == null) {
            rebuildSessionFactory(hibernateConfig);
        }
        return sessionFactory;
    }
    
    private void rebuildSessionFactory(Configuration cfg) {
        logger.debug("Rebuilding the SessionFactory from given Configuration");
        if (sessionFactory != null && !sessionFactory.isClosed())
            sessionFactory.close();
        if (cfg.getProperty(Environment.SESSION_FACTORY_NAME) != null) {
            logger.debug("Managing SessionFactory in JNDI");
            cfg.buildSessionFactory();
        } else {
            logger.debug("Holding SessionFactory in static variable");
            sessionFactory = cfg.buildSessionFactory();
        }
        hibernateConfig = cfg;
     }        
	
	@Override
	public Entry createEntry(Entry entry) throws Exception {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		
		Serializable entryId = null;

		try {
			Object mapping = collectMappingObject(entry, null);
			entryId = session.save(mapping);
			
			tx.commit();
						
		} catch (Exception ex) {
			tx.rollback();
			logger.error("error creating a new entry", ex);			
		} finally {
			session.close();
		}
		
		return getEntry(entryId);
	}

	@Override
	public boolean deleteEntry(Object entryId) throws Exception {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		
		boolean deleted = false;
		try {
			Object mapping = session.load(
					Class.forName((String) config.getProperty(ENTRY_MAPPING_CLASS_NAME)),
					(Serializable) entryId); 
			
			if (mapping != null) {
				session.delete(mapping);
				tx.commit();
			}
			deleted = true;
		} catch (HibernateException ex) {
			tx.rollback();
			logger.error("error deleting the entry", ex);			
		} finally {
			session.close();
		}
				
		return deleted;
	}

	@Override
	public Entry getEntry(Object entryId) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Query query = session.getNamedQuery(config.getFeedId() + "-get-entry");
		query.setParameter("id", entryId);
		query.setResultTransformer(new AtomEntryResultTransformer(
				config.getServerConfiguration().getServerUri() + "/" + config.getFeedId(), getAbdera(), null));		
		
		Entry entry = (Entry) query.uniqueResult();
		
		session.close();
		return entry;
	}

	@Override
	public Feed getFeed() throws Exception {		
		Session session = getSessionFactory().openSession();
		
		String queryName = config.getFeedId() + "-get-feed";		
		Query query = session.getNamedQuery(queryName);
		
		Feed feed = createFeed();
		query.setResultTransformer(new AtomEntryResultTransformer(
				config.getServerConfiguration().getServerUri() + "/" + config.getFeedId(), this.getAbdera(), feed));
		query.list();		
		
		session.close();
		return feed;
	}

	@Override
	public Entry updateEntry(Object entryId, Entry entry) throws Exception {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			Object forUpdate = session.load(
					Class.forName((String) config.getProperty(ENTRY_MAPPING_CLASS_NAME)), 
					(Serializable) entryId);
			if (forUpdate != null) {
				forUpdate = collectMappingObject(entry, forUpdate);
				session.update(forUpdate);
				tx.commit();
			}
		} catch (HibernateException ex) {
			tx.rollback();
			logger.error("error deleting the entry", ex);			
		} finally {
			session.close();
		}
				
		return entry;
	}
	
	protected Object collectMappingObject(Entry entry, Object forUpdate) throws Exception {
		boolean create = false;
		Class clazz = Class.forName((String) config.getProperty(ENTRY_MAPPING_CLASS_NAME));
		if (forUpdate == null) {			
			forUpdate = clazz.newInstance();
			create = true;
		}
		
		for (Field field : clazz.getDeclaredFields()) {
			if (create && field.getName().equals(ENTRY_ELEM_NAME_ID)) {
				collectField(field, clazz, forUpdate, entry.getId().toString());				
			} else if (field.getName().equals(ENTRY_ELEM_NAME_AUTHOR)){
				collectField(field, clazz, forUpdate, entry.getAuthor().getName());
			} else if (field.getName().equals(ENTRY_ELEM_NAME_TITLE)) {
				collectField(field, clazz, forUpdate, entry.getTitle());
			} else if (field.getName().equals(ENTRY_ELEM_NAME_UPDATED)) {
				collectField(field, clazz, forUpdate, entry.getUpdated());
			} else if (field.getName().equals(ENTRY_ELEM_NAME_CONTENT)) {
				collectField(field, clazz, forUpdate, entry.getContent());
			}
		}
		
		return forUpdate;
	}
	
	protected void collectField(Field field, Class clazz, Object mappingObject, Object entryValue) throws Exception {		
		clazz.getMethod(getSetter(field.getName()), new Class[]{field.getType()})
			.invoke(mappingObject, new Object[]{entryValue});
	}
	
	protected String getSetter(String fieldName) {
		return "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
	}

}
