package net.jfabricationgames.notebook.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;
import net.jfabricationgames.notebook.db.DatabaseConnection;
import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

public class NoteBookServiceProvider {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class);
	
	/**
	 * Receives a note and creates it in the database.
	 * 
	 * @param note
	 * @return The notes new id
	 */
	public Integer create_note(Object parameters) throws SQLException, UnsupportedParameterException {
		LOGGER.info("create_note was called (parameters: " + parameters + ")");
		if (parameters instanceof Note) {
			Note note = (Note) parameters;
			DatabaseConnection db = DatabaseConnection.getInstance();
			int id = db.createNote(note);
			return id;
		}
		else {
			throw new UnsupportedParameterException("A Note object is expected for this method (create_note)");
		}
	}
	
	/**
	 * Returns all notes in the database that match the NoteSelector.
	 * 
	 * @param selector
	 * @return All matching notes as a list
	 */
	public List<Note> get_notes(Object parameters) throws SQLException, UnsupportedParameterException {
		LOGGER.info("get_notes was called (parameters: " + parameters + ")");
		if (parameters instanceof NoteSelector) {
			NoteSelector selector = (NoteSelector) parameters;
			DatabaseConnection db = DatabaseConnection.getInstance();
			List<Note> id = db.getNotes(selector);
			return id;
		}
		else {
			throw new UnsupportedParameterException("A NoteSelector object is expected for this method (get_notes)");
		}
	}
	
	/**
	 * Updates the note's content.
	 * 
	 * @param note
	 * @return The number of affected rows
	 */
	public Integer update_note(Object parameters) throws SQLException, UnsupportedParameterException {
		LOGGER.info("update_note was called (parameters: " + parameters + ")");
		if (parameters instanceof Note) {
			Note note = (Note) parameters;
			DatabaseConnection db = DatabaseConnection.getInstance();
			int id = db.updateNote(note);
			return id;
		}
		else {
			throw new UnsupportedParameterException("A Note object is expected for this method (update_note)");
		}
	}
	
	/**
	 * Deletes all notes that match the NoteSelector.
	 * 
	 * @param selector
	 * @return The number of affected rows
	 */
	public Integer delete_notes(Object parameters) throws SQLException, UnsupportedParameterException {
		LOGGER.info("delete_notes was called (parameters: " + parameters + ")");
		if (parameters instanceof NoteSelector) {
			NoteSelector selector = (NoteSelector) parameters;
			DatabaseConnection db = DatabaseConnection.getInstance();
			int id = db.deleteNotes(selector);
			return id;
		}
		else {
			throw new UnsupportedParameterException("A NoteSelector object is expected for this method (delete_notes)");
		}
	}
}