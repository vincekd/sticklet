package com.sticklet.model.base;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sticklet.util.StringUtil;

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

	public boolean setProp(String prop, Object value) {
		try {
			Class<?>[] clazzes = {};//new Class<?>[1];
			
			Method method = this.getClass().getMethod("get" + StringUtil.capitalize(prop), clazzes);
			Class<?> returnType = method.getReturnType();

			clazzes = new Class<?>[1];
			clazzes[0] = returnType;

			method = this.getClass().getMethod("set" + StringUtil.capitalize(prop), clazzes);
			//Integer.parseInt((String)value);
			
			method.invoke(this, returnType.cast(value));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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

	public String getKeyStr() {
		return KeyFactory.keyToString(entity.getKey());
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