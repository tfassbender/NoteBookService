package net.jfabricationgames.notebook.note;

import java.time.LocalDateTime;
import java.util.List;

public class NoteSelector {
	
	private List<Integer> ids;
	private LocalDateTime date;
	private int priority;
	
	private Relation idRelation;
	private Relation dateRelation;
	private Relation priorityRelation;
	
	public NoteSelector() {
		//default constructor for java bean convention
	}
	
	@Override
	public String toString() {
		return "NoteSelector [ids=" + ids + ", date=" + date + ", priority=" + priority + ", idRelation=" + idRelation + ", dateRelation="
				+ dateRelation + ", priorityRelation=" + priorityRelation + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((dateRelation == null) ? 0 : dateRelation.hashCode());
		result = prime * result + ((idRelation == null) ? 0 : idRelation.hashCode());
		result = prime * result + ((ids == null) ? 0 : ids.hashCode());
		result = prime * result + priority;
		result = prime * result + ((priorityRelation == null) ? 0 : priorityRelation.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NoteSelector other = (NoteSelector) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		if (dateRelation != other.dateRelation)
			return false;
		if (idRelation != other.idRelation)
			return false;
		if (ids == null) {
			if (other.ids != null)
				return false;
		}
		else if (!ids.equals(other.ids))
			return false;
		if (priority != other.priority)
			return false;
		if (priorityRelation != other.priorityRelation)
			return false;
		return true;
	}
	
	public static NoteSelector empty() {
		return new NoteSelectorBuilder().build();
	}
	
	public List<Integer> getIds() {
		return ids;
	}
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public Relation getIdRelation() {
		return idRelation;
	}
	public void setIdRelation(Relation idRelation) {
		this.idRelation = idRelation;
	}
	
	public Relation getDateRelation() {
		return dateRelation;
	}
	public void setDateRelation(Relation dateRelation) {
		this.dateRelation = dateRelation;
	}
	
	public Relation getPriorityRelation() {
		return priorityRelation;
	}
	public void setPriorityRelation(Relation priorityRelation) {
		this.priorityRelation = priorityRelation;
	}
}