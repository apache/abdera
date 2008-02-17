package org.apache.abdera.protocol.server.adapters.hibernate;

import java.util.Date;


public class DummyData {
	
	public DummyData(){}
	
	private String id;
	private String author;
	private String title;
	private String content;
	private Date updated;
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
