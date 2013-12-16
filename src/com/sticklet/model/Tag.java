package com.sticklet.model;

import javax.persistence.Column;

import com.sticklet.model.base.BaseModel;

public class Tag extends BaseModel {
	@Column(name="name")
	public String name;
}