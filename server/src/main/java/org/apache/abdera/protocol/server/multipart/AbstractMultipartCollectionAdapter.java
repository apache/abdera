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
package org.apache.abdera.protocol.server.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;

import sun.misc.BASE64Decoder;

@SuppressWarnings("unchecked")
public abstract class AbstractMultipartCollectionAdapter extends AbstractCollectionAdapter
		implements MultipartRelatedCollectionInfo {	
	
	protected Map<String, String> accepts;
	
	public String[] getAccepts(RequestContext request) {
		Collection<String> acceptKeys = getAlternateAccepts(request).keySet();
		return acceptKeys.toArray(new String[acceptKeys.size()]);
	}
	
	protected MultipartRelatedPost getMultipartRelatedData(
			RequestContext request) throws IOException, ParseException,
			MessagingException {

		String boundary = request.getContentType().getParameter("boundary");

		if (boundary == null) {
			throw new IllegalArgumentException("multipart/related stream invalid, boundary parameter is missing.");
		}

		boundary = "--" + boundary;

		String type = request.getContentType().getParameter("type");
		if (!(type != null && MimeTypeHelper.isAtom(type))) {
			throw new ParseException("multipart/related stream invalid, type parameter should be "
							+ Constants.ATOM_MEDIA_TYPE);
		}

		PushbackInputStream pushBackInput = new PushbackInputStream(request.getInputStream(), 2);
		pushBackInput.unread("\r\n".getBytes());

		MultipartInputStream multipart = new MultipartInputStream(pushBackInput, boundary.getBytes());

		multipart.skipBoundary();

		//get the media link entry
		Map<String, String> entryHeaders = getHeaders(multipart);
		if (!(entryHeaders.get("content-type") != null &&
				MimeTypeHelper.isAtom(entryHeaders.get("content-type")))) {
			throw new ParseException("multipart/related stream invalid, media link entry content-type is missing");				
		}
		Document<Entry> entry = getEntry(multipart, request);
		
		multipart.skipBoundary();
		
		//get the media resource
		Map<String, String> dataHeaders = getHeaders(multipart);
		if (dataHeaders.get("content-type") == null) {
			throw new ParseException("multipart/related stream invalid, data content-type is missing");				
		}
		if (!isContentTypeAccepted(dataHeaders.get("content-type"), request)) {
			throw new ParseException("multipart/related stream invalid, content-type not accepted into a multipart file");
		}
		ByteArrayInputStream data = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(multipart));;		

		return new MultipartRelatedPost(entry, data, entryHeaders, dataHeaders);
	}
	
	private Map<String, String> getHeaders(MultipartInputStream multipart) throws IOException, MessagingException {
		Map<String, String> mapHeaders = new HashMap<String, String>();
		moveToHeaders(multipart);
		InternetHeaders headers = new InternetHeaders(multipart);
		
		Enumeration<Header> allHeaders = headers.getAllHeaders();
		if (allHeaders != null) {
			while (allHeaders.hasMoreElements()) {
				Header header = allHeaders.nextElement();
				mapHeaders.put(header.getName().toLowerCase(), header.getValue());
			}
		}
		
		return mapHeaders;
	}

	private boolean moveToHeaders(InputStream stream) throws IOException {
		boolean dash = false;
		boolean cr = false;
		int byteReaded;
		
		while ((byteReaded = stream.read()) != -1) {
			switch (byteReaded) {
			case '\r':
				cr = true;
				dash = false;
				break;
			case '\n':
				if (cr == true)
					return true;
				dash = false;
				break;
			case '-':
				if (dash == true) { // two dashes
					stream.close();
					return false;
				}
				dash = true;
				cr = false;
				break;
			default:
				dash = false;
				cr = false;
			}
		}
		return false;
	}

	private <T extends Element> Document<T> getEntry(InputStream stream,
			RequestContext request) throws ParseException, IOException {		
		Parser parser = request.getAbdera().getParser();
		if (parser == null)
			throw new IllegalArgumentException("No Parser implementation was provided");
		Document<?> document = parser.parse(stream, request.getResolvedUri()
				.toString(), parser.getDefaultParserOptions());
		return (Document<T>) document;
	}
	
	private boolean isContentTypeAccepted(String contentType, RequestContext request) {
		if (getAlternateAccepts(request) == null) {
			return false;
		}
		for (Map.Entry<String, String> accept : getAlternateAccepts(request).entrySet()) {
			if (accept.getKey().equalsIgnoreCase(contentType) &&
					accept.getValue() != null && accept.getValue().equalsIgnoreCase(Constants.LN_ALTERNATE_MULTIPART_RELATED)) {
				return true;
			}
		}
		return false;
	}

	protected class MultipartRelatedPost {
		private final Document<Entry> entry;
		private final InputStream data;
		private final Map<String, String> entryHeaders;
		private final Map<String, String> dataHeaders;

		public MultipartRelatedPost(Document<Entry> entry, InputStream data,
				Map<String, String> entryHeaders, Map<String, String> dataHeaders) {			
			this.entry = entry;
			this.data = data;
			this.entryHeaders = entryHeaders;
			this.dataHeaders = dataHeaders;
		}

		public Document<Entry> getEntry() {
			return entry;
		}

		public InputStream getData() {
			return data;
		}

		public Map<String, String> getEntryHeaders() {
			return entryHeaders;
		}

		public Map<String, String> getDataHeaders() {
			return dataHeaders;
		}		

	}
}
