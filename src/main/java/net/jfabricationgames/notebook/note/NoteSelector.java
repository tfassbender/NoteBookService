package net.jfabricationgames.notebook.note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;
import net.jfabricationgames.json_rpc.util.JsonRpcParserUtil;

/**
 * Requirements for the selection of a note. Multiple requirements are connected using a logical AND.
 * 
 * @author Tobias Faßbender
 *
 */
public class NoteSelector {
	
	private List<Integer> ids;
	private LocalDateTime date;
	private int priority;
	
	private NoteRelation idRelation;
	private NoteRelation dateRelation;
	private NoteRelation priorityRelation;
	
	private transient static final Logger LOGGER = LogManager.getLogger(NoteSelector.class);
	
	public NoteSelector() {
		//default constructor for java bean convention
		idRelation = NoteRelation.NONE;
		dateRelation = NoteRelation.NONE;
		priorityRelation = NoteRelation.NONE;
	}
	
	/**
	 * Create a NoteSelector from a JSON-RPC parameter object
	 * 
	 * @param parameter
	 *        The parameter object from a JSON-RPC message
	 * 
	 * @throws UnsupportedParameterException
	 *         An UnsopportedParameterException is thrown if the NoteSelector can't be built from the parameter object.
	 */
	public static NoteSelector fromJsonRpcParameters(Object parameters) throws UnsupportedParameterException {
		LOGGER.debug("Deserializing JSON-RPC parameter object to NoteSelector (parameters: " + parameters + ")");
		NoteSelector noteSelector = JsonRpcParserUtil.parseToType(parameters, NoteSelector.class);
		return noteSelector;
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
	
	@JsonIgnore
	public boolean isValid() {
		boolean valid = true;
		
		//every relation that is not NONE has values
		valid &= (idRelation == NoteRelation.NONE || (ids != null && !ids.isEmpty()));
		valid &= (dateRelation == NoteRelation.NONE || date != null);
		//if there are multiple ids the relation has to be IN
		valid &= (ids == null || ids.size() <= 1 || idRelation == NoteRelation.IN);
		//date relation and priority relation mussn't use IN
		valid &= (dateRelation != NoteRelation.IN && priorityRelation != NoteRelation.IN);
		//id relation and priority relation mussn't use BEFORE or AFTER
		valid &= (idRelation != NoteRelation.BEFORE && idRelation != NoteRelation.AFTER);
		valid &= (priorityRelation != NoteRelation.BEFORE && priorityRelation != NoteRelation.AFTER);
		
		return valid;
	}
	
	public List<Note> getMatching(List<Note> notes) {
		if (!isValid()) {
			throw new IllegalStateException("This NoteSelector is not valid and can't be used to match notes.");
		}
		Stream<Note> noteStream = notes.stream();
		
		//match id relation
		if (idRelation != NoteRelation.NONE) {
			switch (idRelation) {
				case EQUALS:
					noteStream = noteStream.filter(note -> note.getId() == ids.get(0));
					break;
				case GREATER:
					noteStream = noteStream.filter(note -> note.getId() > ids.get(0));
					break;
				case GREATER_EQUALS:
					noteStream = noteStream.filter(note -> note.getId() >= ids.get(0));
					break;
				case IN:
					noteStream = noteStream.filter(note -> ids.contains(note.getId()));
					break;
				case LESS:
					noteStream = noteStream.filter(note -> note.getId() < ids.get(0));
					break;
				case LESS_EQUALS:
					noteStream = noteStream.filter(note -> note.getId() <= ids.get(0));
					break;
				default:
					throw new IllegalStateException("Unexpected id relation. This NoteSelector seems to be not valid.");
			}
		}
		//match date relation
		if (dateRelation != NoteRelation.NONE) {
			switch (dateRelation) {
				case AFTER:
				case GREATER:
					noteStream = noteStream.filter(note -> note.getExecutionDates().get(0).isAfter(date));
					break;
				case BEFORE:
				case LESS:
					noteStream = noteStream.filter(note -> note.getExecutionDates().get(0).isBefore(date));
					break;
				case EQUALS:
					noteStream = noteStream.filter(note -> note.getExecutionDates().get(0).equals(date));
					break;
				case GREATER_EQUALS:
					noteStream = noteStream
							.filter(note -> note.getExecutionDates().get(0).isAfter(date) || note.getExecutionDates().get(0).equals(date));
					break;
				case LESS_EQUALS:
					noteStream = noteStream
							.filter(note -> note.getExecutionDates().get(0).isBefore(date) || note.getExecutionDates().get(0).equals(date));
					break;
				default:
					throw new IllegalStateException("Unexpected date relation. This NoteSelector seems to be not valid.");
			}
		}
		//match priority relation
		if (priorityRelation != NoteRelation.NONE) {
			switch (priorityRelation) {
				case EQUALS:
					noteStream = noteStream.filter(note -> note.getPriority() == priority);
					break;
				case GREATER:
					noteStream = noteStream.filter(note -> note.getPriority() > priority);
					break;
				case GREATER_EQUALS:
					noteStream = noteStream.filter(note -> note.getPriority() >= priority);
					break;
				case LESS:
					noteStream = noteStream.filter(note -> note.getPriority() < priority);
					break;
				case LESS_EQUALS:
					noteStream = noteStream.filter(note -> note.getPriority() <= priority);
					break;
				default:
					throw new IllegalStateException("Unexpected priority relation. This NoteSelector seems to be not valid.");
			}
		}
		
		List<Note> matching = noteStream.collect(Collectors.toList());
		return matching;
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
	
	public NoteRelation getIdRelation() {
		return idRelation;
	}
	public void setIdRelation(NoteRelation idRelation) {
		this.idRelation = idRelation;
	}
	
	public NoteRelation getDateRelation() {
		return dateRelation;
	}
	public void setDateRelation(NoteRelation dateRelation) {
		this.dateRelation = dateRelation;
	}
	
	public NoteRelation getPriorityRelation() {
		return priorityRelation;
	}
	public void setPriorityRelation(NoteRelation priorityRelation) {
		this.priorityRelation = priorityRelation;
	}
}