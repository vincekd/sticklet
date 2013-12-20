package com.sticklet.www.listener;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;
import com.sticklet.www.constants.StickletConstants;
import com.sticklet.www.model.Note;
import com.sticklet.www.model.Notebook;
import com.sticklet.www.model.User;

public class StickletContextListener implements ServletContextListener {
	public static ServletContext context;

	static {
		ObjectifyService.register(Notebook.class);
		ObjectifyService.register(Note.class);
		ObjectifyService.register(User.class);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		context = event.getServletContext();
		context.setAttribute(StickletConstants.CONTEXT_CHANNELS, new HashMap<Long, List<String>>());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}
	
	public static ServletContext getContext() {
		return context;
	}
}