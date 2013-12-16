package com.sticklet.model;

import com.google.appengine.api.datastore.Entity;
import com.sticklet.model.base.BaseModel;

public class Note extends BaseModel {
	
	public Note() {
		
	}
	
	public Note(Entity entity) {
		super(entity);
	}
	
	//String title;
	public void setTitle(String title) {
		entity.setProperty("title", title);
	}
	public String getTitle() {
		return (String)entity.getProperty("title");
	}
	
	//String content;
	public void setContent(String content) {
		entity.setProperty("content", content);
	}
	public String getContent() {
		return (String)entity.getProperty("content");
	}
	
	//Notebook notebook;
	public void setNotebook(Notebook notebook) {
		entity.setProperty("notebook", notebook);
	}
	public Notebook getNotebook() {
		return (Notebook)entity.getProperty("notebook");
	}
	
	//int color;
	public void setColor(Integer color) {
		entity.setProperty("color", color);
	}
	public Integer getColor() {
		return (Integer)entity.getProperty("color");
	}
	
//	@ManyToMany(fetch=FetchType.EAGER)
//	Set<Tag> tags;
}