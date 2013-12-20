package com.sticklet.www.model;

import javax.persistence.Column;

import com.sticklet.www.model.base.BaseModel;

public class Tag extends BaseModel {
	@Column(name="name")
	public String name;
}