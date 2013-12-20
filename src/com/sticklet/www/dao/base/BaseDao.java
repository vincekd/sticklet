package com.sticklet.www.dao.base;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.sticklet.www.model.User;
import com.sticklet.www.model.base.BaseModel;

public abstract class BaseDao<T> {
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	protected Class entityClass;
	protected User user;
	protected Key userKey;

	public void setUser(User user) {
		this.user = user;
		userKey = Key.create(user);
	}

	public BaseDao() {
		getEntityClass();
	}

	private void getEntityClass() {
		try {
			entityClass = Class.forName(this.getClass().getName().replace("dao", "model").replace("Dao", ""));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Key<T> getKey(T model) {
		return Key.create(model);
	}

	public void save(T model) {
		try {
			((BaseModel)model).onSave();
		} catch (Exception e) {}
		ofy().save().entity(model).now();
	}
	
	public T find(Long id) {
		Key key = Key.create(userKey, entityClass, id);
		return getFirstResult(ofy().load().type(entityClass).ancestor(user).filterKey(key).list());
	}

	public T find(Key<T> key) {
		return ofy().load().key(key).now();
	}

	public List<T> findAll() {
		if (user != null) {
			return ofy().load().type(entityClass).ancestor(user).list();
		}
		return ofy().load().type(entityClass).list();
	}

	public List<T> findAllBy(String name, Object val) {
		if (user != null) {
			return ofy().load().type(entityClass).ancestor(user).filter(name, val).list();
		}
		return ofy().load().type(entityClass).filter(name, val).list();
	}
	
	public Integer findCountBy(String name, Object val) {
		if (user != null) {
			return ofy().load().type(entityClass).ancestor(user).filter(name, val).count();
		}
		return ofy().load().type(entityClass).filter(name, val).count();
	}
	
	public T findBy(String name, Object val) {
		if (user != null) {
			return getFirstResult(ofy().load().type(entityClass).ancestor(user).filter(name, val).list());
		}
		return getFirstResult(ofy().load().type(entityClass).filter(name, val).list());
	}

	public void delete(T model) {
		ofy().delete().entity(model).now();
	}
	
	public void deleteAllBy(String name, Object value) {
		//TODO: fix this to be better if possible
		List<T> list = findAllBy(name, value);
		for (T t : list) {
			ofy().delete().entity(t);
		}
		//ofy().delete().type(entityClass);
	}

	public void delete(Key<T> key) {
		ofy().delete().key(key).now();
	}
	
	private T getFirstResult(List<T> results) {
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}
}