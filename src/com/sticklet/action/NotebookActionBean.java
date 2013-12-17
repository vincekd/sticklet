package com.sticklet.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sticklet.action.base.BaseActionBean;
import com.sticklet.dao.NotebookDao;
import com.sticklet.model.Notebook;

@UrlBinding("/notebook/{notebookKey}")
public class NotebookActionBean extends BaseActionBean {
	public String notebookKey;
	protected NotebookDao notebookDao = new NotebookDao();

	//@DefaultHandler
	public Resolution doGet() {
		//logger.info("notebook: " + notebookKey);
		if (notebookKey == null) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			//get all
			List<HashMap<String, Object>> out = new ArrayList<HashMap<String, Object>>();
			List<Notebook> notebooks = notebookDao.fetch();
			for (int i = 0; i < notebooks.size(); i++) {
				out.add(notebooks.get(i).toHashMap());
			}
			map.put("notebooks", out);
			return streamJSON(map);
		}
		//find notebook
		Notebook notebook = getNotebook();
		return sendNotebook(notebook);
	}
	
	public Resolution doPost() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Notebook notebook = new Notebook();
		notebook.setTitle("New Notebook");
		notebook.setDescription("What kinds of notes do I hold?");
		notebookDao.save(notebook);
		map.put("notebook", notebook.toHashMap());
		//TODO: notify websockets
		return streamJSON(map);
	}
	
	public Resolution doPut() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Notebook notebook = getNotebook();
		//get sent data
//		String prop = context.getRequest().getParameter("prop");
//		Object value = context.getRequest().getParameter("value");

		HashMap<String, Object> json = getRequestData();
		
		boolean success = notebook.setProp((String)json.get("prop"), json.get("value"));
		map.put("success", success);

		//TODO: notify websockets
		return streamJSON(map);
	}

	private Notebook getNotebook() {
		Notebook notebook = notebookDao.find(notebookKey);
		return notebook;
	}
	private Resolution sendNotebook(Notebook notebook) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> nbMap = (notebook != null ? notebook.toHashMap() : null);
		nbMap.put("notes", notebook.getNotes());
		map.put("notebook", nbMap);
		return streamJSON(map);
	}
}