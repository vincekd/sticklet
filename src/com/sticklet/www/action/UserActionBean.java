package com.sticklet.www.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.www.action.base.BaseActionBean;

@UrlBinding("/landing")
public class UserActionBean extends BaseActionBean {

	@DefaultHandler
	public Resolution login() {
		logger.info("landing page");
		return redirect("/landing.html?" + userService.createLoginURL("/"));
	}

}