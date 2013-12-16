package com.sticklet.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.sticklet.model.base.BaseModel;

@Entity
public class Notebook extends BaseModel {
	String title;
	
	@Lob
	String description;
	
	int color;
	
	@ManyToMany(fetch=FetchType.EAGER)
	Set<Tag> tags;
	
	@OneToMany(fetch=FetchType.LAZY)
	Set<Note> notes;
}