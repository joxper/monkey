package com.apps.jorge.monkeybisfire.model;

import java.io.Serializable;

public class Friendb implements Serializable {
	private long id;
	private String name;
	private String photo2;
	private int photo;

	public Friendb() {}

	public Friendb(long id, String name, int photo) {
		this.id = id;
		this.name = name;
		this.photo = photo;
	}

	public Friendb(String name, int photo) {
		this.name = name;
		this.photo = photo;
	}

	public Friendb(String name, String photo2) {
		this.name = name;
		this.photo2 = photo2;
	}
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoto2() {
		return photo2;
	}

	public int getPhoto() {
		return photo;
	}

	public void setId(int id) { this.id = id; }

	public void setName(String name) {
		this.name = name;
	}

	public void setPhoto2(String photo2) {
		this.photo2 = photo2;
	}

	public void setPhoto(int photo) {
		this.photo = photo;
	}
}
