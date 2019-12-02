package net.jfabricationgames.notebook.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;
import net.jfabricationgames.json_rpc.util.JsonRpcParserUtil;

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
	
	@Test
	public void testFromJsonRpcParameters_withoutDates() throws UnsupportedParameterException {
		NoteSelector noteSelector = new NoteSelectorBuilder().addIds(Arrays.asList(1, 2, 3)).setIdRelation(NoteRelation.IN).setPriority(42)
				.setPriorityRelation(NoteRelation.LESS_EQUALS).build();
		try {
			Object noteObject = JsonRpcParserUtil.parseToType(noteSelector, Object.class);
			
			NoteSelector recreatedNoteSelector = NoteSelector.fromJsonRpcParameters(noteObject);
			
			assertEquals(noteSelector, recreatedNoteSelector);
		}
		catch (UnsupportedParameterException upe) {
			upe.printStackTrace();
			throw upe;
		}
	}
	
	@Test
	public void testFromJsonRpcParameters_withDates() throws UnsupportedParameterException {
		NoteSelector noteSelector = new NoteSelectorBuilder().addIds(Arrays.asList(1, 2, 3)).setIdRelation(NoteRelation.IN)
				.setDate(LocalDateTime.now()).setDateRelation(NoteRelation.AFTER).setPriority(42).setPriorityRelation(NoteRelation.LESS_EQUALS)
				.build();
		try {
			Object noteObject = JsonRpcParserUtil.parseToType(noteSelector, Object.class);
			
			NoteSelector recreatedNoteSelector = NoteSelector.fromJsonRpcParameters(noteObject);
			
			assertEquals(noteSelector, recreatedNoteSelector);
		}
		catch (UnsupportedParameterException upe) {
			upe.printStackTrace();
			throw upe;
		}
	}
}