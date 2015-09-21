package com.humanify.localization.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message
{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private Locale locale;
	@Column(name="msgKey")
	private String key;
	@Column(name="msgText")
	private String text;
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Locale getLocale() { return locale; }
	public void setLocale(Locale locale) { this.locale = locale; }
	
	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	protected Message() {}
	
	public Message(Locale locale, String key, String text)
	{
		this.locale = locale;
		this.key = key;
		this.text = text;
	}
	
	public Message(Message other)
	{
		this.id = other.id;
		this.locale = other.locale;
		this.key = other.key;
		this.text = other.text;
	}
	
	@Override
	public String toString()
	{
		return String.format("Message[id:%d, key:%s, text:%s, locale:%s]", id, key, text, locale.toString());
	}
	
}
