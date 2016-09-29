package com.apps.jorge.monkeybisfire.model;

import java.io.Serializable;

public class Friend implements Serializable {
	private String name;
	private String city;
	private String photo;
	private String key;

	public Friend() {
		//Needed for Serialization
	}
	public Friend(String name, String city, String photo) {
		this.name = name;
		this.city = city;
		this.photo = photo;
	}
	public Friend(String name, String photo) {
		this.name = name;
		this.photo = photo;
	}
	public Friend(String key, String name, String city, String photo) {
		this.key = key;
		this.name = name;
		this.city = city;
		this.photo = photo;
	}
	public String getName() {
		return name;
	}

	public String getPhoto() {
		return photo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setValues(Friend updateFriend) {
		this.name = updateFriend.name;
		this.city = updateFriend.city;
		this.photo = updateFriend.photo;
	}
}
