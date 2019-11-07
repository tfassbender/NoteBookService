package net.jfabricationgames.notebook.note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Note {
	
	private int id;
	private String headline;
	private String noteText;
	private int priority;
	private List<LocalDateTime> executionDates;
	private List<LocalDateTime> reminderDates;
	
	public Note() {
		//default constructor for java bean convention
	}
	
	public Note(String headline, String noteText, int priority) {
		this.headline = headline;
		this.noteText = noteText;
		this.priority = priority;
	}
	
	public Note(String headline, String noteText, int priority, List<LocalDateTime> executionDates, List<LocalDateTime> reminderDates) {
		this.headline = headline;
		this.noteText = noteText;
		this.priority = priority;
		this.executionDates = executionDates;
		this.reminderDates = reminderDates;
	}
	
	public static int compareExecutionDates(Note note1, Note note2) {
		LocalDateTime date1 = null;
		LocalDateTime date2 = null;
		if (note1.getExecutionDates() != null && !note1.getExecutionDates().isEmpty()) {
			date1 = note1.getExecutionDates().get(0);
		}
		if (note2.getExecutionDates() != null && !note2.getExecutionDates().isEmpty()) {
			date2 = note2.getExecutionDates().get(0);
		}
		if (date1 != null && date2 != null) {
			return compareLocalDateTimes(date1, date2);
		}
		else if (date1 != null && date2 == null) {
			return -1;
		}
		else if (date1 == null && date2 != null) {
			return 1;
		}
		else {
			return 0;
		}
	}
	public static int compareLocalDateTimes(LocalDateTime date1, LocalDateTime date2) {
		if (date1.equals(date2)) {
			return 0;
		}
		else if (date1.isBefore(date2)) {
			return -1;
		}
		else {
			return 1;
		}
	}
	
	public static Note parseToNote(Object obj) throws IllegalArgumentException {
		if (obj == null) {
			return null;
		}
		else if (obj instanceof Note) {
			return (Note) obj;
		}
		else if (obj instanceof Map) {
			//assume the params Object is a Map, because it's deserialized this way
			@SuppressWarnings("unchecked")
			Map<String, Object> parameterMap = (Map<String, Object>) obj;
			
			//check whether the required fields are included
			if (!parameterMap.containsKey("id") || !parameterMap.containsKey("headline") || !parameterMap.containsKey("noteText")
					|| !parameterMap.containsKey("priority")) {
				throw new IllegalArgumentException("The required fields were not found in the parameters");
			}
			
			try {
				Note note = new Note();
				note.setId(Integer.parseInt((String) parameterMap.get("id")));
				note.setHeadline((String) parameterMap.get("headline"));
				note.setNoteText((String) parameterMap.get("noteText"));
				note.setPriority(Integer.parseInt((String) parameterMap.get("priority")));
				Object executionDates = parameterMap.get("executionDates");
				Object reminderDates = parameterMap.get("reminderDates");
				
				note.setExecutionDates(parseDates(executionDates));
				note.setReminderDates(parseDates(reminderDates));
				
				return note;
			}
			catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("A parameter could not be parsed as int");
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Couldn't get all needed parameters from the object");
			}
		}
		else {
			throw new IllegalArgumentException("Object couldn't be parsed as note");
		}
	}
	
	private static List<LocalDateTime> parseDates(Object dates) {
		@SuppressWarnings("unchecked")
		List<LocalDateTime> parsedDates = (List<LocalDateTime>) dates;
		return parsedDates;
	}
	
	@Override
	public String toString() {
		return "Note [id=" + id + ", headline=" + headline + ", noteText=" + noteText + ", priority=" + priority + ", executionDates="
				+ executionDates + ", reminderDates=" + reminderDates + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((executionDates == null) ? 0 : executionDates.hashCode());
		result = prime * result + ((headline == null) ? 0 : headline.hashCode());
		result = prime * result + id;
		result = prime * result + ((noteText == null) ? 0 : noteText.hashCode());
		result = prime * result + priority;
		result = prime * result + ((reminderDates == null) ? 0 : reminderDates.hashCode());
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
		Note other = (Note) obj;
		if (executionDates == null) {
			if (other.executionDates != null)
				return false;
		}
		else if (!executionDates.equals(other.executionDates))
			return false;
		if (headline == null) {
			if (other.headline != null)
				return false;
		}
		else if (!headline.equals(other.headline))
			return false;
		if (id != other.id)
			return false;
		if (noteText == null) {
			if (other.noteText != null)
				return false;
		}
		else if (!noteText.equals(other.noteText))
			return false;
		if (priority != other.priority)
			return false;
		if (reminderDates == null) {
			if (other.reminderDates != null)
				return false;
		}
		else if (!reminderDates.equals(other.reminderDates))
			return false;
		return true;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public List<LocalDateTime> getExecutionDates() {
		return executionDates;
	}
	public void setExecutionDates(List<LocalDateTime> executionDates) {
		this.executionDates = executionDates;
	}
	public void addExecutionDate(LocalDateTime executionDate) {
		if (this.executionDates == null) {
			this.executionDates = new ArrayList<LocalDateTime>();
		}
		this.executionDates.add(executionDate);
	}
	public void addExecutionDates(List<LocalDateTime> executionDates) {
		if (this.executionDates == null) {
			this.executionDates = new ArrayList<LocalDateTime>();
		}
		this.executionDates.addAll(executionDates);
	}
	
	public List<LocalDateTime> getReminderDates() {
		return reminderDates;
	}
	public void setReminderDates(List<LocalDateTime> reminderDates) {
		this.reminderDates = reminderDates;
	}
	public void addReminderDate(LocalDateTime reminderDate) {
		if (this.reminderDates == null) {
			this.reminderDates = new ArrayList<LocalDateTime>();
		}
		this.reminderDates.add(reminderDate);
	}
	public void addReminderDates(List<LocalDateTime> reminderDates) {
		if (this.reminderDates == null) {
			this.reminderDates = new ArrayList<LocalDateTime>();
		}
		this.reminderDates.addAll(reminderDates);
	}
}