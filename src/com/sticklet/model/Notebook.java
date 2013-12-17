package com.sticklet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.sticklet.dao.NoteDao;
import com.sticklet.model.base.BaseModel;
import com.sticklet.util.DateFormatUtil;

public class Notebook extends BaseModel {
	private static NoteDao noteDao = new NoteDao();
	public Notebook() {
		super();
	}
	
	public Notebook(Entity entity) {
		super(entity);
	}
	
	//private String title;
	public void setTitle(String title) {
		entity.setProperty("title", title);
	}
	public String getTitle() {
		return (String)entity.getProperty("title");
	}

	//private String description;
	public void setDescription(String desc) {
		entity.setProperty("description", desc);
	}
	public String getDescription() {
		return (String)entity.getProperty("description");
	}

	//private int color;
	public void setColor(Integer color) {
		entity.setProperty("color", color);
	}
	public Integer getColor() {
		Long l = (Long)entity.getProperty("color");
		if (l != null) {
			return (int)((long)l);
		}
		return null;
	}
	
	public Integer getNoteCount() {
		return null;
	}
	
	public void setIndex(Integer index) {
		entity.setProperty("index", index);
	}
	public Integer getIndex() {
		return (Integer)entity.getProperty("index");
	}

	public List<HashMap<String, Object>> getNotes() {
		List<Note> notes = noteDao.findAllBy("notebook", this.getKey());
		List<HashMap<String, Object>> noteMaps = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < notes.size(); i++) {
			noteMaps.add(notes.get(i).toHashMap());
		}
		return noteMaps;
	}

	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", getKeyStr());
		Date created = getCreated();
		if (created != null) {
			map.put("created", created.getTime());
		} else {
			map.put("created", "");
		}
		Date updated = getCreated();
		if (updated != null) {
			map.put("updated", getUpdated().getTime());
		} else {
			map.put("updated", "");
		}
		map.put("displayDate", DateFormatUtil.formatDate(updated != null ? updated : created));
		map.put("title", getTitle());
		map.put("description", getDescription());
		map.put("color", getColor());
		map.put("index", getIndex());
		map.put("noteCount", "");
		map.put("firstNote", "");
		map.put("tags", "");
		return map;
	}
}