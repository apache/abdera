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
  
  protected AbstractStreamWriter(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void startDocument() {
    startDocument("1.0");
  }
 
  public void endDocument() {}
  
  public void startFeed() {
    startFeed(null);
  }
  
  public void startFeed(Map<QName,String> attributes) {
    startElement(Constants.FEED, attributes);
  }

  public void endFeed() {
    endElement();
  }
  
  public void startEntry(Map<QName,String> attributes) {
    startElement(Constants.ENTRY, attributes);
  }
  
  public void startEntry() {
    startEntry(null);
  }
  
  public void endEntry() {
    endElement();
  }
  
  public void endCategory() {
    endElement();
  }
  
  public void endContent() {
    endElement();
  }
  
  public void endLink() {
    endElement();
  }
  
  public void endPerson() {
    endElement();
  }
  
  public void endSource() {
    endElement();
  }
  
  public void endText() {
    endElement();
  }
  
  
  public void startCategory(
    String term, 
    Map<QName, String> attributes) {
      startCategory(term,null,null,attributes);
  }
  
  public void startCategory(
    String term, 
    String scheme,
    Map<QName, String> attributes) {
      startCategory(term,scheme,null,attributes);
  }
  
  public void startCategory(
    String term, 
    String scheme, 
    String label,
    Map<QName, String> attributes) {
      if (attributes == null) attributes = new HashMap<QName,String>();
      attributes.put(new QName("term"),term);
      if (scheme != null) attributes.put(new QName("scheme"),term);
      if (label != null) attributes.put(new QName("label"),term);
      startElement(Constants.CATEGORY,attributes);
  }

  public void startLink(
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
    
  }

  public void startPerson(
    QName qname, 
    Map<QName, String> attributes) {
      startElement(qname, attributes);
  }
  
  public void startSource(
    Map<QName, String> attributes) {
      startElement(Constants.SOURCE,attributes);
  }
  
  public void startText(
    QName qname, 
    Text.Type type,
    Map<QName, String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    attributes.put(new QName("type"),type!=null?type.name().toLowerCase():"text");
    startElement(qname,attributes);
  }
  
  public void writeDate(
    QName qname, 
    String date, 
    Map<QName, String> attributes) {
      startElement(qname,attributes);
      writeElementText(date);
      endElement();
  }
  
  public void writeIRIElement(
    QName qname, 
    String iri,
    Map<QName, String> attributes) {
      startElement(qname,attributes);
      writeElementText(iri);
      endElement();
  }
  
  public void writePersonEmail(
    String email, 
    Map<QName, String> attributes) {
      startElement(Constants.EMAIL,attributes);
      writeElementText(email);
      endElement();
  }
  
  public void writePersonName(
    String name, 
    Map<QName, String> attributes) {
      startElement(Constants.NAME,attributes);
      writeElementText(name);
      endElement();    
  }
  
  public void writePersonUri(
    String uri, 
    Map<QName, String> attributes) {
      startElement(Constants.URI,attributes);
      writeElementText(uri);
      endElement();
  }

  public void startContent(
    Content.Type type, 
    String src, 
    Map<QName, String> attributes) {
      startContent(type.name().toLowerCase(),src,attributes);
  }
  
  public void startContent(
    String type, 
    String src,
    Map<QName, String> attributes) {
      if (attributes == null) attributes = new HashMap<QName,String>();
      attributes.put(new QName("type"),type);
      if (src != null) attributes.put(new QName("src"),src);
      startElement(Constants.CONTENT,attributes);
  }
  
  public void startContent(
    Content.Type type, 
    Map<QName, String> attributes) {
      startContent(type,null,attributes);
  }
  
  public void startContent(
    String type, 
    Map<QName, String> attributes) {
      startContent(type,null,attributes);
  }
  
  public void startLink(
    String iri, 
    Map<QName, String> attributes) {
      startLink(iri,null,null,null,null,-1,attributes);
  }
  
  public void startLink(
    String iri, 
    String rel, 
    Map<QName, String> attributes) {
      startLink(iri,rel,null,null,null,-1,attributes);
  }
  
  public void startLink(
    String iri, 
    String rel, 
    String type,
    Map<QName, String> attributes) {
      startLink(iri,rel,type,null,null,-1,attributes);
  }
  
  public void writeCategory(
    String term, Map<QName, 
    String> attributes) {
      startCategory(term, attributes);
      endCategory();
  }
  
  public void writeCategory(
    String term, 
    String scheme,
    Map<QName, String> attributes) {
      startCategory(term,scheme,attributes);
      endCategory();
  }
  
  public void writeCategory(
    String term, 
    String scheme, 
    String label,
    Map<QName, String> attributes) {
      startCategory(term,scheme,label,attributes);
      endCategory();
  }
  
  public void writeContent(
    Content.Type type, 
    String value,
    Map<QName, String> attributes) {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
  }
  
  public void writeContent(
    Content.Type type, 
    InputStream value,
    Map<QName, String> attributes) throws IOException {
      startContent(type,attributes);
      writeElementText(value);
      endContent();    
  }
  
  public void writeContent(
    Content.Type type, 
    DataHandler value,
    Map<QName, String> attributes) throws IOException {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
  }
  
  public void writeContent(
    String type, 
    String value,
    Map<QName, String> attributes) {
      startContent(type,attributes);
      writeElementText(value);
      endContent();
  }
  
  public void writeEdited(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.EDITED, date, attributes);
  }
  
  public void writeId(
    String iri, 
    Map<QName, String> attributes) {
      writeIRIElement(Constants.ID, iri, attributes);
  }
  
  public void writeIcon(
    String iri, 
    Map<QName,String> attributes) {
      writeIRIElement(Constants.ICON, iri, attributes);
  }
  
  public void writeLogo(
    String iri, 
    Map<QName,String> attributes) {
      writeIRIElement(Constants.LOGO, iri, attributes);
  }
  
  
  public void writeLink(
    String iri, 
    Map<QName, String> attributes) {
      startLink(iri, attributes);
      endLink();
  }
  
  public void writeLink(
    String iri, 
    String rel, 
    Map<QName, String> attributes) {
      startLink(iri, rel, attributes);
      endLink();
  }
  
  public void writeLink(
    String iri, 
    String rel, 
    String type,
    Map<QName, String> attributes) {
    startLink(iri, rel, type, attributes);
    endLink();
  }
  
  public void writeLink(
    String iri, 
    String rel, 
    String type, 
    String title,
    String hreflang, 
    int length, 
    Map<QName, String> attributes) {
      startLink(iri, rel, type, title, hreflang, length, attributes);
      endLink();
  }
  
  public void writePerson(
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
  }
  
  public void writePublished(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.PUBLISHED,date,attributes);
  }
  
  public void writeText(
    QName qname, 
    Text.Type type,
    String value, 
    Map<QName, String> attributes) {
      startText(qname,type,attributes);
      writeElementText(value);
      endText();
  }
  
  public void writeUpdated(
    Date date, 
    Map<QName, String> attributes) {
      writeDate(Constants.UPDATED,date,attributes);
  }
  
  public void writeUpdated(String date, Map<QName,String> attributes) {
    writeDate(Constants.UPDATED, date, attributes);
  }
  
  public void writePublished(String date, Map<QName,String> attributes) {
    writeDate(Constants.PUBLISHED, date, attributes);
  }
  
  public void writeEdited(String date, Map<QName,String> attributes) {
    writeDate(Constants.EDITED, date, attributes);
  }
  
  public void writeDate(QName qname, Date date, Map<QName,String> attributes) {
    writeDate(qname, AtomDate.format(date), attributes);
  }
  
  public void writeId(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.ID, iri, attributes);
  }
  
  public void writeIcon(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.ICON, iri, attributes);
  }
  
  public void writeLogo(IRI iri, Map<QName,String> attributes) {
    writeIRIElement(Constants.LOGO, iri, attributes);
  }
  
  public void writeIRIElement(QName qname, IRI iri, Map<QName,String> attributes) {
    writeIRIElement(qname, iri.toString(), attributes);
  }
  
  public void writeElementText(InputStream value) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    int r = -1;
    while ((r = value.read(buf)) != -1) out.write(buf,0,r);
    byte[] data = out.toByteArray();
    writeElementText(new String(Base64.encodeBase64(data),"UTF-8"));
  }
  
  public void writeElementText(DataHandler value) throws IOException {
    writeElementText(value.getInputStream());
  }
  
  public void writeTitle(String value) {
    writeText(Constants.TITLE, Text.Type.TEXT, value, null);
  }
  
  public void writeTitle(Text.Type type, String value) {
    writeText(Constants.TITLE, type, value, null);
  }

  public void writeSubtitle(String value) {
    writeText(Constants.SUBTITLE, Text.Type.TEXT, value, null);
  }
  
  public void writeSubtitle(Text.Type type, String value) {
    writeText(Constants.SUBTITLE, type, value, null);
  }
  
  public void writeSummary(String value) {
    writeText(Constants.SUMMARY, Text.Type.TEXT, value, null);
  }
  
  public void writeSummary(Text.Type type, String value) {
    writeText(Constants.SUMMARY, type, value, null);
  }
  
  public void writeRights(String value) {
    writeText(Constants.RIGHTS, Text.Type.TEXT, value, null);
  }
  
  public void writeRights(Text.Type type, String value) {
    writeText(Constants.RIGHTS, type, value, null);
  }


  
  public void writeTitle(String value, Map<QName,String> attributes) {
    writeText(Constants.TITLE, Text.Type.TEXT, value, attributes);
  }
  
  public void writeTitle(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.TITLE, type, value, attributes);
  }

  public void writeSubtitle(String value, Map<QName,String> attributes) {
    writeText(Constants.SUBTITLE, Text.Type.TEXT, value, attributes);
  }
  
  public void writeSubtitle(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.SUBTITLE, type, value, attributes);
  }
  
  public void writeSummary(String value, Map<QName,String> attributes) {
    writeText(Constants.SUMMARY, Text.Type.TEXT, value, attributes);
  }
  
  public void writeSummary(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.SUMMARY, type, value, attributes);
  }
  
  public void writeRights(String value, Map<QName,String> attributes) {
    writeText(Constants.RIGHTS, Text.Type.TEXT, value, attributes);
  }
  
  public void writeRights(Text.Type type, String value, Map<QName,String> attributes) {
    writeText(Constants.RIGHTS, type, value, attributes);
  }
  
  
  
  public void writeId(String iri) {
    writeIRIElement(Constants.ID, iri, null); 
  }
  
  public void writeIcon(String iri) {
    writeIRIElement(Constants.ICON, iri, null);
  }
  
  public void writeLogo(String iri) {
    writeIRIElement(Constants.LOGO, iri, null);
  }
  
  public void writeIRIElement(QName qname, String iri) {
    writeIRIElement(qname, iri, null);
  }

  public void writeId(IRI iri) {
    writeIRIElement(Constants.ID, iri, null);
  }
  
  public void writeIcon(IRI iri) {
    writeIRIElement(Constants.ICON, iri, null);
  }
  
  public void writeLogo(IRI iri) {
    writeIRIElement(Constants.LOGO, iri, null);
  }
  
  public void writeIRIElement(QName qname, IRI iri) {
    writeIRIElement(qname, iri, null);
  }
  
  public void writeId() {
    writeId((Map<QName,String>)null);
  }
   
  public void writePerson(QName qname, String name, String email, String uri) {
    writePerson(qname,name,email,uri,null);
  }
  
  public void startPerson(QName qname) {
    startPerson(qname,null);
  }
  
  public void writePersonName(String name) {
    writePersonName(name,null);
  }
  
  public void writePersonEmail(String email) {
    writePersonEmail(email,null);
  }
  
  public void writePersonUri(String uri) {
    writePersonUri(uri,null);
  }
 

  public void writeAuthor(String name, String email, String uri) {
    writePerson(Constants.AUTHOR,name,email,uri,null);
  }

  public void writeAuthor(String name, String email, String uri, Map<QName,String> attributes) {
    writePerson(Constants.AUTHOR,name,email,uri,attributes);
  }
  
  public void startAuthor() {
    startElement(Constants.AUTHOR,null);
  }

  public void startAuthor(Map<QName,String> attributes) {
    startElement(Constants.AUTHOR,attributes);
  }
  
  public void endAuthor() {
    endElement();
  }
  

  public void writeContributor(String name, String email, String uri) {
    writePerson(Constants.CONTRIBUTOR,name,email,uri,null);
  }

  public void writeContributor(String name, String email, String uri, Map<QName,String> attributes) {
    writePerson(Constants.CONTRIBUTOR,name,email,uri,attributes);
  }
  
  public void startContributor() {
    startElement(Constants.CONTRIBUTOR,null);
  }

  public void startContributor(Map<QName,String> attributes) {
    startElement(Constants.CONTRIBUTOR,attributes);
  }
  
  public void endContributor() {
    endElement();
  }
  
  public void writeGenerator(String version, String uri, String value) {
    writeGenerator(version,uri,value,null);
  }
  
  public void writeGenerator(String version, String uri, String value, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (version != null) attributes.put(new QName("version"),version);
    if (uri != null) attributes.put(new QName("uri"),uri);
    startElement(Constants.GENERATOR,attributes);
    writeElementText(value);
    endElement();
  }

  public void startGenerator(String version, String uri) {
    startGenerator(version,uri,null);
  }
  
  public void startGenerator(String version, String uri, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (version != null) attributes.put(new QName("version"),version);
    if (uri != null) attributes.put(new QName("uri"),uri);
    startElement(Constants.GENERATOR,attributes);    
  }
  
  public void endGenerator() {
    endElement();
  }
  
  
  public void writeUpdated(Date date) {
    writeUpdated(date,null);
  }
  
  public void writePublished(Date date) {
    writePublished(date,null);
  }
  
  public void writeEdited(Date date) {
    writeEdited(date,null);
  }
  
  public void writeDate(QName qname, Date date) {
    writeDate(qname,date,null);
  }

  public void writeUpdated(String date) {
    writeUpdated(date,null);
  }
  
  public void writePublished(String date) {
    writePublished(date,null);
  }
  
  public void writeEdited(String date) {
    writeEdited(date,null);
  }
  
  public void writeDate(QName qname, String date) {
    writeDate(qname, date,null);
  }
  
  
  public void writeLink(String iri) {
    writeLink(iri,(Map<QName,String>)null);
  }
  
  public void writeLink(String iri, String rel) {
    writeLink(iri,rel,(Map<QName,String>)null);
  }
  
  public void writeLink(String iri, String rel, String type) {
    writeLink(iri,rel,type,(Map<QName,String>)null);
  }
  
  public void writeLink(String iri, String rel, String type, String title, String hreflang, int length) {
    writeLink(iri,rel,type,title,hreflang,length,(Map<QName,String>)null);
  }
  
  public void startLink(String iri) {
    startLink(iri,(Map<QName,String>)null);
  }

  public void startLink(String iri, String rel) {
    startLink(iri,rel,(Map<QName,String>)null);
  }
  
  public void startLink(String iri, String rel, String type) {
    startLink(iri,rel,type,(Map<QName,String>)null);
  }
  
  public void startLink(String iri, String rel, String type, String title, String hreflang, int length) {
    startLink(iri,rel,type,title,hreflang,length,(Map<QName,String>)null);
  }
  
  
  public void writeCategory(String term) {
   writeCategory(term,(Map<QName,String>)null); 
  }
  
  public void writeCategory(String term, String scheme) {
   writeCategory(term,scheme,(Map<QName,String>)null); 
  }
  
  public void writeCategory(String term, String scheme, String label) {
   writeCategory(term,scheme,label,(Map<QName,String>)null); 
  }
  
  public void startCategory(String term) {
   startCategory(term,(Map<QName,String>)null); 
  }
  
  public void startCategory(String term, String scheme) {
   startCategory(term,scheme,(Map<QName,String>)null); 
  }
  
  public void startCategory(String term, String scheme, String label) {
   startCategory(term,scheme,label,(Map<QName,String>)null); 
  }
  
  public void startSource() {
    startElement(Constants.SOURCE,null);
  }
  
  public void writeText(QName qname, Text.Type type, String value) {
    writeText(qname,type,value,null);
  }
  
  public void startText(QName qname, Text.Type type) {
    startText(qname,type,null);
  }
  
  
  public void writeContent(Content.Type type, String value) {
    writeContent(type,value,null);
  }
  
  public void writeContent(Content.Type type, InputStream value) throws IOException {
    writeContent(type,value,null);
  }
  
  public void writeContent(Content.Type type, DataHandler value) throws IOException {
    writeContent(type,value,null);
  }
  
  public void writeContent(String type, String value) {
    writeContent(type,value,null);
  }
  
  
  public void startContent(Content.Type type) {
    startContent(type,(Map<QName,String>)null);
  }
  
  public void startContent(String type) {
    startContent(type,(Map<QName,String>)null);
  }
  
  public void startContent(Content.Type type, String src) {
    startContent(type,(Map<QName,String>)null);
  }
  
  public void startContent(String type, String src) {
    startContent(type,(Map<QName,String>)null);
  }
  
  public void startElement(QName qname) {
    startElement(qname,null);
  }
  
  
  
  public void startService() {
    startElement(Constants.SERVICE);
  }
  
  public void startService(Map<QName,String> attributes) {
    startElement(Constants.SERVICE,attributes);
  }
  
  public void endService() {
    endElement();
  }
  
  public void startWorkspace() {
    startElement(Constants.WORKSPACE, null);
  }
  
  public void startWorkspace(Map<QName,String> attributes) {
    startElement(Constants.WORKSPACE, attributes);
  }
  
  public void endWorkspace() {
    endElement();
  }
  
  public void startCollection(String href) {
    startCollection(href, null);
  }
  
  public void startCollection(String href, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    attributes.put(new QName("href"),href);
    startElement(Constants.COLLECTION, attributes);
  }
  
  public void endCollection() {
    endElement();
  }
  
  public void writeAccepts(String... accepts) {
    for (String accept : accepts) {
      startElement(Constants.ACCEPT,null);
      writeElementText(accept);
      endElement();
    }
  }
  
  public void startCategories() {
    startCategories(false,null,null);
  }
  
  public void startCategories(boolean fixed) {
    startCategories(fixed,null,null);
  }
  
  public void startCategories(boolean fixed, String scheme) {
    startCategories(fixed,scheme,null);
  }
  
  public void startCategories(Map<QName,String> attributes) {
    startCategories(false,null,attributes);
  }
  
  public void startCategories(boolean fixed, Map<QName,String> attributes) {
    startCategories(fixed,null,attributes);
  }
  
  public void startCategories(boolean fixed, String scheme, Map<QName,String> attributes) {
    if (attributes == null) attributes = new HashMap<QName,String>();
    if (fixed) attributes.put(new QName("fixed"),"true");
    else attributes.remove("fixed");
    if (scheme != null) attributes.put(new QName("scheme"),scheme);
    startElement(Constants.CATEGORIES, attributes);
  }
  
  public void endCategories() {
    endElement();
  }

  public void startControl() {
    startElement(Constants.CONTROL,null);
  }
  
  public void startControl(Map<QName,String> attributes) {
    startElement(Constants.CONTROL, attributes);
  }
  
  public void endControl() {
    endElement();
  }
  
  public void writeDraft(boolean draft) {
    startElement(Constants.DRAFT);
    writeElementText(draft?"yes":"no");
    endElement();
  }
  
}
