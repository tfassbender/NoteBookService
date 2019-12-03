package net.jfabricationgames.notebook.service;

import java.util.List;

import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

public enum NoteBookServiceMethods {
	CREATE_NOTE("create_note", Note.class, Integer.class),//
	GET_NOTES("get_notes", NoteSelector.class, List.class),//
	UPDATE_NOTE("update_note", Note.class, Integer.class),//
	DELETE_NOTES("delete_notes", NoteSelector.class, Integer.class);
	
	private final String methodName;
	private final Class<?> parameter;
	private final Class<?> returnType;
	
	private NoteBookServiceMethods(String methodName, Class<?> parameter, Class<?> returnType) {
		this.methodName = methodName;
		this.parameter = parameter;
		this.returnType = returnType;
	}
	
	public String getMethodName() {
		return methodName;
	}
	public Class<?> getParameter() {
		return parameter;
	}
	public Class<?> getReturnType() {
		return returnType;
	}
}