package com.humanify.localization.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Locale
{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String tag;	
	@ManyToOne
	private Source source;
	private long lastModified;
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getTag() { return tag; }
	public void setTag(String tag) { this.tag = tag; }
	
	public Source getSource() { return source; }
	public void setSource(Source source) { this.source = source; }
	
	public long getLastModified() { return lastModified; }
	public void setLastModified(long ts) { this.lastModified = ts; }
	
	public void setModified() { this.lastModified = Instant.now().toEpochMilli(); }
	
	protected Locale() {}
	
	public Locale(Source source, String tag)
	{
		this.source = source;
		this.tag = tag;
	}
	
	public Locale(Locale other)
	{
		this.id = other.id;
		this.source = other.source;
		this.tag = other.tag;
	}
	
	@Override
	public String toString()
	{
		return String.format("Locale[id:%d, tag:%s, src:%s]", id, tag, source.toString());
	}
	
}
