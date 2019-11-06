package net.jfabricationgames.notebook.note;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NoteSelectorTest {
	
	@Test
	public void testIsValid() {
		NoteSelector selector1 = new NoteSelectorBuilder().setDateRelation(NoteRelation.AFTER).build();
		NoteSelector selector2 = new NoteSelectorBuilder().setIdRelation(NoteRelation.GREATER).build();
		NoteSelector selector3 = new NoteSelectorBuilder().setDateRelation(NoteRelation.IN).build();
		NoteSelector selector4 = new NoteSelectorBuilder().addId(1).addId(2).setIdRelation(NoteRelation.LESS).build();
		
		NoteSelector valid1 = new NoteSelectorBuilder().setDate(LocalDateTime.now()).setDateRelation(NoteRelation.BEFORE).build();
		NoteSelector valid2 = new NoteSelectorBuilder().addId(1).addId(2).setIdRelation(NoteRelation.IN).build();
		NoteSelector valid3 = new NoteSelectorBuilder().addId(1).setIdRelation(NoteRelation.EQUALS).setPriority(1)
				.setPriorityRelation(NoteRelation.GREATER_EQUALS).build();
		
		assertFalse(selector1.isValid());
		assertFalse(selector2.isValid());
		assertFalse(selector3.isValid());
		assertFalse(selector4.isValid());
		
		assertTrue(valid1.isValid());
		assertTrue(valid2.isValid());
		assertTrue(valid3.isValid());
	}
}