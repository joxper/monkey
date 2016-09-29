package com.apps.jorge.monkeybisfire.model;

import java.io.Serializable;

public class Chat implements Serializable {
	private long id;
	private String date;
	private boolean read = false;
	private Friendb friend;
	private String snippet;
	public Chat(long id, String date, boolean read, Friendb friend, String snippet) {
		this.id = id;
		this.date = date;
		this.read = read;
		this.friend = friend;
		this.snippet = snippet;
	}

	public long getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public boolean isRead() {
		return read;
	}

	public Friendb getFriend() {
		return friend;
	}

	public String getSnippet() {
		return snippet;
	}
}