package com.sticklet.action;

import java.io.IOException;
import java.security.Principal;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.sticklet.action.base.BaseActionBean;
import com.sticklet.constants.StickletConstants;
import com.sticklet.model.User;
import com.sticklet.util.ChannelUtil;

@UrlBinding("/_ah/channel/{event}")
public class ChannelActionBean extends BaseActionBean {

	@HandlesEvent("connected")
	public Resolution connected() {
		logger.info("channel connecting");
		try {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(context.getRequest());
			Long userId = Long.parseLong(presence.clientId().substring(0, presence.clientId().indexOf(":")));
			ChannelUtil.addClient(userId, presence.clientId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@HandlesEvent("disconnected")
	public Resolution disconnected() {
		logger.info("channel disconnected");
		try {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(context.getRequest());
			Long userId = Long.parseLong(presence.clientId().substring(0, presence.clientId().indexOf(":")));
			ChannelUtil.removeClient(userId, presence.clientId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}