package net.jfabricationgames.notebook.note;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NoteSelectorTest {
	
	@Test
	public void testIsValid() {
		NoteSelector selector1 = new NoteSelectorBuilder().setDateRelation(Relation.AFTER).build();
		NoteSelector selector2 = new NoteSelectorBuilder().setIdRelation(Relation.GREATER).build();
		NoteSelector selector3 = new NoteSelectorBuilder().setDateRelation(Relation.IN).build();
		NoteSelector selector4 = new NoteSelectorBuilder().addId(1).addId(2).setIdRelation(Relation.LESS).build();
		
		NoteSelector valid1 = new NoteSelectorBuilder().setDate(LocalDateTime.now()).setDateRelation(Relation.BEFORE).build();
		NoteSelector valid2 = new NoteSelectorBuilder().addId(1).addId(2).setIdRelation(Relation.IN).build();
		NoteSelector valid3 = new NoteSelectorBuilder().addId(1).setIdRelation(Relation.EQUALS).setPriority(1)
				.setPriorityRelation(Relation.GREATER_EQUALS).build();
		
		assertFalse(selector1.isValid());
		assertFalse(selector2.isValid());
		assertFalse(selector3.isValid());
		assertFalse(selector4.isValid());
		
		assertTrue(valid1.isValid());
		assertTrue(valid2.isValid());
		assertTrue(valid3.isValid());
	}
}