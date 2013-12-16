package com.sticklet.servlet.base;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class StickletServlet extends HttpServlet {
	protected void stream(HttpServletResponse resp, String string) throws IOException {
		resp.setContentType("text/json");
		resp.getWriter().println("Hello, world");
	}
	
	protected void stream(HttpServletResponse resp, JSONObject json) throws IOException {
		stream(resp, json.toString());
	}
}