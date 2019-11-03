package net.jfabricationgames.notebook.note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteSelectorBuilder {
	
	private NoteSelector selector;
	
	public NoteSelectorBuilder() {
		selector = new NoteSelector();
		selector.setIdRelation(Relation.NONE);
		selector.setDateRelation(Relation.NONE);
		selector.setPriorityRelation(Relation.NONE);
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
	
	public NoteSelectorBuilder setIdRelation(Relation idRelation) {
		selector.setIdRelation(idRelation);
		return this;
	}
	
	public NoteSelectorBuilder setDateRelation(Relation dateRelation) {
		selector.setDateRelation(dateRelation);
		return this;
	}
	
	public NoteSelectorBuilder setPriorityRelation(Relation priorityRelation) {
		selector.setPriorityRelation(priorityRelation);
		return this;
	}
	
	public NoteSelector build() {
		return selector;
	}
}