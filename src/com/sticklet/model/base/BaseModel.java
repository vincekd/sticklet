package com.sticklet.model.base;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Key;

@MappedSuperclass
public abstract class BaseModel {
	//attributes
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	public Key key;

	public Date updated;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }
    
	public Date created;

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
    
    @Transient
    @Override
    public String toString() {
    	return this.getClass().getSimpleName() + ": " + this.key;
    }
}