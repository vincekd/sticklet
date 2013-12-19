package com.sticklet.model.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnSave;
import com.sticklet.util.DateFormatUtil;
import com.sticklet.util.StringUtil;

public abstract class BaseModel implements Model {
	@Ignore
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	@Ignore
	protected String modelName = this.getClass().getSimpleName();

	@Id
	public Long id;
	public void setId(long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public Date created;
	public void setCreated(Date date) {
		created = date;
	}
	public Date getCreated() {
		return created;
	}
	public Long formatCreated() {
		return created.getTime();
	}

	@OnSave
	public void onSave() {
		if (created == null) {
			setCreated(new Date());
		}
		setUpdated(new Date());
	}
	
	//@OnDelete

	public Date updated;
	public void setUpdated(Date date) {
		updated = date;
	}
	public Date getUpdated() {
		return updated;
	}
	public Long formatUpdated() {
		return updated.getTime();
	}

	@Ignore
	public String displayDate;
	public String formatDisplayDate() {
		return DateFormatUtil.formatDate(created);
	}

	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Class<?>[] clazzes = {};
		ArrayList<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(this.getClass().getFields()));
		fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
		for (Field field : fields) {
			Method method = null;
			try {
				method = this.getClass().getMethod("format" + StringUtil.capitalize(field.getName()), clazzes);
			} catch (NoSuchMethodException e) {}
			if (!field.isAnnotationPresent(Ignore.class) || method != null) {
				field.setAccessible(true);
				try {
					Object value = method != null ? method.invoke(this) : field.get(this);
					map.put(field.getName(), value);
				} catch (IllegalAccessException e) {
					//e.printStackTrace();
				} catch (InvocationTargetException e) {
					//e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	public boolean setProp(String prop, Object value) {
		try {
			Class<?>[] clazzes = {};//new Class<?>[1];

			Method method = this.getClass().getMethod("get" + StringUtil.capitalize(prop), clazzes);
			Class<?> returnType = method.getReturnType();

			Object curValue = method.invoke(this);

			clazzes = new Class<?>[1];
			clazzes[0] = returnType;

			method = this.getClass().getMethod("set" + StringUtil.capitalize(prop), clazzes);
			if ((returnType.equals(int.class) || returnType.equals(Integer.class))) {
				if (value instanceof Double) {
					value = ((Double)value).intValue();
				} else if (value instanceof Integer) {
					value = ((Integer)value).intValue();
				}
			}

			if (curValue != null && value == null || value != null && curValue == null || !curValue.equals(value)) {
				try {
					method.invoke(this, returnType.cast(value));
				} catch (ClassCastException e) {
					method.invoke(this, value);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return modelName + ": " + id;
	}
}