package com.sticklet.dao.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.sticklet.model.base.BaseModel;


public abstract class BaseDao<T> {
	protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	protected Class entityClass;
	public BaseDao() {
		try {
			entityClass = Class.forName(this.getClass().getName().replace("dao", "model").replace("Dao", ""));
		} catch (ClassNotFoundException e) {

		}
	}
	
	public void save(BaseModel model) {
		model.setUpdated(new Date());
		datastore.put(model.getEntity());
	}
	
	public void delete(BaseModel model) {
		datastore.delete(model.getKey());
	}
	
	public T find(String key) {
		Entity entity;
		try {
			entity = datastore.get(KeyFactory.stringToKey(key));
		} catch (EntityNotFoundException e) {
			return null;
		}
		return getInstanceFromEntity(entity);
	}
	
	public List<T> fetch() {
		List<T> models = new ArrayList<T>();
		try {
			Query query = new Query(entityClass.getSimpleName());
			PreparedQuery pq = datastore.prepare(query);
			for (Entity entity : pq.asIterable()) {
				models.add(getInstanceFromEntity(entity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}
	
	protected List<T> getInstanceFromEntities(List<Entity> entities) {
		List<T> list = new ArrayList<T>();
		if (entities != null) {
			for (int i = 0; i < entities.size(); i++) {
				list.add(getInstanceFromEntity(entities.get(i)));
			}
		}
		return list;
	}
	
	protected T getInstanceFromEntity(Entity entity) {
		try {
			return (T)entityClass.getConstructor(Entity.class).newInstance(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}