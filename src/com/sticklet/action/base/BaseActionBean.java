package com.sticklet.action.base;

import java.security.Principal;
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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.sticklet.constants.StickletConstants;
import com.sticklet.model.User;

public abstract class BaseActionBean implements ActionBean {
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	protected ActionBeanContext context;
	protected com.google.appengine.api.users.User googleUser;
	protected User user = null;
	protected UserService userService = UserServiceFactory.getUserService();
	
	public BaseActionBean() {
		super();
		googleUser = userService.getCurrentUser();
	}

    @DefaultHandler
    public Resolution directByMethod() {
    	Principal userPrincipal = context.getRequest().getUserPrincipal();
    	if (userPrincipal != null) {
    		user = (User)context.getRequest().getSession().getAttribute(StickletConstants.SESSION_USER);
        	switch (context.getRequest().getMethod().toLowerCase()) {
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
        	redirect(userService.createLoginURL("/"));
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

    public HashMap<String, Object> getRequestData() {
    	String data = context.getRequest().getParameter("data");
    	HashMap<String, Object> map = null;
    	try {
    		//TODO: finish this
    		Gson gson = new Gson();
    		map = gson.fromJson(data, HashMap.class);
    		//JSONObject json = gson.fromJson(data, JSONObject.class);
    		//logger.info(json.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return map;
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

	public void setResponseNotFound() {
		context.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	public ActionBeanContext getContext() {
		return context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}
}