package com.sticklet.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.action.base.BaseActionBean;

@UrlBinding("/*")
public class DefaultActionBean extends BaseActionBean {
	@DefaultHandler
	public Resolution doGet() {
		setResponseNotFound();
		return redirect("/");	
	}
}