package net.jfabricationgames.notebook.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

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
	
	public static final String VERSION = "1.0.0";
	
	private static DatabaseConnection instance;
	
	private DatabaseConnection() {
		LOGGER.info("Creating DatabaseConnection; current version is " + VERSION);
		try {
			createDatabaseIfNotExists();
			createTableIfNotExists();
		}
		catch (SQLException sqle) {
			LOGGER.error("Error while creating the database resources", sqle);
		}
	}
	
	public static synchronized DatabaseConnection getInstance() {
		if (instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}
	
	private void createDatabaseIfNotExists() throws SQLException {
		String query = "CREATE DATABASE IF NOT EXISTS " + DATABASE + ";";
		DataSource dataSource = getDataSourceWithoutDatabase();
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
			LOGGER.info("Creating database (if not exists); sending query: " + query);
			statement.execute(query);
		}
	}
	
	private void createTableIfNotExists() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS ws_db_test.entries (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, entry VARCHAR(100));";
		DataSource dataSource = getDataSource();
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
			LOGGER.info("Creating table (if not exists); sending query: " + query);
			statement.execute(query);
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
		dataSource.setPassword("52hgfoklh43bvd04ohif");//add the same password in the environment variables of the docker-compose.ylm
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
	 */
	public int createNote(Note note) {
		LOGGER.info("Creating note: " + note);
		//TODO create note in database
		
		//TODO return the id of the note in the database
		return -1;
	}
	
	/**
	 * Returns all notes in the database that match the NoteSelector.
	 * 
	 * @param selector
	 * @return All matching notes as a list
	 */
	public List<Note> getNotes(NoteSelector selector) {
		LOGGER.info("Searching for notes: " + selector);
		//TODO get the notes from the database
		return null;
	}
	
	/**
	 * Updates the note's content.
	 * 
	 * @param note
	 * @return The number of affected rows
	 */
	public int updateNote(Note note) {
		LOGGER.info("Updating note: " + note);
		//TODO update the note content
		
		//TODO return the affected rows
		return -1;
	}
	
	/**
	 * Deletes all notes that match the NoteSelector.
	 * 
	 * @param selector
	 * @return The number of affected rows
	 */
	public int deleteNotes(NoteSelector selector) {
		LOGGER.info("Deleting notes: " + selector);
		//TODO delete the matching notes
		
		//TODO return the affected rows
		return -1;
	}
	
	//TODO remove other methods
	
	/**
	 * Add an entry to the table.
	 * 
	 * @param entry
	 *        The entry that is to be added to the table
	 * 
	 * @return Returns the id of the entry that was added
	 * 
	 * @throws SQLException
	 */
	/*public int addEntry(String entry) throws SQLException {
		//https://javabeginners.de/Datenbanken/Prepared_Statement.php
		String query = "INSERT INTO ws_db_test.entries(`id`, `entry`) VALUES (\"0\", ?);";
		int id = -1;
		
		DataSource dataSource = getDataSource();
		try (Connection con = dataSource.getConnection();//
				PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, entry);
			int affectedRows = ps.executeUpdate();
			
			if (affectedRows == 0) {
				throw new SQLException("Inserting data failed. No affected rows.");
			}
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					id = generatedKeys.getInt(1);
				}
				else {
					throw new SQLException("Inserting data failed. No ID obtained.");
				}
			}
		}
		return id;
	}*/
	
	/**
	 * Get an entry for an id in the table.
	 * 
	 * @param id
	 *        The id of the entry in the table
	 * 
	 * @return The entry at the given id in the table (or null if there is no such id in the table)
	 * 
	 * @throws SQLException
	 */
	/*public String getEntry(int id) throws SQLException {
		//https://javabeginners.de/Datenbanken/Prepared_Statement.php
		String query = "SELECT entry FROM ws_db_test.entries WHERE id = ?;";
		String entry = null;
		
		DataSource dataSource = getDataSource();
		try (Connection con = dataSource.getConnection();//
				PreparedStatement ps = con.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					entry = rs.getString(1);
				}
			}
		}
		return entry;
	}*/
	
	/**
	 * Get all ids in the table as a list of Integers
	 * 
	 * @return
	 * 
	 * @throws SQLException
	 */
	/*public List<Integer> getIds() throws SQLException {
		String query = "SELECT id FROM ws_db_test.entries";
		List<Integer> ids = new ArrayList<Integer>();
		
		DataSource dataSource = getDataSource();
		try (Connection con = dataSource.getConnection();//
				PreparedStatement ps = con.prepareStatement(query);//
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ids.add(Integer.valueOf(rs.getInt(1)));
			}
		}
		return ids;
	}*/
}