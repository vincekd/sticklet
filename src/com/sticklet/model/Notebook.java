package com.sticklet.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.sticklet.dao.NoteDao;
import com.sticklet.dao.NotebookDao;
import com.sticklet.model.base.BaseModel;

@Entity
@Cache
public class Notebook extends BaseModel {
	@Ignore
	private static NotebookDao notebookDao = new NotebookDao();
	@Ignore
	private static NoteDao noteDao = new NoteDao();
	
	public Notebook() {
		super();
	}
	public Notebook(User user) {
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

	String title;
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
//	String description = "What kind of notes do I save?";
//	public void setDescription(String desc) {
//		description = desc;
//	}
//	public String getDescription() {
//		return description;
//	}

	int color = 1;
	public void setColor(int color) {
		this.color = color;
	}
	public int getColor() {
		return color;
	}
	
	@Index
	int index;
	public void setIndex(int index) {
		this.index = index;
	}
	public int getIndex() {
		return index;
	}

	public List<HashMap<String, Object>> getNotes() {
		noteDao.setUser(user.get());
		List<Note> notes = noteDao.findAllBy("notebook", Ref.create(this));
		ArrayList<HashMap<String, Object>> out = new ArrayList<HashMap<String, Object>>();
		if (notes != null) {
			for (Note note : notes) {
				out.add(note.toHashMap());
			}
		}
		return out;
	}

	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = super.toHashMap();
		map.put("noteCount", this.id != null ? noteDao.findCountBy("notebook", Ref.create(this)) : 0);
		return map;
	}
}