package com.sticklet.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.action.base.BaseActionBean;

@UrlBinding("/_ah/login_required")
public class UserActionBean extends BaseActionBean {

	@DefaultHandler
	public Resolution login() {
		return redirect(userService.createLoginURL("/"));
	}

}