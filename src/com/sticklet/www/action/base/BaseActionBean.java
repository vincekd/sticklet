package com.sticklet.www.action.base;

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
import com.sticklet.www.dao.UserDao;
import com.sticklet.www.model.User;

public abstract class BaseActionBean implements ActionBean {
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	protected ActionBeanContext context;
	protected com.google.appengine.api.users.User googleUser;
	protected User user = null;
	protected UserService userService = UserServiceFactory.getUserService();
	
	public BaseActionBean() {
		super();
	}

    @DefaultHandler
    public Resolution directByMethod() {
    	if (userService.isUserLoggedIn()) {

    		setUser();

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
    	}
    	return logout();
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
    		Gson gson = new Gson();
    		map = gson.fromJson(data, HashMap.class);
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

	protected void setResponseNotFound() {
		context.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	protected void setResponseBad() {
		context.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	public ActionBeanContext getContext() {
		return context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}
	
	private void setUser() {
		//user = (User)context.getRequest().getSession().getAttribute(StickletConstants.SESSION_USER);
		//if (user == null) {
		googleUser = userService.getCurrentUser();
		UserDao userDao = new UserDao();
		user = userDao.findBy("googleUserId", googleUser.getUserId());
		//logger.info(googleUser.getUserId());
		if (user == null) {
			logger.info("creating new user");
			user =  new User();
			user.setGoogleUserId(googleUser.getUserId());
			user.setName(googleUser.getNickname());
			user.setEmail(googleUser.getEmail());
			userDao.save(user);
		}
		//logger.info("user to session " + user.toString());
			//context.getRequest().getSession().setAttribute(StickletConstants.SESSION_USER, (User)user);
		//}
		//logger.info("final user" + user.toString());
	}
	
//	@Override
//	public void getSourcePageResolution() {
//		
//	}
	
	private Resolution logout() {
		return redirect(userService.createLoginURL("/"));	
	}
}