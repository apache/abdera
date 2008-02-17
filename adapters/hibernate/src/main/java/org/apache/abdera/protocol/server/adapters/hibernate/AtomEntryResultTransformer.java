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
