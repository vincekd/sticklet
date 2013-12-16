package com.sticklet.action;

import java.util.HashMap;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.sticklet.action.base.BaseActionBean;
import com.sticklet.dao.NotebookDao;
import com.sticklet.model.Notebook;

@UrlBinding("/notebook/{notebookId}")
public class NotebookActionBean extends BaseActionBean {
	public Long notebookId;
	
	protected NotebookDao notebookDao = new NotebookDao();
	
	public Resolution doGet() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("success", notebookId);
		
		notebookDao.save(new Notebook());
		
		return streamJSON(map);
	}
	
	private Notebook getNotebook() {
		//return notebookDao.find(notebookId);
		return null;
	}
}