package com.sticklet.dao.base;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.Key;


public abstract class BaseDao<T, Long> {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    protected Class entityClass;

    public BaseDao() {
    	try {
    		entityClass = Class.forName(this.getClass().getName().replace("dao", "model").replace("Dao",""));
    	} catch (ClassNotFoundException e) {
    		
    	}
    }
    
    public T find(Key key) {
    	T obj = null;
    	PersistenceManager pm = getEntityManager();
    	try {
    		//pm.newQuery(entityClass, key);
    	} finally {
    		pm.close();
    	}
    	return obj;
    }
    
    public void delete(T obj) {
    	PersistenceManager pm = getEntityManager();
    	try {
    		pm.deletePersistent(obj);
    	} finally {
    		pm.close();
    	}
    }
    
    public void save(T obj) {
    	PersistenceManager pm = getEntityManager();
    	try {
    		pm.makePersistent(obj);
    	} finally {
    		pm.close();
    	}
    }

    private PersistenceManager getEntityManager() {
    	return pmfInstance.getPersistenceManager();
    }
}