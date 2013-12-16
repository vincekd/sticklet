package com.sticklet.action.base;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class BaseActionBean implements ActionBean {
	protected ActionBeanContext context;
	protected User user;
	protected Logger logger = Logger.getLogger(this.getClass().getName());

    @DefaultHandler
    public Resolution directByMethod() {
    	UserService userService = UserServiceFactory.getUserService();
    	user = userService.getCurrentUser();
    	if (user != null) {
        	switch (context.getRequest().getMethod()) {
        	case "put":
        		return this.doPut();
        	case "post":
        		return this.doPost();
        	case "delete":
        		return this.doDelete();
        	case "get":
        	default:
        		return this.doGet();
        	}
        } else {
        	//redirect(userService.createLoginURL("/"));
        }
        return null;
    }

    public Resolution doPut() {
    	context.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    	return null;
    }

    public Resolution doPost() {
    	context.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    	return null;
    }

    public Resolution doDelete() {
    	context.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    	return null;
    }

    public Resolution doGet() {
    	context.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    	return null;
    }

    public Resolution stream(String json) {
        return new StreamingResolution("application/json", json);
    }
    
    public Resolution streamJSON(HashMap<String, Object> map) {
    	return stream(new JSONObject(map));
    }

    public Resolution stream(Object json) {
        return stream(json.toString());
    }

    public Resolution forward(String page) {
        return new ForwardResolution(page);
    }

    public Resolution redirect(String url) {
        return new RedirectResolution(url);
    }
    
	public ActionBeanContext getContext() {
		return context;
	}
	
	public void setResponseNotFound() {
		context.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	public void setContext(ActionBeanContext context) {
		this.context = context;
	}
}