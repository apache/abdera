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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.util.MultipartRelatedRequestEntity;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.Test;
import org.mortbay.io.WriterOutputStream;

public class MultipartRelatedRequestEntityTest {

    @Test
    public void testMultipartFormat() throws IOException {
        Entry entry = Abdera.getInstance().newEntry();
        entry.setTitle("my image");
        entry.addAuthor("david");
        entry.setId("tag:apache.org,2008:234534344");
        entry.setSummary("multipart test");
        entry.setContent(new IRI("cid:234234@example.com"), "image/jpg");
        RequestEntity request =
            new MultipartRelatedRequestEntity(entry, this.getClass().getResourceAsStream("info.png"), "image/jpg",
                                              "asdfasdfasdf");

        StringWriter sw = new StringWriter();
        WriterOutputStream os = new WriterOutputStream(sw);
        request.writeRequest(os);

        String multipart = sw.toString();
        // System.out.println(sw.toString());

        assertTrue(multipart.contains("content-id: <234234@example.com>"));
        assertTrue(multipart.contains("content-type: image/jpg"));
    }

    @Test
    public void testMultimediaRelatedContentType() throws Exception {
        MimeType type = new MimeType("Multipart/Related;boundary=\"35245352345sdfg\"");
        assertTrue(MimeTypeHelper.isMatch("Multipart/Related", type.toString()));
        assertEquals("35245352345sdfg", type.getParameter("boundary"));
    }

    // @Test
    public void testMultipartEncoding() throws Exception {
        InputStream input = this.getClass().getResourceAsStream("info.png");
        int BUFF_SIZE = 1024;

        byte[] line = new byte[BUFF_SIZE];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (input.read(line) != -1) {
            output.write(line);
        }

        Base64 base64 = new Base64();
        byte[] encoded = base64.encode(output.toByteArray());
        ByteArrayInputStream bi = new ByteArrayInputStream(base64.decode(encoded));

        File f = new File("info-out.png");
        if (f.exists())
            f.delete();
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);

        int end;
        while ((end = bi.read(line)) != -1) {
            fo.write(line, 0, end);
        }

        fo.flush();
        fo.close();
    }
}
