package com.sticklet.model;

import java.util.Date;
import java.util.HashMap;

import com.google.appengine.api.datastore.Entity;
import com.sticklet.model.base.BaseModel;
import com.sticklet.util.DateFormatUtil;

public class Notebook extends BaseModel {
	
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
		return (Integer)entity.getProperty("color");
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

	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", getKey());
		Date created = getCreated();
		if (created != null) {
			map.put("created", created.getTime());
		} else {
			map.put("created", null);
		}
		Date updated = getCreated();
		if (updated != null) {
			map.put("updated", getUpdated().getTime());
		} else {
			map.put("updated", null);
		}
		map.put("displayDate", DateFormatUtil.formatDate(updated != null ? updated : created));
		map.put("title", getTitle());
		map.put("description", getDescription());
		map.put("color", getColor());
		map.put("index", getIndex());
		map.put("noteCount", null);
		map.put("firstNote", null);
		map.put("tags", null);
		return map;
	}
}