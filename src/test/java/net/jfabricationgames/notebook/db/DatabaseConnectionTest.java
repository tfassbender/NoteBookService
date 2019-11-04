package net.jfabricationgames.notebook.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.notebook.note.NoteSelector;
import net.jfabricationgames.notebook.note.NoteSelectorBuilder;
import net.jfabricationgames.notebook.note.Relation;

class DatabaseConnectionTest {
	
	@Test
	public void testCreateSelectorQuery() throws SQLException {
		NoteSelector selector1 = new NoteSelectorBuilder().addId(1).addId(2).setIdRelation(Relation.IN).build();
		String expectedWhereClause1 = "WHERE n.id IN (?)";
		
		NoteSelector selector2 = new NoteSelectorBuilder().addId(1).setIdRelation(Relation.GREATER).setPriority(5)
				.setPriorityRelation(Relation.LESS_EQUALS).build();
		String expectedWhereClause2 = "WHERE n.id > ? AND n.priority <= ?";
		
		NoteSelector selector3 = new NoteSelectorBuilder().setPriority(5).setPriorityRelation(Relation.EQUALS).setDate(LocalDateTime.now())
				.setDateRelation(Relation.BEFORE).build();
		String expectedWhereClause3 = "WHERE n.priority = ? AND ex.execution_date < ?";
		
		NoteSelector selector4 = new NoteSelectorBuilder().build();
		String expectedWhereClause4 = "WHERE 1";
		
		assertEquals(expectedWhereClause1, DatabaseConnection.createSelectorQuery(selector1));
		assertEquals(expectedWhereClause2, DatabaseConnection.createSelectorQuery(selector2));
		assertEquals(expectedWhereClause3, DatabaseConnection.createSelectorQuery(selector3));
		assertEquals(expectedWhereClause4, DatabaseConnection.createSelectorQuery(selector4));
	}
}