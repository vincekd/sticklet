package com.sticklet.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.sticklet.model.base.BaseModel;

@Entity
public class Note extends BaseModel {
	
	public Note() {
		
	}
	
	String title;
	
	@Lob
	String content;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="notebook_id", nullable=false, updatable=true)
	Notebook notebook;
	
	int color;
	
	@ManyToMany(fetch=FetchType.EAGER)
	Set<Tag> tags;
}