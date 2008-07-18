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
package org.apache.abdera.test.client.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.activation.MimeType;

import junit.framework.Assert;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.stax.FOMEntry;
import org.apache.abdera.protocol.client.util.MultimediaRelatedRequestEntity;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.Test;
import org.mortbay.io.WriterOutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class MultipartRelatedRequestEntityTest extends Assert {

	@Test
	public void testMultipartFormat() throws IOException {
		Entry entry = new FOMEntry();
		entry.setTitle("my image");
		entry.addAuthor("david");
		entry.setId("tag:apache.org,2008:234534344");
		entry.setSummary("multipart test");
		entry.setContent(new IRI("cid:234234@example.com"), "image/jpg");
		RequestEntity request = new MultimediaRelatedRequestEntity(entry, this.getClass().getResourceAsStream("info.png"));
		
		StringWriter sw = new StringWriter();
		WriterOutputStream os = new WriterOutputStream(sw);
		request.writeRequest(os);
		
		String multipart = sw.toString();
		//System.out.println(sw.toString());
		assertTrue(multipart.contains("content-id: <234234@example.com>"));
		assertTrue(multipart.contains("content-type: image/jpg"));
	}
	
	@Test
	public void testMultimediaRelatedContentType() throws Exception {
		MimeType type = new MimeType("Multipart/Related;boundary=\"--35245352345sdfg\"");
		assertTrue(MimeTypeHelper.isMatch("Multipart/Related", type.toString()));
		assertEquals("--35245352345sdfg", type.getParameter("boundary"));
	}
	
	//@Test
	public void testMultipartEncoding() throws Exception {
		InputStream input = this.getClass().getResourceAsStream("info.png");
		int BUFF_SIZE = 1024;
		
		byte[] line = new byte[BUFF_SIZE];
		int end;
		String s = "";
		while ((end = input.read(line)) != -1) {
			s += new BASE64Encoder().encode(line);
		}		
	    		
		ByteArrayInputStream bi = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(s));
		File f = new File("info-out.png");
		if (f.exists()) f.delete();
		f.createNewFile();
		FileOutputStream fo = new FileOutputStream(f);		
				
		while ((end = bi.read(line)) != -1) {
			fo.write(line, 0, end);
		}

		fo.flush();
		fo.close();
	}
}
