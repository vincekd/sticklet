package com.sticklet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sticklet.constants.StickletConstants;
import com.sticklet.listener.StickletContextListener;
import com.sticklet.model.User;

public class ChannelUtil {
	private static Logger logger = Logger.getLogger(ChannelUtil.class.getName());
	public static ServletContext context = StickletContextListener.getContext();
	
	public static void pushToUser(User user, String callback, HashMap<String, Object> map) {
		JSONObject json = new JSONObject();
		try {
			json.put("callback", callback);
			json.put("data", map);
			pushToUser(user, json.toString());
		} catch (JSONException e) {}
	}

	public static void pushToUser(User user, String message) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		HashMap<Long, List<String>> connections = getConnections();
		List<String> clients = connections.get(user.id);
		if (clients != null) {
			for (String client : clients) {
				channelService.sendMessage(new ChannelMessage(client, message));
			}
		}
	}

	public static HashMap<Long, List<String>> getConnections() {
		HashMap<Long, List<String>> connections = (HashMap<Long, List<String>>)context.getAttribute(StickletConstants.CONTEXT_CHANNELS);
		return connections;
	}

	public static void setConnections(HashMap<Long, List<String>> connections) {
		context.setAttribute(StickletConstants.CONTEXT_CHANNELS, connections);
	}

	public static void addClient(Long user, String clientId) {
		HashMap<Long, List<String>> connections = getConnections();
		if (connections != null) {
			List<String> clients = connections.get(user);
			if (clients == null) {
				clients = new ArrayList<String>();
			}
			clients.add(clientId);
			connections.put(user, clients);
			setConnections(connections);
		}
	}
	
	public static void removeClient(Long user, String clientId) {
		HashMap<Long, List<String>> connections = getConnections();
		List<String> clients = connections.get(user);
		if (clients != null) {
			clients.remove(clientId);
			connections.put(user, clients);
			setConnections(connections);
		}
	}
}