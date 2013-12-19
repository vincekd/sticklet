package com.sticklet.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.googlecode.objectify.Ref;
import com.sticklet.action.base.BaseActionBean;
import com.sticklet.dao.NoteDao;
import com.sticklet.dao.NotebookDao;
import com.sticklet.model.Notebook;
import com.sticklet.util.ChannelUtil;

@UrlBinding("/notebook/{notebookKey}")
public class NotebookActionBean extends BaseActionBean {
	public Long notebookKey;
	protected NotebookDao notebookDao = new NotebookDao();
	
	public NotebookActionBean() {
		super();
	}

	public Resolution doGet() {
		if (notebookKey == null) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			//get all
			List<HashMap<String, Object>> out = new ArrayList<HashMap<String, Object>>();
			notebookDao.setUser(user);
			List<Notebook> notebooks = notebookDao.findAll();
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
		Notebook notebook = new Notebook(user);
		if (notebook != null) {
			notebookDao.save(notebook);

			ChannelUtil.pushToUser(user, "notebook.created", notebook.toHashMap());
		}
		return null;
	}

	public Resolution doPut() {
		Notebook notebook = getNotebook();
		if (notebook != null) {
			HashMap<String, Object> json = getRequestData();
			
			boolean success = notebook.setProp((String)json.get("prop"), json.get("value"));
			
			if (success) {
				notebookDao.save(notebook);
				ChannelUtil.pushToUser(user, "notebook.updated", notebook.toHashMap());
			} else {
				setResponseBad();
			}
		}
		return null;
	}

	public Resolution doDelete() {
		Notebook notebook = getNotebook();
		if (notebook != null) {
			notebookDao.delete(notebook);
			
			NoteDao noteDao = new NoteDao();
			noteDao.setUser(user);
			noteDao.deleteAllBy("notebook", Ref.create(notebook));
			
			ChannelUtil.pushToUser(user, "notebook.deleted", notebook.toHashMap());
		}
		return null;
	}

	private Notebook getNotebook() {
		if (notebookKey != null) {
			notebookDao.setUser(user);
			Notebook notebook = notebookDao.find(notebookKey);
			return notebook;
		}
		return null;
	}
	
	private Resolution sendNotebook(Notebook notebook) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (notebook != null) {
			HashMap<String, Object> nbMap = notebook.toHashMap();
			nbMap.put("notes", notebook.getNotes());
			map.put("notebook", nbMap);
		}
		return streamJSON(map);
	}
}