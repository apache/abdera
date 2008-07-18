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
package org.apache.abdera.protocol.client.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.abdera.model.Entry;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.httpclient.methods.RequestEntity;

import sun.misc.BASE64Encoder;

public class MultimediaRelatedRequestEntity implements RequestEntity {

	static final int BUFF_SIZE = 1024;
    static final byte[] buffer = new byte[BUFF_SIZE];
    private final Entry entry;
    private final InputStream input;
    private final String contentType;
	private String boundary;
	
	public MultimediaRelatedRequestEntity(Entry entry, InputStream input) {
		this(entry, input, null);
	}	
	
	public MultimediaRelatedRequestEntity(Entry entry, InputStream input, String contentType) {
		this(entry, input, contentType, null);
	}
	
	public MultimediaRelatedRequestEntity(Entry entry, InputStream input, String contentType, String boundary) {		
		this.input = input;		
		this.entry = entry;
		this.contentType = contentType != null?contentType:entry.getContentMimeType().toString()	;
		this.boundary = boundary != null?boundary:String.valueOf(System.currentTimeMillis());		
	}
		
	public void writeRequest(OutputStream arg0) throws IOException {
		DataOutputStream out = new DataOutputStream(arg0);
		out.writeBytes("--" + boundary + "\r\n");
		writeEntry(out);
		writeInput(out);		
	}
	
	private void writeEntry(DataOutputStream out) throws IOException {
		out.writeBytes("content-type: " + MimeTypeHelper.getMimeType(entry) + "\r\n\r\n");
		entry.writeTo(out);
		out.writeBytes("\r\n" + "--" + boundary + "\r\n");
	}
	
	private void writeInput(DataOutputStream out) throws IOException {
		if (contentType == null) {
			throw new NullPointerException("media content type can't be null");
		}
		out.writeBytes("content-type: " + contentType + "\r\n");
		
		String contentId = entry.getContentSrc().toString();
		if (!contentId.matches("cid:.+")) {
			throw new IllegalArgumentException("entry content source is not a correct content-ID");
		}
		out.writeBytes("content-id: <" + contentId.substring(4) + ">\r\n\r\n");
		
		int end;
		while ((end = input.read(buffer)) != -1) {
			out.writeBytes(new BASE64Encoder().encode(buffer));
		}	
		out.writeBytes("\r\n" + "--" + boundary + "\r\n");
	}

	public long getContentLength() {		
		return -1;
	}

	public String getContentType() {		
		return "Multipart/Related; boundary=\"--" + boundary 
			+ "\";type=\"" + MimeTypeHelper.getMimeType(entry) + "\"";
	}

	public boolean isRepeatable() {		
		return true;
	}	
}
