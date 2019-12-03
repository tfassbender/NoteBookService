package net.jfabricationgames.notebook.note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteSelectorBuilder {
	
	private NoteSelector selector;
	
	public NoteSelectorBuilder() {
		selector = new NoteSelector();
		selector.setIdRelation(NoteRelation.NONE);
		selector.setDateRelation(NoteRelation.NONE);
		selector.setPriorityRelation(NoteRelation.NONE);
	}
	
	public NoteSelectorBuilder setIds(List<Integer> ids) {
		selector.setIds(ids);
		return this;
	}
	public NoteSelectorBuilder addIds(List<Integer> ids) {
		if (selector.getIds() == null) {
			selector.setIds(new ArrayList<Integer>(ids));
		}
		else {
			selector.getIds().addAll(ids);
		}
		return this;
	}
	public NoteSelectorBuilder addId(int id) {
		if (selector.getIds() == null) {
			selector.setIds(new ArrayList<Integer>());
		}
		selector.getIds().add(id);
		return this;
	}
	
	public NoteSelectorBuilder setDate(LocalDateTime date) {
		selector.setDate(date);
		return this;
	}
	
	public NoteSelectorBuilder setPriority(int priority) {
		selector.setPriority(priority);
		return this;
	}
	
	public NoteSelectorBuilder setIdRelation(NoteRelation idRelation) {
		selector.setIdRelation(idRelation);
		return this;
	}
	
	public NoteSelectorBuilder setDateRelation(NoteRelation dateRelation) {
		selector.setDateRelation(dateRelation);
		return this;
	}
	
	public NoteSelectorBuilder setPriorityRelation(NoteRelation priorityRelation) {
		selector.setPriorityRelation(priorityRelation);
		return this;
	}
	
	public NoteSelector build() {
		return selector;
	}
}