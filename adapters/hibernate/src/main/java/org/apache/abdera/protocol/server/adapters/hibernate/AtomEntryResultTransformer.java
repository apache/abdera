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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.serializer.ConventionSerializationContext;
import org.apache.abdera.ext.serializer.impl.EntrySerializer;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.transform.ResultTransformer;

/**
 * Converts Hibernate results into an Atom document using the Abdera Java Object Serializer
 */
public class AtomEntryResultTransformer implements ResultTransformer {
	
	private static Log logger = LogFactory.getLog(AtomEntryResultTransformer.class);
	
	private String feedId;
	private Abdera abdera;
	private Feed feed;
	
	public AtomEntryResultTransformer(String feedId, Abdera abdera, Feed feed) {
		this.feedId = feedId;
		this.abdera = abdera;
		this.feed = feed;
	}
	
	public List transformList(List collection) {
		return collection;
	}

	public Object transformTuple(Object[] tuple, String[] aliases) {
		try {
			if (tuple.length == 1) {				
				StreamWriter sw = abdera.newStreamWriter();
			    ByteArrayOutputStream out = new ByteArrayOutputStream();
			    sw.setOutputStream(out).setAutoIndent(true);
			    ConventionSerializationContext c = 
			      new ConventionSerializationContext(sw);
			    c.setSerializer(tuple[0].getClass(), new EntrySerializer());
			    sw.startDocument();
			    c.serialize(tuple[0]);
			    sw.endDocument();
			    
			    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			    Document<Entry> doc = abdera.getParser().parse(in);
			    Entry entry = doc.getRoot();
			    if (ProviderHelper.getEditUriFromEntry(entry) == null) {
			    	entry.addLink(entry.getId().toString(), "edit");
			    }
			    entry.setId(feedId + "/" + entry.getId().toString());
			    entry.getEditLink().setHref(entry.getId().toString());			    
			    if (feed != null) {
			    	feed.addEntry(entry);
			    }
			    return entry;				
			} else {
				return tuple;
			}
		} catch (Exception ex) {
			logger.error("error creating an entry with the row data", ex);
			throw new ParseException(ex);
		}
	}
	
}
