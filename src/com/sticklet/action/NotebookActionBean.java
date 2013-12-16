package com.sticklet.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.action.base.BaseActionBean;
import com.sticklet.dao.NotebookDao;
import com.sticklet.model.Notebook;

@UrlBinding("/notebook/{notebookKey}")
public class NotebookActionBean extends BaseActionBean {
	public String notebookKey;
	protected NotebookDao notebookDao = new NotebookDao();

	//@DefaultHandler
	public Resolution doGet() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		logger.info("notebook: " + notebookKey);
		if (notebookKey == null) {
			//get all
			List<HashMap<String, Object>> out = new ArrayList<HashMap<String, Object>>();
			List<Notebook> notebooks = notebookDao.fetch();
			for (int i = 0; i < notebooks.size(); i++) {
				out.add(notebooks.get(i).toHashMap());
			}
			map.put("notebooks", out);
		} else {
			//find notebooks
			Notebook notebook = notebookDao.find(notebookKey);
			map.put("notebook", (notebook != null ? notebook.toHashMap() : null));
		}

		return streamJSON(map);
	}
	
	public Resolution doPost() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Notebook notebook = new Notebook();
		notebook.setTitle("testing");
		notebookDao.save(notebook);
		logger.info(notebook.toString());
		map.put("notebook", notebook.toHashMap());
		logger.info(notebook.toHashMap().toString());
		return streamJSON(map);
	}

	private Notebook getNotebook() {
		//return notebookDao.find(notebookKey);
//		return DatestoreKeyFactory.stringToKey(notebookKey));
		return null;
	}
}