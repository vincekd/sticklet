package com.sticklet.www.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.sticklet.www.model.base.BaseModel;

@Entity
public class User extends BaseModel implements Serializable {

	//private final static long serialVersionUID = 1L;

	public String email;
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}

	public String name;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	@Index
	public String googleUserId;
	public void setGoogleUserId(String id) {
		googleUserId = id;
	}
	public String getGoogleUserId() {
		return googleUserId;
	}
}