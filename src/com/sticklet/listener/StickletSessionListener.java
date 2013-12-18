package com.sticklet.listener;

import java.util.logging.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sticklet.constants.StickletConstants;
import com.sticklet.dao.UserDao;
import com.sticklet.model.User;

public class StickletSessionListener implements HttpSessionListener {
	protected Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
    public void sessionCreated(HttpSessionEvent se) {
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
		UserDao userDao = new UserDao();
		User user = userDao.findBy("googleUserId", googleUser.getUserId());
		logger.info(googleUser.getUserId());
		if (user == null) {
			logger.info("creating new user");
			user =  new User();
			user.setGoogleUserId(googleUser.getUserId());
			user.setName(googleUser.getNickname());
			user.setEmail(googleUser.getEmail());
			userDao.save(user);
		}
		logger.info(user.toString());
		se.getSession().setAttribute(StickletConstants.SESSION_USER, user);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    	se.getSession().setAttribute("user", null);
    }
}