package com.apps.jorge.monkeybisfire.model;

import java.io.Serializable;

public class ChatsDetails implements Serializable {
	private long id;
	private String date;
	private Friendb friend;
	private String content;
	private boolean fromMe;

	public ChatsDetails(long id, String date, Friendb friend, String content, boolean fromMe) {
		this.id = id;
		this.date = date;
		this.friend = friend;
		this.content = content;
		this.fromMe = fromMe;
	}

	public long getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public Friendb getFriend() {
		return friend;
	}

	public String getContent() {
		return content;
	}

	public boolean isFromMe() {
		return fromMe;
	}
}