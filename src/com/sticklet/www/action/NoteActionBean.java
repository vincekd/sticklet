package com.sticklet.www.action;

import java.util.Date;
import java.util.HashMap;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.googlecode.objectify.Ref;
import com.sticklet.www.action.base.BaseActionBean;
import com.sticklet.www.dao.NoteDao;
import com.sticklet.www.dao.NotebookDao;
import com.sticklet.www.model.Note;
import com.sticklet.www.model.Notebook;
import com.sticklet.www.util.ChannelUtil;

@UrlBinding("/note/{noteId}")
public class NoteActionBean extends BaseActionBean {
	protected NoteDao noteDao = new NoteDao();
	protected NotebookDao notebookDao = new NotebookDao();
	public Long noteId;

	public Resolution doGet() {
		this.context.getRequest().getMethod();
		Note note = getNote();
		if (note != null) {
			return streamJSON(note.toHashMap());
		}
		return null;
	}

	public Resolution doPost() {
		HashMap<String, Object> data = getRequestData();
		if (data != null && data.get("notebook") != null) {
			long notebookId = Long.parseLong((String)data.get("notebook"));
			Notebook notebook = getNotebook(notebookId);
			if (notebook != null) {

				Note note = new Note(user);
				note.setNotebook(Ref.create(notebook));
				noteDao.save(note);

				ChannelUtil.pushToUser(user, "note.created", note.toHashMap());
			}
		}
		return null;
	}

	public Resolution doPut() {
		Note note = getNote();
		if (note != null) {
			HashMap<String, Object> json = getRequestData();
			String prop = ((String)json.get("prop")).trim();

			if (prop.equals("notebook")) {
				Notebook notebook = getNotebook(Long.parseLong((String)json.get("value")));
				json.put("value", Ref.create(notebook));
			} 

			boolean success = note.setProp(prop, json.get("value"));

			if (success) {
				noteDao.save(note);
				ChannelUtil.pushToUser(user, "note.updated", note.toHashMap());
			} else {
				setResponseBad();
			}
		}
		return null;
	}

	public Resolution doDelete() {
		Note note = getNote();
		if (note != null) {
			noteDao.delete(note);
			ChannelUtil.pushToUser(user, "note.deleted", note.toHashMap());
		}
		return null;
	}

	private Note getNote() {
		if (noteId != null) {
			noteDao.setUser(user);
			return noteDao.find(noteId);
		}
		return null;
	}

	private Notebook getNotebook(Long notebookId) {
		notebookDao.setUser(user);
		return notebookDao.find(notebookId);
	}
}