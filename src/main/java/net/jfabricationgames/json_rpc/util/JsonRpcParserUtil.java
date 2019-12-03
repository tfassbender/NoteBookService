package net.jfabricationgames.json_rpc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;

public abstract class JsonRpcParserUtil {
	
	public static <T> T parseToType(Object obj, Class<T> clazz) throws UnsupportedParameterException {
		return parseToType(obj, clazz, true);
	}
	
	public static <T> T parseToType(Object obj, Class<T> clazz, boolean useJavaTimeModule) throws UnsupportedParameterException {
		T parsed;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (useJavaTimeModule) {
				//register java time module for serializing and deserializing LocalDateTime
				mapper.registerModule(new JavaTimeModule());
			}
			
			//serialize parameters object to string
			String parametersJson = mapper.writeValueAsString(obj);
			
			//deserialize as clazz object
			parsed = mapper.readValue(parametersJson, clazz);
		}
		catch (Exception e) {
			throw new UnsupportedParameterException("Parsing a NoteSelector from the parameter object failed", e);
		}
		
		return parsed;
	}
}