package net.jfabricationgames.notebook.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;
import net.jfabricationgames.notebook.note.Relation;

/**
 * Create a connection to a database and add or get values of one specific table for testing.
 * 
 * @author Tobias Faßbender
 */
public class DatabaseConnection {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class);
	
	/**
	 * The connection url for the mysql database in the docker
	 * <ul>
	 * <li>"jdbc:mysql://" just tells the driver to use jdbc:mysql (needed)</li>
	 * <li>"mysql" is the name or alias of the docker container</li>
	 * <li>"useSSL=false" obviously tells to not use SSL (and don't warn because of no SSL every time), because SSL is not needed when communicating
	 * between docker containers</li>
	 * </ul>
	 */
	public static final String URL = "jdbc:mysql://mysql?useSSL=false";
	public static final String USER = "notebook";
	public static final String DATABASE = "notebook";
	public static final String TABLE_NOTES = "notes";
	public static final String TABLE_EXECUTION_DATES = "execution_dates";
	public static final String TABLE_REMINDER_DATES = "reminder_dates";
	
	/**
	 * The passwords are loaded from a properties file "passwords.properties" which is not added to the git-repository (for obvious reasons)
	 * <p>
	 * The file is located in src/main/resources/passwords.properties
	 * <p>
	 * Add the same password in the environment variables of the docker-compose.ylm
	 */
	private static String USER_PASSWORD;
	//private static String ROOT_PASSWORD;
	
	public static final String VERSION = "1.0.0";
	
	private static final boolean autoCommit = false;
	
	private static DatabaseConnection instance;
	
	private DatabaseConnection() throws SQLException {
		LOGGER.info("Creating DatabaseConnection; current version is " + VERSION);
		try {
			loadUserPassword();
			createDatabaseIfNotExists();
			createTableIfNotExists();
		}
		catch (SQLException sqle) {
			LOGGER.error("Error while creating the database resources", sqle);
			throw sqle;
		}
		catch (IOException ioe) {
			LOGGER.error("MySQL passwords couldn't be loaded", ioe);
			throw new SQLException("MySQL passwords couldn't be loaded", ioe);
		}
	}
	
	public static synchronized DatabaseConnection getInstance() throws SQLException {
		if (instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}
	
	/**
	 * Load the password from the properties
	 * 
	 * @throws IOException
	 */
	private void loadUserPassword() throws IOException {
		String resourceName = "passwords.properties";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties passwordProperties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			passwordProperties.load(resourceStream);
		}
		USER_PASSWORD = passwordProperties.getProperty("MYSQL_USER_PASSWORD");
		
		if (USER_PASSWORD == null || USER_PASSWORD.equals("")) {
			throw new IOException("No user password could be loaded from properties.");
		}
	}
	
	private void createDatabaseIfNotExists() throws SQLException {
		String query = "CREATE DATABASE IF NOT EXISTS " + DATABASE + ";";
		DataSource dataSource = getDataSourceWithoutDatabase();
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				connection.setAutoCommit(autoCommit);
				LOGGER.info("Creating database (if not exists); sending query: " + query);
				statement.execute(query);
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
	}
	
	private void createTableIfNotExists() throws SQLException {
		String queryCreateTableNotes = "CREATE TABLE IF NOT EXISTS " + DATABASE + "." + TABLE_NOTES + " ("//
				+ "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "//
				+ "headline VARCHAR(150), "//
				+ "note_text TEXT, "//
				+ "priority INT "//
				+ ");";
		String queryCreateTableExecutionDates = "CREATE TABLE IF NOT EXISTS " + DATABASE + "." + TABLE_EXECUTION_DATES + " ("//
				+ "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "//
				+ "note_id INT NOT NULL, "//
				+ "execution_date DATETIME, "//
				+ "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE "//
				+ ");";
		String queryCreateTableReminderDates = "CREATE TABLE IF NOT EXISTS " + DATABASE + "." + TABLE_REMINDER_DATES + " ("//
				+ "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "//
				+ "note_id INT NOT NULL, "//
				+ "reminder_date DATETIME, "//
				+ "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE "//
				+ ");";
		DataSource dataSource = getDataSource();
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(autoCommit);
			try (Statement statement = connection.createStatement()) {
				LOGGER.info("Creating table notes (if not exists); sending query: " + queryCreateTableNotes);
				statement.execute(queryCreateTableNotes);
				
				LOGGER.info("Creating table execution dates (if not exists); sending query: " + queryCreateTableExecutionDates);
				statement.execute(queryCreateTableExecutionDates);
				
				LOGGER.info("Creating table reminder dates (if not exists); sending query: " + queryCreateTableReminderDates);
				statement.execute(queryCreateTableReminderDates);
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
	}
	
	private MysqlDataSource getDataSource() {
		//https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
		MysqlDataSource dataSource = getDataSourceWithoutDatabase();
		dataSource.setDatabaseName(DATABASE);
		return dataSource;
	}
	private MysqlDataSource getDataSourceWithoutDatabase() {
		//https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setURL(URL);
		dataSource.setUser(USER);
		dataSource.setPassword(USER_PASSWORD);
		dataSource.setPort(3306);//the default mysql port
		try {
			//enable public key retrieval because I want to get the keys of the inserted data 
			//(which seems to cause problems when using useSSL=false in the url)
			dataSource.setAllowPublicKeyRetrieval(true);
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return dataSource;
	}
	
	/**
	 * Receives a note and creates it in the database.
	 * 
	 * @param note
	 * @return The notes new id
	 * 
	 * @throws SQLException
	 */
	public int createNote(Note note) throws SQLException {
		LOGGER.info("Creating note: " + note);
		String queryNote = "INSERT INTO " + DATABASE + "." + TABLE_NOTES + " (`id`, `headline`, `note_text`, `priority`) VALUES (\"0\", ?, ?, ?);";
		
		int id = -1;
		
		DataSource dataSource = getDataSource();
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(autoCommit);
			
			//add the note content to the note table
			try {
				try (PreparedStatement statement = connection.prepareStatement(queryNote, Statement.RETURN_GENERATED_KEYS)) {
					statement.setString(1, note.getHeadline());
					statement.setString(2, note.getNoteText());
					statement.setInt(3, note.getPriority());
					
					LOGGER.debug("Executing PreparedStatement: " + statement);
					int affectedRows = statement.executeUpdate();
					
					if (affectedRows == 0) {
						throw new SQLException("Inserting data to note table failed. No affected rows.");
					}
					
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							id = generatedKeys.getInt(1);
						}
						else {
							throw new SQLException("Inserting data to note table failed. No ID obtained.");
						}
					}
				}
				
				//add the execution and reminder dates in the respective tables
				insertExecutionAndReminderDates(connection, note, id);
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
		
		return id;
	}
	
	/**
	 * Returns all notes in the database that match the NoteSelector.
	 * 
	 * @param selector
	 * @return All matching notes as a list
	 * 
	 * @throws SQLException
	 */
	public List<Note> getNotes(NoteSelector selector) throws SQLException {
		LOGGER.info("Searching for notes: " + selector);
		String selectionQuery = createSelectorQuery(selector);
		String query = "SELECT n.id, n.headline, n.note_text, n.priority, ex.execution_date, re.reminder_date "//
				+ "FROM " + DATABASE + "." + TABLE_NOTES + " n "//
				+ "JOIN " + TABLE_EXECUTION_DATES + " ex ON n.id = ex.note_id "//
				+ "JOIN " + TABLE_REMINDER_DATES + " re ON n.id = re.note_id "//
				+ selectionQuery + ";";//selectionQuery contains the WHERE clause
		
		Map<Integer, Note> notes = new HashMap<Integer, Note>();
		DataSource dataSource = getDataSource();
		
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(autoCommit);
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				connection.setAutoCommit(autoCommit);
				
				addSelectorValues(statement, selector, 1);
				LOGGER.debug("Executing PreparedStatement: " + statement);
				
				try (ResultSet rs = statement.executeQuery()) {
					//read the rows of the result set
					while (rs.next()) {
						//get the fields from each row
						int id = rs.getInt(1);
						String headline = rs.getString(2);
						String noteText = rs.getString(3);
						int priority = rs.getInt(4);
						LocalDateTime executionDate = rs.getObject(5, LocalDateTime.class);
						LocalDateTime reminderDate = rs.getObject(6, LocalDateTime.class);
						
						//add the result data to the notes
						Note note = notes.get(id);
						if (note == null) {
							note = new Note();
						}
						note.setId(id);
						note.setHeadline(headline);
						note.setNoteText(noteText);
						note.setPriority(priority);
						note.addExecutionDate(executionDate);
						note.addReminderDate(reminderDate);
						
						notes.put(id, note);
					}
				}
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
		
		return new ArrayList<Note>(notes.values());
	}
	
	/**
	 * Updates the note's content.
	 * 
	 * @param note
	 * @return The number of affected rows
	 * 
	 * @throws SQLException
	 */
	public int updateNote(Note note) throws SQLException {
		LOGGER.info("Updating note: " + note);
		String queryNote = "UPDATE " + DATABASE + "." + TABLE_NOTES + " SET headline = ?, note_text = ?, priority = ? WHERE id = ?;";
		
		int affectedRows = 0;
		
		DataSource dataSource = getDataSource();
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(autoCommit);
			
			try {
				//add the note content to the note table
				try (PreparedStatement statement = connection.prepareStatement(queryNote)) {
					statement.setString(1, note.getHeadline());
					statement.setString(2, note.getNoteText());
					statement.setInt(3, note.getPriority());
					statement.setInt(4, note.getId());
					
					LOGGER.debug("Executing PreparedStatement: " + statement);
					affectedRows = statement.executeUpdate();
					
					if (affectedRows == 0) {
						throw new SQLException("Updating data to note table failed. No affected rows.");
					}
				}
				
				//remove all execution and reminder dates
				deleteExecutionAndReminderDates(connection, note.getId());
				
				//(re-)insert the execution and reminder dates
				insertExecutionAndReminderDates(connection, note, note.getId());
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
		
		return affectedRows;
	}
	
	/**
	 * Deletes all notes that match the NoteSelector.
	 * 
	 * @param selector
	 * @return The number of affected rows
	 * 
	 * @throws SQLException
	 */
	public int deleteNotes(NoteSelector selector) throws SQLException {
		LOGGER.info("Deleting notes: " + selector);
		LOGGER.info("Searching for matching notes");
		List<Note> matchingNotes = getNotes(selector);
		List<Integer> matchingIds = matchingNotes.stream().map(note -> note.getId()).collect(Collectors.toList());
		LOGGER.info("Found matching note ids: " + matchingIds);
		
		String deleteQuery = "DELETE FROM " + DATABASE + "." + TABLE_NOTES + " WHERE id = ?;";
		
		int affectedRows = 0;
		
		DataSource dataSource = getDataSource();
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(autoCommit);
			
			//add the note content to the note table
			try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
				for (int id : matchingIds) {
					statement.setInt(1, id);
					LOGGER.debug("Executing PreparedStatement: " + statement);
					affectedRows += statement.executeUpdate();
					
					if (affectedRows == 0) {
						throw new SQLException("Updating data to note table failed. No affected rows.");
					}
				}
				
				connection.commit();
			}
			catch (SQLException sqle) {
				connection.rollback();
				throw sqle;
			}
		}
		
		return affectedRows;
	}
	
	/**
	 * Creates a SQL WHERE clause from the NoteSelector object
	 * <p>
	 * Assuming the note table is called 'n' and the execution date table is called 'ex'
	 * 
	 * @param selector
	 * @return
	 * 
	 * @throws SQLException
	 */
	/*private */static String createSelectorQuery(NoteSelector selector) throws SQLException {
		StringBuilder sb = new StringBuilder();
		boolean added = false;//shows if something was already added and an AND is needed
		
		if (!selector.isValid()) {
			throw new SQLException("The given NoteSelector can't be converted to a valid query: " + selector);
		}
		
		sb.append("WHERE ");
		if (selector.getIdRelation() == Relation.NONE && selector.getPriorityRelation() == Relation.NONE
				&& selector.getDateRelation() == Relation.NONE) {
			sb.append("1");//no further selection
		}
		else {
			if (selector.getIdRelation() != Relation.NONE) {
				if (selector.getIdRelation() == Relation.IN) {
					sb.append("n.id ").append(Relation.IN.getSign()).append(" (?)");
				}
				else {
					sb.append("n.id ").append(selector.getIdRelation().getSign()).append(" ?");
				}
				added = true;
			}
			if (selector.getPriorityRelation() != Relation.NONE) {
				if (added) {
					sb.append(" AND ");
				}
				sb.append("n.priority ").append(selector.getPriorityRelation().getSign()).append(" ?");
				added = true;
			}
			if (selector.getDateRelation() != Relation.NONE) {
				if (added) {
					sb.append(" AND ");
				}
				sb.append("ex.execution_date ").append(selector.getDateRelation().getSign()).append(" ?");
			}
		}
		
		return sb.toString();
	}
	/**
	 * Add the selector values to the prepared statement.
	 * 
	 * @throws SQLException
	 */
	private static void addSelectorValues(PreparedStatement statement, NoteSelector selector, int firstIndex) throws SQLException {
		if (!selector.isValid()) {
			throw new SQLException("The given NoteSelector can't be converted to a valid query: " + selector);
		}
		
		int added = 0;
		if (selector.getIdRelation() != Relation.NONE) {
			if (selector.getIds().size() == 1) {
				statement.setInt(firstIndex + added, selector.getIds().get(0));
				added++;
			}
			else {
				//ids use an IN relation
				String allIdsAsCsv = selector.getIds().stream().map(i -> Integer.toString(i)).collect(Collectors.joining(","));
				statement.setString(firstIndex + added, allIdsAsCsv);
			}
		}
		if (selector.getPriorityRelation() != Relation.NONE) {
			statement.setInt(firstIndex + added, selector.getPriority());
			added++;
		}
		if (selector.getDateRelation() != Relation.NONE) {
			statement.setObject(firstIndex + added, selector.getDate());
			added++;
		}
	}
	
	/**
	 * Insert execution dates and reminder dates to the database
	 *
	 * @param con
	 * @param note
	 *        The note that contains the dates that are to be added
	 * @param id
	 *        The id of the note in the database
	 * 
	 * @throws SQLException
	 */
	private void insertExecutionAndReminderDates(Connection con, Note note, int id) throws SQLException {
		String queryExecutionDates = "INSERT INTO " + TABLE_EXECUTION_DATES + " (`id`, `note_id`, `execution_date`) VALUES (\"0\", ?, ?);";
		String queryReminderDates = "INSERT INTO " + TABLE_REMINDER_DATES + " (`id`, `note_id`, `reminder_date`) VALUES (\"0\", ?, ?);";
		
		//add the note execution dates to the execution dates table
		if (note.getExecutionDates() != null && !note.getExecutionDates().isEmpty()) {
			try (PreparedStatement statement = con.prepareStatement(queryExecutionDates)) {
				for (LocalDateTime executionTime : note.getExecutionDates()) {
					statement.setInt(1, id);
					statement.setObject(2, executionTime);//detection and conversation of LocalDateTime is done automatically
					
					LOGGER.debug("Executing PreparedStatement: " + statement);
					int affectedRows = statement.executeUpdate();
					
					if (affectedRows == 0) {
						throw new SQLException("Inserting data to execution_dates table failed. No ID obtained.");
					}
				}
			}
		}
		
		//add the note execution dates to the execution dates table
		if (note.getReminderDates() != null && !note.getReminderDates().isEmpty()) {
			try (PreparedStatement statement = con.prepareStatement(queryReminderDates)) {
				for (LocalDateTime reminderTime : note.getReminderDates()) {
					statement.setInt(1, id);
					statement.setObject(2, reminderTime);//detection and conversation of LocalDateTime is done automatically
					
					LOGGER.debug("Executing PreparedStatement: " + statement);
					int affectedRows = statement.executeUpdate();
					
					if (affectedRows == 0) {
						throw new SQLException("Inserting data to reminder_dates table failed. No ID obtained.");
					}
				}
			}
		}
	}
	
	/**
	 * Delete all execution dates and reminder dates that refer the the given id
	 * 
	 * @param con
	 * @param id
	 *        The id of the note, whichs execution and reminder dates are to be deleted
	 * 
	 * @throws SQLException
	 */
	private void deleteExecutionAndReminderDates(Connection con, int id) throws SQLException {
		String queryRemoveExecutionDates = "DELETE FROM " + TABLE_EXECUTION_DATES + " WHERE note_id = ?;";
		String queryRemoveReminderDates = "DELETE FROM " + TABLE_REMINDER_DATES + " WHERE note_id = ?;";
		
		try (PreparedStatement statement = con.prepareStatement(queryRemoveExecutionDates)) {
			statement.setInt(1, id);
			LOGGER.debug("Executing PreparedStatement: " + statement);
			statement.execute();
		}
		try (PreparedStatement statement = con.prepareStatement(queryRemoveReminderDates)) {
			statement.setInt(1, id);
			LOGGER.debug("Executing PreparedStatement: " + statement);
			statement.execute();
		}
	}
}