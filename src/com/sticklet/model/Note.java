package com.sticklet.model;

import java.util.HashMap;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.sticklet.model.base.BaseModel;

@Entity
public class Note extends BaseModel {
	
	public Note() {
		super();
	}
	public Note(User user) {
		super();
		setUser(Ref.create(user));
	}
	
	@Parent
	@Index
	public Ref<User> user;
	public void setUser(Ref<User> user) {
		this.user = user;
	}
	public Ref<User> getUser() {
		return user;
	}
	public HashMap<String, Object> formatUser() {
		if (user != null) {
			return user.get().toHashMap();
		}
		return null;
	}

	@Index
	Ref<Notebook> notebook;
	public void setNotebook(Ref<Notebook> ref) {
		notebook = ref;
	}
	public Ref<Notebook> getNotebook() {
		return notebook;
	}
	public Long formatNotebook() {
		if (notebook != null) {
			return notebook.get().getId();
		}
		return null;
	}
	
	String title;
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
	String content;
	public void setContent(String desc) {
		content = desc;
	}
	public String getContent() {
		return content;
	}

	int color;
	public void setColor(int color) {
		this.color = color;
	}
	public int getColor() {
		return color;
	}
	
	int index;
	public void setIndex(int index) {
		this.index = index;
	}
	public int getIndex() {
		return index;
	}
}