package com.sticklet.model.base;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class BaseModel {
	protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	protected Entity entity;
	protected String modelName = this.getClass().getSimpleName();

	public BaseModel() {
		entity = new Entity(modelName);
		setCreated(new Date());
	}

	public BaseModel(Entity entity) {
		if (entity == null) {
			throw new NullPointerException();
		}
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Key getKey() {
		return entity.getKey();
	}

	private void setCreated(Date date) {
		entity.setProperty("created", date);
	}

	public Date getCreated() {
		return (Date)entity.getProperty("created");
	}

	public void setUpdated(Date date) {
		entity.setProperty("updated", date);
	}
	public Date getUpdated() {
		return (Date)entity.getProperty("updated");
	}

	@PrePersist
	protected void onCreate() {
		setCreated(new Date());
	}

    @PreUpdate
    protected void onUpdate() {
        setUpdated(new Date());
    }

    @Override
    public String toString() {
    	//return modelName + ": " + entity.getKey();
    	return modelName + ": " + getKey();
    }
    
    public HashMap<String, Object> toHashMap() {
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	map.put("type", entity.getKind());
    	map.put("key", KeyFactory.keyToString(getKey()));
    	return map;
    }
}