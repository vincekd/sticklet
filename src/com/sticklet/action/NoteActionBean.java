package com.sticklet.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.action.base.BaseActionBean;

@UrlBinding("/note/{noteId}")
public class NoteActionBean extends BaseActionBean {
	Long noteId;
	
	public Resolution doGet() {
		this.context.getRequest().getMethod();
		return new StreamingResolution("['hi', 3]");
	}
}