package com.sticklet.action;

import java.util.HashMap;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.sticklet.action.base.BaseActionBean;
import com.sticklet.model.Note;
import com.sticklet.model.Notebook;

@UrlBinding("/pages/{event}")
public class DefaultActionBean extends BaseActionBean {
	public Resolution doGet() {
		HashMap<String, Object> map = new HashMap<String, Object>();

		Notebook notebook = new Notebook();
		Note note = new Note();
		map.put("notebook", notebook.toHashMap());
		map.put("note", note.toHashMap());

		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String token = channelService.createChannel("" + user.getId() + ":" + Math.random());
		map.put("token", token);

		return streamJSON(map);
	}
	
	@HandlesEvent("logout")
	public Resolution logout() {
		return redirect(userService.createLogoutURL("/"));
	}
}