package net.jfabricationgames.notebook.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;
import net.jfabricationgames.json_rpc.util.JsonRpcParserUtil;

class NoteTest {
	
	@Test
	public void testFromJsonRpcParameters_withoutDates() throws UnsupportedParameterException {
		Note note = new Note("headline", "some text", 42);
		try {
			Object noteObject = JsonRpcParserUtil.parseToType(note, Object.class);
			
			Note recreatedNote = Note.fromJsonRpcParameters(noteObject);
			
			assertEquals(note, recreatedNote);
		}
		catch (UnsupportedParameterException upe) {
			upe.printStackTrace();
			throw upe;
		}
	}

	@Test
	public void testFromJsonRpcParameters_withDates() throws UnsupportedParameterException {
		Note note = new Note("headline", "some text", 42, Arrays.asList(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)),
				Arrays.asList(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10)));
		try {
			Object noteObject = JsonRpcParserUtil.parseToType(note, Object.class);
			
			Note recreatedNote = Note.fromJsonRpcParameters(noteObject);
			
			assertEquals(note, recreatedNote);
		}
		catch (UnsupportedParameterException upe) {
			upe.printStackTrace();
			throw upe;
		}
	}
}