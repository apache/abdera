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
package org.apache.abdera.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractStreamWriter 
  implements StreamWriter {
  
  protected final String name;
  protected boolean autoflush = false;
  protected boolean autoclose = false;
  
  protected AbstractStreamWriter(String name) {
    this.name = name;
  }
  
  public StreamWriter setAutoflush(boolean auto) {
    this.autoflush = auto;
    return this;
  }
  
  public StreamWriter setAutoclose(boolean auto) {
    this.autoclose = auto;
    return this;
  }
  
  public StreamWriter setChannel(WritableByteChannel channel) {
    return setOutputStream(Channels.newOutputStream(channel));
  }

  public StreamWriter setChannel(WritableByteChannel channel, String charset) {
    return setWriter(Channels.newWriter(channel, charset));
  }
  
  public String getName() {
    return name;
  }
  
  public StreamWriter startDocument() {
    startDocument("1.0");
    return this;
  }
 
  public StreamWriter endDocument() {
    return this;
  }
  
  public StreamWriter startFeed() {
    startFeed(null);
    return this;
  }
  
  public StreamWriter startFeed(Map<QName,String> attributes) {
    startElement(Constants.FEED, attributes);
    return this;
  }

  public StreamWriter endFeed() {
    endElement();
    return this;
  }
  
  public StreamWriter startEntry(Map<QName,String> attributes) {
    startElement(Constants.ENTRY, attributes);
    return this;
  }
  
  public StreamWriter startEntry() {
    startEntry(null);
    return this;
  }
  
  public StreamWriter endEntry() {
    endElement();
    return this;
  }
  
  public StreamWriter endCategory() {
    endElement();
    return this;
  }
  
  public StreamWriter endContent() {
    endElement();
    return this;
  }
  
  public StreamWriter endLink() {
    endElement();
    return this;
  }
  
  public StreamWriter endPerson() {
    endElement();
    return this;
  }
  
  public StreamWriter endSource() {
    endElement();
    return this;
  }
  
  public StreamWriter endText() {
    endElement();
    return this;
  }
  
  
  public StreamWriter startCategory(
    String term, 
    Map<QName, String> attributes) {
      startCategory(term,null,null,attributes);
      return this;
  }
  
  public StreamWriter startCategory(
    String term, 
    String scheme,
    Map<QName, String> attributes) {
      startCategory(term,scheme,null,attributes);
      return this;
  }
  
  public StreamWriter startCategory(
    String term, 
    String scheme, 
    String label,
    Map<QName, String> attributes) {
      if (attributes == null) attributes = new HashMap<QName,String>();
      attributes.put(new QName("term"),term);
      if (scheme != null) attributes.put(new QName("scheme"),term);
      if (label != null) attributes.put(new QName("label"),term);
      startElement(Constants.CATEGORY,attributes);
      return this;
  }

  public StreamWriter startLink(
    String iri, 
    String rel, 
    String type, 
    String title,
    String hreflang, 
    int length, 
    Map<QName, String> attributes) {
      if (attributes == null) attributes = new HashMap<QName,String>();
      attributes.put(new QName("href"),iri);
      if (rel != null) attributes.put(new QName("rel"),rel);
      if (type != null) attributes.put(new QName("type"),type);
      if (title != null) attributes.put(new QName("title"),title);
      if (hreflang != null) attributes.put(new QName("hreflang"),hreflang);
      if (length > -1) attributes.put(new QName("length"),String.valueOf(length));
      startElement(Constants.LINK,attributes);
      return this;
  }

  public StreamWriter startPerson(
    QName qname, 
    Map<QName, String> attributes) {
      startElement(qname, attributes);
      return this;
  }
  
  public StreamWriter startSource(
    Map<QName, String> attributes) {
      startElement(Constants.SOURCE,attributes);
      return this;
  }
  
  public StreamWriter startText(
    QName qname, 
    Text.Type type,
    Map<QName, String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    attributes.put(new QName("type"),type!=null?type.name().toLowerCase():"text");
    startElement(qname,attributes);
    return this;
  }
  
  public StreamWriter writeDate(
    QName qname, 
    String date, 
    Map<QName, String> attributes) {
      startElement(qname,attributes);
      writeElementText(date);
      endElement();
      return this;
  }
  
  public StreamWriter writeIRIElement(
    QName qname, 
    String iri,
    Map<QName, String> attributes) {
      startElement(qname,attributes);
      writeElementText(iri);
      endElement();
      return this;
  }
  
  public StreamWriter writePersonEmail(
    String email, 
    Map<QName, String> attributes) {
      startElement(Constants.EMAIL,attributes);
      writeElementText(email);
      endElement();
      return this;
  }
  
  public StreamWriter writePersonName(
    String name, 
    Map<QName, String> attributes) {
      startElement(Constants.NAME,attributes);
      writeElementText(name);
      endElement();
      return this;
  }
  
  public StreamWriter writePersonUri(
    String uri, 
    Map<QName, String> attributes) {
      startElement(Constants.URI,attributes);
      writeElementText(uri);
      endElement();
      return this;
  }

  public StreamWriter startContent(
    Content.Type type, 
    String src, 
    Map<QName, String> attributes) {
      startContent(type.name().toLowerCase(),src,attributes);
      return this;
  }
  
  public StreamWriter startContent(
    String type, 
    String src,
    Map<QName, String> attributes) {
      if (attributes == null) attributes = new HashMap<QName,String>();
      attributes.put(new QName("type"),type);
      if (src != null) attributes.put(new QName("src"),src);
      startElement(Constants.CONTENT,attributes);
      return this;
  }
  
  public StreamWriter startContent(
    Content.Type type, 
    Map<QName, String> attributes) {
      startContent(type,null,attributes);
      return this;
  }
  
  public StreamWriter startContent(
    String type, 
    Map<QName, String> attributes) {
      startContent(type,null,attributes);
      return this;
  }
  
  public StreamWriter startLink(
    String iri, 
    Map<QName, String> attributes) {
      startLink(iri,null,null,null,null,-1,attributes);
      return this;
  }
  
  public StreamWriter startLink(
    String iri, 
    String rel, 
    Map<QName, String> attributes) {
      startLink(iri,rel,null,null,null,-1,attributes);
      return this;
  }
  
  public StreamWriter startLink(
    String iri, 
    String rel, 
    String type,
    Map<QName, String> attributes) {
      startLink(iri,rel,type,null,null,-1,attributes);
      return this;
  }
  
  public StreamWriter writeCategory(
    String term, Map<QName, 
    String> attributes) {
      startCategory(term, attributes);
      endCategory();
      return this;
  }
  
  public StreamWriter writeCategory(
    String term, 
    String scheme,
    Map<QName, String> attributes) {
      startCategory(term,scheme,attributes);
      endCategory();
      return this;
  }
  
  public StreamWriter writeCategory(
    String term, 
    String scheme, 
    String label,
    Map<QName, String> attributes) {
      startCategory(term,scheme,label,attributes);
      endCategory();
      return this;
  }
  
  public StreamWriter writeContent(
    Content.Type type, 
    String value,
    Map<QName, String> attributes) {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
      return this;
  }
  
  public StreamWriter writeContent(
    Content.Type type, 
    InputStream value,
    Map<QName, String> attributes) throws IOException {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
      return this;
  }
  
  public StreamWriter writeContent(
    Content.Type type, 
    DataHandler value,
    Map<QName, String> attributes) throws IOException {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
      return this;
  }
  
  public StreamWriter writeContent(
    String type, 
    String value,
    Map<QName, String> attributes) {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
      return this;
  }
  
  public StreamWriter writeEdited(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.EDITED, date, attributes);
      return this;
  }
  
  public StreamWriter writeId(
    String iri, 
    Map<QName, String> attributes) {
      writeIRIElement(Constants.ID, iri, attributes);
      return this;
  }
  
  public StreamWriter writeIcon(
    String iri, 
    Map<QName,String> attributes) {
      writeIRIElement(Constants.ICON, iri, attributes);
      return this;
  }
  
  public StreamWriter writeLogo(
    String iri, 
    Map<QName,String> attributes) {
      writeIRIElement(Constants.LOGO, iri, attributes);
      return this;
  }
  
  
  public StreamWriter writeLink(
    String iri, 
    Map<QName, String> attributes) {
      startLink(iri, attributes);
      endLink();
      return this;
  }
  
  public StreamWriter writeLink(
    String iri, 
    String rel, 
    Map<QName, String> attributes) {
      startLink(iri, rel, attributes);
      endLink();
      return this;
  }
  
  public StreamWriter writeLink(
    String iri, 
    String rel, 
    String type,
    Map<QName, String> attributes) {
    startLink(iri, rel, type, attributes);
    endLink();
    return this;
  }
  
  public StreamWriter writeLink(
    String iri, 
    String rel, 
    String type, 
    String title,
    String hreflang, 
    int length, 
    Map<QName, String> attributes) {
      startLink(iri, rel, type, title, hreflang, length, attributes);
      endLink();
      return this;
  }
  
  public StreamWriter writePerson(
    QName qname, 
    String name, 
    String email, 
    String uri,
    Map<QName, String> attributes) {
      startPerson(qname,attributes);
      writePersonName(name,null);
      if (email != null) writePersonEmail(email,null);
      if (uri != null) writePersonUri(uri,null);
      endPerson();
      return this;
  }
  
  public StreamWriter writePublished(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.PUBLISHED,date,attributes);
      return this;
  }
  
  public StreamWriter writeText(
    QName qname, 
    Text.Type type,
    String value, 
    Map<QName, String> attributes) {
      startText(qname,type,attributes);
      writeElementText(value);
      endText();
      return this;
  }
  
  public StreamWriter writeUpdated(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.UPDATED,date,attributes);
      return this;
  }
  
  public StreamWriter writeUpdated(String date, Map<QName,String> attributes) {
    writeDate(Constants.UPDATED, date, attributes);
    return this;
  }
  
  public StreamWriter writePublished(String date, Map<QName,String> attributes) {
    writeDate(Constants.PUBLISHED, date, attributes);
    return this;
  }
  
  public StreamWriter writeEdited(String date, Map<QName,String> attributes) {
    writeDate(Constants.EDITED, date, attributes);
    return this;
  }
  
  public StreamWriter writeDate(QName qname, Date date, Map<QName,String> attributes) {
    writeDate(qname, AtomDate.format(date), attributes);
    return this;
  }
  
  public StreamWriter writeId(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.ID, iri, attributes);
    return this;
  }
  
  public StreamWriter writeIcon(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.ICON, iri, attributes);
    return this;
  }
  
  public StreamWriter writeLogo(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.LOGO, iri, attributes);
    return this;
  }
  
  public StreamWriter writeIRIElement(QName qname, IRI iri, Map<QName,String> attributes) {
    writeIRIElement(qname, iri.toString(), attributes);
    return this;
  }
  
  public StreamWriter writeElementText(InputStream value) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    int r = -1;
    while ((r = value.read(buf)) != -1) out.write(buf,0,r);
    byte[] data = out.toByteArray();
    writeElementText(new String(Base64.encodeBase64(data),"UTF-8"));
    return this;
  }
  
  public StreamWriter writeElementText(DataHandler value) throws IOException {
    writeElementText(value.getInputStream());
    return this;
  }
  
  public StreamWriter writeTitle(String value) {
    writeText(Constants.TITLE, Text.Type.TEXT, value, null);
    return this;
  }
  
  public StreamWriter writeTitle(Text.Type type, String value) {
    writeText(Constants.TITLE, type, value, null);
    return this;
  }

  public StreamWriter writeSubtitle(String value) {
    writeText(Constants.SUBTITLE, Text.Type.TEXT, value, null);
    return this;
  }
  
  public StreamWriter writeSubtitle(Text.Type type, String value) {
    writeText(Constants.SUBTITLE, type, value, null);
    return this;
  }
  
  public StreamWriter writeSummary(String value) {
    writeText(Constants.SUMMARY, Text.Type.TEXT, value, null);
    return this;
  }
  
  public StreamWriter writeSummary(Text.Type type, String value) {
    writeText(Constants.SUMMARY, type, value, null);
    return this;
  }
  
  public StreamWriter writeRights(String value) {
    writeText(Constants.RIGHTS, Text.Type.TEXT, value, null);
    return this;
  }
  
  public StreamWriter writeRights(Text.Type type, String value) {
    writeText(Constants.RIGHTS, type, value, null);
    return this;
  }


  
  public StreamWriter writeTitle(String value, Map<QName,String> attributes) {
    writeText(Constants.TITLE, Text.Type.TEXT, value, attributes);
    return this;
  }
  
  public StreamWriter writeTitle(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.TITLE, type, value, attributes);
    return this;
  }

  public StreamWriter writeSubtitle(String value, Map<QName,String> attributes) {
    writeText(Constants.SUBTITLE, Text.Type.TEXT, value, attributes);
    return this;
  }
  
  public StreamWriter writeSubtitle(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.SUBTITLE, type, value, attributes);
    return this;
  }
  
  public StreamWriter writeSummary(String value, Map<QName,String> attributes) {
    writeText(Constants.SUMMARY, Text.Type.TEXT, value, attributes);
    return this;
  }
  
  public StreamWriter writeSummary(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.SUMMARY, type, value, attributes);
    return this;
  }
  
  public StreamWriter writeRights(String value, Map<QName,String> attributes) {
    writeText(Constants.RIGHTS, Text.Type.TEXT, value, attributes);
    return this;
  }
  
  public StreamWriter writeRights(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.RIGHTS, type, value, attributes);
    return this;
  }
  
  public StreamWriter writeId(String iri) {
    writeIRIElement(Constants.ID, iri, null);
    return this;
  }
  
  public StreamWriter writeIcon(String iri) {
    writeIRIElement(Constants.ICON, iri, null);
    return this;
  }
  
  public StreamWriter writeLogo(String iri) {
    writeIRIElement(Constants.LOGO, iri, null);
    return this;
  }
  
  public StreamWriter writeIRIElement(QName qname, String iri) {
    writeIRIElement(qname, iri, null);
    return this;
  }

  public StreamWriter writeId(IRI iri) {
    writeIRIElement(Constants.ID, iri, null);
    return this;
  }
  
  public StreamWriter writeIcon(IRI iri) {
    writeIRIElement(Constants.ICON, iri, null);
    return this;
  }
  
  public StreamWriter writeLogo(IRI iri) {
    writeIRIElement(Constants.LOGO, iri, null);
    return this;
  }
  
  public StreamWriter writeIRIElement(QName qname, IRI iri) {
    writeIRIElement(qname, iri, null);
    return this;
  }
  
  public StreamWriter writeId() {
    writeId((Map<QName,String>)null);
    return this;
  }
   
  public StreamWriter writePerson(QName qname, String name, String email, String uri) {
    writePerson(qname,name,email,uri,null);
    return this;
  }
  
  public StreamWriter startPerson(QName qname) {
    startPerson(qname,null);
    return this;
  }
  
  public StreamWriter writePersonName(String name) {
    writePersonName(name,null);
    return this;
  }
  
  public StreamWriter writePersonEmail(String email) {
    writePersonEmail(email,null);
    return this;
  }
  
  public StreamWriter writePersonUri(String uri) {
    writePersonUri(uri,null);
    return this;
  }
 
  public StreamWriter writeAuthor(String name, String email, String uri) {
    writePerson(Constants.AUTHOR,name,email,uri,null);
    return this;
  }

  public StreamWriter writeAuthor(String name, String email, String uri, Map<QName,String> attributes) {
    writePerson(Constants.AUTHOR,name,email,uri,attributes);
    return this;
  }
  
  public StreamWriter startAuthor() {
    startElement(Constants.AUTHOR,null);
    return this;
  }

  public StreamWriter startAuthor(Map<QName,String> attributes) {
    startElement(Constants.AUTHOR,attributes);
    return this;
  }
  
  public StreamWriter endAuthor() {
    endElement();
    return this;
  }
  

  public StreamWriter writeContributor(String name, String email, String uri) {
    writePerson(Constants.CONTRIBUTOR,name,email,uri,null);
    return this;
  }

  public StreamWriter writeContributor(String name, String email, String uri, Map<QName,String> attributes) {
    writePerson(Constants.CONTRIBUTOR,name,email,uri,attributes);
    return this;
  }
  
  public StreamWriter startContributor() {
    startElement(Constants.CONTRIBUTOR,null);
    return this;
  }

  public StreamWriter startContributor(Map<QName,String> attributes) {
    startElement(Constants.CONTRIBUTOR,attributes);
    return this;
  }
  
  public StreamWriter endContributor() {
    endElement();
    return this;
  }
  
  public StreamWriter writeGenerator(String version, String uri, String value) {
    writeGenerator(version,uri,value,null);
    return this;
  }
  
  public StreamWriter writeGenerator(String version, String uri, String value, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (version != null) attributes.put(new QName("version"),version);
    if (uri != null) attributes.put(new QName("uri"),uri);
    startElement(Constants.GENERATOR,attributes);
    writeElementText(value);
    endElement();
    return this;
  }

  public StreamWriter startGenerator(String version, String uri) {
    startGenerator(version,uri,null);
    return this;
  }
  
  public StreamWriter startGenerator(String version, String uri, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (version != null) attributes.put(new QName("version"),version);
    if (uri != null) attributes.put(new QName("uri"),uri);
    startElement(Constants.GENERATOR,attributes);
    return this;
  }
  
  public StreamWriter endGenerator() {
    endElement();
    return this;
  }
  
  
  public StreamWriter writeUpdated(Date date) {
    writeUpdated(date,null);
    return this;
  }
  
  public StreamWriter writePublished(Date date) {
    writePublished(date,null);
    return this;
  }
  
  public StreamWriter writeEdited(Date date) {
    writeEdited(date,null);
    return this;
  }
  
  public StreamWriter writeDate(QName qname, Date date) {
    writeDate(qname,date,null);
    return this;
  }

  public StreamWriter writeUpdated(String date) {
    writeUpdated(date,null);
    return this;
  }
  
  public StreamWriter writePublished(String date) {
    writePublished(date,null);
    return this;
  }
  
  public StreamWriter writeEdited(String date) {
    writeEdited(date,null);
    return this;
  }
  
  public StreamWriter writeDate(QName qname, String date) {
    writeDate(qname, date,null);
    return this;
  }
  
  
  public StreamWriter writeLink(String iri) {
    writeLink(iri,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter writeLink(String iri, String rel) {
    writeLink(iri,rel,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter writeLink(String iri, String rel, String type) {
    writeLink(iri,rel,type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter writeLink(String iri, String rel, String type, String title, String hreflang, int length) {
    writeLink(iri,rel,type,title,hreflang,length,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startLink(String iri) {
    startLink(iri,(Map<QName,String>)null);
    return this;
  }

  public StreamWriter startLink(String iri, String rel) {
    startLink(iri,rel,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startLink(String iri, String rel, String type) {
    startLink(iri,rel,type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startLink(String iri, String rel, String type, String title, String hreflang, int length) {
    startLink(iri,rel,type,title,hreflang,length,(Map<QName,String>)null);
    return this;
  }
  
  
  public StreamWriter writeCategory(String term) {
   writeCategory(term,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter writeCategory(String term, String scheme) {
   writeCategory(term,scheme,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter writeCategory(String term, String scheme, String label) {
   writeCategory(term,scheme,label,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter startCategory(String term) {
   startCategory(term,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter startCategory(String term, String scheme) {
   startCategory(term,scheme,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter startCategory(String term, String scheme, String label) {
   startCategory(term,scheme,label,(Map<QName,String>)null);
   return this;
  }
  
  public StreamWriter startSource() {
    startElement(Constants.SOURCE,null);
    return this;
  }
  
  public StreamWriter writeText(QName qname, Text.Type type, String value) {
    writeText(qname,type,value,null);
    return this;
  }
  
  public StreamWriter startText(QName qname, Text.Type type) {
    startText(qname,type,null);
    return this;
  }
  
  
  public StreamWriter writeContent(Content.Type type, String value) {
    writeContent(type,value,null);
    return this;
  }
  
  public StreamWriter writeContent(Content.Type type, InputStream value) throws IOException {
    writeContent(type,value,null);
    return this;
  }
  
  public StreamWriter writeContent(Content.Type type, DataHandler value) throws IOException {
    writeContent(type,value,null);
    return this;
  }
  
  public StreamWriter writeContent(String type, String value) {
    writeContent(type,value,null);
    return this;
  }
  
  
  public StreamWriter startContent(Content.Type type) {
    startContent(type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startContent(String type) {
    startContent(type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startContent(Content.Type type, String src) {
    startContent(type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startContent(String type, String src) {
    startContent(type,(Map<QName,String>)null);
    return this;
  }
  
  public StreamWriter startElement(QName qname) {
    startElement(qname,null);
    return this;
  }
  
  
  
  public StreamWriter startService() {
    startElement(Constants.SERVICE);
    return this;
  }
  
  public StreamWriter startService(Map<QName,String> attributes) {
    startElement(Constants.SERVICE,attributes);
    return this;
  }
  
  public StreamWriter endService() {
    endElement();
    return this;
  }
  
  public StreamWriter startWorkspace() {
    startElement(Constants.WORKSPACE, null);
    return this;
  }
  
  public StreamWriter startWorkspace(Map<QName,String> attributes) {
    startElement(Constants.WORKSPACE, attributes);
    return this;
  }
  
  public StreamWriter endWorkspace() {
    endElement();
    return this;
  }
  
  public StreamWriter startCollection(String href) {
    startCollection(href, null);
    return this;
  }
  
  public StreamWriter startCollection(String href, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    attributes.put(new QName("href"),href);
    startElement(Constants.COLLECTION, attributes);
    return this;
  }
  
  public StreamWriter endCollection() {
    endElement();
    return this;
  }
  
  public StreamWriter writeAccepts(String... accepts) {
    for (String accept : accepts) {
      startElement(Constants.ACCEPT,null);
      writeElementText(accept);
      endElement();
    }
    return this;
  }
  
  public StreamWriter startCategories() {
    startCategories(false,null,null);
    return this;
  }
  
  public StreamWriter startCategories(boolean fixed) {
    startCategories(fixed,null,null);
    return this;
  }
  
  public StreamWriter startCategories(boolean fixed, String scheme) {
    startCategories(fixed,scheme,null);
    return this;
  }
  
  public StreamWriter startCategories(Map<QName,String> attributes) {
    startCategories(false,null,attributes);
    return this;
  }
  
  public StreamWriter startCategories(boolean fixed, Map<QName,String> attributes) {
    startCategories(fixed,null,attributes);
    return this;
  }
  
  public StreamWriter startCategories(boolean fixed, String scheme, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (fixed) attributes.put(new QName("fixed"),"true");
    else attributes.remove("fixed");
    if (scheme != null) attributes.put(new QName("scheme"),scheme);
    startElement(Constants.CATEGORIES, attributes);
    return this;
  }
  
  public StreamWriter endCategories() {
    endElement();
    return this;
  }

  public StreamWriter startControl() {
    startElement(Constants.CONTROL,null);
    return this;
  }
  
  public StreamWriter startControl(Map<QName,String> attributes) {
    startElement(Constants.CONTROL, attributes);
    return this;
  }
  
  public StreamWriter endControl() {
    endElement();
    return this;
  }
  
  public StreamWriter writeDraft(boolean draft) {
    startElement(Constants.DRAFT);
    writeElementText(draft?"yes":"no");
    endElement();
    return this;
  }
  
}
